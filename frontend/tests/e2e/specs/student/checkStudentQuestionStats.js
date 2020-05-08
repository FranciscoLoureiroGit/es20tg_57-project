describe('Check Student Question status', () => {
  beforeEach(() => {
    cy.demoStudentLogin();
  });

  it('login checks the student questions status', () => {
    cy.checkStudentQStats();
    cy.wait(2000);
  });
});
