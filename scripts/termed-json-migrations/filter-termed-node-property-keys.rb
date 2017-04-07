require 'json'

def migrate_property_keys(nodes, source_type_id, selected_property_keys)
	nodes.each { |n|
		next if n["type"]["id"] != source_type_id

		filtered_properties = {}

		n["properties"].each { |key, values|
			filtered_properties[key] = values if selected_property_keys.include? key
		}

		n["properties"] = filtered_properties
	}
end

unless ARGV.length >= 1
	puts "Usage: ruby #{$PROGRAM_NAME} <source-type-id> <select-property-key-1> ... <select-property-key-n> < cat <nodes-json-file-name>\n"
	exit
end

begin
	STDOUT.write( 
		JSON.pretty_generate(
			migrate_property_keys(JSON.parse(STDIN.read), ARGV[0], ARGV[1, ARGV.length])))
rescue Errno::EPIPE
	exit(74)
end
