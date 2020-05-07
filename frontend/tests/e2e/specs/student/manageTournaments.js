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

  it('creates a tournament with illegal number of questions', () => {
    cy.goToOpenQuestionsTournaments();
    cy.contains('New Tournament').click();
    cy.createQuestionsTournament('d', 'Case Studies');
  });

  it('creates a tournament with illegal dates', () => {
    cy.goToOpenQuestionsTournaments();
    cy.contains('New Tournament').click();
    cy.createQuestionsTournamentWithIllegalDates('20', 'Case Studies');
  });

  it('login registers in a tournament', () => {
    cy.goToOpenQuestionsTournaments();
    cy.registerStudentInTournament('1');
  });

  it('login goes to registered tournaments', () => {
    cy.goToOpenQuestionsTournaments();
    cy.contains('New Tournament').click();
    cy.createQuestionsTournament('12', 'Case Studies');
    cy.goToRegisteredQuestionsTournaments();
    cy.goToOpenQuestionsTournaments();
  });

  it('cancels a tournament', () => {
    cy.goToOpenQuestionsTournaments();
    cy.contains('New Tournament').click();
    cy.createQuestionsTournament('12', 'Case Studies');
    cy.cancelTournament();
  });
});
