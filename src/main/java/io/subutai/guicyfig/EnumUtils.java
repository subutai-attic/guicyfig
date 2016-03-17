package io.subutai.guicyfig;


/** Utility for converting strings to enum type */
public class EnumUtils {

    /**
     * Get an instance of the enum value for the target class
     *
     * @param value the value of the enumeration to match for
     * @param enumClass the type of the enumerated type
     * @return the Enum instance associated with the value
     */
    public static Object getEnumInstance( String value, Class<?> enumClass ) {
        Object[] constants = enumClass.getEnumConstants();

        Object configuredInstance = null;

        for ( Object current : constants ) {
            final Enum<?> constant = ( Enum<?> ) current;

            final String name = constant.name();

            if ( name.equals( value ) ) {
                configuredInstance = constant;
                break;
            }
        }

        if ( configuredInstance == null ) {
            throw new RuntimeException( "Enum of type " + value + " does not exist for enum class " + enumClass );
        }

        return configuredInstance;
    }
}
