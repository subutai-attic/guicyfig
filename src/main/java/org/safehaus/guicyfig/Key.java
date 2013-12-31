package org.safehaus.guicyfig;


import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


/**
 * Annotation to override the default property key using the default naming
 * convention.
 *
 * @since 1.0
 */
@Target( METHOD )
@Retention( RUNTIME )
public @interface Key {
    String value();
}
