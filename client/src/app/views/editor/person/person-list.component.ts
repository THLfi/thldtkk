import {of as observableOf, Subject} from 'rxjs';

import {catchError, switchMap, distinctUntilChanged, debounceTime} from 'rxjs/operators';
import {Component, OnInit} from '@angular/core';
import {ConfirmationService} from 'primeng/primeng'
import {TranslateService} from '@ngx-translate/core';

import {Person} from "../../../model2/person";
import {PersonService} from "../../../services-common/person.service";

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
    private translateService: TranslateService
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
    this.personService.getRoleReferences(person).subscribe(personInRoles => {
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
    searchTerms.pipe(debounceTime(this.searchDelay),
      distinctUntilChanged(),
      switchMap(term => {
        this.loadingPeople = true;
        return this.personService.search(term)
      }),
      catchError(error => {
        this.initSearchSubscription(searchTerms)
        return observableOf<Person[]>([])
      }),)
      .subscribe(people => {
        this.people = people
        this.loadingPeople = false
      })
  }

}
