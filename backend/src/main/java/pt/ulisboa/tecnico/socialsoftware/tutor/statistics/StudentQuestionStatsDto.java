package pt.ulisboa.tecnico.socialsoftware.tutor.statistics;

import java.io.Serializable;

public class StudentQuestionStatsDto implements Serializable{
    private Integer nrTotalQuestions = 0;
    private Integer nrApprovedQuestions = 0;

    public void setNrTotalQuestions(Integer nrTotalQuestions){
        this.nrTotalQuestions = nrTotalQuestions;
    }

    public Integer getNrTotalQuestions(){
        return this.nrTotalQuestions;
    }

    public void setNrApprovedQuestions(Integer nrApprovedQuestions){
        this.nrApprovedQuestions = nrApprovedQuestions;
    }

    public Integer getNrApprovedQuestions(){
        return this.nrApprovedQuestions;
    }

    @Override
    public String toString(){
        return "StudentQuestionStatsDto{" +
                "totalQuestions=" + nrTotalQuestions +
                ", approvedQuestions=" + nrApprovedQuestions +
                '}';
    }

}
