package org.safehaus.guicyfig;


/**
 * Another configuration interface.
 */
public interface AnotherFig extends GuicyFig {
    @Key( "com.foo.bar" )
    int getFoobar();
}
