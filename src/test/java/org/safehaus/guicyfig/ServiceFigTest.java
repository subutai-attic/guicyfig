package org.safehaus.guicyfig;


import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.jukito.JukitoModule;
import org.jukito.JukitoRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.configuration.AbstractConfiguration;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.netflix.config.ConcurrentCompositeConfiguration;
import com.netflix.config.ConcurrentMapConfiguration;
import com.netflix.config.ConfigurationManager;
import com.netflix.config.DynamicBooleanProperty;
import com.netflix.config.DynamicDoubleProperty;
import com.netflix.config.DynamicFloatProperty;
import com.netflix.config.DynamicIntProperty;
import com.netflix.config.DynamicLongProperty;
import com.netflix.config.DynamicPropertyFactory;
import com.netflix.config.DynamicStringProperty;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertNull;
import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertNotEquals;


/**
 * Tests the GuicyFigModule.
 */
@RunWith( JukitoRunner.class )
public class ServiceFigTest extends AbstractTest {
    private static final Logger LOG = LoggerFactory.getLogger( ServiceFigTest.class );
    private static final String HOSTNAME = "bullshlaka";


    @Inject
    @Overrides( name = "for-testing",
        options = { @Option( method = "getHost", override = HOSTNAME ) }
    )
    ServiceFig withOverrides;

    @Inject
    ServiceFig noOverrides;


    private static final String BASE = "org.safehaus.guicyfig.ServiceFig.";
    private static final String PORT = BASE + "getPort";
    private static final String WAIT_TIME = BASE + "getThreadWaitTime";
    private static final String RESET_NEEDED = BASE + "isResetNeeded";
    private static final String EXECUTION_COUNT = BASE + "getExecutionCount";
    private static final String DEBUG_ENABLED = BASE + "isDebugEnabled";
    private static final String LOAD_THRESHOLD = BASE + "getLoadThreshold";
    private static final String LOAD_AVERAGE = BASE + "getLoadAverage";
    private static final String THROTTLING_ENABLED = BASE + "isThrottlingEnabled";
    private static final String MAX_CONNECTIONS = BASE + "getMaxConnections";
    private static final String STARTUP_TIMEOUT = BASE + "getStartupTimeout";
    private static final String PI = BASE + "getPi";
    private static final String AVAGADROS_NO = BASE + "getAvagadrosNumber";
    private static final String THREAD_WAIT = "thread.wait.time";
    private static final String HOST = BASE + "getHost";


    @Test
    public void testAppValues() throws IOException {
        assertProperty( PORT, "8345" );
    }


    @Test
    public void testUndefined() throws IOException {
        assertUndefined( BASE + "bogus" );
        assertUndefined( WAIT_TIME );
        assertUndefined( RESET_NEEDED );
        assertUndefined( EXECUTION_COUNT );
        assertUndefined( DEBUG_ENABLED );
    }


    @Test
    public void testUnitCascadedValues() throws IOException {
        // these three were changed from 0.9000, 450.38918, true respectively to
        assertProperty( LOAD_THRESHOLD, "2.81" );
        assertProperty( LOAD_AVERAGE, "30.05" );
        assertProperty( THROTTLING_ENABLED, "false" );

        // these have only been defined in the UNIT scope
        assertProperty( MAX_CONNECTIONS, "7" );
        assertProperty( THREAD_WAIT, "500" );
        assertProperty( STARTUP_TIMEOUT, "800" );
        assertProperty( PI, "3.14159f" );
        assertProperty( AVAGADROS_NO, "6.0221413e+23" );
    }


    @Test
    public void testBypass() throws Exception {
        assertEquals( 25, noOverrides.getExecutionCount() );
        noOverrides.bypass( "getExecutionCount", "123" );
        assertEquals( 123, noOverrides.getExecutionCount() );
        noOverrides.bypass( "getExecutionCount", null );
        assertEquals( 25, noOverrides.getExecutionCount() );

        noOverrides.bypass( "getExecutionCount", "456" );
        assertEquals( 456, noOverrides.getExecutionCount() );
        noOverrides.setBypass( null );
        assertEquals( 25, noOverrides.getExecutionCount() );
    }


