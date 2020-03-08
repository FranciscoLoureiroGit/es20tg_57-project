package pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Topic;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.domain.QuestionsTournament;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto;

import java.io.Serializable;
import java.util.List;

public class QuestionsTournamentDto {
    private int id;
    //private String creationDate = null;
    private String startingDate = null;
    private String endingDate = null;
    private int numberOfQuestions;
    private List<Topic> topics;
    private UserDto studentUser;

    public QuestionsTournamentDto(){
    }

    public QuestionsTournamentDto(QuestionsTournament questionsTournament){
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStartingDate() {
        return startingDate;
    }

    public void setStartingDate(String startingDate) {
        this.startingDate = startingDate;
    }

    public String getEndingDate() {
        return endingDate;
    }

    public void setEndingDate(String endingDate) {
        this.endingDate = endingDate;
    }

    public int getNumberOfQuestions() {
        return numberOfQuestions;
    }

    public void setNumberOfQuestions(int numberOfQuestions) {
        this.numberOfQuestions = numberOfQuestions;
    }

    public List<Topic> getTopics() {
        return topics;
    }

    public void setTopics(List<Topic> topics) {
        this.topics = topics;
    }

    public UserDto getStudentUser() {
        return studentUser;
    }

    public void setStudentUser(UserDto studentUser) {
        this.studentUser = studentUser;
    }
}
