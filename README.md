# Development

Development instructions are written mainly for macOS but are applicable to
other environments as well.

## Install and start Termed

Clone Termed API and Termed UI to your workstation:

- https://github.com/THLfi/termed-api
- https://github.com/THLfi/termed-ui

Follow the instructions in above-mentioned repos to start Termed.

## Starting API module

Install Oracle JDK 8 (if not already installed). You can download it on Oracle's
website. Application should work with other JDK implementation (e.g. OpenJDK)
but it has not been tested.

Install Maven 3.3 or newer (if not already installed): `brew install maven`

Configure server properties:

    <nano/vim/emacs> server/src/main/resources/config/application.properties
    Enter the correct values for the following properties:
    - termed.apiUrl=http://localhost:8080/api
    - termed.username=<termed-username-for-api-calls>
    - termed.password=<termed-password-for-api-calls>
    - termed.editorGraphId=<termed-dtkk-editor-graph-uuid>
    - termed.publicGraphId=<termed-dtkk-public-graph-uuid>
    - termed.commonGraphId=<termed-dtkk-common-graph-uuid>
    - server.port=8680
    - users.properties.resource=<users-file-location-as-spring-resource>
    - sso.url=<THL SSO api url>
    - sso.application=aineistoeditori<-qa>
    - sso.secretKey=<secret key, obtainable from THL SSO admin user interface>
    - spring.mail.host=mail-hub.thl.fi (You may wanna use dev smtp servers like https://github.com/mailslurper/mailslurper or https://mailcatcher.me/ so that test mails are not sent to real email addresses)
    - spring.mail.from=info@aineistokatalogi.fi
    - spring.mail.port=25

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

You can test that the API works by accessing it via
`http://localhost:8680/api/v1/public/studies`.

## Starting UI module

Note. API module must be started before starting UI module.

Install NodeJS (if not already installed): `brew install node`

Install Angular CLI (if not already installed): `npm install -g @angular/cli`

Run dev server:

    cd client
    npm start

UI can be accessed via `http://localhost:8082/`. Changes to source files will
cause an automatic reload in your browser.

During development, a live testing session can be initiated using:

    ng test

# Virtu configuration

Editor has a built-in feature that allows users to login with
[Virtu identity and authorization service](https://wiki.eduuni.fi/display/CSCVIRTU/Virtu).

First, you must register your (test) environment as a service provider (SP)
by following [Virtu SP registration instructions](https://wiki.eduuni.fi/pages/viewpage.action?pageId=28345593).
The rest of the steps assume you have completed the registration and are
somewhat familiar with concepts SAML2 protocol that Virtu is based on.

To use Virtu in editor enable Spring profile named `virtu`. You can do it
via _application.properties_ by adding line `spring.profiles.active=virtu` or
you can specify the profile as command line argument when starting Tomcat with
`-Dspring.profiles.active=virtu`.

Next, define required Virtu properties in your _applications.properties_ file.
Properties with examples are listed below:

    # Entity ID that you used register service to Virtu resource register.  
    virtu.entityId=https://qa.aineistoeditori.fi/virtu
    # Service's base URL. 
    virtu.entityBaseUrl=https://qa.aineistoeditori.fi
    # Java keystore (JKS) that includes SP certificate (and its private key) and Virtu metadata certificate.
    virtu.samlKeystoreResource=file:/path/to/your/saml-certificates-file.jks
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

Afterwards you can verify contents of the keystore by running:

    keytool -list -keystore thldtkk-saml.jks -storepass somepassword

## Licence

The Work Aineistokatalogi/Aineistoeditori is licenced under European Union 
Public Licence (EUPL). You irrevocably accept this Licence (EUPL) and all 
of its terms and conditions by exercising any rights granted to You by 
Article 2 of this Licence (EUPL), such as the use of the Work, the creation 
by You of a Derivative Work or the Distribution or Communication by You of 
the Work or copies thereof.

In addition, THL requests that the layout of any Derivative Work should 
differ from the original Work in such a distinctive way that there is no 
possibility of confusion between Aineistokatalogi/Aineistoeditori and the 
Derivative Work to the end user.

Please provide information of any published Derivative Works 
to info@aineistokatalogi.fi
