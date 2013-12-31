package org.safehaus.guicyfig;


import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.netflix.config.ConcurrentCompositeConfiguration;
import com.netflix.config.ConfigurationManager;
import com.netflix.config.DynamicPropertyFactory;
import com.netflix.config.PropertyWrapper;


/**
 * Base class for injected dynamic configuration beans.
 */
class BaseGuicyFig implements GuicyFig {
    private static final Logger LOG = LoggerFactory.getLogger( BaseGuicyFig.class );
    private final PropertyChangeSupport changeSupport = new PropertyChangeSupport( this );
    private final Map<String,InternalOptionState> options = new HashMap<String, InternalOptionState>();
    private final Map<Method,InternalOptionState> methodOptionMap = new HashMap<Method, InternalOptionState>();
    private final Map<String,InternalOptionState> methodNameOptionMap = new HashMap<String, InternalOptionState>();
    private DynamicPropertyFactory factory = DynamicPropertyFactory.getInstance();
    private OverridesImpl overrides;
    private boolean singleton;

    /** The user defined fig (configuration) interface that extends GuicyFig */
    private Class figInterface;
    private BypassImpl bypass;


    OptionState add( final String key, @Nullable final String defval, Method method ) {
        Preconditions.checkNotNull( key, "key cannot be null" );
        Preconditions.checkNotNull( method, "method cannot be null for option with key {}", key );

        InternalOptionState option;
        PropertyWrapper property;

        if ( method.getReturnType().equals( int.class ) || method.getReturnType().equals( Integer.class ) ) {
            property = factory.getIntProperty( key, ( defval == null ) ? 0 : Integer.parseInt( defval ) );
        }
        else if ( method.getReturnType().equals( String.class ) ) {
            property = factory.getStringProperty( key, defval );
        }
        else if ( method.getReturnType().equals( long.class ) || method.getReturnType().equals( Long.class ) ) {
            property = factory.getLongProperty( key, ( defval == null ) ? 0 : Long.parseLong( defval ) );
        }
        else if ( method.getReturnType().equals( float.class ) || method.getReturnType().equals( Float.class ) ) {
            property = factory.getFloatProperty( key, ( defval == null ) ? 0 : Float.parseFloat( defval ) );
            //noinspection unchecked
        }
        else if ( method.getReturnType().equals( double.class ) ) {
            property = factory.getDoubleProperty( key, ( defval == null ) ? 0 : Double.parseDouble( defval ) );
        }
        else if ( method.getReturnType().equals( boolean.class ) ) {
            property = factory.getBooleanProperty( key, ( defval != null ) && Boolean.parseBoolean( defval ) );
        }
        else {
            LOG.error( "Configuration methods with return type {} are not supported. Property {} will be ignored.",
                    method.getReturnType(), key );
            return null;
        }

        //noinspection unchecked
        option = new InternalOptionState( key, property, method );
        //noinspection ConstantConditions
        property.addCallback( new PropertyChangeRunner( option ) );
        methodOptionMap.put( method, option );
        methodNameOptionMap.put( method.getName(), option );
        return options.put( option.getKey(), option );
    }


    class PropertyChangeRunner implements Runnable {
        final InternalOptionState state;

        PropertyChangeRunner( InternalOptionState state ) {
            this.state = state;
        }


        @Override
        public void run() {
            if ( ! state.getValue().equals( state.getOldValue() ) ) {
                if ( LOG.isDebugEnabled() ) {
                    StringBuilder sb = new StringBuilder();
                    sb.append( state.getKey() ).append( " changed from " ).append( state.getOldValue() )
                            .append( " to " ).append( state.getValue() );

                    LOG.debug( sb.toString() );
                }

                changeSupport.firePropertyChange( state.getKey(), state.getOldValue(), state.getValue() );
                state.update();
            }
        }
    }


    OptionState getOption( Method method ) {
        return methodOptionMap.get( method );
    }


