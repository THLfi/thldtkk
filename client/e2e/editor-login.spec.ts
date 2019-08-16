import { EditorLogin, Users } from './page-objects/editor-login.po';
import { browser } from 'protractor';
import { EditorNavbar } from './page-objects/editor-navbar.po';

describe('Test logging in and logging out', () => {
  let page: EditorLogin;

  beforeEach(() => {
    page = new EditorLogin();
  });

  afterEach(() => {
    browser.executeScript('window.sessionStorage.clear();');
    browser.executeScript('window.localStorage.clear();');
    browser.driver.manage().deleteAllCookies();
  })

  it('should log in with admin credentials', () => {
    page.get();
    page.login(Users.ADMIN);
    const editorNavbar = new EditorNavbar();
    expect(editorNavbar.studiesLink.isPresent()).toBe(true);
  });

  it('should redirect back to log in page after log out', () => {
    page.get();
    page.login(Users.ADMIN);
    const editorNavbar = new EditorNavbar();
    editorNavbar.logout();
    expect(page.usernameField.isPresent()).toBe(true);
  });
});
