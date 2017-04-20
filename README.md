# Development

## Starting Metadata API module

    cd api/metadata
    mvn spring-boot:run

Metadata API can be accessed via `http://localhost:8080/metadata-api/`.

## Starting Catalog UI module

Note. Metadata API module must be started before starting Catalog UI.

    cd ui/catalog
    mvn spring-boot:run

Catalog UI API can be accessed via `http://localhost:8081/catalog/`.
