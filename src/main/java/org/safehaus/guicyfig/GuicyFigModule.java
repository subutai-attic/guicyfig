package org.safehaus.guicyfig;


import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
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
 * your GuicyFig extending configuration interfaces. See the TEST cases or the
 * wiki for an example.
 *
 * @since 1.0
 */
public class GuicyFigModule extends AbstractModule {
    private static final Logger LOG = LoggerFactory.getLogger( GuicyFigModule.class );
    private final Class[] classes;

    // Static because singleton instances to be injected regardless of Module instance
    private static final Map<Class<? extends GuicyFig>,BaseGuicyFig> singletons =
            new HashMap<Class<? extends GuicyFig>, BaseGuicyFig>();


    /**
     * Configure Guice injection for a specific GuicyFig type.
     *
     * @param clazz the GuicyFig type
     */
    public GuicyFigModule( Class<? extends GuicyFig> clazz ) {
        classes = new Class[] { clazz };
    }


    /**
     * Configure Guice injection for a collection of GuicyFig types.
     *
     * @param classCollection a collection of GuicyFig types
     */
    public GuicyFigModule( Collection<Class<? extends GuicyFig>> classCollection ) {
        classes = classCollection.toArray( new Class[ classCollection.size()] );
    }


    /**
     * Configure Guice injection for a var arg array of GuicyFig types.
     *
     * @param classes array of GuicyFig types
     */
    public GuicyFigModule( Class<? extends GuicyFig>... classes ) {
        this.classes = classes;
    }


    protected void configure() {
        // add configuration logic here
        LOG.debug( "Configuring ..."  );

        for ( final Class clazz : classes ) {

            //noinspection unchecked
            bind( clazz ).toProvider( new Provider() {
                @SuppressWarnings( "unchecked" )
                @Override
                public Object get() {
                    if ( clazz.isAnnotationPresent( FigSingleton.class ) ) {
                        BaseGuicyFig config;

                        if ( !singletons.containsKey( clazz ) ) {
                            config = getConcreteObject( true, clazz );
                            singletons.put( clazz, config );
                        }
                        else {
                            config = singletons.get( clazz );
                        }

                        return config;
                    }
                    else {
                        //noinspection unchecked
                        return getConcreteObject( false, clazz );
                    }
                }
            } );

            binder().bindListener( Matchers.any(), new TypeListener() {
                @SuppressWarnings( "unchecked" )
                @Override
                public <I> void hear( final TypeLiteral<I> type, final TypeEncounter<I> encounter ) {
                    for ( final Field field : type.getRawType().getDeclaredFields() ) {

                        if ( field.getType() == clazz &&
                                clazz.isAnnotationPresent( FigSingleton.class ) &&
                                field.isAnnotationPresent( Overrides.class ) ) {

                            if ( ! singletons.containsKey( clazz ) ) {
                                singletons.put( clazz, getConcreteObject( true, clazz ) );
                            }

                            final BaseGuicyFig newInstance = singletons.get( clazz );
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

                        else if ( field.getType() == clazz && field.isAnnotationPresent( Overrides.class ) ) {
                            final BaseGuicyFig newInstance = getConcreteObject( false, clazz );
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

                        else if ( field.getType() == clazz &&
                                clazz.isAnnotationPresent( FigSingleton.class ) &&
                                field.isAnnotationPresent( Bypass.class ) ) {

                            if ( ! singletons.containsKey( clazz ) ) {
                                singletons.put( clazz, getConcreteObject( true, clazz ) );
                            }

                            final BaseGuicyFig newInstance = singletons.get( clazz );
                            newInstance.setBypass( field.getAnnotation( Bypass.class ) );
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

                        else if ( field.getType() == clazz && field.isAnnotationPresent( Bypass.class ) ) {
                            final BaseGuicyFig newInstance = getConcreteObject( false, clazz );
                            newInstance.setBypass( field.getAnnotation( Bypass.class ) );
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


    static BaseGuicyFig getConcreteObject( boolean singleton, final Class<? extends GuicyFig> configInterface ) {
        final BaseGuicyFig config = buildBaseObject( configInterface );
        config.setSingleton( singleton );

        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass( BaseGuicyFig.class );
        enhancer.setInterfaces( new Class[] { configInterface } );
        Callback[] callbacks = new Callback[] { new MethodInterceptor() {
            @Override
            public Object intercept( final Object o, final Method method, final Object[] objects,
                                     final MethodProxy methodProxy ) throws Throwable {
                InternalOptionState option = ( InternalOptionState ) config.getOption( method );

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
                        return config.setOverrides( ( Overrides ) objects[0] );
                    }

                    if ( method.getName().equals( "override" ) ) {
                        config.override( ( String ) objects[0], ( String ) objects[1] );
                        return null;
                    }

                    if ( method.getName().equals( "bypass" ) ) {
                        config.bypass( ( String ) objects[0], ( String ) objects[1] );
                        return null;
                    }

                    if ( method.getName().equals( "getOverrides" ) ) {
                        return config.getOverrides();
                    }

                    if ( method.getName().equals( "setBypass" ) ) {
                        return config.setBypass( ( Bypass ) objects[0] );
                    }

                    if ( method.getName().equals( "getBypass" ) ) {
                        return config.getBypass();
                    }

                    if ( method.getName().equals( "getFigInterface" ) ) {
                        return config.getFigInterface();
                    }

                    if ( method.getName().equals( "isSingleton" ) ) {
                        return config.isSingleton();
                    }

                    if ( method.getName().equals( "equals" ) ) {
                        return o == objects[0];
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
                LOG.debug( "Invoking method {} to get property with key {}", method.getName(), option.getKey() );

                if ( option.isBypassed() ) {
                    return option.getBypassValue();
                }
                else if ( option.isOverridden() ) {
                    return option.getOverrideValue();
                }
                else {
                    return option.getValue();
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


    /**
     * Scans the package that the object is contained in looking for GuicyFig extending
     * interfaces and wires up a GuicyFigModule to create them while injecting the members
     * of obj.
     *
     * @param obj the object whose members are to be injected
     */
    public static void injectMembers( Object obj ) {
        Reflections reflections = new Reflections( obj.getClass().getPackage().getName() );
        Set<Class<? extends GuicyFig>> subTypes = reflections.getSubTypesOf( GuicyFig.class );
        Injector injector = Guice.createInjector( new GuicyFigModule( subTypes ) );
        injector.injectMembers( obj );
    }
}
