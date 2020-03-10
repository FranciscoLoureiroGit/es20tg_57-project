package pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain.ClarificationAnswer;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto.ClarificationDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto;

import java.io.Serializable;

public class ClarificationAnswerDto implements Serializable {

    private Integer id;
    private Integer key;
    private String answer;

    public ClarificationAnswerDto(){}

    public ClarificationAnswerDto(ClarificationAnswer clarificationAnswer){
        this.id = clarificationAnswer.getId();
        this.answer = clarificationAnswer.getAnswer();
        this.key = clarificationAnswer.getKey();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getKey() {
        return key;
    }

    public void setKey(Integer key) {
        this.key = key;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
