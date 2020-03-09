package pt.ulisboa.tecnico.socialsoftware.tutor.clarification;

import org.springframework.stereotype.Service;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto.ClarificationDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto;

@Service
public class ClarificationService {
    public ClarificationDto createClarification(QuestionDto questionDto, String title, String description, UserDto user){
        // check if input is ok
        // verify if question and user exist and throw exception or create it
        return null;
    }
}
