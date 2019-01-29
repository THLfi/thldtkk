import {Component, OnInit} from '@angular/core';
import {ConfirmationService} from 'primeng/primeng'
import {Observable, Subject} from 'rxjs';
import {TranslateService} from '@ngx-translate/core';

import {Person} from "../../../model2/person";
import {PersonInRole} from "../../../model2/person-in-role";
import {PersonService} from "../../../services-common/person.service";
import {LangPipe} from '../../../utils/lang.pipe'

import 'rxjs/add/observable/of';
import 'rxjs/add/operator/catch';
import 'rxjs/add/operator/debounceTime';
import 'rxjs/add/operator/distinctUntilChanged';
import 'rxjs/add/operator/switchMap';

@Component({
  templateUrl: './person-list.component.html',
  styleUrls: ['./person-list.component.css']
})
export class PersonListComponent implements OnInit {

  people: Person[] = []
  editPerson: Person

  searchTerms: Subject<string>
  searchInput: string

  loadingPeople: boolean

  readonly searchDelay = 300;
  readonly personRemoveConfirmationKey: string = 'confirmRemovePersonModal.message'

  constructor(
    private personService: PersonService,
    private confirmationService: ConfirmationService,
    private translateService: TranslateService,
    private langPipe: LangPipe,
  ) {
    this.searchTerms = new Subject<string>()
  }

  ngOnInit(): void {
    this.loadingPeople = true
    this.personService.getAll()
      .subscribe(people => {
        this.people = people
        this.loadingPeople = false
      })
    this.initSearchSubscription(this.searchTerms)
  }

  showEditPersonModal(person: Person) {
    this.editPerson = person
  }

  closeEditPersonModal() {
    this.editPerson = null
    this.refreshPeople()
  }

  savePerson(person: Person) {
    this.personService.save(person).subscribe(() => {
      this.editPerson = null
      this.refreshPeople()
    })
  }

  showAddNewPersonModal(): void {
    this.editPerson = this.personService.initNew()
  }

  confirmRemovePerson(person: Person): void {
    Observable.forkJoin(
      this.personService.getRoleReferences(person)
    ).subscribe(data => {
      let personInRoles: PersonInRole[] = data[0]
      let name:string = person.lastName ? person.firstName + ' ' + person.lastName : person.firstName

      let translationParams: {} = {
        personName: name,
        personInRoleCount: personInRoles.length
      }

      this.translateService.get(this.personRemoveConfirmationKey, translationParams).subscribe(confirmationMessage => {
        this.confirmationService.confirm({
          message: confirmationMessage,
          accept: () => {
            this.personService.delete(person.id).subscribe(() => this.refreshPeople())
          }
        })
      })
    })
  }

  refreshPeople(): void {
    this.instantSearchPeople(this.searchInput)
  }

  searchPeople(literalSearchTerms: string): void {
    this.searchTerms.next(literalSearchTerms)
  }

  instantSearchPeople(literalSearchTerms: string): void {
    this.loadingPeople = true
    this.personService.search(literalSearchTerms).subscribe(people => {
      this.people = people
      this.loadingPeople = false
    })
  }

  private initSearchSubscription(searchTerms: Subject<string>): void {
    searchTerms.debounceTime(this.searchDelay)
      .distinctUntilChanged()
      .switchMap(term => {
        this.loadingPeople = true;
        return this.personService.search(term)
      })
      .catch(error => {
        this.initSearchSubscription(searchTerms)
        return Observable.of<Person[]>([])
      })
      .subscribe(people => {
        this.people = people
        this.loadingPeople = false
      })
  }

}
