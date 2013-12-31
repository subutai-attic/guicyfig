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
 * members and method/constructor parameters. This annotation does not actually
 * change any configuration parameter but rather uses method interception to
 * inject bypass values. Bypass values do not trigger change notifications to
 * listeners as do overrides. This annotation is especially ideal for
 * annotating your TEST cases.
 *
 * Bypass options do just as is suggested, they bypass the entire hierarchy of
 * configuration properties. Do not use a bypass if you would like notifications
 * on property changes to work. For this reason this annotation should be used
 * with care.
 *
 * @since 3.0
 */
@Retention( RUNTIME )
@Target( { METHOD, FIELD, PARAMETER, CONSTRUCTOR } )
@BindingAnnotation
public @interface Bypass {
    /**
     * The options to override.
     *
     * @return the options to override
     */
    Option[] options();

    /**
     * The environments in which to apply these Overrides.
     *
     * @return the environments in which to apply the Overrides
     */
    Env[] environments() default Env.ALL;
}
