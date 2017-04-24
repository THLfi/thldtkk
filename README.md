# Development

## Starting Metadata API module

    cd api/metadata
    mvn spring-boot:run

Metadata API can be accessed via `http://localhost:8080/metadata-api/`.

## Starting Catalog UI module

Note. Metadata API module must be started before starting Catalog UI.

Install NodeJS (if not already installed): `brew install node`

Install Angular CLI (if not already installed): `npm install -g @angular/cli`

Go to Catalog UI module: `cd ui/catalog`

Install dependencies (first time only): `npm install`

Start dev server: `npm start`

Catalog UI can be accessed via `http://localhost:8081/`.
