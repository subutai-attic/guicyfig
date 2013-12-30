package org.safehaus.guicyfig.bypass;


import org.safehaus.guicyfig.FigSingleton;
import org.safehaus.guicyfig.GuicyFig;


/**
 * Another configuration interface.
 */
@FigSingleton
public interface BypassSingletonFig extends GuicyFig {
    int getFoobar();
}
