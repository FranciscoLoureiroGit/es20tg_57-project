package pt.ulisboa.tecnico.socialsoftware.tutor.clarification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.dto.ClarificationDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.dto.UserDto;

import javax.validation.Valid;


@RestController
public class ClarificationController {
    private static Logger logger = LoggerFactory.getLogger(ClarificationController.class);

    private ClarificationService clarificationService;

    ClarificationController(ClarificationService clarificationService1) { this.clarificationService = clarificationService1; }

    @PostMapping("/quiz/quizAnswer/{questionAnswerId}/clarifications")
    @PreAuthorize("hasRole('ROLE_STUDENT')") // Only student can create a clarification request
    public ClarificationDto createClarification(@PathVariable int questionAnswerId,
                                                @Valid @RequestBody ClarificationDto clarificationDto,
                                                @RequestBody UserDto userDto){
        return clarificationService.createClarification(questionAnswerId, clarificationDto, userDto);
    }

}
