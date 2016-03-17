package io.subutai.guicyfig;


import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * Internal overrides implementation.
 */
@SuppressWarnings( "ClassExplicitlyAnnotation" )
class OverridesImpl implements Overrides {
    private final Set<Env> environments = new HashSet<Env>();
    private final Map<String,Option> options = new HashMap<String, Option>();
    private final String name;


    OverridesImpl( String name ) {
        this.name = name;
    }


    OverridesImpl( Overrides overrides ) {
        this.name = overrides.name();
        Collections.addAll( environments, overrides.environments() );

        for ( Option option : overrides.options() ) {
            options.put( option.method(), option );
        }
    }


    Option addOption( Option option ) {
        options.put( option.method(), option );
        return option;
    }


    Option addOption( String method, String override ) {
        OptionImpl option = new OptionImpl( method, override );
        addOption( option );
        return option;
    }


    @Override
    public String name() {
        return name;
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
        return Overrides.class;
    }


    public Option removeOption( final String key ) {
        return options.remove( key );
    }
}
