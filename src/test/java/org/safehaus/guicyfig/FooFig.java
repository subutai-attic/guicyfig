package org.safehaus.guicyfig;


/**
 * Another configuration interface.
 */
public interface FooFig extends GuicyFig {
    @Key( "foo.fig.fun" )
    int getFoobar();

    @Key( "getSomething" )
    int getSomething();
}
