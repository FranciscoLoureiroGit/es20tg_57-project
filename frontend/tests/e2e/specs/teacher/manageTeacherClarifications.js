describe('Teacher clarifications walkthrough', () => {
  let variation = Date.now()
  beforeEach(() => {

    cy.demoTeacherLogin()
  })

  afterEach(() => {
    cy.wait(1000)
    cy.get('[data-cy="logoutButton"]').click()
  })

  it('login answers existing clarification request', () => {
    cy.addClarificationQA(variation)
    cy.answerClarification('ANSWER_' + String(variation), variation)
  });

  it('check for closed clarification in the management list', () => {
    cy.listClarificationWithAnswer('TITLE_' + String(variation))
  })

});
