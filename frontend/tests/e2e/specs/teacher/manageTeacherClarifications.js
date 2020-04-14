describe('Teacher clarifications walkthrough', () => {
  let variation = Date.now()
  beforeEach(() => {
    cy.addClarificationQA(variation)
    cy.demoTeacherLogin()
  })

  afterEach(() => {
    cy.wait(1000)
    cy.get('[data-cy="logoutButton"]').click()
  })

  it('login answers existing clarification request', () => {
    cy.answerClarification('ANSWER', variation)
  });






});
