package pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.ImageDto;

import java.io.Serializable;

public class StatementClarificationDto implements Serializable {
    private String title ;
    private String description;
    private ImageDto image; // in case the user wants to attach an image

    public StatementClarificationDto() {
    }

    public StatementClarificationDto(String _title, String _description) {
        this.title = _title;
        this.description = _description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String _title) {
        this.title = _title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String _description) {
        this.description = _description;
    }



    @Override
    public String toString() {
        return "";
    }
}
