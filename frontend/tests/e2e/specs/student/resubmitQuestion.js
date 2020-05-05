describe('Resubmit question walkthrough', () => {

  let date = Date.now();
  before(() => {
    cy.demoStudentLogin();
    cy.get('[data-cy="questions"]').click();
    cy.get('[data-cy="my-questions-button"]').click();

    cy.createQuestionByStudent('New Testing Questions Reject'+String(date), 'Testing content', 'Answer 1', 'Answer 2', 'Answer 3', 'Answer 4')
    cy.contains('Logout').click();

  });

  beforeEach(() => {
    cy.demoTeacherLogin();
  });

  afterEach(() => {
    cy.contains('Logout').click()
  });

  it('student resubmit the question with success', () => {
    cy.get('[data-cy="TeacherManagementButton"]').click();
    cy.get('[data-cy="StudentQuestionsButton"]').click();
    cy.changeQuestionToRemovedTest('New Testing Questions Reject'+String(date));
    cy.contains('Logout').click()

    cy.demoStudentLogin();
    cy.get('[data-cy="questions"]').click();
    cy.get('[data-cy="my-questions-button"]').click();
    cy.resubmitQuestionSuccessAndInsuccess('New Testing Questions Reject'+String(date), 'New testing content'+String(date), 'Answer 1', 'Answer 2', 'Answer 3', 'Answer 4');
  });

  /*it('student has no success to resubmit the question', () =>{
    cy.checkQuestionByStudent('Testing Questions '+String(date))
  })*/


});
