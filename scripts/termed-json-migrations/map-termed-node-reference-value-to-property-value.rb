require 'json'

def migrate(nodes, src_type_id, src_ref_key, src_ref_val_id,
			target_prop_key, target_prop_val, target_prop_lang, target_prop_regex)

	nodes.each { |n|
		next if n["type"]["id"] != src_type_id

		n["references"].each { |key, vals|
			vals.each { |val|
				if key == src_ref_key and val["id"] == src_ref_val_id
					n["properties"][target_prop_key] ||= []
					n["properties"][target_prop_key] << {
						value: target_prop_val,
						lang: target_prop_lang,
						regex: target_prop_regex }
				end
			}
		}
	}
end

unless ARGV.length == 7
	puts "Usage: ruby #{$PROGRAM_NAME} "\
			"<src-type-id> <src-ref-key> <src-ref-val-id> "\
			"<target-property-key> <target-property-val> <target-property-lang> <target-property-regex> "\
			"< cat <nodes-json-file-name>\n"
	exit
end

begin
	STDOUT.write(JSON.pretty_generate(
		migrate(JSON.parse(STDIN.read),
			ARGV[0],
			ARGV[1],
			ARGV[2],
			ARGV[3],
			ARGV[4],
			ARGV[5],
			ARGV[6])))
rescue Errno::EPIPE
	exit(74)
end
