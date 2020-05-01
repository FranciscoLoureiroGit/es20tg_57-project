describe('Tournament walkthrough', () => {
  beforeEach(() => {
    cy.demoStudentLogin();
  });

  afterEach(() => {
    cy.contains('Logout').click();
  });


  // no error cases, for example invalid user input or missing information
  it('creates a tournament', () => {
    cy.goToOpenQuestionsTournaments();
    cy.contains('New Tournament').click();
    cy.createQuestionsTournament('12', 'Case Studies');
  });

  it('login registers in a tournament', () => {
    cy.goToOpenQuestionsTournaments();
    cy.registerStudentInTournament('1');
  });
});
