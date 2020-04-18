package pt.ulisboa.tecnico.socialsoftware.tutor.clarification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.ClarificationAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto.ClarificationDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto;

import javax.validation.Valid;
import java.security.Principal;


@RestController
public class ClarificationController {
    private static Logger logger = LoggerFactory.getLogger(ClarificationController.class);

    @Autowired
    private AnswerService answerService;

    @Autowired
    private ClarificationService clarificationService;

    // url should be case insensitive -> ex. quiz-answer
    @GetMapping("/quiz/quizAnswer/{questionAnswerId}/clarifications")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#questionAnswerId, 'QUESTION_ANSWER.ACCESS')")
    public ClarificationDto getClarification(@PathVariable int questionAnswerId,
                                             Principal principal) {
        return clarificationService.getClarification(((User)((Authentication) principal).getPrincipal()).getId(), questionAnswerId);
    }



    // urls case insensitive ...
    // also it is unnecessarily large, compare with the rest of the project
    @GetMapping("/{quizId}/quizAnswer/questionAnswer/{clarificationId}/answers")
    @PreAuthorize("(hasRole('ROLE_STUDENT') or hasRole('ROLE_TEACHER')) and hasPermission(#quizId,'QUIZ.ACCESS')")
    public ClarificationAnswerDto getClarificationAnswer(@PathVariable int quizId, @PathVariable int clarificationId) {
        return answerService.getClarificationAnswer(clarificationId);
    }

    @PostMapping("/quiz/quizAnswer/{questionAnswerId}/clarifications")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#questionAnswerId, 'QUESTION_ANSWER.ACCESS')") //
    public ClarificationDto createClarification(@PathVariable int questionAnswerId,
                                                @Valid @RequestBody ClarificationDto clarificationDto,
                                                Principal principal){
        return clarificationService.createClarification(questionAnswerId, clarificationDto, ((User)((Authentication) principal).getPrincipal()).getId());
    }

    // the same as above
    @PostMapping("/{quizId}/quizAnswer/questionAnswer/clarifications/answer")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#quizId, 'QUIZ.ACCESS')")
    public ClarificationAnswerDto createClarificationAnswer(@PathVariable int quizId,
                                                            @RequestBody ClarificationAnswerDto clarificationAnswerDto,
                                                            Principal principal){


        return answerService.createClarificationAnswer(clarificationAnswerDto, ((User)((Authentication)principal).getPrincipal()).getId());
    }

}
