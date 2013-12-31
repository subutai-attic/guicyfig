package org.safehaus.guicyfig;


import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;


/**
 * An annotation signaling that the configuration interface is a singleton. Unfortunately
 * we have to have our own annotation here since Guice does not allow @Singleton annotations
 * on non-concrete types.
 *
 * @since 2.0
 */
@Target( TYPE )
@Retention( RUNTIME )
public @interface FigSingleton {}
