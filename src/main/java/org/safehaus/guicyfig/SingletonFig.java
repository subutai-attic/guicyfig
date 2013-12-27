package org.safehaus.guicyfig;


/**
 * Another configuration interface.
 */
@FigSingleton
public interface SingletonFig extends GuicyFig {
    @Key( "com.foo.bar" )
    int getFoobar();
}
