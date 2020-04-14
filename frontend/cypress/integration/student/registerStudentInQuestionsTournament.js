describe('Questions Tournament Registration walkthrough', () => {
  beforeEach(() => {
    cy.demoStudentLogin()
  })

  afterEach(() => {
    cy.contains('Logout').click()
  })

  it('login registers in a tournament', () => {
    cy.goToOpenQuestionsTournaments()
    cy.registerStudentInTournament('1012')
  });
});