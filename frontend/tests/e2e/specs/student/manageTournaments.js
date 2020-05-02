describe('Tournament walkthrough', () => {
  beforeEach(() => {
    cy.demoStudentLogin();
  });

  afterEach(() => {
    cy.contains('Logout').click();
  });

  it('creates a tournament', () => {
    cy.goToOpenQuestionsTournaments();
    cy.contains('New Tournament').click();
    cy.createQuestionsTournament('12', 'Case Studies');
  });

  it('login registers in a tournament', () => {
    cy.goToOpenQuestionsTournaments();
    cy.registerStudentInTournament('1');
  });

  it('login goes to registered tournaments', () => {
    cy.goToRegisteredQuestionsTournaments();
    cy.goToOpenQuestionsTournaments();
  });

  it('cancels a tournament', () => {
    cy.goToOpenQuestionsTournaments();
    cy.cancelTournament();
  });
});
