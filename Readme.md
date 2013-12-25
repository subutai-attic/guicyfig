# What is it?

Do you:

* Hate to deal with configuration properties files?
* Want a type safe interface based configuration bean backed by your properties?
* Want to be able to register listeners for configuration property changes?
* Not have to create an implementation of a configuration interface?
* Want Guice to handle everything including property to config bean mapping
  and the bean's injection where you like?

GuicyFig gives you all this. It is Guice and Archaius based property config 
management in a Java way without a bunch of constants you loose track of in
big projects. 

# How do I use it?

Just create one or more "configuration" bean interfaces and have them extend the
GuicyFig interface. Then install the GuicyFig guice module in your modules to
get configuration beans as interfaces. No  more properties constants for 
property keys and defaults: GuicyFig makes that way 2010! 

~~~~~~~~
package com.foo;

public BarConfig extends GuicyFig {
    int getPort();
    String getHostname();
}
~~~~~~~~

~~~~~~~~
package com.foo;

public MyModule extends AbstractModule {
...
    protected void configure() {
        install( new GuicyFigModule( MyConfig.class ) );
        ...
    }
...
}
~~~~~~~~

## Mappings, Defaults and Overrides

GuicyFig uses the package.Class.MethodName naming convention as the default 
convention for configuration interface properties. For the example above the
properties would would default to the following key value pairs:

  * com.foo.BarConfig.getHostname=
  * com.foo.BarConfig.getPort=0

"Smart" defaults are settings that should enable your app, service, component
or whateva, to work properly in case the properties file you load does not have
a value or key for your configuration settings. There are 3 ways in which 
default values are provided:

1. Do nothing and the defaults will be standard Java type defaults
2. Provide a defaults file using the package.Class.properties convention
3. Use the @Key, and @Default annotations to control key naming and default
   values

**NOTE**: For (2) if you have a GuicyFig conf bean iface named com.foo.BarConfig
it's defaults configuration file will be in com/foo/BarConfig.properties. Place 
properties you would like to use for your interface there. Usually you want to
bundle this with your module/bean's artifact so it is always avaliable.

So with the @Key and @Default annotations you can override the way keys are
mapped to your interface methods and the defaults used but what about when
you want to override actual properties? That is just as easy, just use the 
@Overrides and @Option annotations to programmatically override values where
you inject in your config bean:

~~~~~~~
    @Inject
    @Overrides(
        name = "JustForMyTest",
        options = {
            @Option( method = "getHostname", override = "bullshakala" )
        }
    )
    BarConfig serviceConfig;
~~~~~~~

**NOTE**: Another really neat feature of the overrides is that you can make it
use a specific environment to trigger the Archaius deployment context under the
hood to cascade your actual properties files for PROD, TEST, DEV, INTEG etc.

# Project Resources

[Alt text](/path/to/img.jpg)
[Alt text](/path/to/img.jpg)
[Alt text](/path/to/img.jpg)
[Alt text](/path/to/img.jpg)

# Special Thanks

* Todd for pushing to do something better. Awesomeness!
* Netflix for Archaius which GuicyFig builds on
* Apache for Apache Commons which GuicyFig builds on
* And to Google for being cool enough to pump out a DI container like Guice

Rock on!

Alex
