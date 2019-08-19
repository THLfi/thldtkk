import { browser, element, by } from 'protractor';

export class EditorStudyList {

  get() {
    return browser.get('/editor/studies');
  }
}
