import { browser, element, by } from 'protractor';

class User {
  constructor(public username: string, public password: string) { }
}

export class Users {
  static readonly ADMIN = new User('admin', 'admin')
}

export class EditorLogin {
  usernameField = element(by.id('username'));
  passwordField = element(by.id('password'));
  thlLoginExpansionButton = element(by.id('thl-login-expansion-button'));
  loginButton = element(by.id('login-button'));

  get() {
    return browser.get('/editor');
  }

  login(user: User) {
    this.thlLoginExpansionButton.click();
    this.usernameField.sendKeys(user.username);
    this.passwordField.sendKeys(user.password);
    this.loginButton.click();
  }

  getParagraphText() {
    return element(by.css('app-root h1')).getText();
  }

  getDatasets() {
    return element.all(by.css('ul li'));
  }
}
