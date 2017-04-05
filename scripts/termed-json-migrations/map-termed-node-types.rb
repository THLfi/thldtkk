require 'json'

def migrate_types(nodes, source_type_id, target_type_id)
	nodes.each { |n|
		n["type"]["id"] = target_type_id if n["type"]["id"] == source_type_id

		n["references"].each { |key, values|
			values.each { |r| 
				r["type"]["id"] = target_type_id if r["type"]["id"] == source_type_id
			}
		}
		
		n["referrers"].each { |key, values|
			values.each { |r| 
				r["type"]["id"] = target_type_id if r["type"]["id"] == source_type_id
			}
		}
	}
end

unless ARGV.length == 2
	puts "Usage: ruby #{$PROGRAM_NAME} <source-type-id> <target-type-id> < cat <nodes-json-file-name>\n"
	exit
end

begin
	STDOUT.write(
		JSON.pretty_generate(
			migrate_types(JSON.parse(STDIN.read), ARGV[0], ARGV[1])))
rescue Errno::EPIPE
	exit(74)
end
