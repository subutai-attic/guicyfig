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

import static junit.framework.Assert.assertFalse;
import static junit.framework.TestCase.assertEquals;


/**
 * Tests InternalOption.
 */
public class InternalOptionTest extends AbstractTest {
    private static final DynamicPropertyFactory factory = DynamicPropertyFactory.getInstance();


    @Test
    public void testConversions() {
        InternalOption<DynamicLongProperty> longProperty = 
                new InternalOption<DynamicLongProperty>( "foo", factory.getLongProperty( "foo", 30 ) );
        assertEquals( 30L, longProperty.value() );
        longProperty.setBypass( "60" );
        assertEquals( 60L, longProperty.getBypass() );

        InternalOption<DynamicIntProperty> intProperty =
                new InternalOption<DynamicIntProperty>( "fooInt", factory.getIntProperty( "fooInt", 456 ) );
        assertEquals( 456, intProperty.value() );
        intProperty.setBypass( "789" );
        assertEquals( 789, intProperty.getBypass() );

        InternalOption<DynamicStringProperty> strProperty =
                new InternalOption<DynamicStringProperty>( "fooString",
                        factory.getStringProperty( "fooStr", "example" ) );
        assertEquals( "example", strProperty.value() );
        strProperty.setBypass( "serious" );
        assertEquals( "serious", strProperty.getBypass() );

        InternalOption<DynamicFloatProperty> floatProperty =
                new InternalOption<DynamicFloatProperty>( "fooFloat",
                        factory.getFloatProperty( "fooFloat", 3.45f ) );
        assertEquals( 3.45f, floatProperty.value() );
        floatProperty.setBypass( "76.99" );
        assertEquals( 76.99f, floatProperty.getBypass() );

        InternalOption<DynamicDoubleProperty> doubleProperty =
                new InternalOption<DynamicDoubleProperty>( "fooDouble",
                        factory.getDoubleProperty( "fooDouble", 6.0221413e+23 ) );
        assertEquals( 6.0221413e+23, doubleProperty.value() );
        doubleProperty.setBypass( "3.302e+45" );
        assertEquals( 3.302e+45, doubleProperty.getBypass() );

        InternalOption<DynamicBooleanProperty> booleanProperty =
                new InternalOption<DynamicBooleanProperty>( "fooBoolean",
                        factory.getBooleanProperty( "fooBoolean", false ) );
        assertEquals( false, booleanProperty.value() );
        booleanProperty.setBypass( "true" );
        assertEquals( true, booleanProperty.getBypass() );
    }


    @Test ( expected = IllegalArgumentException.class )
    public void unknownConversion() {
        //noinspection unchecked
        InternalOption<PropertyWrapper> bogusProperty =
                new InternalOption<PropertyWrapper>( "aabbccdd", new PropertyWrapper( "foo", "bar" ) {
                    @Override
                    public Object getValue() {
                        return "";
                    }
                } );
        assertFalse( "food".hashCode() == bogusProperty.hashCode() );
        bogusProperty.setBypass( "food" );
        assertEquals( "food", bogusProperty.getBypass() );
    }
}
