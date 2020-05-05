package pt.ulisboa.tecnico.socialsoftware.tutor.statistics;

import org.springframework.data.util.Pair;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ClarificationStatsDto implements Serializable {



    private Integer totalClarifications = 0;
    private Integer publicClarifications = 0;
    private Integer answeredClarifications = 0;
    private Integer reopenedClarifications = 0;
    private Map<Integer, Long> clarificationsPerMonth = new HashMap<>(); //Map of format YYYYMM, nrOfClarifications


    public ClarificationStatsDto(){}


    public Integer getTotalClarifications() {
        return totalClarifications;
    }

    public void setTotalClarifications(Integer totalClarifications) {
        this.totalClarifications = totalClarifications;
    }

    public Integer getPublicClarifications() {
        return publicClarifications;
    }

    public void setPublicClarifications(Integer publicClarifications) {
        this.publicClarifications = publicClarifications;
    }

    public Integer getAnsweredClarifications() {
        return answeredClarifications;
    }

    public void setAnsweredClarifications(Integer answeredClarifications) {
        this.answeredClarifications = answeredClarifications;
    }

    public Integer getReopenedClarifications() {
        return reopenedClarifications;
    }

    public void setReopenedClarifications(Integer reopenedClarifications) {
        this.reopenedClarifications = reopenedClarifications;
    }

    public Map<Integer, Long> getClarificationsPerMonth() {
        return clarificationsPerMonth;
    }

    public void setClarificationsPerMonth(Map<Integer, Long> clarificationsPerMonth) {
        this.clarificationsPerMonth = clarificationsPerMonth;
    }

    public void addClarificationsPerMonth(Integer yearMonth, Long total) {
        this.clarificationsPerMonth.put(yearMonth, total);
    }



}
