keycloak-login
===============

GONG plugin that allow user authentication on the website through [Keycloak](http://www.keycloak.org)!

## Installation

To add the plugin dependencies to your project, edit the project pom.xml and add:

```xml
<project>
  ...
  <dependencies>
    ...
    <dependency>
      <groupId>com.atex.plugins</groupId>
      <artifactId>keycloak-login</artifactId>
      <version>2.6</version>
    </dependency>
    <dependency>
      <groupId>com.atex.plugins</groupId>
      <artifactId>keycloak-login</artifactId>
      <version>2.6</version>
      <classifier>contentdata</classifier>
    </dependency>
    ...
  </dependencies>
  ...
</project>
```

Adding a login/logout button

  An output template is provided which can be added to the page layout velocity template
  in order to display the login or logout button.

```
#render({"outputTemplate": "com.atex.plugins.keycloak.login.LoginLogout.ot"})
```

  in a standard GONG you may want to add it after:

```
#render({"outputTemplate": "com.atex.plugins.socialauth.core.MainElement.ot", "content":$content})
```

  You'll then need to configure keycloak and the plugin, have a look at the plugin documentation.

## Polopoly Version
10.16.5


## Release Notes

### 2.6

- initial release based on keycloak 3.4.3.Final

## Code Status
The code in this repository is provided with the following status: **EXAMPLE**

Under the open source initiative, Atex provides source code for plugin with different levels of support. There are three different levels of support used. These are:

- EXAMPLE  
The code is provided as an illustration of a pattern or blueprint for how to use a specific feature. Code provided as is.

- PROJECT  
The code has been identified in an implementation project to be generic enough to be useful also in other projects. This means that it has actually been used in production somewhere, but it comes "as is", with no support attached. The idea is to promote code reuse and to provide a convenient starting point for customization if needed.

- PRODUCT  
The code is provided with full product support, just as the core Polopoly product itself.
If you modify the code (outside of configuraton files), the support is voided.


## License
Atex Polopoly Source Code License
Version 1.0 February 2012

See file **LICENSE** for details
