describe('Tournament walkthrough', () => {
  beforeEach(() => {
    cy.demoStudentLogin()
  })

  afterEach(() => {
    cy.contains('Logout').click()
  })

  it('creates a tournament', () => {
    cy.goToOpenQuestionsTournaments()
    cy.contains('New Tournament').click()
    cy.createQuestionsTournament('12','Case Studies')
  });
});