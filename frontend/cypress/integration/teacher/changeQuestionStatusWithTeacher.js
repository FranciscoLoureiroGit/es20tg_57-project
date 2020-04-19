describe('Administration walkthrough', () => {
  beforeEach(() => {
    cy.demoTeacherLogin()
  })

  afterEach(() => {
    cy.contains('Logout').click()
  })

  /*NOVOS*/
  it('login changes the state of question to AVAILABLE', () => {
    cy.changeQuestionToAvailableTest('TitleTest','justification')
  });

  it('login changes the state of question to DISABLED without and with justification', () => {
    cy.changeQuestionToDisabledTest('OtherTest','otherjustification')
  });

/*  it('login creates a question, changes the state to REMOVED and deletes the question', () => {
    /!* falta o teste para criar a pergunta com o titulo 'RemoveTest' *!/
    cy.changeQuestionToRemovedTest('RemoveTest1','removejustification')

    cy.removeQuestionTest('RemoveTest1')
  });*/

});
