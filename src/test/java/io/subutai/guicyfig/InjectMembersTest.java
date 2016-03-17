package io.subutai.guicyfig;


import org.junit.Test;

import com.google.inject.Inject;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;


/**
 * Tests the injectMembers functionality.
 */
public class InjectMembersTest {

    @Inject
    FooFig fooFig;

    @Test
    public void testInjectMembers() {
        GuicyFigModule.injectMembers( this );
        assertNotNull( fooFig );
        assertEquals( 0, fooFig.getFoobar() );
    }
}
