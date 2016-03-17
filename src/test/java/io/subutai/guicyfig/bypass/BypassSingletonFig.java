package io.subutai.guicyfig.bypass;


import io.subutai.guicyfig.FigSingleton;
import io.subutai.guicyfig.GuicyFig;


/**
 * Another configuration interface.
 */
@FigSingleton
public interface BypassSingletonFig extends GuicyFig {
    int getFoobar();
}
