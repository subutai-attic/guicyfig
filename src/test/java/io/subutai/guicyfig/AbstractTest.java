package io.subutai.guicyfig;


import java.io.IOException;

import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.netflix.config.ConcurrentCompositeConfiguration;
import com.netflix.config.ConfigurationManager;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;


/**
 * Abstract test to make sure Archaius setup is consistent across the board.
 */
public abstract class AbstractTest {
    private final static Logger LOG = LoggerFactory.getLogger( AbstractTest.class );
    private final static String ARCHAIUS_ENVIRON = "archaius.deployment.environment";
    private final static String ARCHAIUS_INITDELAY = "archaius.fixedDelayPollingScheduler.initialDelayMills";
    private final static String ARCHAIUS_POLLDELAY = "archaius.fixedDelayPollingScheduler.delayMills";
    private final static ConcurrentCompositeConfiguration CCC = new ConcurrentCompositeConfiguration();

    static {
        synchronized ( CCC ) {
            if ( ! ConfigurationManager.isConfigurationInstalled() ) {
                assertFalse( ConfigurationManager.isConfigurationInstalled() );
                assertEquals( "Unset Env should default to ALL", Env.ALL, Env.getEnvironment() );
                LOG.info( "Environment defaulting to {}", Env.getEnvironment().toString() );

                setArchaiusProperties();

                LOG.info( "Environment setup for {}", Env.getEnvironment().toString() );
                assertEquals( "After setting Env it should be equal to 'UNIT'", Env.UNIT, Env.getEnvironment() );

                ConfigurationManager.install( CCC );
                assertTrue( ConfigurationManager.isConfigurationInstalled() );
            }
        }

        try {
            ConfigurationManager.loadCascadedPropertiesFromResources( "guicyfig" );
        }
        catch ( Exception e ) {
            LOG.error( "Failed to load", e );
        }
    }


    public static ConcurrentCompositeConfiguration getConfiguration() {
        return ( ConcurrentCompositeConfiguration ) ConfigurationManager.getConfigInstance();
    }


    public static void setArchaiusProperties() {
        if ( System.getProperty( ARCHAIUS_ENVIRON ) == null ) {
            LOG.info( "{} is not defined setting it to {}", ARCHAIUS_ENVIRON, Env.UNIT.toString() );
            System.setProperty( ARCHAIUS_ENVIRON, Env.UNIT.toString() );
        }

        if ( System.getProperty( ARCHAIUS_POLLDELAY ) == null ) {
            LOG.info( "{} is not defined setting it to {}", ARCHAIUS_POLLDELAY, 500 );
            System.setProperty( ARCHAIUS_POLLDELAY, "500" );
        }

        if ( System.getProperty( ARCHAIUS_INITDELAY ) == null ) {
            LOG.info( "{} is not defined stetting it to {}", ARCHAIUS_INITDELAY, 500 );
            System.setProperty( ARCHAIUS_INITDELAY, "500" );
        }
    }


    public static void assertUndefined( String key ) {
        Preconditions.checkState( ConfigurationManager.isConfigurationInstalled() );
        assertFalse( CCC.containsKey( key ) );
    }


    public static void assertProperty( String key, Object expected ) {
        Preconditions.checkState( ConfigurationManager.isConfigurationInstalled() );
        assertTrue( CCC.containsKey( key ) );
        Object actual = CCC.getProperty( key );
        assertNotNull( CCC.getProperty( key ) );
        assertEquals( expected, actual );
    }


    @BeforeClass
    public static void setup() throws IOException {
        System.setProperty( ARCHAIUS_ENVIRON, "UNIT" );
        ConfigurationManager.getDeploymentContext().setDeploymentEnvironment( "UNIT" );
        ConfigurationManager.loadCascadedPropertiesFromResources( "guicyfig" );
    }
}
