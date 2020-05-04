describe('Changing status of questions with Teacher walkthrough', () => {
  let date = Date.now();
  before(() => {
    cy.demoStudentLogin();
    cy.get('[data-cy="questions"]').click();
    cy.get('[data-cy="my-questions-button"]').click();

    cy.createQuestionByStudent('New Testing Questions '+String(date), 'Testing content', 'Answer 1', 'Answer 2', 'Answer 3', 'Answer 4')
    cy.contains('Logout').click();
  });

  beforeEach(() => {
    cy.demoTeacherLogin();
    cy.get('[data-cy="TeacherManagementButton"]').click();
    cy.get('[data-cy="StudentQuestionsButton"]').click();
  });

  afterEach(() => {
    cy.contains('Logout').click();
  });

  it('login changes the state of question to AVAILABLE', () => {
    cy.changeQuestionToAvailableTest('New Testing Questions '+String(date))
  });

  it('login changes the state to REMOVED and puts a justification', () => {
    cy.changeQuestionToRemovedTest('New Testing Questions '+String(date),'removejustification')
  });

  it('login changes the state of question to DISABLED without and with a justification', () => {
    cy.changeQuestionToDisabledTest('New Testing Questions '+String(date),'otherjustification')
  });
});
