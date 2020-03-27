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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

        formatDates(questionsTournament);
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

    private void formatDates(QuestionsTournamentDto tournament) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        if (tournament.getStartingDate() != null && !tournament.getStartingDate().matches("(\\d{4})-(\\d{2})-(\\d{2}) (\\d{2}):(\\d{2})")){
            tournament.setStartingDate(LocalDateTime.parse(tournament.getStartingDate().replaceAll(".$", ""), DateTimeFormatter.ISO_DATE_TIME).format(formatter));
        }
        if (tournament.getEndingDate() !=null && !tournament.getEndingDate().matches("(\\d{4})-(\\d{2})-(\\d{2}) (\\d{2}):(\\d{2})"))
            tournament.setEndingDate(LocalDateTime.parse(tournament.getEndingDate().replaceAll(".$", ""), DateTimeFormatter.ISO_DATE_TIME).format(formatter));
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
