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
    cy.visit('/')
    cy.get('[data-cy="adminButton"]').click()
    cy.contains('Administration').click()
    cy.contains('Manage Courses').click()
})

Cypress.Commands.add('demoStudentLogin', () => {
    cy.visit('/')
    cy.get('[data-cy="studentButton"]').click()
    cy.contains('Questions').click()
    cy.contains('My Questions').click()
})

/*NOVO*/
Cypress.Commands.add('demoTeacherLogin', () => {
    cy.visit('/')
    cy.get('[data-cy="teacherButton"]').click()
    cy.contains('Management').click()
    cy.contains('StudentSubmittedQuestions').click()
})


/*NOVO*/
Cypress.Commands.add('changeQuestionToAvailableTest', (title, justification) => {
    cy.contains(title)
        .parent()
        .should('have.length', 1)
        .children()
        .should('have.length', 7)
        .find('[data-cy="changeQuestionStateDialog"]')
        .click({force: true})
    cy.get('[data-cy="Status"]').type('AVAILABLE{enter}', {force: true})
    cy.get('[data-cy="Justification"]').type(justification)
    cy.get('[data-cy="changeQuestionButton"]').click()
})

/*NOVO*/
Cypress.Commands.add('changeQuestionToDisabledTest', (title, justification) => {
    cy.contains(title)
        .parent()
        .should('have.length', 1)
        .children()
        .should('have.length', 7)
        .find('[data-cy="changeQuestionStateDialog"]')
        .click({force: true})
    cy.get('[data-cy="Status"]').type('DISABLED{enter}', {force: true})
    cy.get('[data-cy="changeQuestionButton"]').click()
    cy.closeErrorMessage()
    cy.get('[data-cy="Status"]').type('DISABLED{enter}', {force: true})
    cy.get('[data-cy="Justification"]').type(justification)
    cy.get('[data-cy="changeQuestionButton"]').click()
})

/*NOVO*/
Cypress.Commands.add('changeQuestionToRemovedTest', (title, justification) => {
    cy.contains(title)
        .parent()
        .should('have.length', 1)
        .children()
        .should('have.length', 7)
        .find('[data-cy="changeQuestionStateDialog"]')
        .click({force: true})
    cy.get('[data-cy="Status"]').type('REMOVED{enter}', {force: true})
    cy.get('[data-cy="Justification"]').type(justification)
    cy.get('[data-cy="changeQuestionButton"]').click()
})

Cypress.Commands.add( 'checkQuestionByStudent', (title) =>{
    cy.get('[data-cy="search-question"]').type(title, {force: true})
    cy.get('[data-cy="show-question"]').click()
    cy.contains('close').click()
})

/*NOVO*/
Cypress.Commands.add('removeQuestionTest', (title) => {
    cy.contains(title)
        .parent()
        .should('have.length', 1)
        .children()
        .should('have.length', 7)
        .find('[data-cy="deleteQuestionButton"]')
        .click({force: true})
})



Cypress.Commands.add('createCourseExecution', (name, acronym, academicTerm) => {
    cy.get('[data-cy="createButton"]').click()
    cy.get('[data-cy="Name"]').type(name)
    cy.get('[data-cy="Acronym"]').type(acronym)
    cy.get('[data-cy="AcademicTerm"]').type(academicTerm)
    cy.get('[data-cy="saveButton"]').click()
})

Cypress.Commands.add('createQuestionByStudent', (title, ans1, ans2, ans3, ans4, ans5) => {
    cy.contains('New Question').click()
    cy.get('[data-cy="QuestionTitle"]').type(title, {force:true})
    cy.get('[outline="textarea1"]').type(ans1, {force:true})
    cy.get('[data-cy="option"]').filter('#input-110').click({force: true})
    cy.get('[outline="textarea"]').filter('#input-114').type(ans2,{force: true})
    cy.get('[outline="textarea"]').filter('#input-121').type(ans3,{force: true})
    cy.get('[outline="textarea"]').filter('#input-128').type(ans4,{force: true})
    cy.get('[outline="textarea"]').filter('#input-135').type(ans5,{force: true})
    cy.contains('Save').click()
})

Cypress.Commands.add('closeErrorMessage', (name, acronym, academicTerm) => {
    cy.contains('Error')
        .parent()
        .find('button')
        .click()
})

Cypress.Commands.add('deleteCourseExecution', (acronym) => {
    cy.contains(acronym)
        .parent()
        .should('have.length', 1)
        .children()
        .should('have.length', 7)
        .find('[data-cy="deleteCourse"]')
        .click()
})

Cypress.Commands.add('createFromCourseExecution', (name, acronym, academicTerm) => {
    cy.contains(name)
        .parent()
        .should('have.length', 1)
        .children()
        .should('have.length', 7)
        .find('[data-cy="createFromCourse"]')
        .click()
    cy.get('[data-cy="Acronym"]').type(acronym)
    cy.get('[data-cy="AcademicTerm"]').type(academicTerm)
    cy.get('[data-cy="saveButton"]').click()
})

