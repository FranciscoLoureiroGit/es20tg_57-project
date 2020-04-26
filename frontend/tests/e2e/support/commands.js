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

Cypress.Commands.add('demoStudentLogin', () => {
  cy.visit('/');
  cy.get('[data-cy="demoStudentLoginButton"]').click();
});

Cypress.Commands.add('demoTeacherLogin', () => {
  cy.visit('/');
  cy.get('[data-cy="demoTeacherLoginButton"]').click();
});

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
  cy.contains('Create').click();
  cy.get('[data-cy="createButton"]').click();
  cy.get('[data-cy="endButton"]').click();
  cy.get('[data-cy="sureButton"]').click();
});

Cypress.Commands.add(
  'createClarificationRequestFromQuiz',
  (title, description) => {
    cy.get('[data-cy="createClarificationButton"]').click();
    cy.get('[data-cy="title"]').type(title);
    cy.get('[data-cy="description"]').type(description);
    cy.get('[data-cy="saveButton"]').click();
  }
);

Cypress.Commands.add(
  'createClarificationRequestFromSolved',
  (title, description) => {
    cy.contains('Solved').click();
    cy.contains('Generated Quiz').click();
    cy.get('[data-cy="createClarificationButton"]').click();
    cy.get('[data-cy="title"]').type(title);
    cy.get('[data-cy="description"]').type(description);
    cy.get('[data-cy="saveButton"]').click();
  }
);

Cypress.Commands.add('showClarificationRequests', () => {
  cy.get('[data-cy="quizzesButton"]').click();
  cy.get('[data-cy="clarificationsButton"]').click();
});

Cypress.Commands.add('openClarificationDescription', title => {
  cy.contains(title)
    .parent()
    .should('have.length', 1)
    .children()
    .should('have.length', 7)
    .find('[data-cy="showClarification"]')
    .click();
  cy.get('[data-cy="closeButton"]').click();
});

Cypress.Commands.add('openClarificationQuestion', title => {
  cy.contains(title)
    .parent()
    .should('have.length', 1)
    .children()
    .should('have.length', 7)
    .find('[data-cy="showQuestion"]')
    .click();
  cy.get('[data-cy="closeButton"]').click();
});

Cypress.Commands.add('addClarificationQA', variation => {
  cy.get('[data-cy="logoutButton"]').click();
  cy.demoStudentLogin();
  cy.get('[data-cy="quizzesButton"]').click();
  cy.createAndAnswerQuiz();
  cy.createClarificationRequestFromQuiz('TITLE_' + String(variation), 'DESC');
  cy.get('[data-cy="logoutButton"]').click();
  cy.demoTeacherLogin();
});

Cypress.Commands.add('answerClarification', (answer, variation) => {
  cy.contains('Management').click();
  cy.contains('Clarification Requests').click();
  cy.contains('TITLE_' + String(variation))
    .parent()
    .should('have.length', 1)
    .children()
    .should('have.length', 8)
    .find('[data-cy="answerClarification"]')
    .click();

  cy.get('[data-cy="answerField"]').type(answer);
  cy.contains('Save').click();
});

Cypress.Commands.add('listClarificationWithAnswer', title => {
  cy.contains('Management').click();
  cy.contains('Clarification Requests').click();
  cy.contains(title)
    .parent()
    .should('have.length', 1)
    .children()
    .should('have.length', 8)
    .find('[data-cy="requestStatus"]')
    .should('contain.text', 'CLOSED');

  cy.contains(title)
    .parent()
    .find('[data-cy="showAnswer"]')
    .click();
  cy.contains('close').click();
});

Cypress.Commands.add(
  'createQuestionByStudent',
  (title, ans1, ans2, ans3, ans4, ans5) => {
    cy.contains('New Question').click();
    cy.get('[data-cy="QuestionTitle"]').type(title, { force: true });
    cy.get('[data-cy="content"]').type(ans1, { force: true });
    cy.get('[data-cy="option1"]').click({ force: true });
    cy.get('[data-cy="content1"]').type(ans2, { force: true });
    cy.get('[data-cy="content2"]').type(ans3, { force: true });
    cy.get('[data-cy="content3"]').type(ans4, { force: true });
    cy.get('[data-cy="content4"]').type(ans5, { force: true });
    cy.contains('Save').click();
  }
);

Cypress.Commands.add('changeQuestionToAvailableTest', title => {
  cy.contains(title)
    .parent()
    .should('have.length', 1)
    .children()
    .should('have.length', 7)
    .find('[data-cy="changeQuestionStateDialog"]')
    .click({ force: true });
  cy.get('[data-cy="Status"]').type('AVAILABLE{enter}', { force: true });
  cy.get('[data-cy="changeQuestionButton"]').click();
});

Cypress.Commands.add('changeQuestionToRemovedTest', (title, justification) => {
  cy.contains(title)
    .parent()
    .should('have.length', 1)
    .children()
    .should('have.length', 7)
    .find('[data-cy="changeQuestionStateDialog"]')
    .click({ force: true });
  cy.get('[data-cy="Status"]').type('REMOVED{enter}', { force: true });
  cy.get('[data-cy="Justification"]').clear();
  cy.get('[data-cy="Justification"]').type(justification);
  cy.get('[data-cy="changeQuestionButton"]').click();
});

Cypress.Commands.add('changeQuestionToDisabledTest', (title, justification) => {
  cy.contains(title)
    .parent()
    .should('have.length', 1)
    .children()
    .should('have.length', 7)
    .find('[data-cy="changeQuestionStateDialog"]')
    .click({ force: true });
  cy.get('[data-cy="Status"]').type('DISABLED{enter}', { force: true });
  cy.get('[data-cy="Justification"]').clear();
  cy.get('[data-cy="changeQuestionButton"]').click();
  cy.closeErrorMessage();
  cy.get('[data-cy="Status"]').type('DISABLED{enter}', { force: true });
  cy.get('[data-cy="Justification"]').type(justification);
  cy.get('[data-cy="changeQuestionButton"]').click();
});

Cypress.Commands.add('checkQuestionByStudent', title => {
  cy.get('[data-cy="search-question"]').type(title, { force: true });
  cy.get('[data-cy="show-question"]').click();
  cy.contains('close').click();
});

Cypress.Commands.add('removeQuestionTest', title => {
  cy.contains(title)
    .parent()
    .should('have.length', 1)
    .children()
    .should('have.length', 7)
    .find('[data-cy="deleteQuestionButton"]')
    .click({ force: true });
});

Cypress.Commands.add('changeClarificationPrivacy', (title, privacy) => {
  cy.contains('Management').click();
  cy.contains('Clarification Requests').click();
  cy.contains(title)
    .parent()
    .should('have.length', 1)
    .children()
    .should('have.length', 8)
    .contains('[data-cy="set-' + privacy + '"]')
    .click({ force: true })
    .contains('[data-cy="requestPrivacy"]')
});
