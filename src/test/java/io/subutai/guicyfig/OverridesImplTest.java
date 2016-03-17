package io.subutai.guicyfig;


import org.junit.Test;

import static junit.framework.TestCase.assertEquals;


/**
 * Tests OverridesImpl.
 */
public class OverridesImplTest {
    @Test
    public void testAll() {
        OverridesImpl a = new OverridesImpl( "testName" );

        assertEquals( 0, a.environments().length );
        assertEquals( 0, a.options().length );
        assertEquals( "testName", a.name() );

        a.addOption( new OptionImpl( "fooKey", "fooValue" ) );
        assertEquals( 1, a.options().length );
        assertEquals( new OptionImpl( "fooKey", "fooValue" ), a.options()[0] );

        assertEquals( Overrides.class, a.annotationType() );
    }
}
