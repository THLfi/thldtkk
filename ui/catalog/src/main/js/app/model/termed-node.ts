export interface TermedNode {
    id: string;
    properties: { [key:string]: Property[] };
    references: { [key:string]: Reference[] };
}

interface Property {
    lang: string;
    value: string;
}

interface Reference {
    id: string;
}