    @Test
    public void testOverride() throws Exception {
        assertEquals( 25, noOverrides.getExecutionCount() );
        noOverrides.override( "getExecutionCount", "123" );
        assertEquals( 123, noOverrides.getExecutionCount() );
        noOverrides.override( "getExecutionCount", null );
        assertEquals( 25, noOverrides.getExecutionCount() );

        noOverrides.override( "getExecutionCount", "456" );
        assertEquals( 456, noOverrides.getExecutionCount() );
        noOverrides.setOverrides( null );
        assertEquals( 25, noOverrides.getExecutionCount() );
    }


    @Test
    public void testDynamicProperties() throws IOException, InterruptedException {
        DynamicPropertyFactory factory = DynamicPropertyFactory.getInstance();

        DynamicIntProperty port = factory.getIntProperty( PORT, 8080 );
        assertEquals( 8345, port.get() );

        DynamicFloatProperty loadThreshold = factory.getFloatProperty( LOAD_THRESHOLD, 0.1f );
        assertEquals( 2.81f, loadThreshold.get() );

        DynamicFloatProperty loadAverage = factory.getFloatProperty( LOAD_AVERAGE, 0.1f );
        assertEquals( 30.05f, loadAverage.get() );

        DynamicBooleanProperty throttling = factory.getBooleanProperty( THROTTLING_ENABLED, true );
        assertEquals( false, throttling.get() );

        DynamicIntProperty connections = factory.getIntProperty( MAX_CONNECTIONS, 33 );
        assertEquals( 7, connections.get() );

        DynamicLongProperty threadWait = factory.getLongProperty( THREAD_WAIT, 52 );
        assertEquals( 500, threadWait.get() );

        DynamicLongProperty startupTimeout = factory.getLongProperty( STARTUP_TIMEOUT, 1215 );
        assertEquals( 800, startupTimeout.get() );

        DynamicFloatProperty pi = factory.getFloatProperty( PI, 3.14f );
        assertEquals( 3.14159f, pi.get() );

        DynamicDoubleProperty avagadro = factory.getDoubleProperty( AVAGADROS_NO, 0 );
        assertEquals( 6.0221413e+23, avagadro.get() );

        DynamicStringProperty host = factory.getStringProperty( HOST, "localhost" );
        assertEquals( "bullshlaka", host.get() );

        /*
         * Now we inject a new configuration into the hierarchy and see what happens
         */

        ConcurrentMapConfiguration mapConfiguration = new ConcurrentMapConfiguration();
        mapConfiguration.setProperty( LOAD_AVERAGE, "40.32" );
        mapConfiguration.setProperty( HOST, "maxwell" );
        mapConfiguration.setProperty( DEBUG_ENABLED, true );
        getConfiguration().addConfigurationAtFront( mapConfiguration, "test" );

        assertProperty( DEBUG_ENABLED, true );
        assertEquals( 8345, port.get() );
        assertEquals( 2.81f, loadThreshold.get() );
        assertEquals( 40.32f, loadAverage.get() );
        assertEquals( false, throttling.get() );
        assertEquals( 7, connections.get() );
        assertEquals( 500, threadWait.get() );
        assertEquals( 800, startupTimeout.get() );
        assertEquals( 3.14159f, pi.get() );
        assertEquals( 6.0221413e+23, avagadro.get() );
        assertEquals( "bullshlaka", host.get() );

        mapConfiguration.clear();
        getConfiguration().removeConfiguration( "test" );
        getConfiguration().removeConfiguration( mapConfiguration );

        assertUndefined( DEBUG_ENABLED );
        assertEquals( 8345, port.get() );
        assertEquals( 2.81f, loadThreshold.get() );
        assertEquals( 30.05f, loadAverage.get() );
        assertEquals( false, throttling.get() );
        assertEquals( 7, connections.get() );
        assertEquals( 500, threadWait.get() );
        assertEquals( 800, startupTimeout.get() );
        assertEquals( 3.14159f, pi.get() );
        assertEquals( 6.0221413e+23, avagadro.get() );
        assertEquals( HOSTNAME, host.get() );
    }


