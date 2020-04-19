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
// -- This will overwrite an existing command --
// Cypress.Commands.overwrite("visit", (originalFn, url, options) => { ... })
/// <reference types="Cypress" />
Cypress.Commands.add('demoAdminLogin', () => {
  cy.visit('/');
  cy.get('[data-cy="adminButton"]').click();
  cy.contains('Administration').click();
  cy.contains('Manage Courses').click();
});

Cypress.Commands.add('demoStudentLogin', () => {
  cy.visit('/');
  cy.get('[data-cy="studentButton"]').click();
});

Cypress.Commands.add('demoTeacherLogin', () => {
  cy.visit('/')
  cy.get('[data-cy="teacherButton"]').click()
})

Cypress.Commands.add('createCourseExecution', (name, acronym, academicTerm) => {
  cy.get('[data-cy="createButton"]').click();
  cy.get('[data-cy="Name"]').type(name);
  cy.get('[data-cy="Acronym"]').type(acronym);
  cy.get('[data-cy="AcademicTerm"]').type(academicTerm);
  cy.get('[data-cy="saveButton"]').click();
});

Cypress.Commands.add('closeErrorMessage', (name, acronym, academicTerm) => {
  cy.contains('Error')
    .parent()
    .find('button')
    .click();
});

Cypress.Commands.add('deleteCourseExecution', acronym => {
  cy.contains(acronym)
    .parent()
    .should('have.length', 1)
    .children()
    .should('have.length', 7)
    .find('[data-cy="deleteCourse"]')
    .click();
});

Cypress.Commands.add(
  'createFromCourseExecution',
  (name, acronym, academicTerm) => {
    cy.contains(name)
      .parent()
      .should('have.length', 1)
      .children()
      .should('have.length', 7)
      .find('[data-cy="createFromCourse"]')
      .click();
    cy.get('[data-cy="Acronym"]').type(acronym);
    cy.get('[data-cy="AcademicTerm"]').type(academicTerm);
    cy.get('[data-cy="saveButton"]').click();
  }
);

Cypress.Commands.add('goToOpenQuestionsTournaments', () => {
  cy.contains('Questions Tournaments').click();
  cy.contains('Open').click();
});

Cypress.Commands.add('registerStudentInTournament', tournamentId => {
  cy.get('tbody')
    .contains(tournamentId)
    .parent()
    .children()
    .should('have.length', 9)
    .find('[data-cy="registerStudent"]')
    .click();
});

Cypress.Commands.add(
  'createQuestionsTournament',
  (numberOfQuestions, topicId) => {
    cy.get('[data-cy="numberOfQuestions"]').type(numberOfQuestions);
    cy.contains('*Ending Date')
      .parent()
      .find('input')
      .click();
    cy.contains('30').click();
    cy.contains('OK').click();
    cy.clearLocalStorage();
    cy.contains('*Starting Date')
      .parent()
      .find('input')
      .click();
    cy.get('.v-dialog__content--active')
      .contains('.v-btn__content', '25')
      .click();
    cy.get('.v-dialog__content--active')
      .contains('OK')
      .click();
    cy.contains(topicId)
      .parent()
      .should('have.length', 1)
      .children()
      .should('have.length', 4)
      .find('[data-cy="addTopic"]')
      .click();
    cy.contains('Show Tournament').click();
    cy.get('[data-cy="saveButton"]').click({ force: true });
  }
);


Cypress.Commands.add('createAndAnswerQuiz', () => {
  cy.contains('Create').click()
  cy.get('[data-cy="createButton"]').click()
  cy.get('[data-cy="endButton"]').click()
  cy.get('[data-cy="sureButton"]').click()
})

Cypress.Commands.add('createClarificationRequestFromQuiz', (title, description) => {
  cy.get('[data-cy="createClarificationButton"]').click()
  cy.get('[data-cy="title"]').type(title)
  cy.get('[data-cy="description"]').type(description)
  cy.get('[data-cy="saveButton"]').click()
})

Cypress.Commands.add('createClarificationRequestFromSolved', (title, description) => {
  cy.contains('Solved').click()
  cy.contains('Generated Quiz').click()
  cy.get('[data-cy="createClarificationButton"]').click()
  cy.get('[data-cy="title"]').type(title)
  cy.get('[data-cy="description"]').type(description)
  cy.get('[data-cy="saveButton"]').click()
})

Cypress.Commands.add('showClarificationRequests', () => {
  cy.get('[data-cy="quizzesButton"]').click()
  cy.get('[data-cy="clarificationsButton"]').click()
})

Cypress.Commands.add('openClarificationDescription', (title) => {
  cy.contains(title)
    .parent()
    .should('have.length', 1)
    .children()
    .should('have.length', 7)
    .find('[data-cy="showClarification"]')
    .click()
  cy.get('[data-cy="closeButton"]').click()
})

Cypress.Commands.add('openClarificationQuestion', (title) => {
  cy.contains(title)
    .parent()
    .should('have.length', 1)
    .children()
    .should('have.length', 7)
    .find('[data-cy="showQuestion"]')
    .click()
  cy.get('[data-cy="closeButton"]').click()
})

Cypress.Commands.add('addClarificationQA', (variation) => {
  cy.get('[data-cy="logoutButton"]').click()
  cy.demoStudentLogin()
  cy.createAndAnswerQuiz()
  cy.createClarificationRequestFromQuiz('TITLE_' + String(variation), 'DESC')
  cy.get('[data-cy="logoutButton"]').click()
  cy.demoTeacherLogin()
})

Cypress.Commands.add('answerClarification', (answer, variation) => {
  cy.contains('Management').click();
  cy.contains('Clarification Requests').click();
  cy.contains('TITLE_' + String(variation))
    .parent()
    .should('have.length', 1)
    .children()
    .should('have.length', 7)
    .find('[data-cy="answerClarification"]')
    .click()

  cy.get('[data-cy="answerField"]').type(answer)
  cy.contains('Save').click()

})

Cypress.Commands.add('listClarificationWithAnswer', (title) => {
  cy.contains('Management').click();
  cy.contains('Clarification Requests').click();
  cy.contains(title)
    .parent()
    .should('have.length', 1)
    .children()
    .should('have.length', 7)
    .find('[data-cy="requestStatus"]')
    .should('contain.text', 'CLOSED')

  cy.contains(title)
    .parent()
    .find('[data-cy="showAnswer"]')
    .click()
  cy.contains('close').click()

})
