package org.safehaus.guicyfig.overrides;


import org.jukito.JukitoModule;
import org.jukito.JukitoRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.safehaus.guicyfig.GuicyFigModule;
import org.safehaus.guicyfig.Option;
import org.safehaus.guicyfig.Overrides;
import org.safehaus.guicyfig.bypass.SingletonFig;

import com.google.inject.Inject;

import static junit.framework.Assert.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;


/**
 * Tests that singleton are adhered to.
 */
@RunWith( JukitoRunner.class )
public class SingletonFigTest {

    @Inject
    @Overrides( name = "junit-TEST", options = { @Option( method = "getFoobar", override = "234" ) } )
    public SingletonFig withOverrides;

    @Inject
    public SingletonFig secondFig;

    @Inject
    public AnotherFig anotherFig;


    @Test
    public void testAll() {
        assertNotNull( withOverrides );
        assertNotNull( secondFig );
        assertNotNull( withOverrides.getOverrides() );
        assertNotNull( secondFig.getOverrides() );
        assertEquals( 234, secondFig.getFoobar() );
        assertTrue( secondFig.isSingleton() );
        assertTrue( withOverrides.isSingleton() );
        assertTrue( secondFig == withOverrides );
        assertEquals( secondFig, withOverrides );
        assertFalse( anotherFig.isSingleton() );
    }


    @SuppressWarnings( "UnusedDeclaration" )
    public static class SingletonModule extends JukitoModule {
        @Override
        protected void configureTest() {
            //noinspection unchecked
            install( new GuicyFigModule( SingletonFig.class, AnotherFig.class ) );
        }
    }
}
