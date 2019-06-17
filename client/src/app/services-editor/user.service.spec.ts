import { TestBed, tick, fakeAsync } from '@angular/core/testing';

import { CurrentUserService } from './user.service';
import { TranslateModule } from '@ngx-translate/core';
import { Http } from '@angular/http';
import { LangPipe } from 'app/utils/lang.pipe';
import { Router } from '@angular/router';
import { asyncData } from 'app/utils/async-observable-helpers';

const routerSpy = jasmine.createSpyObj('router', ['navigate']);
const httpMock = jasmine.createSpyObj('HttpModule', ['get', 'post'])
const mockUserResponse = {
  json: () => ({ userName: "testUser" })
};
let service: CurrentUserService;

describe('CurrentUserService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        TranslateModule.forRoot(),
      ],
      providers: [
        CurrentUserService,
        LangPipe,
        { provide: Router, useValue: routerSpy },
        { provide: Http, useValue: httpMock }
      ]
    });
    httpMock.get.and.returnValue(asyncData(mockUserResponse));
    service = TestBed.get(CurrentUserService);
  });

  it('should redirect to login page on logout', fakeAsync(() => {
        httpMock.post.and.returnValue(asyncData(true));
        httpMock.get.and.returnValue(asyncData(mockUserResponse));
        service.logout();
        tick(1);
        expect(routerSpy.navigate.calls.count()).toBe(1, 'router redirected once');
        expect(routerSpy.navigate.calls.first().args[1].queryParams.logout).toBeTruthy();
    }));
  });
