package org.safehaus.guicyfig;


import org.junit.Test;

import com.netflix.config.ConfigurationManager;
import com.netflix.config.DeploymentContext;

import static junit.framework.TestCase.assertEquals;
import static org.safehaus.guicyfig.Env.getEnvironment;


/**
 * Tests Env.
 */
public class EnvTest {
    @Test
    public void testGetEnvironment() {
        assertEquals( Env.UNIT, getEnvironment( Env.UNIT.name() ) );
        assertEquals( Env.ALL, getEnvironment( Env.ALL.name() ) );
        assertEquals( Env.ALL, getEnvironment( "foo" ) );
        assertEquals( Env.ACCEPT, getEnvironment( Env.ACCEPT.name() ) );
        assertEquals( Env.TEST, getEnvironment( Env.TEST.name() ) );
        assertEquals( Env.CHOP, getEnvironment( Env.CHOP.name() ) );
        assertEquals( Env.DEV, getEnvironment( Env.DEV.name() ) );
        assertEquals( Env.INTEG, getEnvironment( Env.INTEG.name() ) );
        assertEquals( Env.PROD, getEnvironment( Env.PROD.name() ) );
    }


    @Test
    public void testNullDeploymentContext() {
        DeploymentContext context = ConfigurationManager.getDeploymentContext();
        if ( context != null ) {
            return;
        }
        assertEquals( Env.ALL, getEnvironment() );
        ConfigurationManager.setDeploymentContext( context );
    }
}
