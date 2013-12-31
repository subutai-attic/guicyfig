package org.safehaus.guicyfig;


import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * Simple Bypass implementation for internal use.
 */
public class BypassImpl implements Bypass {
    private final Set<Env> environments = new HashSet<Env>();
    private final Map<String,Option> options = new HashMap<String, Option>();

    BypassImpl( Bypass overrides ) {
        Collections.addAll( environments, overrides.environments() );

        for ( Option option : overrides.options() ) {
            options.put( option.method(), option );
        }
    }


    BypassImpl() {
    }


    Option addOption( Option option ) {
        options.put( option.method(), option );
        return option;
    }


    Option addOption( String method, String override ) {
        OptionImpl option = new OptionImpl( method, override );
        return addOption( option );
    }


    @Override
    public Option[] options() {
        Option[] retval = new Option[options.size()];

        int ii = 0;
        for ( String method : options.keySet() ) {
            retval[ii] = options.get( method );
            ii++;
        }

        return retval;
    }


    @Override
    public Env[] environments() {
        return environments.toArray( new Env[environments.size()] );
    }


    @Override
    public Class<? extends Annotation> annotationType() {
        return Bypass.class;
    }
}
