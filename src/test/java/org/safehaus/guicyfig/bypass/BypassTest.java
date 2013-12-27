package org.safehaus.guicyfig.bypass;


import org.jukito.JukitoModule;
import org.jukito.JukitoRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.safehaus.guicyfig.Bypass;
import org.safehaus.guicyfig.GuicyFigModule;
import org.safehaus.guicyfig.Option;

import com.google.inject.Inject;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertNotSame;


/**
 * Bypass behavior tests.
 */
@RunWith( JukitoRunner.class )
public class BypassTest {

    @Inject
    public AnotherFig nonSingleton;

    @Inject
    @Bypass( options = @Option( method = "getFoobar", override = "33" ) )
    public AnotherFig bypassedNonSingleton;

    @Inject
    public SingletonFig singleton;

    @Inject
    @Bypass( options = @Option( method = "getFoobar", override = "55" ) )
    public SingletonFig bypassedSingleton;


    @Test
    public void testNoDefaultsConfig() {
        assertNotNull( nonSingleton );
        assertEquals( 0, nonSingleton.getFoobar() );
        assertNotNull( bypassedNonSingleton );
    }


    @Test
    public void testBypass() {
        // AnotherFig is NOT a FigSingleton so they should be different objects
        assertNotSame( nonSingleton, bypassedNonSingleton );

        // Bypass should work on the one that is annotated
        assertEquals( 33, bypassedNonSingleton.getFoobar() );

        // Bypass should not work on the one that is NOT annotated
        assertEquals( 0, nonSingleton.getFoobar() );

        // SingletonFig is a FigSingleton so they should be the same objects
        assertEquals( singleton, bypassedSingleton );

        // Bypass should work on both since it is a singleton and the same object
        assertEquals( 55, singleton.getFoobar() );
        assertEquals( 55, bypassedSingleton.getFoobar() );
    }


    @SuppressWarnings( "UnusedDeclaration" )
    public static class BypassModule extends JukitoModule {
        @Override
        protected void configureTest() {
            //noinspection unchecked
            install( new GuicyFigModule( AnotherFig.class, SingletonFig.class ) );
        }
    }
}