    @Test
    public void testObjectMethods() {
        assertNotNull( noOverrides.toString() );
        assertNotNull( noOverrides.hashCode() );
        assertFalse( noOverrides.equals( withOverrides ) );
    }


    @Test
    public void testWithoutOverride() {
        assertNotEquals( noOverrides, withOverrides );
        assertNull( noOverrides.getOverrides() );
        assertNotNull( withOverrides.getOverrides() );
        assertNotNull( noOverrides );

        // Notice that the overrides are being applied even though this fig is not a
        // singleton and has no overrides defined on its injection point (member) - this
        // is because the configuration is global, and overrides are applied in a
        // layered hierarchy. Overrides layer properties globally, while bypass
        // instructions completely bypass it locally on the injected object.
        assertEquals( HOSTNAME, noOverrides.getHost() );
    }


    @Test
    public void testNotifications() throws InterruptedException {
        final List<PropertyChangeEvent> events = new ArrayList<PropertyChangeEvent>();
        PropertyChangeListener listener = new PropertyChangeListener() {
            @Override
            public void propertyChange( final PropertyChangeEvent evt ) {
                events.add( evt );
            }
        };
        withOverrides.addPropertyChangeListener( listener );

        AbstractConfiguration config = new ConcurrentMapConfiguration();
        config.addProperty( withOverrides.getKeyByMethod( "getExecutionCount" ), "12" );
        config.addProperty( withOverrides.getKeyByMethod( "getAbc" ), "true" );

        ConcurrentCompositeConfiguration cmc = ( ConcurrentCompositeConfiguration )
                ConfigurationManager.getConfigInstance();
        cmc.addConfigurationAtFront( config, "testNotifications" );

        Thread.sleep( 500 );
        assertFalse( events.isEmpty() );
        assertEquals( 2, events.size() );

        for ( PropertyChangeEvent event : events ) {
            if ( event.getPropertyName().equals( withOverrides.getKeyByMethod( "getExecutionCount" ) ) ) {
                assertEquals( 25, event.getOldValue() );
                assertEquals( 12, event.getNewValue() );
            }
            else if ( event.getPropertyName().equals( withOverrides.getKeyByMethod( "getAbc" ) ) ) {
                assertEquals( false, event.getOldValue() );
                assertEquals( true, event.getNewValue() );
            }
            else {
                fail( "we should not have an event with property name set to " + event.getPropertyName() );
            }
        }

        // set values back to originals so other tests don't fail
        config.setProperty( withOverrides.getKeyByMethod( "getExecutionCount" ), "25" );
        withOverrides.removePropertyChangeListener( listener );
        config.clearProperty( withOverrides.getKeyByMethod( "getAbc" ) );

        assertEquals( 3, events.size() );
        cmc.removeConfiguration( "testNotifications" );
    }


