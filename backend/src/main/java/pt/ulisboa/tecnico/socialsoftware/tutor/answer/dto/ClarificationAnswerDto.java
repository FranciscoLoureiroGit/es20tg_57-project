package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.ClarificationAnswer;

import java.io.Serializable;

public class ClarificationAnswerDto implements Serializable {

    private Integer id;
    private String answer;
    private Integer userId;
    private Integer clarificationId;

    public ClarificationAnswerDto(){}

    public ClarificationAnswerDto(ClarificationAnswer clarificationAnswer){
        this.id = clarificationAnswer.getId();
        this.answer = clarificationAnswer.getAnswer();
        this.userId = clarificationAnswer.getUser().getId();
        this.clarificationId = clarificationAnswer.getClarification().getId();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public Integer getUserId() { return userId; }

    public void setUserId(Integer userId) { this.userId = userId; }

    public Integer getClarificationId() { return clarificationId; }

    public void setClarificationId(Integer clarificationId) { this.clarificationId = clarificationId; }

    @Override
    public String toString(){
        return "ClarificationAnswerDto{" +
                "id=" + id +
                ". answer=\'" + answer + "\'" +
                "}";
    }
}
