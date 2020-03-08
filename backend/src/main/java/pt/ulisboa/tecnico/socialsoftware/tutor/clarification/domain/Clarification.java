package pt.ulisboa.tecnico.socialsoftware.tutor.clarification.domain;

import javax.persistence.*;

@Entity
@Table(name = "clarification")
public class Clarification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

}
