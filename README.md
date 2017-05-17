# Development

Development instructions are written mainly for macOS but are applicable to
other environments as well.

## Starting API module

Install Oracle JDK 8 (if not already installed). You can download it on Oracle's
website.

Install Maven 3.3 or newer (if not already installed): `brew install maven`

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
