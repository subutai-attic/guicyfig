package io.subutai.guicyfig;


import junit.framework.TestCase;
import org.junit.Test;

import com.netflix.config.ConfigurationManager;
import com.netflix.config.DeploymentContext;

import static junit.framework.TestCase.assertEquals;
import static io.subutai.guicyfig.Env.getEnvironment;


/**
 * Tests Env.
 */
public class EnvTest {
    @Test
    public void testGetEnvironment() {
        TestCase.assertEquals( Env.UNIT, Env.getEnvironment( Env.UNIT.name() ) );
        TestCase.assertEquals( Env.ALL, Env.getEnvironment( Env.ALL.name() ) );
        TestCase.assertEquals( Env.ALL, Env.getEnvironment( "foo" ) );
        TestCase.assertEquals( Env.ACCEPT, Env.getEnvironment( Env.ACCEPT.name() ) );
        TestCase.assertEquals( Env.TEST, Env.getEnvironment( Env.TEST.name() ) );
        TestCase.assertEquals( Env.CHOP, Env.getEnvironment( Env.CHOP.name() ) );
        TestCase.assertEquals( Env.DEV, Env.getEnvironment( Env.DEV.name() ) );
        TestCase.assertEquals( Env.INTEG, Env.getEnvironment( Env.INTEG.name() ) );
        TestCase.assertEquals( Env.PROD, Env.getEnvironment( Env.PROD.name() ) );
    }


    @Test
    public void testNullDeploymentContext() {
        DeploymentContext context = ConfigurationManager.getDeploymentContext();
        if ( context != null ) {
            return;
        }
        TestCase.assertEquals( Env.ALL, Env.getEnvironment() );
        ConfigurationManager.setDeploymentContext( context );
    }
}
