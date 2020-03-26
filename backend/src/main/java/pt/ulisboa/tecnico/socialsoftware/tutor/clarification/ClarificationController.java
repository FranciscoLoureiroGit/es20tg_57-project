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
/*
    @GetMapping("/quiz/quizAnswer/{questionAnswerId}/clarifications")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#questionAnswerId, 'QUESTION_ANSWER.ACCESS')")
    public ClarificationDto getClarification(@PathVariable int questionAnswerId,
                                             @PathVariable int studentId) {
        return clarificationService.getClarification(studentId, questionAnswerId);
    }


    @GetMapping("/{quizId}/quizAnswer/questionAnswer/clarifications/{clarificationId}/")
    @PreAuthorize("(hasRole('ROLE_STUDENT') or hasRole('ROLE_TEACHER')) and hasPermission('QUIZ.ACCESS')")
    public ClarificationAnswerDto getClarificationAnswer(@PathVariable int studentId, @PathVariable int questionAnswerId) {
        return clarificationService.getClarificationAnswer(studentId, questionAnswerId);
    }*/

    @PostMapping("/quiz/quizAnswer/{questionAnswerId}/clarifications")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#questionAnswerId, 'QUESTION_ANSWER.ACCESS')")
    public ClarificationDto createClarification(@PathVariable int questionAnswerId,
                                                @Valid @RequestBody ClarificationDto clarificationDto,
                                                Principal principal){
        return clarificationService.createClarification(questionAnswerId, clarificationDto, ((User)((Authentication) principal).getPrincipal()).getId());
    }

    @PostMapping("/{quizId}/quizAnswer/questionAnswer/clarifications/{clarificationId}/")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#quizId, 'QUIZ.ACCESS')")
    public ClarificationAnswerDto createClarificationAnswer(@PathVariable int clarificationId, @PathVariable int quizId,
                                                            @Valid @RequestBody ClarificationAnswerDto clarificationAnswerDto,
                                                            Principal principal){
        ClarificationDto clr = new ClarificationDto();
        clr.setId(clarificationId);

        return answerService.createClarificationAnswer(clr, ((User)((Authentication)principal).getPrincipal()).getId(), clarificationAnswerDto.getAnswer());
    }

}
