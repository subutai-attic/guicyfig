package io.subutai.guicyfig;


import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertNull;
import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;


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


    @Test
    public void testUseKeyForMethod() {
        Injector injector = Guice.createInjector( new GuicyFigModule( FooFig.class ) );
        FooFig config = injector.getInstance( FooFig.class );
        assertNotNull( config );
        assertEquals( FooFig.class, config.getFigInterface() );

        assertEquals( 0, config.getFoobar() );

        config.bypass( "foo.fig.fun", "10" );
        assertEquals( 10, config.getFoobar() );
        config.bypass( "getFoobar", null );
        assertEquals( 0, config.getFoobar() );

        try {
            config.bypass( "asdf", "123" );
            fail( "should not get here" );
        }
        catch ( Exception e ) {
        }

        config.bypass( "getSomething", "12345" );
        assertEquals( 12345, config.getSomething() );
        config.bypass( "getSomething", null );
        assertEquals( 0, config.getSomething() );
    }


    @Test
    public void testNotifications() {
        Injector injector = Guice.createInjector( new GuicyFigModule( FooFig.class ) );
        FooFig config = injector.getInstance( FooFig.class );
        final List<PropertyChangeEvent> events = new ArrayList<PropertyChangeEvent>();
        PropertyChangeListener listener = new PropertyChangeListener() {
            @Override
            public void propertyChange( final PropertyChangeEvent evt ) {
                events.add( evt );
            }
        };
        config.addPropertyChangeListener( listener );

        config.bypass( "getFoobar", null );
        assertEquals( 0, events.size() );
        config.bypass( "getFoobar", null );
        assertEquals( 0, events.size() );

        config.bypass( "getFoobar", "123" );
        config.bypass( "getFoobar", "123" );
        Bypass bypass = config.getBypass();
        assertEquals( 1, bypass.options().length );
        assertEquals( 1, events.size() );
        config.bypass( "getFoobar", null );
        assertEquals( 2, events.size() );
        assertEquals( 0, bypass.options().length );

        config.override( "getFoobar", "123" );
        config.override( "getFoobar", "123" );
        Overrides overrides = config.getOverrides();
        assertEquals( 1, overrides.options().length );
        assertEquals( 3, events.size() );
        config.override( "getFoobar", null );
        assertEquals( 4, events.size() );
        assertEquals( 0, overrides.options().length );

        bypass = new BypassImpl() {
            @Override
            public Env[] environments() {
                return new Env[] { Env.CHOP };
            }
        };
        assertFalse( config.setBypass( bypass ) );

        bypass = new BypassImpl() {
            @Override
            public Env[] environments() {
                return new Env[] { Env.CHOP, Env.UNIT };
            }
        };
        assertTrue( config.setBypass( bypass ) );
    }
}
