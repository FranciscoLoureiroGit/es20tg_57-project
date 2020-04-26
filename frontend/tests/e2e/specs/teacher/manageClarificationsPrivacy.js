describe('Teacher clarifications walkthrough', () => {
  let variation = Date.now()
  beforeEach(() => {

    cy.demoTeacherLogin()
  })

  afterEach(() => {
    cy.wait(1000)
    cy.get('[data-cy="LogoutButton"]').click()
  })

  it('login answers existing clarification request', () => {
    cy.addClarificationQA(variation)
  });

  it('check for private clarification request and make it public', () => {
    cy.changeClarificationPrivacy('TITLE_' + String(variation), 'public')
  })

  it('check for public clarification request and make it private', () => {
    cy.changeClarificationPrivacy('TITLE_' + String(variation), 'private')
  })






});
