import { ActivatedRoute, Router } from '@angular/router';
import { Component, OnInit } from '@angular/core';

import { DataSet } from '../../model/data-set';
import { DataSetService } from '../../services/data-set.service';
import { Organization } from "../../model/organization";
import { Observable } from "rxjs";

@Component({
  templateUrl: './data-set-edit.component.html'
})
export class DataSetEditComponent implements OnInit {

  dataSet: DataSet;
  ownerOrganization: Organization;

  constructor(
    private dataSetService: DataSetService,
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
      this.dataSetService.getDataSetOwners(datasetId)
    ).subscribe(
      data => {
        this.dataSet = data[0],
        this.ownerOrganization = data[1][0]
      }
    );
  }

  save() {
    this.dataSetService.updateDataSet(this.dataSet)
      .subscribe(dataSet => {
        this.dataSet = dataSet;
        this.goBack();
      });
  }

  goBack() {
    this.router.navigate(['/datasets', this.dataSet.id]);
  }
}
