import { FrontPage } from './app.po';

describe('basic tests for catalog front page', () => {
  let page: FrontPage;

  beforeEach(() => {
    page = new FrontPage();
  });

  it('should display app name', () => {
    page.navigateTo();
    expect(page.getParagraphText()).toEqual('Aineistot');
  });

  it('should list datasets', () => {
     page.navigateTo();
     expect(page.getDatasets().count()).toBeGreaterThan(0);
  });

});
