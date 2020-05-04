package pt.ulisboa.tecnico.socialsoftware.tutor.question.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.QuestionService;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.TopicDto;
import org.springframework.security.core.Authentication;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;
import static pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage.AUTHENTICATION_ERROR;
import java.security.Principal;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
public class QuestionController {
    private static Logger logger = LoggerFactory.getLogger(QuestionController.class);

    private QuestionService questionService;

    @Value("${figures.dir}")
    private String figuresDir;

    QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @GetMapping("/courses/{courseId}/questions")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#courseId, 'COURSE.ACCESS')")
    public List<QuestionDto> getCourseQuestions(@PathVariable int courseId) {
        return this.questionService.findQuestions(courseId);
    }

    @GetMapping(value = "/courses/{courseId}/questions/export")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#courseId, 'COURSE.ACCESS')")
    public void exportQuestions(HttpServletResponse response,@PathVariable int courseId) throws IOException {
        response.setHeader("Content-Disposition", "attachment; filename=file.zip");
        response.setContentType("application/zip");
        response.getOutputStream().write(this.questionService.exportCourseQuestions(courseId).toByteArray());

        response.flushBuffer();
    }

    @GetMapping("/courses/{courseId}/questions/available")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#courseId, 'COURSE.ACCESS')")
    public List<QuestionDto> getAvailableQuestions(@PathVariable int courseId) {
        return this.questionService.findAvailableQuestions(courseId);
    }

    @GetMapping("/courses/{courseId}/questions/availableFiltered")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#courseId, 'COURSE.ACCESS')")
    public List<QuestionDto> getAllApprovedQuestions(@PathVariable int courseId){
        return questionService.findAvailableQuestionsWithStudentsIncluded(courseId);
    }

    @GetMapping("/courses/{courseId}/questions/studentQuestions")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#courseId, 'COURSE.ACCESS')")
    public List<QuestionDto> getStudentSubmittedQuestions(@PathVariable int courseId){

        List<QuestionDto> av = this.questionService.findQuestions(courseId);
        List<QuestionDto> outputList = new ArrayList<>();

        for (QuestionDto question : av) {
            if(question.getRoleAuthor() == null) //The old data in database does not have role author on question, however, the new data does.
                continue;
            else if(question.getRoleAuthor().equals(User.Role.STUDENT.name()))
                outputList.add(question);
        }

        return outputList;
    }

    @PostMapping("/courses/{courseId}/questions/createQuestion")
    @PreAuthorize("(hasRole('ROLE_TEACHER') or hasRole('ROLE_STUDENT')) and hasPermission(#courseId, 'COURSE.ACCESS')")
    public QuestionDto createQuestion(Principal principal, @PathVariable int courseId, @Valid @RequestBody QuestionDto question) {
        User user = (User) ((Authentication) principal).getPrincipal();

        if(user == null)
            throw new TutorException(AUTHENTICATION_ERROR);

        if(user.getRole().name().equals(User.Role.STUDENT.name()))
            question.setStatus(Question.Status.PENDING.name());
        else if(user.getRole().name().equals(User.Role.TEACHER.name()))
            question.setStatus(Question.Status.AVAILABLE.name());

        question.setUser(user);
        question.setUser_id(user.getId());
        question.setRoleAuthor(user.getRole().name());
        return this.questionService.createQuestion(courseId, question);
    }

    @GetMapping("/questions/showMyQuestions/")
    @PreAuthorize("hasRole('ROLE_TEACHER') or hasRole('ROLE_STUDENT')")
    public List<QuestionDto> showMyQuestions(Principal principal) {
        User user = (User) ((Authentication) principal).getPrincipal();

        if(user == null || user.getRole().name().equals(User.Role.DEMO_ADMIN.name()) || user.getRole().name().equals(User.Role.ADMIN.name()))
            throw new TutorException(AUTHENTICATION_ERROR);


        return questionService.findQuestionsByUserId(user.getId());


    }