    void setFigInterface( Class figInterface ) {
        Preconditions.checkNotNull( figInterface, "The configuration interface cannot be null." );

        this.figInterface = figInterface;
    }


    @Override
    public void override( String key, String value ) {
        if ( overrides == null ) {
            overrides = new OverridesImpl( "default" );
        }

        if ( methodNameOptionMap.containsKey( key ) ) {
            InternalOptionState configOption = methodNameOptionMap.get( key );
            Option option = overrides.addOption( key, value );
            configOption.setOverride( option );
        }
        else if ( options.containsKey( key ) ) {
            InternalOptionState configOption = options.get( key );
            Option option = overrides.addOption( configOption.getMethod().toString(), value );
            configOption.setOverride( option );
        }
        else {
            throw new IllegalArgumentException( "Supplied key " + key + " is not a valid key or method name for this "
                    + getFigInterface().toString() );
        }

        ( ( ConcurrentCompositeConfiguration ) ConfigurationManager.getConfigInstance() )
                .setOverrideProperty( key, value );
    }


    @Override
    public Class getFigInterface() {
        return figInterface;
    }


    void setSingleton( boolean singleton ) {
        this.singleton = singleton;
    }


    @Override
    public boolean isSingleton() {
        return singleton;
    }


    @Override
    public void bypass( String key, String bypassValue ) {
        if ( bypass == null ) {
            bypass = new BypassImpl();
        }

        if ( methodNameOptionMap.containsKey( key ) ) {
            InternalOptionState state = methodNameOptionMap.get( key );
            Option option = bypass.addOption( key, bypassValue );
            state.setBypass( option );
        }
        else if ( options.containsKey( key ) ) {
            InternalOptionState state = options.get( key );
            Option option = bypass.addOption( state.getMethod().getName(), bypassValue );
            state.setBypass( option );
        }
        else {
            throw new IllegalArgumentException( "Supplied key " + key + " is not a valid key or method name for this "
                    + getFigInterface().toString() );
        }
    }


    @Override
    public Bypass getBypass() {
        return bypass;
    }


    private void applyBypass( Bypass bypass ) {
        Preconditions.checkNotNull( bypass );
        for ( Option annotation : bypass.options() ) {
            if ( methodNameOptionMap.containsKey( annotation.method() ) ) {
                InternalOptionState option = methodNameOptionMap.get( annotation.method() );
                option.setBypass( annotation );

                if ( LOG.isInfoEnabled() ) {
                    StringBuilder sb = new StringBuilder();
                    sb.append( "OptionState " ).append( option.getKey() ).append( " had value " )
                      .append( option.getValue() ).append( " bypassed by " ).append( option.getBypass() );
                    LOG.info( sb.toString() );
                }
            }
        }
    }


    @Override
    public void setBypass( Bypass bypass ) {
        // A null bypass will clear out all the bypass settings in effect
        if ( bypass == null ) {
            for ( Option annotation : this.bypass.options() ) {
                InternalOptionState option = methodNameOptionMap.get( annotation.method() );
                option.setBypass( null );
            }
            this.bypass = null;
            return;
        }

        HashSet<Env> environs = new HashSet<Env>( bypass.environments().length );
        Collections.addAll( environs, bypass.environments() );
        Env ctxEnv = Env.getEnvironment();

        // if the ALL environment is present then we just add all bypasses
        if ( environs.contains( Env.ALL ) || ctxEnv == Env.ALL ) {
            applyBypass( bypass );
            return;
        }

        /*
         * If we got here we need to make sure that the environment from the
         * Archaius DeploymentContext (on the configuration) if present maps
         * to one of the environments where the bypass is valid.
         */

        if ( environs.contains( ctxEnv ) ) {
            applyBypass( bypass );
        }

        if ( bypass instanceof BypassImpl ) {
            this.bypass = ( BypassImpl ) bypass;
        }
        else {
            this.bypass = new BypassImpl( bypass );
        }
    }


