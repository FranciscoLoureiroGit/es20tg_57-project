describe('Notifications walkthrough', () => {
  beforeEach(() => {
  });

  afterEach(() => {
    cy.contains('Logout').click();
  });

  it('checks no notifications', () => {
    cy.demoStudentLogin();
    cy.get('[data-cy="notificationButton"]').click();
    cy.get('[data-cy="exitButton"]').click();
  });

  it('teacher notifies all students', () => {
    cy.demoTeacherLogin();
    cy.notifyStudents('notification title for all','this is a notification description')
  });

  it('teacher notifies demo student', () => {
    cy.demoTeacherLogin();
    cy.notifyDemoStudent('notification title for demo student','this is a notification description')
  });

  it('student checks new notifications and clears all', () => {
    cy.demoStudentLogin();
    cy.get('[data-cy="notificationButton"]').click();
    cy.contains('notification title for all');
    cy.contains('notification title for demo student');
    cy.get('[data-cy="clearAllButton"]').click();
    cy.get('[data-cy="exitButton"]').click();
  });


});