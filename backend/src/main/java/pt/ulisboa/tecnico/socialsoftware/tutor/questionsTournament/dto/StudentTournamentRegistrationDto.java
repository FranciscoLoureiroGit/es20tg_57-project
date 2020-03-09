package pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.domain.StudentTournamentRegistration;

import java.time.format.DateTimeFormatter;

public class StudentTournamentRegistrationDto {
    private Integer id;
    private String registrationDate;
    private Integer userId;
    private String userName;
    private Integer tournamentId;

    public StudentTournamentRegistrationDto() {
    }

    public StudentTournamentRegistrationDto(StudentTournamentRegistration registration) {
        this.id = registration.getId();
        this.registrationDate = registration.getRegistrationDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        this.userId = registration.getStudent().getId();
        this.userName = registration.getStudent().getUsername();
        this.tournamentId = registration.getQuestionsTournament().getId();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getTournamentId() {
        return tournamentId;
    }

    public void setTournamentId(Integer tournamentId) {
        this.tournamentId = tournamentId;
    }
}