    @Override
    public void setOverrides( Overrides overrides ) {
        if ( overrides == null ) {
            if ( ConfigurationManager.getConfigInstance() instanceof ConcurrentCompositeConfiguration ) {
                ConcurrentCompositeConfiguration ccc = ( ConcurrentCompositeConfiguration )
                        ConfigurationManager.getConfigInstance();

                if( this.overrides != null ) {
                    for ( Option option : this.overrides.options() ) {
                        ccc.clearOverrideProperty( getKeyByMethod( option.method() ) );
                    }
                }
            }

            this.overrides = null;
            return;
        }

        Env ctxEnv = Env.getEnvironment();
        HashSet<Env> environs = new HashSet<Env>( overrides.environments().length );
        Collections.addAll( environs, overrides.environments() );

        if ( ctxEnv != Env.ALL && ! environs.contains( Env.ALL ) && ! environs.contains( ctxEnv ) ) {
            return;
        }


        if ( ConfigurationManager.getConfigInstance() instanceof ConcurrentCompositeConfiguration ) {
            ConcurrentCompositeConfiguration ccc = ( ConcurrentCompositeConfiguration )
                    ConfigurationManager.getConfigInstance();

            for ( Option annotation : overrides.options() ) {
                InternalOptionState state = methodNameOptionMap.get( annotation.method() );
                state.setOverride( annotation );
                ccc.setOverrideProperty( state.getKey(), annotation.override() );

                if ( LOG.isInfoEnabled() ) {
                    StringBuilder sb = new StringBuilder();
                    sb.append( "OptionState " ).append( state.getKey() ).append( " had value " )
                      .append( state.getValue() ).append( " overridden by " ).append( annotation.override());
                    LOG.info( sb.toString() );
                }
            }
        }

        if ( overrides instanceof OverridesImpl ) {
            this.overrides = ( OverridesImpl ) overrides;
        }
        else {
            this.overrides = new OverridesImpl( overrides );
        }
    }


    @Override
    public Overrides getOverrides() {
        return overrides;
    }


    @Override
    public void addPropertyChangeListener( final PropertyChangeListener listener ) {
        changeSupport.addPropertyChangeListener( listener );
    }


    @Override
    public void removePropertyChangeListener( final PropertyChangeListener listener ) {
        changeSupport.removePropertyChangeListener( listener );
    }


    @Override
    public OptionState[] getOptions() {
        return options.values().toArray( new OptionState[options.size()] );
    }


    @Override
    public OptionState getOption( String key ) {
        return options.get( key );
    }


    @Override
    public String getKeyByMethod( final String methodName ) {
        if ( methodNameOptionMap.containsKey( methodName ) ) {
            return methodNameOptionMap.get( methodName ).getKey();
        }

        return null;
    }


    @Override
    public Object getValueByMethod( final String methodName ) {
        if ( methodNameOptionMap.containsKey( methodName ) ) {
            return methodNameOptionMap.get( methodName ).getValue();
        }

        return null;
    }


    @Override
    public Properties filterOptions( final Properties properties ) {
        Preconditions.checkNotNull( properties );
        Properties filtered = new Properties();

        for ( OptionState option : options.values() ) {
            if ( properties.containsKey( option.getKey() ) ) {
                filtered.put( option.getKey(), properties.getProperty( option.getKey() ) );
            }
        }

        return filtered;
    }


    @Override
    public Map<String, Object> filterOptions( final Map<String, Object> properties ) {
        Preconditions.checkNotNull( properties );
        Map<String,Object> filtered = new HashMap<String, Object>();

        for ( OptionState option : options.values() ) {
            if ( properties.containsKey( option.getKey() ) ) {
                filtered.put( option.getKey(), properties.get( option.getKey() ) );
            }
        }

        return filtered;
    }
}
