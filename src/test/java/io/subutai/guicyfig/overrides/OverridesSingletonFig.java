package io.subutai.guicyfig.overrides;


import io.subutai.guicyfig.FigSingleton;
import io.subutai.guicyfig.GuicyFig;


/**
 * Another configuration interface.
 */
@FigSingleton
public interface OverridesSingletonFig extends GuicyFig {
    int getAbc();
}
