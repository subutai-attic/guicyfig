package org.safehaus.guicyfig;


import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


/**
 * An option override used in conjunction with the @Overrides annotation at the
 * point where a configuration interface is being dependency injected as a field,
 * or as a parameter to a method.
 */
@Target( { FIELD, PARAMETER } )
@Retention( RUNTIME )
public @interface Option {
    /**
     * The name of the configuration interface's property getter to override.
     *
     * @return the method name as would be returned by {@link Method#getName()}
     */
    String method();

    /**
     * The value to override with as a String representation. This will be
     * automatically converted to the appropriate type if need be.
     *
     * @return the value to override with
     */
    String override();
}
