describe('Student clarifications walkthrough', () => {
  beforeEach(() => {
    cy.demoStudentLogin()
  })

  afterEach(() => {
    cy.contains('Logout').click()
  })

  it('login creates a clarification request from quiz', () => {
    cy.createAndAnswerQuiz()
    cy.createClarificationRequestFromQuiz('TITLE', 'DESCRIPTION')
  });

  it('login creates a clarification request from solved', () => {
    cy.createAndAnswerQuiz()
    cy.contains('Logout').click()
    cy.createClarificationRequestFromQuiz('TITLE', 'DESCRIPTION')
  });

  it('login creates two course executions and deletes it', () => {
    cy.createAndAnswerQuiz()
    cy.contains('Logout').click()
    cy.createClarificationRequestFromSolved('TITLE', 'DESCRIPTION')

    cy.log('try to create on same answer')
    cy.createClarificationRequestFromSolved('TITLE', 'DESCRIPTION')

    cy.closeErrorMessage()

    cy.log('close dialog')
    cy.get('[data-cy="cancelButton"]').click()
  });

  it('login shows all student clarifications', () => {
    cy.createAndAnswerQuiz()
    cy.createClarificationRequestFromQuiz('TITLE', 'DESCRIPTION')
    cy.showClarificationRequests()
  });

  it('login shows clarifications a visualizes one', () => {
    let variation = Date.now() // used for always having a different clarification title
    cy.createAndAnswerQuiz()
    cy.createClarificationRequestFromQuiz('TITLE_' + String(variation), 'DESCRIPTION')
    cy.showClarificationRequests()
    cy.openClarificationDescription('TITLE_' + String(variation))
  });

});
