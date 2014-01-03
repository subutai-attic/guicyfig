package org.safehaus.guicyfig;


import java.beans.PropertyChangeListener;
import java.util.Map;
import java.util.Properties;


/**
 * Super interface for dynamic configurations. Make your configuration interface
 * extend GuicyFig to enable your own dynamic GuicyFig configuration. Don't worry
 * about implementing a concrete implementation of your configuration. GuicyFig's
 * Guice Module {@link GuicyFigModule} will handle that and injection for you.
 *
 * @since 1.0
 */
public interface GuicyFig {
    /**
     * Adds a {@link PropertyChangeListener} to this configuration bean to listen
     * for property changes.
     *
     * @param listener the listener to add
     */
    void addPropertyChangeListener( PropertyChangeListener listener );


    /**
     * Removes a {@link PropertyChangeListener} from this configuration bean to prevent
     * if from receiving change notifications.
     *
     * @param listener the listener to remove
     */
    void removePropertyChangeListener( PropertyChangeListener listener );


    /**
     * Gets the configuration options for this GuicyFig.
     *
     * @return the configuration options
     */
    OptionState[] getOptions();


    /**
     * Gets a specific configuration option by key NOT by method name.
     *
     * @param key the key of the configuration option
     * @return the configuration option
     */
    OptionState getOption( String key );


    /**
     * Gets the configuration option key (in properties file) using the method name.
     *
     * @param methodName the name of the configuration option method
     * @return the key used for the configuration option
     */
    String getKeyByMethod( String methodName );


    /**
     * Gets the configuration option value using the method name.
     *
     * @param methodName the name of the configuration option method
     * @return the value of the configuration option
     */
    Object getValueByMethod( String methodName );


    /**
     * Filters out properties from a properties file retaining only those properties
     * that have options with a matching key. There are no side effects to the
     * {@link Properties} parameter supplied: a new Properties object is returned.
     *
     * @param properties the properties to filter
     * @return the filtered properties with only our options remaining
     */
    Properties filterOptions( Properties properties );


    /**
     * Filters out key value pairs from a {@link Map} retaining only those entries
     * that have keys matching the option keys of this GuicyFig. There are no side
     * effects to the Map parameter supplied: a new Map object is returned.
     *
     * @param entries the Map of entries to filter
     * @return a Map of entries whose keys map to this GuicyFig's options
     */
    Map<String,Object> filterOptions( Map<String,Object> entries );


    /**
     * Sets a method (or key) value override.
     *
     * @param key the key or the method name (either can be provided)
     * @param override the override value
     */
    void override( String key, String override );


    /**
     * Sets the {@link Overrides} annotations, if applied to this GuicyFig.
     *
     * @param overrides the overrides to apply to this GuicyFig
     * @return true if the environment matches and the override is applied,
     * false otherwise
     */
    boolean setOverrides( Overrides overrides );


    /**
     * Gets the {@link Overrides} annotations, if applied to this GuicyFig.
     *
     * @return the overrides, if any, applied to this GuicyFig
     */
    Overrides getOverrides();


    /**
     * Sets a method (or key) value bypass.
     * @param key the key or the method name (either can be provided)
     * @param bypass the bypass value
     */
    void bypass( String key, String bypass );


    /**
     * Sets the {@link Bypass} annotations to be applied to this GuicyFig if the
     * environments match.
     *
     * @param bypass to apply to this GuicyFig,
     * @return true if bypass is applied, false if not
     */
    boolean setBypass( Bypass bypass );


    /**
     * Sets the {@link Bypass} annotations, if applied to this GuicyFig.
     *
     * @return the bypass, if any, applied to this GuicyFig
     */
    Bypass getBypass();


    /**
     * Gets the user defined configuration interface.
     *
     * @return the user defined configuration interface
     */
    Class getFigInterface();


    /**
     * Checks whether or not this configuration interface is a Singleton.
     */
    boolean isSingleton();
}
