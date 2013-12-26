package org.safehaus.guicyfig;


/**
 * Another configuration interface.
 */
public interface AnotherConfig extends GuicyFig {
    @Key( "com.foo.bar" )
    int getFoobar();
}