    @GetMapping("/questions/{questionId}")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#questionId, 'QUESTION.ACCESS')")
    public QuestionDto getQuestion(@PathVariable Integer questionId) {
        return this.questionService.findQuestionById(questionId);
    }

    @PutMapping("/questions/{questionId}")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#questionId, 'QUESTION.ACCESS')")
    public QuestionDto updateQuestion(@PathVariable Integer questionId, @Valid @RequestBody QuestionDto question) {
        return this.questionService.updateQuestion(questionId, question);
    }

    @DeleteMapping("/questions/{questionId}")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#questionId, 'QUESTION.ACCESS')")
    public ResponseEntity removeQuestion(@PathVariable Integer questionId) throws IOException {
        logger.debug("removeQuestion questionId: {}: ", questionId);
        QuestionDto questionDto = questionService.findQuestionById(questionId);
        String url = questionDto.getImage() != null ? questionDto.getImage().getUrl() : null;

        questionService.removeQuestion(questionId);

        if (url != null && Files.exists(getTargetLocation(url))) {
            Files.delete(getTargetLocation(url));
        }

        return ResponseEntity.ok().build();
    }

    @PostMapping("/questions/{questionId}/set-status")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#questionId, 'QUESTION.ACCESS')")
    public ResponseEntity questionSetStatus(@PathVariable Integer questionId, @Valid @RequestBody String status) {
        logger.debug("questionSetStatus questionId: {}: ", questionId);
        questionService.questionSetStatus(questionId, Question.Status.valueOf(status));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/questions/{questionId}/change-status")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#questionId, 'QUESTION.ACCESS')")
    public QuestionDto questionChangeStatus(@PathVariable Integer questionId, @Valid @RequestBody QuestionDto question) {
        logger.debug("questionChangeStatus questionId: {}: ", questionId);
        Question.Status status = Question.Status.valueOf(question.getStatus());
        String justification = question.getJustification();

        return questionService.questionChangeStatus(questionId, status, justification);
        //ResponseEntity.ok().build();
    }

    @PostMapping("/questions/{questionId}/approve-question")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#questionId, 'QUESTION.ACCESS')")
    public QuestionDto approveQuestion(@PathVariable Integer questionId) {

        return questionService.questionSetApproved(questionId);
    }

    @PutMapping("/questions/{questionId}/image")
    @PreAuthorize("hasRole('ROLE_TEACHER') and hasPermission(#questionId, 'QUESTION.ACCESS')")
    public String uploadImage(@PathVariable Integer questionId, @RequestParam("file") MultipartFile file) throws IOException {
        logger.debug("uploadImage  questionId: {}: , filename: {}", questionId, file.getContentType());

        QuestionDto questionDto = questionService.findQuestionById(questionId);
        String url = questionDto.getImage() != null ? questionDto.getImage().getUrl() : null;
        if (url != null && Files.exists(getTargetLocation(url))) {
            Files.delete(getTargetLocation(url));
        }

        int lastIndex = Objects.requireNonNull(file.getContentType()).lastIndexOf('/');
        String type = file.getContentType().substring(lastIndex + 1);

        questionService.uploadImage(questionId, type);

        url = questionService.findQuestionById(questionId).getImage().getUrl();
        Files.copy(file.getInputStream(), getTargetLocation(url), StandardCopyOption.REPLACE_EXISTING);

        return url;
    }

    @PutMapping("/questions/{questionId}/topics")
    @PreAuthorize("hasRole('ROLE_TEACHER') or hasRole('ROLE_STUDENT') and hasPermission(#questionId, 'QUESTION.ACCESS')")
    public ResponseEntity updateQuestionTopics(@PathVariable Integer questionId, @RequestBody TopicDto[] topics) {
        questionService.updateQuestionTopics(questionId, topics);

        return ResponseEntity.ok().build();
    }

    private Path getTargetLocation(String url) {
        String fileLocation = figuresDir + url;
        return Paths.get(fileLocation);
    }
}