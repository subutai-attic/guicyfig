package org.safehaus.guicyfig;


import org.junit.Test;

import static junit.framework.TestCase.assertEquals;


/**
 * Tests BypassImpl.
 */
public class BypassImplTest {

    @Test
    public void testAll() {
        BypassImpl a = new BypassImpl();
        a.addOption( new OptionImpl( "foo", "bar" ) );
        a.addOption( new OptionImpl( "big", "bad" ) );
        BypassImpl b = new BypassImpl( a );
        assertEquals( 0, b.environments().length );
        assertEquals( Bypass.class, b.annotationType() );
    }
}
