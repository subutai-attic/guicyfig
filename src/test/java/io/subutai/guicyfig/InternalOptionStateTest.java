package io.subutai.guicyfig;


import java.lang.reflect.Method;

import org.junit.Test;

import com.netflix.config.DynamicBooleanProperty;
import com.netflix.config.DynamicDoubleProperty;
import com.netflix.config.DynamicFloatProperty;
import com.netflix.config.DynamicIntProperty;
import com.netflix.config.DynamicPropertyFactory;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertNull;


/**
 * Tests InternalOptionState.
 */
public class InternalOptionStateTest {
    DynamicPropertyFactory factory = DynamicPropertyFactory.getInstance();

    @Test
    public void testAll() throws NoSuchMethodException {
        DynamicIntProperty property = factory.getIntProperty( "foo.key", 10 );
        Method method = FooFig.class.getMethod( "getFoobar" );
        InternalOptionState state = new InternalOptionState
                <Integer,DynamicIntProperty>( "foo.key", property, method );
        assertEquals( 10, state.getValue() );
        assertNull( state.getBypass() );
        assertNull( state.getBypassValue() );
        assertNull( state.getOverride() );
        assertNull( state.getOverrideValue() );


        DynamicFloatProperty barProp = factory.getFloatProperty( "bar.key", 20.2f );
        state = new InternalOptionState
                <Float,DynamicFloatProperty>( "bar.key", barProp, method );
        assertEquals( 20.2f, state.getValue() );
        assertEquals( 98.6f, state.convertValue( "98.6" ) );

        DynamicDoubleProperty doubleProp = factory.getDoubleProperty( "double.key", 20.2e10 );
        state = new InternalOptionState
                <Double,DynamicDoubleProperty>( "bar.key", doubleProp, method );
        assertEquals( 20.2e10, state.getValue() );
        assertEquals( 1.345, state.convertValue( "1.345" ) );

        DynamicBooleanProperty boolProp = factory.getBooleanProperty( "bool.key", true );
        state = new InternalOptionState
                <Boolean,DynamicBooleanProperty>( "bool.key", boolProp, method );
        assertEquals( true, state.getValue() );
        assertEquals( false, state.convertValue( "false" ) );
        assertNotNull( state.getProperty() );
        assertEquals( true, state.getProperty().getValue() );
        assertNotNull( state.getMethod() );
    }
}
