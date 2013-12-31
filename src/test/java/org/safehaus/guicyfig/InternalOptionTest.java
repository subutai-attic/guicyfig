package org.safehaus.guicyfig;


import org.junit.Test;

import com.netflix.config.DynamicBooleanProperty;
import com.netflix.config.DynamicDoubleProperty;
import com.netflix.config.DynamicFloatProperty;
import com.netflix.config.DynamicIntProperty;
import com.netflix.config.DynamicLongProperty;
import com.netflix.config.DynamicPropertyFactory;
import com.netflix.config.DynamicStringProperty;
import com.netflix.config.PropertyWrapper;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertEquals;


/**
 * Tests InternalOptionState.
 */
public class InternalOptionTest extends AbstractTest {
    private static final DynamicPropertyFactory factory = DynamicPropertyFactory.getInstance();


    @Test
    public void testConversions() {
        InternalOptionState<Long,DynamicLongProperty> longProperty =
                new InternalOptionState<Long,DynamicLongProperty>( "foo", factory.getLongProperty( "foo", 30 ), null ) ;
        assertEquals( 30L, ( long ) longProperty.getValue() );
        longProperty.setBypass( new OptionImpl( "foo", "60" ) );
        assertEquals( "60", longProperty.getBypass().override() );

        InternalOptionState<Integer,DynamicIntProperty> intProperty =
                new InternalOptionState<Integer,DynamicIntProperty>( "fooInt", factory.getIntProperty( "fooInt", 456 ), null );
        assertEquals( 456, ( int ) intProperty.getValue() );
        intProperty.setBypass( new OptionImpl( "fooInt", "789" ) );
        assertEquals( "789", intProperty.getBypass().override() );

        InternalOptionState<String,DynamicStringProperty> strProperty =
                new InternalOptionState<String,DynamicStringProperty>( "fooString",
                        factory.getStringProperty( "fooStr", "example" ), null );
        assertEquals( "example", strProperty.getValue() );
        strProperty.setBypass( new OptionImpl( "fooStr", "serious" ) );
        assertEquals( "serious", strProperty.getBypass().override() );

        InternalOptionState<Float,DynamicFloatProperty> floatProperty =
                new InternalOptionState<Float,DynamicFloatProperty>( "fooFloat",
                        factory.getFloatProperty( "fooFloat", 3.45f ), null );
        assertEquals( 3.45f, floatProperty.getValue() );
        floatProperty.setBypass( new OptionImpl( "fooFloat", "76.99" ) );
        assertEquals( "76.99", floatProperty.getBypass().override() );

        InternalOptionState<Double,DynamicDoubleProperty> doubleProperty =
                new InternalOptionState<Double,DynamicDoubleProperty>( "fooDouble",
                        factory.getDoubleProperty( "fooDouble", 6.0221413e+23 ), null );
        assertEquals( 6.0221413e+23, doubleProperty.getValue() );
        doubleProperty.setBypass( new OptionImpl( "fooDouble", "3.302e+45" ) );
        assertEquals( "3.302e+45", doubleProperty.getBypass().override() );

        InternalOptionState<Boolean,DynamicBooleanProperty> booleanProperty =
                new InternalOptionState<Boolean,DynamicBooleanProperty>( "fooBoolean",
                        factory.getBooleanProperty( "fooBoolean", false ), null );
        assertEquals( false, ( boolean ) booleanProperty.getValue() );
        booleanProperty.setBypass( new OptionImpl( "fooBoolean", "true" ) );
        assertEquals( "true", booleanProperty.getBypass().override() );
    }


    @Test ( expected = IllegalArgumentException.class )
    public void unknownConversion() {
        //noinspection unchecked,SpellCheckingInspection
        InternalOptionState<String,?> bogusProperty =
                new InternalOptionState( "aabbccdd", new PropertyWrapper( "foo", "bar" ) {
                    @Override
                    public Object getValue() {
                        return "";
                    }
                }, null );
        assertFalse( "food".hashCode() == bogusProperty.hashCode() );
        bogusProperty.setBypass( new OptionImpl( "foo", "food" ) );
        //noinspection AssertEqualsBetweenInconvertibleTypes
        assertEquals( "food", bogusProperty.getBypass() );
    }
}
