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
import pt.ulisboa.tecnico.socialsoftware.tutor.question.api.QuestionController;
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

    // === HTTP GET REQUESTS ===
    @GetMapping("/clarifications/public")
    @PreAuthorize("hasRole('ROLE_STUDENT') or hasRole('ROLE_TEACHER')")
    public List<ClarificationDto> getPublicClarifications(Principal principal) {
        return clarificationService.getPublicClarifications();
    }

    @GetMapping("/clarifications/{clarificationId}/answers")
    @PreAuthorize("(hasRole('ROLE_STUDENT') or hasRole('ROLE_TEACHER'))")
    public ClarificationAnswerDto getClarificationAnswer(@PathVariable int clarificationId) {
        return answerService.getClarificationAnswer(clarificationId);
    }

    @GetMapping("/teacher/clarifications")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public List<ClarificationDto> getTeacherClarifications(Principal principal){
        return clarificationService.getClarificationsByTeacher(((User)((Authentication)principal).getPrincipal()).getId());
    }

    @GetMapping("/student/clarifications")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public List<ClarificationDto> getStudentClarifications(Principal principal) {
        return clarificationService.getClarificationsByStudent(((User)((Authentication) principal).getPrincipal()).getId());
    }

    @GetMapping("/question/{questionId}/clarifications/public")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public List<ClarificationDto> getPublicQuestionClarifications(@PathVariable int questionId) {
        return clarificationService.getPublicQuestionClarifications(questionId);
    }

    // === HTTP POST REQUESTS ===
    @PostMapping("/question-answer/{questionAnswerId}/clarifications/answer")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#questionAnswerId, 'QUESTION_ANSWER.ACCESS')")
    public ClarificationAnswerDto createClarificationAnswer(@PathVariable int questionAnswerId,
                                                            @RequestBody ClarificationAnswerDto clarificationAnswerDto,
                                                            Principal principal){
        return answerService.createClarificationAnswer(clarificationAnswerDto, ((User)((Authentication)principal).getPrincipal()).getId());
    }

    @PostMapping("/question-answer/{questionAnswerId}/clarifications")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#questionAnswerId, 'QUESTION_ANSWER.ACCESS') ")
    public ClarificationDto createClarification(@PathVariable int questionAnswerId,
                                                @Valid @RequestBody ClarificationDto clarificationDto,
                                                Principal principal){
        return clarificationService.createClarification(questionAnswerId, clarificationDto, ((User)((Authentication) principal).getPrincipal()).getId());
    }

    // === HTTP PUT REQUESTS ===
    @PutMapping("/clarifications/{clarificationId}/privacy")
    @PreAuthorize("hasRole('ROLE_TEACHER')")
    public ClarificationDto setClarificationPrivacy(@PathVariable int clarificationId, @RequestBody ClarificationDto clarificationDto){
        return clarificationService.setPrivacy(clarificationId, clarificationDto.getPublic());
    }

}
