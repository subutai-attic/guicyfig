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
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.configuration.AbstractConfiguration;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.netflix.config.ConcurrentMapConfiguration;
import com.netflix.config.ConfigurationManager;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertNotEquals;


/**
 * Tests the GuicyFigModule.
 */
@RunWith( JukitoRunner.class )
public class ServiceFigTest {
    private static final Logger LOG = LoggerFactory.getLogger( ServiceFigTest.class );
    private static final String HOST = "bullshlaka";

    @Inject
    @Overrides(
        name = "ServiceFigTest",
        options = {
            @Option( method = "getHost", override = HOST )
        }
    )
    ServiceFig withOverrides;

    @Inject
    ServiceFig noOverrides;


    @BeforeClass
    public static void setupClass() throws IOException {
        System.setProperty( "archaius.fixedDelayPollingScheduler.initialDelayMills", "500" );
        System.setProperty( "archaius.dynamicPropertyFactory.registerConfigWithJMX", "true" );
        System.setProperty( "archaius.fixedDelayPollingScheduler.delayMills", "500" );

        // ConfigurationManager.loadCascadedPropertiesFromResources( "guicyfig" );
        if ( ConfigurationManager.isConfigurationInstalled() ) {
            LOG.debug( "Configuration seems to already been installed, not making changes." );
        }
        else {
            ConfigurationManager.getDeploymentContext().setDeploymentEnvironment( "UNIT" );
            ConfigurationManager.loadCascadedPropertiesFromResources( "guicyfig" );
        }
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
        // singleton with no overrides defined on its injection point (member) - this
        // is because the configuration is global, and overrides are applied in a
        // layered hierarchy. Overrides layer properties globally, while bypass
        // instructions completely bypass it locally on the injected object.
        assertEquals( HOST, noOverrides.getHost() );
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
        ConfigurationManager.install( config );
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
    }


    @Test
    public void testMeHard() {
        LOG.debug( "Check for valid withOverrides object." );
        assertNotNull( withOverrides );

        // this will use the value from the ServiceFig.properties file but will be overridden: no annotations
        assertEquals( HOST, withOverrides.getHost() );

        // this will use the value from the ServiceFig.properties file: no annotations
        assertEquals( 8345, withOverrides.getPort() );

        // this will use the value from the interface annotation overriding
        // the default value of 5 in the ServiceFig.properties file
        assertEquals( 7, withOverrides.getMaxConnections() );

        // this was annotated with a non-conventional key (thread.wait.time) and
        // no Default annotation
        assertEquals( 500, withOverrides.getThreadWaitTime() );

        // ALL these have interface annotations and no defaults settings in the
        // ServiceFig.properties file
        assertEquals( 0.9f, withOverrides.getLoadThreshold() );
        assertEquals( 450.38918d, withOverrides.getLoadAverage() );
        assertEquals( true, withOverrides.isThrottlingEnabled() );
        assertEquals( true, withOverrides.isResetNeeded() );
        assertEquals( 500, withOverrides.getStartupTimeout() );
        assertEquals( 500L, withOverrides.getValueByMethod( "getStartupTimeout" ) );
        assertNull( withOverrides.getValueByMethod( "bogus" ) );

        assertEquals( 3.14f, withOverrides.getPi() );
        assertEquals( 6.0221413e+23, withOverrides.getAvagadrosNumber() );
        assertEquals( 25, withOverrides.getExecutionCount() );
        assertEquals( false, withOverrides.isDebugEnabled() );

        /*
         * Let's check and see if the super interface methods are working.
         */

        ConfigOption[] options = withOverrides.getOptions();
        for ( ConfigOption option : options ) {
            assertEquals( option, withOverrides.getOption( option.key() ) );
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
    }


    @Test
    public void useSingleClassArg() {
        AnotherFig anotherFig = Guice.createInjector(
                new GuicyFigModule( AnotherFig.class ) ).getInstance( AnotherFig.class );
        assertNotNull( anotherFig );
        assertNotNull( anotherFig.getFoobar() );
        assertEquals( 10, anotherFig.getFoobar() );
    }


    @SuppressWarnings( "UnusedDeclaration" )
    public static class ServiceModule extends JukitoModule {
        @Override
        protected void configureTest() {
            //noinspection unchecked
            install( new GuicyFigModule( ServiceFig.class, AnotherFig.class ) );
        }
    }
}
