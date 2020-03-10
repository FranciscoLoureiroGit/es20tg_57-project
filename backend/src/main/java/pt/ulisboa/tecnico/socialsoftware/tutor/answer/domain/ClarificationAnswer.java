package pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto.ClarificationDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto;

public class ClarificationAnswer {

    private Integer _id;

    private ClarificationDto _clarification_dto;

    private UserDto _user_dto;

    private String _answer;

    public ClarificationAnswer(){}

    public ClarificationAnswer(ClarificationDto request, UserDto user, String answer){
        if(request == null) throw new TutorException(ErrorMessage.NO_CLARIFICATION_REQUEST);
        if(answer == null || answer.trim().isEmpty()) throw new TutorException(ErrorMessage.NO_CLARIFICATION_ANSWER);
        _clarification_dto = request;
        _user_dto = user;
        _answer = answer;
    }

    public Integer get_id() {
        return _id;
    }

    public void set_id(Integer id) {
        this._id = id;
    }

    public ClarificationDto get_clarification_dto() {
        return _clarification_dto;
    }

    public void set_clarification_dto(ClarificationDto clarification_dto) {
        this._clarification_dto = clarification_dto;
    }

    public UserDto get_user_dto() {
        return _user_dto;
    }

    public void set_user_dto(UserDto user_dto) {
        this._user_dto = user_dto;
    }

    public String get_answer() {
        return _answer;
    }

    public void set_answer(String answer) {
        this._answer = answer;
    }
}
