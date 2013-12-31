package org.safehaus.guicyfig;


/**
 * Basic configuration option interface.
 *
 * @since 3.0
 */
public interface OptionState<V> {
    String getKey();
    V getValue();
    V getOldValue();
    V getOverriddenValue();
    V getBypassedValue();
    Option getOverride();
    boolean isOverridden();
    Option getBypass();
    boolean isBypassed();
}
