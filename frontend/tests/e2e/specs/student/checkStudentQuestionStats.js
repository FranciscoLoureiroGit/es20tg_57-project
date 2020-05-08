describe('Check Student Question status', () => {

  beforeEach(() => {
    cy.demoStudentLogin()
  })

/*  afterEach(() => {
    cy.contains('Logout').click()
  })*/

  it('login checks the student questions status', () => {
    cy.checkStudentQStats()
  });
});

