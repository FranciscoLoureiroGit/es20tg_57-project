package pt.ulisboa.tecnico.socialsoftware.tutor.statistics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.UserRepository;

import javax.validation.Valid;
import java.security.Principal;

import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.AUTHENTICATION_ERROR;
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.USER_NOT_FOUND;

@RestController
public class StatsController {

    @Autowired
    private StatsService statsService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/executions/{executionId}/stats")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public StatsDto getStats(Principal principal, @PathVariable int executionId) {
        User user = (User) ((Authentication) principal).getPrincipal();

        if (user == null) {
            throw new TutorException(AUTHENTICATION_ERROR);
        }

        return statsService.getStats(user.getId(), executionId);
    }

    @GetMapping("/executions/{executionId}/stats-clarifications/{executionRequest}")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public ClarificationStatsDto getClarificationStats(Principal principal, @PathVariable int executionId, @PathVariable int executionRequest){
        return statsService.getClarificationStats(((User)((Authentication)principal).getPrincipal()).getId(), executionRequest);
    }

    @PostMapping("/privacy/dashboard")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public ResponseEntity setDashboardPrivacyStatus(Principal principal, @Valid @RequestBody String privacyStatus) {
        User user = getAuthenticationUser(principal);
        statsService.setDashboardPrivacy(user.getId(), User.PrivacyStatus.valueOf(privacyStatus));
        return ResponseEntity.ok().build();
    }

    private User getAuthenticationUser(Principal principal) {
        User user = (User) ((Authentication) principal).getPrincipal();
        checkNullUser(user);
        return user;
    }

    private void checkNullUser(User user) {
        if (user == null)
            throw new TutorException(AUTHENTICATION_ERROR);
    }

    @GetMapping("/executions/{executionId}/stats/tournaments")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public TournamentStatsDto getTournamentStats(Principal principal, @PathVariable int executionId) {
        User user = (User) ((Authentication) principal).getPrincipal();


        if (user == null) {
            throw new TutorException(AUTHENTICATION_ERROR);
        }

        return statsService.getTournamentStats(user.getId(), executionId);
    }

    @GetMapping("/executions/{executionId}/stats-clarifications/{executionRequest}/{yearMonth}")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public ClarificationStatsDto getClarificationStats(Principal principal, @PathVariable int executionId,
                                                       @PathVariable int executionRequest, @PathVariable int yearMonth){
        return statsService.getClarificationMonthlyStats(((User)((Authentication)principal).getPrincipal()).getId(), executionRequest, yearMonth);
    }
}
