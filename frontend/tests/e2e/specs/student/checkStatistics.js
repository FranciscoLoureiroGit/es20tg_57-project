describe('Statistics walkthrough', () => {
  beforeEach(() => {
    cy.demoStudentLogin();
  });

  afterEach(() => {
    cy.contains('Logout').click();
  });

  it('checks tournament stats', () => {
    cy.goToDashboard();
  });
});