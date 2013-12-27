package org.safehaus.guicyfig;


import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.MembersInjector;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matchers;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;


/**
 * Install this module, to automatically generate an implementation and inject
 * your GuicyFig extending configuration interfaces. See the test cases or the
 * wiki for an example.
 */
public class GuicyFigModule extends AbstractModule {
    private static final Logger LOG = LoggerFactory.getLogger( GuicyFigModule.class );
    private final Class[] classes;


    public GuicyFigModule( Class<? extends GuicyFig> clazz ) {
        classes = new Class[] { clazz };
    }


    @SuppressWarnings( "UnusedDeclaration" )
    public GuicyFigModule( Class<? extends GuicyFig>... classes ) {
        this.classes = classes;
    }


    protected void configure() {
        // add configuration logic here
        LOG.debug( "Configuring ..."  );

        for ( final Class clazz : classes ) {

            //noinspection unchecked
            bind( clazz ).toProvider( new Provider() {
                @Override
                public Object get() {
                    //noinspection unchecked
                    return getConcreteObject( clazz );
                }
            } );

            binder().bindListener( Matchers.any(), new TypeListener() {
                @Override
                public <I> void hear( final TypeLiteral<I> type, final TypeEncounter<I> encounter ) {
                    for ( final Field field : type.getRawType().getDeclaredFields() ) {

                        if ( field.getType() == clazz && field.isAnnotationPresent( Overrides.class ) ) {

                            //noinspection unchecked
                            final BaseGuicyFig newInstance = getConcreteObject( clazz );
                            newInstance.setOverrides( field.getAnnotation( Overrides.class ) );
                            encounter.register( new MembersInjector<I>() {
                                @Override
                                public void injectMembers( final I i ) {
                                    try {
                                            field.set( i, newInstance );
                                    }
                                    catch ( IllegalAccessException e ) {
                                        throw new RuntimeException( e );
                                    }
                                }
                            } );
                        }


                    }
                }
            } );
        }
        LOG.debug( "Done with configuration ..." );
    }


    static BaseGuicyFig getConcreteObject( final Class<? extends GuicyFig> configInterface ) {
        final BaseGuicyFig config = buildBaseObject( configInterface );
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass( BaseGuicyFig.class );
        enhancer.setInterfaces( new Class[] { configInterface } );
        Callback[] callbacks = new Callback[] { new MethodInterceptor() {
            @Override
            public Object intercept( final Object o, final Method method, final Object[] objects,
                                     final MethodProxy methodProxy ) throws Throwable {
                InternalOption option = ( InternalOption ) config.getOption( method );

                /*
                 * ------------------------------------------------------------
                 * Handle the forwarding calls to other interface methods on
                 * GuicyFig here: to register property change listeners
                 * and to access options as well as filter various properties
                 * ------------------------------------------------------------
                 */

                Class[] classes = new Class[objects.length];
                for ( int ii = 0; ii < classes.length; ii ++ ) {
                    classes[ii] = objects.getClass();
                    LOG.debug( "{}-th argument is {}", ii, objects[ii] );
                }

                if ( option == null ) {
                    LOG.debug( "Invoking method {} in declaring class {}",
                            method.getName(), method.getDeclaringClass() );

                    if ( method.getName().equals( "getOptions" ) ) {
                        return config.getOptions();
                    }

                    if ( method.getName().equals( "getOption" ) ) {
                        return config.getOption( ( String ) objects[0] );
                    }

                    if ( method.getName().equals( "getKeyByMethod" ) ) {
                        return config.getKeyByMethod( ( String ) objects[0] );
                    }

                    if ( method.getName().equals( "getValueByMethod" ) ) {
                        return config.getValueByMethod( ( String ) objects[0] );
                    }

                    if ( method.getName().equals( "filterOptions" ) ) {
                        if ( Properties.class == objects[0].getClass() ) {
                            return config.filterOptions( ( Properties ) objects[0] );
                        }
                        else {
                            //noinspection unchecked
                            return config.filterOptions( ( Map<String,Object> ) objects[0] );
                        }
                    }

                    if ( method.getName().equals( "addPropertyChangeListener" ) ) {
                        config.addPropertyChangeListener( ( PropertyChangeListener ) objects[0] );
                        return null;
                    }

                    if ( method.getName().equals( "removePropertyChangeListener" ) ) {
                        config.removePropertyChangeListener( ( PropertyChangeListener ) objects[0] );
                        return null;
                    }

                    if ( method.getName().equals( "setOverrides" ) ) {
                        config.setOverrides( ( Overrides ) objects[0] );
                        return null;
                    }

                    if ( method.getName().equals( "getOverrides" ) ) {
                        return config.getOverrides();
                    }

                    if ( method.getName().equals( "getFigInterface" ) ) {
                        return config.getFigInterface();
                    }

                    if ( method.getName().equals( "equals" ) ) {
                        return config.equals( objects[0] );
                    }

                    if ( method.getName().equals( "toString" ) ) {
                        return config.toString();
                    }

                    if ( method.getName().equals( "hashCode" ) ) {
                        return config.hashCode();
                    }

                    return config.getClass().getMethod( method.getName() ).invoke( o, objects );
                }

                // OK this stuff is redirected to the dynamic properties
                LOG.debug( "Invoking method {} to get property with key {}", method.getName(), option.key() );

                if ( option.isOverridden() ) {
                    return option.getOverrideValue();
                }
                else {
                    return option.value();
                }
            } }
        };

        //noinspection unchecked
        enhancer.setCallbacks( callbacks );
        return ( BaseGuicyFig ) enhancer.create();
    }


    /**
     * Loads a defaults properties file with properties associated with the
     * configuration interface methods. The expected properties file uses
     * the same name as the configuration interface with the .properties
     * extension.
     *
     * @param configInterface the configuration interface
     * @return the properties loaded from the properties file
     */
    static Properties loadProperties( Class<? extends GuicyFig> configInterface ) {
        Properties properties = new Properties();
        String packageName = configInterface.getPackage().getName();
        String name = packageName.replace( '.', '/' ) + "/" + configInterface.getSimpleName() + ".properties";
        InputStream in = configInterface.getClassLoader().getResourceAsStream( name );

        try {
            properties.load( in );
        }
        catch ( NullPointerException e ) {
            LOG.warn( "No property defaults file {} found for {}.", name, configInterface.getSimpleName() );
        }
        catch ( IOException e ) {
            LOG.warn( "No property defaults file {} found for {}.", name, configInterface.getSimpleName() );
        }

        return properties;
    }


    static BaseGuicyFig buildBaseObject( Class<? extends GuicyFig> configInterface ) {
        Properties defaults = loadProperties( configInterface );
        BaseGuicyFig config = new BaseGuicyFig();
        config.setFigInterface( configInterface );

        Method[] methods = configInterface.getDeclaredMethods();
        for ( Method method : methods ) {

            // Key annotation overrides standard key convention
            String key;
            if ( method.getAnnotation( Key.class ) != null ) {
                key = method.getAnnotation( Key.class ).value();
            }
            else {
                StringBuilder sb = new StringBuilder();
                sb.append( configInterface.getCanonicalName() )
                        .append( '.' )
                        .append( method.getName() );
                key = sb.toString();
            }

            // Default annotation overrides defaults properties file
            String defval = defaults.getProperty( key );
            if ( method.getAnnotation( Default.class ) != null ) {
                defval = method.getAnnotation( Default.class ).value();
            }

            config.add( key, defval, method );
        }

        return config;
    }
}
