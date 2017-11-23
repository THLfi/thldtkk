# Development

Development instructions are written mainly for macOS but are applicable to
other environments as well.

## Install and start Termed

Clone Termed API and Termed UI to your workstation:

https://github.com/THLfi/termed-api
https://github.com/THLfi/termed-ui

Follow the instructions in above-mentioned repos to start Termed.

## Starting API module

Install Oracle JDK 8 (if not already installed). You can download it on Oracle's
website.

Install Maven 3.3 or newer (if not already installed): `brew install maven`

Configure server properties:

    <nano/vim/emacs> server/application.properties
    Enter the correct values for the following properties:
    - termed.apiUrl=http://localhost:8080/api
    - termed.username=<termed-username-for-api-calls>
    - termed.password=<termed-password-for-api-calls>
    - termed.graphId=<current-termed-dtkk-graph-id>
    - server.port=8680
    - users.properties.resource=<users-file-location-as-spring-resource>
    - sso.url=<THL SSO api url>
    - sso.application=aineistoeditori<-qa>
    - sso.secretKey=<secret key, obtainable from THL SSO admin user interfce>
    Save the file and exit the editor.

Add users to user properties file you configured above. See
`src/main/resources/users.properties` for example. Note 1: The application
will start without any configured users but credentials are required if you
want to access the editor. Note 2: Users file must be UTF-8 encoded.

Run dev server:

    cd server
    mvn spring-boot:run -Dskip.npm=true

Omit the `-Dskip.npm=true` on first start so that Node and npm packages get
installed by frontend-maven-plugin.

API can be accessed via `http://localhost:8680/api/v2/datasets/`.

## Starting UI module

Note. API module must be started before starting UI module.

Install NodeJS (if not already installed): `brew install node`

Install Angular CLI (if not already installed): `npm install -g @angular/cli`

Run dev server:

    cd client
    npm start

UI can be accessed via `http://localhost:8082/`. Changes to source files will
cause an automatic reload in your browser.

## Transforming XML files from outside schemas to internal schema

Transformation requires a specific XSLT file for the inbound format. Also, a working xalan installation is needed for the task. You can download it from: http://www.apache.org/dyn/closer.cgi/xml/xalan-j

Usage:

    java -jar <path-to-xalan-jar>/xalan.jar -IN <inbound file> -XSL <xsl template file> -OUT <resulting xml file>

Implemented transformations:

    Tilastokeskus format: TilastokeskusToAineistokatalogi.xsl

# Virtu configuration

We assume you have already registered the service provider (SP) via
[Virtu resource register](https://virtus.csc.fi/).

To use Virtu you must first enable Spring profile named `virtu`. You can do it
via _application.properties_ with `spring.profiles.active=virtu` or you can
specify the profile as command line argument when starting Tomcat with
`-Dspring.profiles.active=virtu`.

Next you must define required Virtu properties in your
_applications.properties_ file. Properties with examples are listed below:

    # Entity ID that you used register service to Virtu resource register.  
    virtu.entityId=https://qa.aineistoeditori.fi/virtu
    # Service's base URL. 
    virtu.entityBaseUrl=https://qa.aineistoeditori.fi
    # Java keystore (JKS) that includes SP certificate (and its private key) and Virtu metadata certificate.
    virtu.samlKeystoreResource=file:/Users/joni/certs/thldtkk-saml/thldtkk-saml.jks
    # Keystore's password.
    virtu.samlKeystorePassword=somepassword
    # Alias of SP certificate.
    virtu.spCertificateAliasInKeystore=qa.aineistoeditori.fi
    # Password of SP certificate's private key.
    virtu.spCertificatePassword=someotherpassword
    # URL where Virtu metadata can be loaded.
    virtu.metadataUrl=https://virtu-ds.csc.fi/fed/virtu-test/CSC_Virtu_Test_Servers-metadata.xml
    # Alias of Virtu metadata signing certificate in keystore.
    virtu.metadataSigningCertificateAliases=virtu-test-metadata-signing-crt-2015
    # Virtu discovery service URL (address where IdP selection is done).
    virtu.idpDiscoveryServiceUrl=https://industria.csc.fi/DS/

After this you should be able to start the application and login with Virtu.
Note that the service URL must match the URL registered to Virtu. If you want
to run the service locally on your developer machine you can edit your `hosts`
file to point the service URL (e.g. `qa.aineistoeditori.fi`) to your local
Tomcat.

## Importing certificates to Java keystore

To import SP certificate and it's private key you must first convert them to
PKCS12 format that JDK's keytool supports:

    openssl pkcs12 -export -in qa.aineistoeditori.fi.pem -inkey qa.aineistoeditori.fi.key -out qa.aineistoeditori.fi.p12 -name qa.aineistoeditori.fi

Conversion will ask for a password. You can enter anything here since the
password is only used during the next step. Now you can import the certificate
and private key into a JKS file.

    keytool -importkeystore -srckeystore qa.aineistoeditori.fi.p12 -srcstoretype PKCS12 -srcstorepass <sourcekeystorepassword> -destkeystore thldtkk-saml.jks -deststorepass somepassword -destkeypass someotherpassword -alias qa.aineistoeditori.fi

In above command parameter `-deststorepass` corresponds to property `virtu.samlKeystorePassword` and `-destkeypass` to `virtu.spCertificatePassword`.

When importing Virtu metadata signing certificate the process is more simple:

    keytool -importcert -noprompt -file virtu-metadata-signing-crt-2017.pem -keystore thldtkk-saml.jks -storepass somepassword -alias virtu-metadata-signing-crt-2017
