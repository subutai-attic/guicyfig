package org.safehaus.guicyfig;


import java.lang.annotation.Annotation;

import com.google.common.hash.HashCode;


/**
 * Basic Option implementation.
 */
@SuppressWarnings( "ClassExplicitlyAnnotation" )
class OptionImpl implements Option {
    private final String override;
    private final String method;


    OptionImpl( String method, String override ) {
        this.method = method;
        this.override = override;
    }


    @Override
    public String method() {
        return method;
    }


    @Override
    public String override() {
        return override;
    }


    @Override
    public Class<? extends Annotation> annotationType() {
        return Option.class;
    }


    @Override
    public int hashCode() {
        return HashCode.fromString( method ).hashCode();
    }


    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }

        if ( o instanceof Option ) {
            Option oo = ( Option ) o;

            return oo.method().equals( method );
        }

        return false;
    }
}
