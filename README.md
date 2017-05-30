# Development

Development instructions are written mainly for macOS but are applicable to
other environments as well.

## Starting API module

Install Oracle JDK 8 (if not already installed). You can download it on Oracle's
website.

Install Maven 3.3 or newer (if not already installed): `brew install maven`

Configure server properties:

    <nano/vim/emacs> server/application.properties
    Enter the correct values for the following properties:
    - termed.apiUrl=http://qa-wwwapp6a.thl.fi:8580/termed/api
    - termed.username=<termed-username-for-api-calls>
    - termed.password=<termed-password-for-api-calls>
    - termed.graphId=<current-termed-dtkk-graph-id>
    - server.port=8680
    Save the file and exit the editor.

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
