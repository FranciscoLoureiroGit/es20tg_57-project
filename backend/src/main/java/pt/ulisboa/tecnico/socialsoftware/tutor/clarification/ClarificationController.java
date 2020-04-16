package pt.ulisboa.tecnico.socialsoftware.tutor.clarification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.AnswerService;
import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.ClarificationAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto.ClarificationDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;


@RestController
public class ClarificationController {
    @Autowired
    private AnswerService answerService;

    @Autowired
    private ClarificationService clarificationService;

    @GetMapping("/quiz/quizAnswer/{questionAnswerId}/clarifications")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#questionAnswerId, 'QUESTION_ANSWER.ACCESS')")
    public ClarificationDto getClarification(@PathVariable int questionAnswerId,
                                             Principal principal) {
        return clarificationService.getClarification(((User)((Authentication) principal).getPrincipal()).getId(), questionAnswerId);
    }

    @GetMapping("/quiz/quizAnswer/{questionAnswerId}/clarifications/public")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#questionAnswerId, 'QUESTION_ANSWER.ACCESS')")
    public List<ClarificationDto> getPublicQuestionClarification(@PathVariable int questionAnswerId,
                                             Principal principal) {
        return clarificationService.getPublicQuestionClarification(questionAnswerId);
    }


    @GetMapping("/quiz/quizAnswer/questionAnswers/clarifications/public")
    @PreAuthorize("hasRole('ROLE_STUDENT') or hasRole('ROLE_TEACHER')")
    public List<ClarificationDto> getPublicClarifications(Principal principal) {
        return clarificationService.getPublicClarifications();
    }

    @GetMapping("/quiz/quizAnswer/questionAnswers/clarifications")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public List<ClarificationDto> getStudentClarifications(Principal principal) {
        return clarificationService.getClarificationsByStudent(((User)((Authentication) principal).getPrincipal()).getId());
    }

    @GetMapping("/quiz/quizAnswer/{questionAnswerId}/{clarificationId}/answers")
    @PreAuthorize("(hasRole('ROLE_STUDENT') or hasRole('ROLE_TEACHER')) and hasPermission(#questionAnswerId,'QUESTION_ANSWER.ACCESS')")
    public ClarificationAnswerDto getClarificationAnswer(@PathVariable int questionAnswerId, @PathVariable int clarificationId) {
        return answerService.getClarificationAnswer(clarificationId);
    }
    
    @GetMapping("/teacher/clarifications")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public List<ClarificationDto> getTeacherClarifications(Principal principal){
        return clarificationService.getClarificationsByTeacher(((User)((Authentication)principal).getPrincipal()).getId());
    }

    @PostMapping("/quiz/quizAnswer/{questionAnswerId}/clarifications")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#questionAnswerId, 'QUESTION_ANSWER.ACCESS') ")
    public ClarificationDto createClarification(@PathVariable int questionAnswerId,
                                                @Valid @RequestBody ClarificationDto clarificationDto,
                                                Principal principal){
        return clarificationService.createClarification(questionAnswerId, clarificationDto, ((User)((Authentication) principal).getPrincipal()).getId());
    }

    @PostMapping("/quiz/quizAnswer/{questionAnswerId}/clarifications/answer")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#questionAnswerId, 'QUESTION_ANSWER.ACCESS')")
    public ClarificationAnswerDto createClarificationAnswer(@PathVariable int questionAnswerId,
                                                            @RequestBody ClarificationAnswerDto clarificationAnswerDto,
                                                            Principal principal){
        return answerService.createClarificationAnswer(clarificationAnswerDto, ((User)((Authentication)principal).getPrincipal()).getId());
    }

    @PostMapping("/{quizId}/quizAnswer/questionAnswer/{clarificationId}")
    @PreAuthorize("hasRole('ROLE_TEACHER') ") //and hasPermission(#quizId, 'QUIZ.ACCESS')
    public ResponseEntity setClarificationPrivacy(@PathVariable int quizId,
                                                  @PathVariable int clarificationId,
                                                  @RequestBody boolean isPublic, Principal principal){
        clarificationService.setPrivacy(clarificationId, isPublic);
        return ResponseEntity.ok().build();
    }

}
