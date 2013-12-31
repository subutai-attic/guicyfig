package org.safehaus.guicyfig;


import java.lang.reflect.Method;

import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertNull;


/**
 * Tests BaseGuicyFig.
 */
public class BaseGuicyFigTest extends AbstractTest {

    @Test
    public void badMethodNames() {
        BaseGuicyFig baseGuicyFig = new BaseGuicyFig();
        assertNull( baseGuicyFig.getKeyByMethod( "foo" ) );
        assertNull( baseGuicyFig.getValueByMethod( "bar" ) );
    }


    @Test
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


    @Test
    public void getFigInterface() {
        Injector injector = Guice.createInjector( new GuicyFigModule( FooFig.class ) );
        FooFig config = injector.getInstance( FooFig.class );
        assertNotNull( config );

        assertEquals( FooFig.class, config.getFigInterface() );
    }
}
