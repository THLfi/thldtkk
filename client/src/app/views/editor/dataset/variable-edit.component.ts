import {Component, OnInit, Input, Output, EventEmitter} from '@angular/core';

import {Variable} from '../../../model2/variable'
import {VariableService} from '../../../services2/variable.service';
import {TranslateService} from '@ngx-translate/core';

@Component({
    templateUrl: './variable-edit.component.html',
    selector: 'edit-variable',
})
export class VariableEditComponent implements OnInit {

    variable: Variable
    language: string

    savingInProgress: boolean = false

    @Input() showTitle: boolean = true
    @Output() variableSaved:EventEmitter<Variable>

    showDialog: boolean = false
    constructor(
        private variableService: VariableService,
        private translateService: TranslateService
    ) {
        this.language = translateService.currentLang
        this.variableSaved = new EventEmitter<Variable>()
    }
    ngOnInit(): void {
            const variable = this.createEmptyVariable()
            this.initVariable(variable)
            this.variable = variable
    }

    private initVariable(variable: Variable): void {
      this.initProperties(variable, ['prefLabel', 'description'])
    }

    private initProperties(variable: Variable, properties: string[]): void {
        properties.forEach(property => {
            if (!variable[property]) {
                variable[property] = {}
            }
            if (!variable[property][this.language]) {
                variable[property][this.language] = ''
            }
        })
    }

    private createEmptyVariable(): Variable {
        const variable = {
                id: null,
                prefLabel: null,
                description: null,
            }
        return variable;
    }

    clear(): void {
        const variable = this.createEmptyVariable()
        this.initVariable(variable)
        this.variable = variable
    }

    save(): void {
        this.savingInProgress = true

        this.variableService.saveVariable(this.variable)
            .finally(() => {
              this.savingInProgress = false
            })
            .subscribe(variable => {
                this.variable = variable
                this.variableSaved.emit(variable)
                this.goBack()
            })
    }

    show(): void {
        this.showDialog = true;
    }

    hide(): void {
        this.showDialog = false
    }

    goBack(): void {
       this.hide()
    }

}
