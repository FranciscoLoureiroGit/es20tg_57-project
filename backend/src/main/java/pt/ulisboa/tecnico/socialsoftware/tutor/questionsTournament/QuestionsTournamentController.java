package pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.domain.QuestionsTournament;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.dto.QuestionsTournamentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.dto.StudentTournamentRegistrationDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.quiz.dto.QuizDto;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@RestController
public class QuestionsTournamentController {

    @Autowired
    QuestionsTournamentService questionsTournamentService;

    @PostMapping("/executions/{executionId}/questionsTournaments")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public QuestionsTournamentDto createQuestionsTournament(@PathVariable int executionId, @PathVariable int userId, @Valid @RequestBody QuestionsTournamentDto questionsTournament) {
        formatDates(questionsTournament);
        return this.questionsTournamentService.createQuestionsTournament(executionId,userId, questionsTournament);
    }

    @GetMapping("/executions/{executionId}/questionsTournament/studentRegister")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#userId, 'USER.ACCESS')")
    public StudentTournamentRegistrationDto studentRegister(@PathVariable Integer userId, @PathVariable Integer questionsTournamentId) {
        return questionsTournamentService.studentRegister(userId, questionsTournamentId);
    }

    private void formatDates(QuestionsTournamentDto tournament) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        if (tournament.getStartingDate() != null && !tournament.getStartingDate().matches("(\\d{4})-(\\d{2})-(\\d{2}) (\\d{2}):(\\d{2})")){
            tournament.setStartingDate(LocalDateTime.parse(tournament.getStartingDate().replaceAll(".$", ""), DateTimeFormatter.ISO_DATE_TIME).format(formatter));
        }
        if (tournament.getEndingDate() !=null && !tournament.getEndingDate().matches("(\\d{4})-(\\d{2})-(\\d{2}) (\\d{2}):(\\d{2})"))
            tournament.setEndingDate(LocalDateTime.parse(tournament.getEndingDate().replaceAll(".$", ""), DateTimeFormatter.ISO_DATE_TIME).format(formatter));
    }
}
