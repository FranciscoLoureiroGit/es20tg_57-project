// ***********************************************
// This example commands.js shows you how to
// create various custom commands and overwrite
// existing commands.
//
// For more comprehensive examples of custom
// commands please read more here:
// https://on.cypress.io/custom-commands
// ***********************************************
//
//
// -- This is a parent command --
// Cypress.Commands.add("login", (email, password) => { ... })
//
//
// -- This is a child command --
// Cypress.Commands.add("drag", { prevSubject: 'element'}, (subject, options) => { ... })
//
//
// -- This is a dual command --
// Cypress.Commands.add("dismiss", { prevSubject: 'optional'}, (subject, options) => { ... })
//
//
// -- This is will overwrite an existing command --
// Cypress.Commands.overwrite("visit", (originalFn, url, options) => { ... })

Cypress.Commands.add('demoStudentLogin', () => {
  cy.visit('/')
  cy.get('[data-cy="studentButton"]').click()
  cy.contains('Questions').click()
  cy.contains('My Questions').click()
})

Cypress.Commands.add('createQuestionByStudent', (title, ans1, ans2, ans3, ans4, ans5) => {
  cy.contains('New Question').click()
  cy.get('[data-cy="QuestionTitle"]').type(title, {force:true})
  cy.get('[outline="textarea1"]').type(ans1, {force:true})
  cy.get('[data-cy="option"]').filter('#input-143').click({force: true})
  cy.get('[outline="textarea2"]').filter('#input-147').type(ans2,{force: true})
  cy.get('[outline="textarea2"]').filter('#input-154').type(ans3,{force: true})
  cy.get('[outline="textarea2"]').filter('#input-161').type(ans4,{force: true})
  cy.get('[outline="textarea2"]').filter('#input-168').type(ans5,{force: true})
  cy.contains('Save').click()
})