describe('Teacher clarifications walkthrough', () => {
  let variation = Date.now()
  beforeEach(() => {
    cy.demoTeacherLogin()
  })

  afterEach(() => {
    cy.wait(1000)
    cy.get('[data-cy="logoutButton"]').click()
  })

  it('teacher answers existing clarification request', () => {
    cy.addClarificationQA(variation)
  });

  it('teacher checks for private clarification request and make it public', () => {
    cy.changeClarificationPrivacy('TITLE_' + String(variation))
  })

  it('teacher checks  for public clarification request and make it private', () => {
    cy.changeClarificationPrivacy('TITLE_' + String(variation))
  })

  it('student checks for all public clarifications', () => {
    cy.changeClarificationPrivacy('TITLE_' + String(variation)) // make it public again
    cy.get('[data-cy="logoutButton"]').click()
    cy.demoStudentLogin()
    cy.showPublicClarifications();
    cy.openClarificationDescription('TITLE_' + String(variation));
  })

  it('student checks for a specific question public clarifications', () => {
    cy.get('[data-cy="logoutButton"]').click()
    cy.demoStudentLogin()
    cy.get('[data-cy="quizzesButton"]').click();
    cy.openQuestionPublicClarifications();
  })

});
