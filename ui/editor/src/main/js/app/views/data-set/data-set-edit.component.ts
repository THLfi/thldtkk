import { ActivatedRoute, Router } from '@angular/router';
import { Component, OnInit } from '@angular/core';
import { Observable } from "rxjs";

import { DataSet } from '../../model/data-set';
import { DataSetService } from '../../services/data-set.service';
import { Organization } from "../../model/organization";
import { Population } from "../../model/population";
import { PopulationService } from "../../services/population.service";

@Component({
  templateUrl: './data-set-edit.component.html'
})
export class DataSetEditComponent implements OnInit {

  dataSet: DataSet;
  ownerOrganization: Organization;
  population: Population;

  constructor(
    private dataSetService: DataSetService,
    private populationService: PopulationService,
    private route: ActivatedRoute,
    private router: Router
  ) { }

  ngOnInit() {
    this.getDataSet();
  }

  private getDataSet() {
    const datasetId = this.route.snapshot.params['id'];
    Observable.forkJoin(
      this.dataSetService.getDataSet(datasetId),
      this.dataSetService.getDataSetOwners(datasetId),
      this.dataSetService.getDataSetPopulations(datasetId)
    ).subscribe(
      data => {
        this.dataSet = data[0],
        this.ownerOrganization = data[1][0],
        this.population = this.initPopulationFields(data[2][0]);
      }
    );
  }

  private initPopulationFields(population: Population): Population {
    if (!population) {
      population = {
        id: null,
        type: {
          id: null,
          graph: {
            id: null
          }
        },
        properties: {
          'prefLabel': [
            {
              lang: 'fi',
              value: null
            }
          ]
        },
        references: {}
      };
    }
    else if (!population.properties['prefLabel'] || !population.properties['prefLabel'][0]) {
      population.properties['prefLabel'] = [
        {
          lang: 'fi',
          value: null
        }
      ];
    }
    return population;
  }

  save() {
    this.populationService.savePopulation(this.population)
      .subscribe(savedPopulation => {
          this.population = this.initPopulationFields(savedPopulation);

          this.dataSet.references['population'] = [ savedPopulation ];

          this.dataSetService.saveDataSet(this.dataSet)
            .subscribe(savedDataSet => {
              this.dataSet = savedDataSet;
              this.goBack();
            });
        });
  }

  goBack() {
    this.router.navigate(['/datasets', this.dataSet.id]);
  }
}
