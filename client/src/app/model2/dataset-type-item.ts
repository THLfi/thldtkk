import { Node } from './node'
import { SelectItem } from 'primeng/primeng';

export class DatasetTypeItem implements SelectItem {
  
    label: string
    value: string

    constructor(selectItemLabel: string, datasetTypeId: string) {
        this.label = selectItemLabel
        this.value = datasetTypeId
  }


  public static compare(compare: DatasetTypeItem, to: DatasetTypeItem): number {
        
        if(compare.label > to.label) {
            return 1;
        }

        if(compare.label < to.label) {
            return -1;
        }

        return 0;
  }
}
