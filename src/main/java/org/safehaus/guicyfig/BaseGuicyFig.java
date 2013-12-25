package org.safehaus.guicyfig;


import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.netflix.config.DynamicPropertyFactory;
import com.netflix.config.PropertyWrapper;


/**
 * Base class for injected dynamic configuration beans.
 */
class BaseGuicyFig implements GuicyFig {
    private static final Logger LOG = LoggerFactory.getLogger( BaseGuicyFig.class );
    private final PropertyChangeSupport changeSupport = new PropertyChangeSupport( this );
    private final Map<String,InternalOption> options = new HashMap<String, InternalOption>();
    private final Map<Method,InternalOption> methodOptionMap = new HashMap<Method, InternalOption>();
    private final Map<String,InternalOption> methodNameOptionMap = new HashMap<String, InternalOption>();
    private DynamicPropertyFactory factory = DynamicPropertyFactory.getInstance();
    private Overrides overrides;


    ConfigOption add( final String key, @Nullable final String defval, Method method ) {
        Preconditions.checkNotNull( key, "key cannot be null" );
        Preconditions.checkNotNull( method, "method cannot be null for option with key {}", key );

        InternalOption option;
        PropertyWrapper property = null;

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
        }

        //noinspection unchecked
        option = new InternalOption( key, property );
        //noinspection ConstantConditions
        property.addCallback( new PropertyChangeRunner( option ) );
        methodOptionMap.put( method, option );
        methodNameOptionMap.put( method.getName(), option );
        return options.put( option.key(), option );
    }


    class PropertyChangeRunner implements Runnable {
        final InternalOption option;

        PropertyChangeRunner( InternalOption option ) {
            this.option = option;
        }


        @Override
        public void run() {
            if ( ! option.getCurrentValue().equals( option.getNewPropertyValue() ) ) {
                if ( LOG.isDebugEnabled() ) {
                    StringBuilder sb = new StringBuilder();
                    sb.append( option.key() ).append( " changed from " ).append( option.getCurrentValue() )
                            .append( " to " ).append( option.getNewPropertyValue() );

                    LOG.debug( sb.toString() );
                }

                changeSupport.firePropertyChange( option.key(), option.getCurrentValue(), option.getNewPropertyValue() );
                option.setCurrentValue( option.getNewPropertyValue() );
            }
        }
    }


    ConfigOption getOption( Method method ) {
        return methodOptionMap.get( method );
    }


    @Override
    public void setOverrides( Overrides overrides ) {
        LOG.info( "Applying overrides: {}", overrides );

        if ( this.overrides != null ) {
            LOG.warn( "Overrides have already been set previously: {}", this.overrides );
        }

        for ( Option optAnnot : overrides.options() ) {
            if ( methodNameOptionMap.containsKey( optAnnot.method() ) ) {
                InternalOption option = methodNameOptionMap.get( optAnnot.method() );
                option.setOverrideValue( optAnnot.override() );

                if ( LOG.isInfoEnabled() ) {
                    StringBuilder sb = new StringBuilder();
                    sb.append( "ConfigOption " ).append( option.key() ).append( " had value " )
                      .append( option.value() ).append( " overridden by " ).append( option.getOverrideValue() );
                    LOG.info( sb.toString() );
                }
            }
        }

        this.overrides = overrides;
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
    public ConfigOption[] getOptions() {
        return options.values().toArray( new ConfigOption[options.size()] );
    }


    @Override
    public ConfigOption getOption( String key ) {
        return options.get( key );
    }


    @Override
    public String getKeyByMethod( final String methodName ) {
        if ( methodNameOptionMap.containsKey( methodName ) ) {
            return methodNameOptionMap.get( methodName ).key();
        }

        return null;
    }


    @Override
    public Object getValueByMethod( final String methodName ) {
        if ( methodNameOptionMap.containsKey( methodName ) ) {
            return methodNameOptionMap.get( methodName ).value();
        }

        return null;
    }


    @Override
    public Properties filterOptions( final Properties properties ) {
        Preconditions.checkNotNull( properties );
        Properties filtered = new Properties();

        for ( ConfigOption option : options.values() ) {
            if ( properties.containsKey( option.key() ) ) {
                filtered.put( option.key(), properties.getProperty( option.key() ) );
            }
        }

        return filtered;
    }


    @Override
    public Map<String, Object> filterOptions( final Map<String, Object> properties ) {
        Preconditions.checkNotNull( properties );
        Map<String,Object> filtered = new HashMap<String, Object>();

        for ( ConfigOption option : options.values() ) {
            if ( properties.containsKey( option.key() ) ) {
                filtered.put( option.key(), properties.get( option.key() ) );
            }
        }

        return filtered;
    }
}
