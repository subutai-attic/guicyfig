package org.safehaus.guicyfig;


/**
 * Basic configuration option interface.
 */
public interface OptionState<V> {
    String getKey();
    V getValue();
    V getOldValue();
    Option getOverride();
    boolean isOverridden();
    Option getBypass();
    boolean isBypassed();
}
