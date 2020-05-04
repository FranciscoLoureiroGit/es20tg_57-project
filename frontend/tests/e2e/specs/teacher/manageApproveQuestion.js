describe('Administration walkthrough', () => {
  before(() => {
    cy.demoStudentLogin();
    cy.get('[data-cy="questions"]').click();
    cy.get('[data-cy="my-questions-button"]').click();

    cy.createQuestionByStudent('NewTestForApprove', 'Testing content', 'Answer 1', 'Answer 2', 'Answer 3', 'Answer 4');
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

  it('login approves question with AVAILABLE status', () => {
    cy.approveQuestionTest('NewTestForApprove');
  });
});
