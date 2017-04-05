#!/bin/sh

cat data.json | \
	ruby ../../termed-json-migrations/map-termed-node-types.rb aineisto DataSet | \
	ruby ../../termed-json-migrations/map-termed-node-property-keys.rb DataSet kuvaus description | \
	ruby ../../termed-json-migrations/filter-termed-node-property-keys.rb DataSet prefLabel description | \
	ruby ../../termed-json-migrations/filter-termed-node-reference-keys.rb DataSet > \
	migrated.json
