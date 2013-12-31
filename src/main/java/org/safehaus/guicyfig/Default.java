package org.safehaus.guicyfig;


import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


/**
 * An annotation used to override or set the dynamic property default. If used
 * in conjunction with a defaults properties file, this annotation's value will
 * override the default value set in the defaults property file.
 *
 * The defaults properties file uses the convention of being present in the
 * package of the configuration interface with its class name. So for example
 * com.foo.Bar would have a com/foo/Bar.properties file for the defaults.
 *
 * @since 1.0
 */
@Target( METHOD )
@Retention( RUNTIME )
public @interface Default {
    /**
     * Gets the default value to use if the main configuration file
     * does not have the value.
     *
     * @return the default value
     */
    String value();
}
