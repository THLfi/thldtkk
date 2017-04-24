export interface DataSet {
    id: string;
    properties: Properties;
}

interface Properties {
    prefLabel: Property[];
    description: Property[];
}

interface Property {
    lang: string;
    value: string;
}