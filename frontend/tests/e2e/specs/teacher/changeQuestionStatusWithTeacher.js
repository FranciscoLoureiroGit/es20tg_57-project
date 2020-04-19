describe('Administration walkthrough', () => {
  beforeEach(() => {
    cy.demoTeacherLogin()
  })

  afterEach(() => {
    cy.contains('Logout').click()
  })


  it('login changes the state of question to AVAILABLE', () => {
    cy.changeQuestionToAvailableTest('AvailableTest')
  });

  it('login changes the state to REMOVED and puts a justification', () => {
    /* falta o teste para criar a pergunta com o titulo 'RemoveTest' */
    cy.changeQuestionToRemovedTest('RemovedTest','removejustification')
  });

  it('login changes the state of question to DISABLED without and with a justification', () => {
    cy.changeQuestionToDisabledTest('DisabledTest','otherjustification')
  });

});
