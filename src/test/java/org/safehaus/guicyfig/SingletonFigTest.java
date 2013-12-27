package org.safehaus.guicyfig;


import java.lang.reflect.AnnotatedElement;

import org.jukito.JukitoModule;
import org.jukito.JukitoRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

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
    @Overrides( name = "junit-test", options = { @Option( method = "getFoobar", override = "234" ) } )
    SingletonFig withOverrides;

    @Inject
    SingletonFig secondFig;

    @Inject
    AnotherFig anotherFig;


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
            install( new GuicyFigModule( SingletonFig.class ) );
        }
    }
}
