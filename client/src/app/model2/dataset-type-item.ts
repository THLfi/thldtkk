import { Node } from './node'
import { LangValues } from './lang-values';
import { SelectItem } from 'primeng/primeng';
import { DatasetType} from './dataset-type'

export class DatasetTypeItem implements SelectItem {
  
    label: string
    value: string

    constructor(selectItemLabel: string, datasetTypeId: string) {
        this.label = selectItemLabel
        this.value = datasetTypeId
  }

}
