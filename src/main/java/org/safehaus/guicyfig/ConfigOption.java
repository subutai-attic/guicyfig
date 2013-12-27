package org.safehaus.guicyfig;


/**
 * Basic configuration option interface.
 */
public interface ConfigOption {
    String key();
    Object value();
    void setOverride( String value );
    void setBypass( String value );
}
