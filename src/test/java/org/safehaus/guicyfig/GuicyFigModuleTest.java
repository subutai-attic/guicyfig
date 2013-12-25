package org.safehaus.guicyfig;


import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.jukito.JukitoModule;
import org.jukito.JukitoRunner;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertNotNull;


/**
 * Tests the GuicyFigModule.
 */
@RunWith( JukitoRunner.class )
public class GuicyFigModuleTest {
    private static final Logger LOG = LoggerFactory.getLogger( GuicyFigModuleTest.class );


    @Inject
    @Overrides(
        name = "GuicyFigModuleTest",
        options = {
            @Option( method = "getHost", override = "bullshakala" )
        }
    )
    ServiceConfig serviceConfig;


    @BeforeClass
    public static void setupClass() {
        System.setProperty( "guicyfig.fixedDelayPollingScheduler.initialDelayMills", "500" );
        System.setProperty( "guicyfig.dynamicPropertyFactory.registerConfigWithJMX", "true" );
        System.setProperty( "guicyfig.fixedDelayPollingScheduler.delayMills", "500" );
    }

    @Test
    public void testMeHard() {
        LOG.debug( "Check for valid serviceConfig object." );
        assertNotNull( serviceConfig );

        // this will use the value from the ServiceConfig.properties file but will be overridden: no annotations
        assertEquals( "bullshakala", serviceConfig.getHost() );

        // this will use the value from the ServiceConfig.properties file: no annotations
        assertEquals( 8345, serviceConfig.getPort() );

        // this will use the value from the interface annotation overriding
        // the default value of 5 in the ServiceConfig.properties file
        assertEquals( 7, serviceConfig.getMaxConnections() );

        // this was annotated with a non-conventional key (thread.wait.time) and
        // no Default annotation
        assertEquals( 500, serviceConfig.getThreadWaitTime() );

        // all these have interface annotations and no defaults settings in the
        // ServiceConfig.properties file
        assertEquals( 0.9f, serviceConfig.getLoadThreshold() );
        assertEquals( 450.38918d, serviceConfig.getLoadAverage() );
        assertEquals( true, serviceConfig.isThrottlingEnabled() );
        assertEquals( true, serviceConfig.isResetNeeded() );
        assertEquals( 500, serviceConfig.getStartupTimeout() );
        assertEquals( 3.14f, serviceConfig.getPi() );
        assertEquals( 6.0221413e+23, serviceConfig.getAvagadrosNumber() );
        assertEquals( 25, serviceConfig.getExecutionCount() );
        assertEquals( false, serviceConfig.isDebugEnabled() );

        /*
         * Let's check and see if the super interface methods are working.
         */

        ConfigOption[] options = serviceConfig.getOptions();
        for ( ConfigOption option : options ) {
            assertEquals( option, serviceConfig.getOption( option.key() ) );
        }

        Properties starting = new Properties();
        starting.setProperty( "foo.bar.foe", "I will be filtered out" );
        starting.setProperty( "org.safehaus.guicyfig.ServiceConfig.getPort", "9999" );
        Properties filtered = serviceConfig.filterOptions( starting );
        assertFalse( filtered.containsKey( "foo.bar.foe" ) );
        assertTrue( filtered.containsKey( "org.safehaus.guicyfig.ServiceConfig.getPort" ) );


        Map<String,Object> startingMap = new HashMap<String, Object>();
        startingMap.put( "foo.bar", "I will be filtered out" );
        startingMap.put( "org.safehaus.guicyfig.ServiceConfig.getMaxConnections", "7" );
        Map<String,Object> filteredMap = serviceConfig.filterOptions( startingMap );
        assertFalse( filteredMap.containsKey( "foo.bar" ) );
        assertTrue( filteredMap.containsKey( "org.safehaus.guicyfig.ServiceConfig.getMaxConnections" ) );
        assertEquals( filteredMap.get( "org.safehaus.guicyfig.ServiceConfig.getMaxConnections" ), "7" );


        PropertyChangeListener listener = new PropertyChangeListener() {
            @Override
            public void propertyChange( final PropertyChangeEvent evt ) {
            }
        };

        serviceConfig.addPropertyChangeListener( listener );
        serviceConfig.removePropertyChangeListener( listener );
    }


    @SuppressWarnings( "UnusedDeclaration" )
    public static class ServiceModule extends JukitoModule {
        @Override
        protected void configureTest() {
            install( new GuicyFigModule( ServiceConfig.class ) );
        }
    }
}
