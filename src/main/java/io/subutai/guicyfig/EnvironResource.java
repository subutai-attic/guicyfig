package io.subutai.guicyfig;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * A base class for environment specific Rules that set up an external
 * resource before a test (a file, socket, server, database connection, etc.),
 * and guarantee to tear it down afterward:
 *
 * @since 3.0
 */
public abstract class EnvironResource implements TestRule {
    private static final Logger LOG = LoggerFactory.getLogger( EnvironResource.class );
    private final Set<Env> environs;


    /**
     * Creates an environment specific resource active only in the environments provided.
     *
     * @param environs the active environments for this resource
     */
    public EnvironResource( Env... environs ) {
        LOG.debug( "Set to operate in environments: {}", environs );
        this.environs = new HashSet<Env>( environs.length );
        Collections.addAll( this.environs, environs );
    }


    /**
     * Creates an environment specific resource that is not yet activated for any environment.
     * Such a resource will only be active if the {@link Env#getEnvironment()} call returns
     * {@link Env#ALL}.
     */
    public EnvironResource() {
        this.environs = new HashSet<Env>( 5 );
    }


    /**
     * Adds an {@link Env} to this resource's set of valid environments to be active in.
     *
     * @param env an environment for this resource to be active in
     * @return true if added and was not present before, false if already present
     */
    public final boolean addEnvironment( Env env ) {
        LOG.debug( "Adding environment {} to set of environments {}", env, environs );
        return environs.add( env );
    }


    /**
     * Executes the statement sandwiched by the conditional invocation of before() and after()
     * @param base the base statement
     * @param description the test description
     * @return the modified statement
     */
    public final Statement apply( Statement base, Description description ) {
        return statement( base );
    }


    private Statement statement( final Statement base ) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                Env env = Env.getEnvironment();

                LOG.info( "Operating environment = {}", env );

                if ( env == Env.ALL || environs.contains( Env.ALL ) || environs.contains( env ) ) {
                    LOG.info( "Operating environment {} matched by valid environments: ", environs );
                    before();
                }
                else {
                    LOG.info( "Skipping before() invocation: current env {} NOT in valid environment set: {}",
                            env, environs );
                }

                try {
                    base.evaluate();
                }
                finally {
                    if ( env == Env.ALL || environs.contains( Env.ALL ) || environs.contains( env ) ) {
                        LOG.info( "Operating environment {} matched by valid environments: ", environs );
                        after();
                    }
                    else {
                        LOG.info( "Skipping after() invocation: current env {} NOT in valid environment set: {}",
                                env, environs );
                    }
                }
            }
        };
    }


    /**
     * Override to set up your specific external resource.
     *
     * @throws Throwable if setup fails (which will disable {@code after}
     */
    protected void before() throws Throwable {
        // do nothing
    }


    /**
     * Override to tear down your specific external resource.
     */
    protected void after() {
        // do nothing
    }
}
