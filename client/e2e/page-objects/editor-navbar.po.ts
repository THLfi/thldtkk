import { browser, element, by } from 'protractor';

export class EditorNavbar {
  studiesLink = element(by.id('studies-link'));
  instanceVariablesLink = element(by.id('instance-variables-link'));

  adminMenuToggle = element(by.id('admin-menu-toggle'));

  adminMenuVariablesLink = element(by.id('admin-menu-variables-link'));
  adminMenuUnitTypesLink = element(by.id('admin-menu-unit-types-link'));
  adminMenuUniversesLink = element(by.id('admin-menu-universes-link'));
  adminMenuCodelistsLink = element(by.id('admin-menu-codelists-link'));
  adminMenuOrganizationsLink = element(by.id('admin-menu-organizations-link'));
  adminMenuPeopleLink = element(by.id('admin-menu-people-link'));

  userMenuToggle = element(by.id('user-menu-toggle'));
  userMenuLogoutLink = element(by.id('user-menu-logout-link'));

  logout() {
    this.userMenuToggle.click();
    this.userMenuLogoutLink.click();
  }
}
