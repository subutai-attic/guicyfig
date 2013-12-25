package org.safehaus.guicyfig;


/**
 * Example configurable object Interface.
 */
public interface ServiceConfig extends GuicyFig {
    String getHost();

    int getPort();

    @Default( "10" )
    int getMaxConnections();

    @Key( "thread.wait.time" )
    long getThreadWaitTime();

    @Default( "20.5" )
    float getLoadThreshold();

    @Default( "11.23" )
    double getLoadAverage();

    @Default( "false" )
    boolean isThrottlingEnabled();

    @Default( "true" )
    boolean isResetNeeded();

    @Default( "500" )
    long getStartupTimeout();

    @Default( "3.14" )
    float getPi();

    @Default( "6.0221413e+23" )
    double getAvagadrosNumber();

    @Default( "25" )
    int getExecutionCount();

    @Default( "false" )
    boolean isDebugEnabled();

    boolean getAbc();
}
