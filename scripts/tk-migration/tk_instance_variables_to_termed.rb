require 'csv'
require 'json'
require 'securerandom'

graph_id = '6d403ed1-341a-419b-9d2a-1c8de7e1ee84'
dataset = { references: { instanceVariable: [] } }
variables = []

def parse_date(d)
  if match = d.match(/(\d{2})\/(\d{2})\/(\d{4})/)
    day, month, year = match.captures
    return "#{year}-#{month}-#{day}"
  else
    return ""
  end
end

CSV.foreach("tk-variables.csv", headers: true) { |row|
  variable_id = SecureRandom.uuid

  dataset[:references][:instanceVariable] << {
    id: variable_id,
    type: {
      id: 'InstanceVariable',
      graph: {
        id: graph_id
      }
    }
  }

  variables << {
    id: variable_id,
    code: "FOLK_19872014_VAA_tutk16_1_#{row['Muuttujan nimilyhenne/TechnicalName'].to_s}",
    properties: {
      prefLabel: [{ lang: 'fi', value: row['Muuttujan nimi/Name'].to_s }],
      description: [{ lang: 'fi', value: row['Muuttujan kuvaus / Instance variable: Description'].to_s }],
      referencePeriodStart: [{ lang:'',  value: parse_date(row['ReferencePeriod start'].to_s), regex: "^\\d{4}-\\d{2}-\\d{2}$"}],
      referencePeriodEnd: [{ lang:'',  value: parse_date(row['ReferencePeriod end'].to_s), regex: "^\\d{4}-\\d{2}-\\d{2}$"}],
    }
  }
}

File.open("tk-dataset.json", 'w') { |file| file.write(JSON.pretty_generate(dataset)) }
File.open("tk-variables.json", 'w') { |file| file.write(JSON.pretty_generate(variables)) }
