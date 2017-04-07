require 'json'

def migrate_property_keys(nodes, source_type_id, source_property_key, target_property_key)
	nodes.each { |n|
		next if n["type"]["id"] != source_type_id

		mapped_properties = {}

		n["properties"].each { |key, values|
			mapped_properties[key == source_property_key ? target_property_key : key] = values
		}

		n["properties"] = mapped_properties
	}
end

unless ARGV.length == 3
	puts "Usage: ruby #{$PROGRAM_NAME} <source-type-id> <source-property-key> <target-property-key> < cat <nodes-json-file-name>\n"
	exit
end

begin
	STDOUT.write( 
		JSON.pretty_generate(
			migrate_property_keys(JSON.parse(STDIN.read), ARGV[0], ARGV[1], ARGV[2])))
rescue Errno::EPIPE
	exit(74)
end
