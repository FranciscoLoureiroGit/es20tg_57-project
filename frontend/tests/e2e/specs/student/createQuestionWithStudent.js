describe('Student question walkthrough', () => {
  let date = Date.now()

  beforeEach(() => {
    cy.demoStudentLogin()
    cy.get('[data-cy="questions"]').click()
    cy.get('[data-cy="my-questions-button"]').click()
  })

  afterEach(() => {
    cy.contains('Logout').click()
  })

  it('login creates and deletes a question', () => {
    cy.createQuestionByStudent('Testing Questions '+String(date), 'Testing content', 'Answer 1', 'Answer 2', 'Answer 3', 'Answer 4')
  });

  it('once created the question, the student can list and choose his question', () =>{
    cy.checkQuestionByStudent('Testing Questions '+String(date))
  })


});
