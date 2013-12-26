package org.safehaus.guicyfig;


import java.lang.reflect.Method;

import org.junit.Test;

import static junit.framework.TestCase.assertNull;


/**
 * Tests BaseGuicyFig.
 */
public class BaseGuicyFigTest {

    @Test
    public void badMethodNames() {
        BaseGuicyFig baseGuicyFig = new BaseGuicyFig();
        assertNull( baseGuicyFig.getKeyByMethod( "foo" ) );
        assertNull( baseGuicyFig.getValueByMethod( "bar" ) );
    }


    @Test ( expected = NullPointerException.class )
    public void nullOverrides() {
        BaseGuicyFig baseGuicyFig = new BaseGuicyFig();
        baseGuicyFig.setOverrides( null );
        assertNull( baseGuicyFig.getOverrides() );
    }


    @Test
    public void voidMethod() {
        BaseGuicyFig baseGuicyFig = new BaseGuicyFig();

        // just for giggles we know some methods will be void and will blow up
        for ( Method method : Object.class.getMethods() ) {
            if ( method.getReturnType() == Void.TYPE ) {
                baseGuicyFig.add( method.getName(), "bar", method );
            }
        }
    }
}
