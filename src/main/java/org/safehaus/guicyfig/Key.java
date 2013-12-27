package org.safehaus.guicyfig;


import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


/**
 * Annotation to setOverride the default property key using the default naming
 * convention.
 */
@Target( METHOD )
@Retention( RUNTIME )
public @interface Key {
    String value();
}
