package org.safehaus.guicyfig.overrides;


import org.jukito.JukitoModule;
import org.jukito.JukitoRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.safehaus.guicyfig.AbstractTest;
import org.safehaus.guicyfig.GuicyFigModule;
import org.safehaus.guicyfig.Option;
import org.safehaus.guicyfig.Overrides;

import com.google.inject.Inject;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;


/**
 * Tests that singleton are adhered to.
 */
@RunWith( JukitoRunner.class )
public class OverridesTest extends AbstractTest {

    @Inject
    @Overrides( name = "junit-tests", options = { @Option( method = "getAbc", override = "234" ) } )
    public OverridesSingletonFig withOverrides;

    @Inject
    public OverridesSingletonFig secondFig;

    @Inject
    public OverridesFig overridesFig;


    @Test
    public void testAll() {
        assertNotNull( withOverrides );
        assertNotNull( secondFig );
        assertNotNull( withOverrides.getOverrides() );
        assertNotNull( secondFig.getOverrides() );
        assertEquals( 234, withOverrides.getAbc() );
        assertEquals( 234, secondFig.getAbc() );
        assertTrue( secondFig.isSingleton() );
        assertTrue( withOverrides.isSingleton() );
        assertTrue( secondFig == withOverrides );
        assertEquals( secondFig, withOverrides );
        assertFalse( overridesFig.isSingleton() );
    }


    @SuppressWarnings( "UnusedDeclaration" )
    public static class SingletonModule extends JukitoModule {
        @Override
        protected void configureTest() {
            //noinspection unchecked
            install( new GuicyFigModule( OverridesSingletonFig.class, OverridesFig.class ) );
        }
    }
}
