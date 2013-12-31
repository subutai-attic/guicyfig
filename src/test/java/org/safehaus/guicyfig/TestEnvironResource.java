package org.safehaus.guicyfig;


import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.netflix.config.ConfigurationManager;

import static junit.framework.TestCase.assertEquals;


/**
 * Tests the proper functioning of the environment resource.
 */
public class TestEnvironResource {
    private static final Logger LOG = LoggerFactory.getLogger( TestEnvironResource.class );

    private static int addToMe = 0;
    private static int subtractFromMe = 0;

    static {
        ConfigurationManager.getDeploymentContext().setDeploymentEnvironment( Env.UNIT.toString() );
    }


    @Rule
    public Resource resA = new Resource( Env.ALL );

    @Rule
    public Resource resB = new Resource( Env.PROD );

    @Rule
    public Resource resC = new Resource( Env.UNIT );



    @Test
    public void testEnvironResource() {
        assertEquals( 2, addToMe );
        assertEquals( 0, subtractFromMe );
    }


    class Resource extends EnvironResource {
        Resource( Env env ) {
            super( env );
        }


        public void before() {
            addToMe++;
        }


        public void after() {
            subtractFromMe--;
        }
    }
}
