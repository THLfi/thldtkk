#!/bin/sh

cat data.json | \
  ruby ../../termed-json-migrations/map-termed-node-types.rb aineisto DataSet | \
  ruby ../../termed-json-migrations/map-termed-node-property-keys.rb DataSet kuvaus description | \
  ruby ../../termed-json-migrations/map-termed-node-reference-value-to-property-value.rb \
    DataSet julkaistavissaJulkisessaVerkkopalvelussa bf61aedc-463e-4567-857f-86bd42473edf published "true" "" "^(true|false)$"  | \
  ruby ../../termed-json-migrations/map-termed-node-reference-value-to-property-value.rb \
    DataSet julkaistavissaJulkisessaVerkkopalvelussa 1d639d96-3c0e-4a95-9158-42e2f98b5c0b published "false" "" "^(true|false)$" | \
  ruby ../../termed-json-migrations/filter-termed-node-property-keys.rb DataSet prefLabel description published | \
  ruby ../../termed-json-migrations/filter-termed-node-reference-keys.rb DataSet > \
  migrated.json
