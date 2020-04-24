describe('Student clarifications walkthrough', () => {
  beforeEach(() => {
    cy.demoStudentLogin();
  });

  afterEach(() => {
    cy.wait(1000);
    cy.get('[data-cy="logoutButton"]').click();
  });

  it('login creates a clarification request from quiz', () => {
    cy.get('[data-cy="quizzesButton"]').click();
    cy.createAndAnswerQuiz();
    cy.createClarificationRequestFromQuiz('TITLE', 'DESCRIPTION');
  });

  it('login creates a clarification request from solved', () => {
    cy.get('[data-cy="quizzesButton"]').click();
    cy.createAndAnswerQuiz();
    cy.get('[data-cy="logoutButton"]').click();
    cy.demoStudentLogin();
    cy.get('[data-cy="quizzesButton"]').click();
    cy.createClarificationRequestFromSolved('TITLE', 'DESCRIPTION');
  });

  it('login creates two clarification requests on same questionAnswer', () => {
    cy.get('[data-cy="quizzesButton"]').click();
    cy.createAndAnswerQuiz();
    cy.createClarificationRequestFromQuiz('TITLE', 'DESCRIPTION');

    cy.log('try to create on same answer');

    cy.createClarificationRequestFromQuiz('TITLE', 'DESCRIPTION');

    cy.closeErrorMessage();
  });

  it('login shows clarifications a visualizes a specific one by description', () => {
    let variation = Date.now(); // used for always having a different clarification title
    cy.get('[data-cy="quizzesButton"]').click();
    cy.createAndAnswerQuiz();
    cy.createClarificationRequestFromQuiz(
      'TITLE_' + String(variation),
      'DESCRIPTION'
    );
    cy.showClarificationRequests();
    cy.openClarificationDescription('TITLE_' + String(variation));
  });

  it('login shows clarifications a visualizes a specific one by question', () => {
    let variation = Date.now(); // used for always having a different clarification title
    cy.get('[data-cy="quizzesButton"]').click();
    cy.createAndAnswerQuiz();
    cy.createClarificationRequestFromQuiz(
      'TITLE_' + String(variation),
      'DESCRIPTION'
    );
    cy.showClarificationRequests();
    cy.openClarificationQuestion('TITLE_' + String(variation));
  });
});
