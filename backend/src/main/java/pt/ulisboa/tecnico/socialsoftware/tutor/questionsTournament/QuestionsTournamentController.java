package pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.dto.QuestionsTournamentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.dto.StudentTournamentRegistrationDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.AUTHENTICATION_ERROR;


@RestController
public class QuestionsTournamentController {

    @Autowired
    QuestionsTournamentService questionsTournamentService;

    @PostMapping("/executions/{executionId}/questionsTournament")
    @PreAuthorize("hasRole('ROLE_DEMO_ADMIN') or (hasRole('ROLE_STUDENT') and hasPermission(#executionId, 'EXECUTION.ACCESS'))")
    public QuestionsTournamentDto createQuestionsTournament(Principal principal, @PathVariable int executionId, @Valid @RequestBody QuestionsTournamentDto questionsTournament) {
        User user = getAuthenticationUser(principal);
        return this.questionsTournamentService.createQuestionsTournament(executionId,user.getId(), questionsTournament);
    }

    @PostMapping("/questionsTournaments/{questionsTournamentId}/studentRegistrations")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#questionsTournamentId, 'TOURNAMENT.ACCESS')")
    public StudentTournamentRegistrationDto studentRegister(Principal principal, @PathVariable Integer questionsTournamentId) {
        User user = getAuthenticationUser(principal);
        return this.questionsTournamentService.studentRegister(user.getId(), questionsTournamentId);
    }

    @GetMapping("/executions/{executionId}/questionsTournament")
    @PreAuthorize("(hasRole('ROLE_STUDENT') or hasRole('ROLE_TEACHER')) and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public List<QuestionsTournamentDto> getOpenTournamentsByCourse(@PathVariable int executionId) {
        return this.questionsTournamentService.getOpenTournamentsByCourse(executionId);
    }

    @PostMapping("/executions/{executionId}/questionsTournament/{questionsTournamentId}/cancelTournament")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#questionsTournamentId, 'TOURNAMENT.ACCESS') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public void cancelTournament(Principal principal, @PathVariable Integer questionsTournamentId, @PathVariable int executionId) {
        User user = getAuthenticationUser(principal);
        this.questionsTournamentService.cancelTournament(executionId, user.getId(), questionsTournamentId);
    }

    private User getAuthenticationUser(Principal principal) {
        User user = (User) ((Authentication) principal).getPrincipal();
        checkNullUser(user);
        return user;
    }

    private void checkNullUser(User user) {
        if(user == null)
            throw new TutorException(AUTHENTICATION_ERROR);
    }
}
