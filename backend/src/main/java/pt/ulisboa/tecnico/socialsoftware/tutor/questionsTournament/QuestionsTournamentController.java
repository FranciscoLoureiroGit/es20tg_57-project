package pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.dto.QuestionsTournamentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.questionsTournament.dto.StudentTournamentRegistrationDto;

import javax.validation.Valid;


@RestController
public class QuestionsTournamentController {

    @Autowired
    QuestionsTournamentService questionsTournamentService;

    @GetMapping("/executions/{executionId}/questionsTournament/studentRegister")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#userId, 'USER.ACCESS')")
    public StudentTournamentRegistrationDto studentRegister(@PathVariable Integer userId, @Valid @RequestBody QuestionsTournamentDto questionsTournamentDto) {
        return questionsTournamentService.studentRegister(userId, questionsTournamentDto);
    }
}
