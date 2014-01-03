package org.safehaus.guicyfig;


import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;


/**
 * Tests OptionImpl.
 */
public class OptionImplTest {
    @Test
    public void testEquals() {
        OptionImpl a = new OptionImpl( "foo", "bar" );
        OptionImpl b = new OptionImpl( "foo", "far" );
        OptionImpl c = new OptionImpl( "free", "form" );

        assertTrue( a.equals( b ) );
        assertTrue( a.equals( a ) );
        assertFalse( a.equals( c ) );
        assertFalse( b.equals( c ) );
        assertFalse( b.equals( new Object() ) );

        assertEquals( Option.class, a.annotationType() );
    }
}
