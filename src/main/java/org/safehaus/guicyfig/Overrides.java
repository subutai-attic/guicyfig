package org.safehaus.guicyfig;


import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.google.inject.BindingAnnotation;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


/**
 * This annotation is used on configuration interfaces which are injected as
 * members and method/constructor parameters. It enables guicyfig dynamic
 * property setting overrides. This annotation is especially ideal for
 * annotating your test cases.
 *
 * Note that the array elements must align: meaning for example, the method
 * name properties specified in position 2 of the array must align with its
 * value in position 2 of the values array.
 */
@Retention( RUNTIME )
@Target( { METHOD, FIELD, PARAMETER, CONSTRUCTOR } )
@BindingAnnotation
public @interface Overrides {
    public static enum Env {
        all, unit, test, chop, dev, integ, accept, prod
    }

    String name();
    Option[] options() default {};
    Env[] contexts() default Env.all;
}
