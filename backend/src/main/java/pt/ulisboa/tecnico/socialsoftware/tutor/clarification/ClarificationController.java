package pt.ulisboa.tecnico.socialsoftware.tutor.clarification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.ClarificationAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto.ClarificationDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto;

import javax.validation.Valid;


@RestController
public class ClarificationController {
    private static Logger logger = LoggerFactory.getLogger(ClarificationController.class);

    @Autowired
    private AnswerService answerService;

    private ClarificationService clarificationService;

    ClarificationController(ClarificationService clarificationService1) { this.clarificationService = clarificationService1; }

    @PostMapping("/quiz/quizAnswer/{questionAnswerId}/clarifications")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#questionAnswerId, 'QUESTION_ANSWER.ACCESS')") // Only student can create a clarification request
    public ClarificationDto createClarification(@PathVariable int questionAnswerId,
                                                @Valid @RequestBody ClarificationDto clarificationDto,
                                                @RequestBody UserDto userDto){
        return clarificationService.createClarification(questionAnswerId, clarificationDto, userDto);
    }

    @PostMapping("/{quizId}/quizAnswer/questionAnswer/clarifications/{clarificationId}/")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#quizId, 'QUIZ.ACCESS')")
    public ClarificationAnswerDto createClarificationAnswer(@PathVariable int clarificationId, @PathVariable int quizId,
                                                            @Valid @RequestBody ClarificationAnswerDto clarificationAnswerDto,
                                                            @RequestBody UserDto userDto){
        ClarificationDto clr = new ClarificationDto();
        clr.setId(clarificationId);

        return answerService.createClarificationAnswer(clr, userDto, clarificationAnswerDto.getAnswer());
    }

}
