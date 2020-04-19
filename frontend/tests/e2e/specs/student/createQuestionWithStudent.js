describe('Student question walkthrough', () => {
  beforeEach(() => {
    cy.demoStudentLogin()
  })

  afterEach(() => {
    cy.contains('Logout').click()
  })

  it('login creates and deletes a question', () => {
    cy.createQuestionByStudent('Testing Questions', 'Testing content', 'Answer 1', 'Answer 2', 'Answer 3', 'Answer 4')
  });

  it('once created the question, the student can list and choose his question', () =>{
    cy.checkQuestionByStudent('Testing Questions')
  })


});
