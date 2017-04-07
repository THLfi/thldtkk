require 'json'

def migrate_property_keys(nodes, source_type_id, selected_reference_keys)
	nodes.each { |n|
		next if n["type"]["id"] != source_type_id

		filtered_references = {}

		n["references"].each { |key, values|
			filtered_references[key] = values if selected_reference_keys.include? key
		}

		n["references"] = filtered_references
	}
end

unless ARGV.length >= 1
	puts "Usage: ruby #{$PROGRAM_NAME} <source-type-id> <select-ref-key-1> ... <select-ref-key-n> < cat <nodes-json-file-name>\n"
	exit
end

begin
	STDOUT.write( 
		JSON.pretty_generate(
			migrate_property_keys(JSON.parse(STDIN.read), ARGV[0], ARGV[1, ARGV.length])))
rescue Errno::EPIPE
	exit(74)
end
