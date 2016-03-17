package io.subutai.guicyfig;


import com.netflix.config.ConfigurationManager;
import com.netflix.config.DeploymentContext;


/**
 * The environment enumeration type.
 *
 * @since 2.0
 */
public enum Env {
    ALL, UNIT, TEST, CHOP, DEV, INTEG, ACCEPT, PROD;


    /**
     * Converts a String based environment name into the Enum.
     *
     * @param environment the environment String
     * @return the Enum representing the environment or ALL if the supplied
     * String matches no environment or is null
     */
    public static Env getEnvironment( String environment ) {
        if ( environment == null ) {
            return ALL;
        }

        if ( environment.equalsIgnoreCase( ALL.toString() ) ) {
            return ALL;
        }
        else if ( environment.equalsIgnoreCase( UNIT.toString() ) ) {
            return UNIT;
        }
        else if ( environment.equalsIgnoreCase( TEST.toString() ) ) {
            return TEST;
        }
        else if ( environment.equalsIgnoreCase( CHOP.toString() ) ) {
            return CHOP;
        }
        else if ( environment.equalsIgnoreCase( DEV.toString() ) ) {
            return DEV;
        }
        else if ( environment.equalsIgnoreCase( INTEG.toString() ) ) {
            return INTEG;
        }
        else if ( environment.equalsIgnoreCase( ACCEPT.toString() ) ) {
            return ACCEPT;
        }
        else if ( environment.equalsIgnoreCase( PROD.toString() ) ) {
            return PROD;
        }

        return ALL;
    }


    /**
     * Gets the Env from the Archaius context. If one is not set then the ALL
     * Env enum value is returned.
     *
     * @return the Archaius Env
     */
    public static Env getEnvironment() {
        DeploymentContext context = ConfigurationManager.getDeploymentContext();
        if ( context == null ) {
            return ALL;
        }

        return getEnvironment( context.getDeploymentEnvironment() );
    }
}