    @Test
    public void testMeHard() throws Exception {
        testDynamicProperties();

        assertNotNull( withOverrides );

        // this will use the value from the ServiceFig.properties file but will be overridden: no annotations
        assertEquals( HOSTNAME, withOverrides.getHost() );

        // this will use the value from the ServiceFig.properties file: no annotations
        assertEquals( 8345, withOverrides.getPort() );

        // this will use the value from the interface annotation overriding
        // the default value of 5 in the ServiceFig.properties file
        assertEquals( 7, withOverrides.getMaxConnections() );

        // this was annotated with a non-conventional key (thread.wait.time) and
        // no Default annotation, the ServiceFig.properties contains the default
        assertEquals( 500, withOverrides.getThreadWaitTime() );

        // ALL these have interface annotations and no defaults settings in the
        // ServiceFig.properties file
        assertEquals( 2.81f, withOverrides.getLoadThreshold() );
        assertEquals( 30.05, withOverrides.getLoadAverage() );
        assertEquals( false, withOverrides.isThrottlingEnabled() );
        assertEquals( true, withOverrides.isResetNeeded() );
        assertEquals( 800L, withOverrides.getStartupTimeout() );
        assertEquals( 800L, withOverrides.getValueByMethod( "getStartupTimeout" ) );
        assertNull( withOverrides.getValueByMethod( "bogus" ) );

        assertEquals( 3.14159f, withOverrides.getPi() );
        assertEquals( 6.0221413e+23, withOverrides.getAvagadrosNumber() );
        assertEquals( 25, withOverrides.getExecutionCount() );
        assertEquals( false, withOverrides.isDebugEnabled() );

        /*
         * Let's check and see if the super interface methods are working.
         */

        OptionState[] options = withOverrides.getOptions();
        for ( OptionState option : options ) {
            assertEquals( option, withOverrides.getOption( option.getKey() ) );
        }

        Properties starting = new Properties();
        starting.setProperty( "foo.bar.foe", "I will be filtered out" );
        starting.setProperty( "org.safehaus.guicyfig.ServiceFig.getPort", "9999" );
        Properties filtered = withOverrides.filterOptions( starting );
        assertFalse( filtered.containsKey( "foo.bar.foe" ) );
        assertTrue( filtered.containsKey( "org.safehaus.guicyfig.ServiceFig.getPort" ) );


        Map<String,Object> startingMap = new HashMap<String, Object>();
        startingMap.put( "foo.bar", "I will be filtered out" );
        startingMap.put( "org.safehaus.guicyfig.ServiceFig.getMaxConnections", "7" );
        Map<String,Object> filteredMap = withOverrides.filterOptions( startingMap );
        assertFalse( filteredMap.containsKey( "foo.bar" ) );
        assertTrue( filteredMap.containsKey( "org.safehaus.guicyfig.ServiceFig.getMaxConnections" ) );
        assertEquals( filteredMap.get( "org.safehaus.guicyfig.ServiceFig.getMaxConnections" ), "7" );


        PropertyChangeListener listener = new PropertyChangeListener() {
            @Override
            public void propertyChange( final PropertyChangeEvent evt ) {
            }
        };

        withOverrides.addPropertyChangeListener( listener );
        withOverrides.removePropertyChangeListener( listener );

        withOverrides.override( "getStartupTimeout", "100" );
        assertEquals( 100L, withOverrides.getStartupTimeout() );
    }


    @Test
    public void useSingleClassArg() {
        FooFig fooFig = Guice.createInjector(
                new GuicyFigModule( FooFig.class ) ).getInstance( FooFig.class );
        assertNotNull( fooFig );
        assertNotNull( fooFig.getFoobar() );
        assertEquals( 0, fooFig.getFoobar() );
    }


    @Test
    public void enumTest() throws InterruptedException {
        ServiceFig serviceFig = Guice.createInjector( new GuicyFigModule( ServiceFig.class ) ).getInstance( ServiceFig.class );

        assertEquals(ConfigEnum.THREE, serviceFig.getEnum());
        //test we can update the config

        final List<PropertyChangeEvent> events = new ArrayList<PropertyChangeEvent>();
        PropertyChangeListener listener = new PropertyChangeListener() {
            @Override
            public void propertyChange( final PropertyChangeEvent evt ) {
                events.add( evt );
            }
        };
        serviceFig.addPropertyChangeListener( listener );



        serviceFig.override( serviceFig.getKeyByMethod( "getEnum" ), ConfigEnum.TWO.toString() );

        Thread.sleep( 500 );
        assertFalse( events.isEmpty() );
        assertEquals( 1, events.size() );

        PropertyChangeEvent event = events.get( 0 );


        assertEquals(serviceFig.getKeyByMethod( "getEnum" ), event.getPropertyName());

        assertEquals(ConfigEnum.THREE,event.getOldValue() );
        assertEquals(ConfigEnum.TWO, event.getNewValue());


    }


    @SuppressWarnings( "UnusedDeclaration" )
    public static class ServiceModule extends JukitoModule {
        @Override
        protected void configureTest() {
            //noinspection unchecked
            install( new GuicyFigModule( ServiceFig.class, FooFig.class ) );
        }
    }
}
