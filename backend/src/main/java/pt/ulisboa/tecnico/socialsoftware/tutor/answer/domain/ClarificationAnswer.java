package pt.ulisboa.tecnico.socialsoftware.tutor.answer.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.ClarificationAnswerDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain.Clarification;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.User;

import javax.persistence.*;

@Entity
@Table(name = "clarification_answer")
public class ClarificationAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name="clarification_id")
    private Clarification clarification;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;


    @Column(name = "answer")
    private String answer;


    public ClarificationAnswer(){}

    public ClarificationAnswer(String answer){
        this.answer = answer;
    }

    public ClarificationAnswer(ClarificationAnswerDto clarificationAnswerDto){
        this.answer = clarificationAnswerDto.getAnswer();
        this.id = clarificationAnswerDto.getId();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Clarification getClarification() {
        return clarification;
    }

    public void setClarification(Clarification clarification) {
        if(this.clarification != null) this.clarification.setClarificationAnswer(null);
        this.clarification = clarification;
        this.clarification.setClarificationAnswer(this);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        if(this.user != null) this.user.removeClarificationAnswer(this);
        this.user = user;
        this.user.addClarificationAnswer(this);
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }


}
