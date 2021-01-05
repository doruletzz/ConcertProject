package app.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.springframework.stereotype.Component;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.sql.Date;
import java.sql.Time;

@Entity
@Component
public class CheckData {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long idCheckData;

    private Date date;
    private Time time;
    private String response;

    public CheckData() {
    }

    public Long getIdCheckData() {
        return idCheckData;
    }

    public void setIdCheckData(Long idCheckData) {
        this.idCheckData = idCheckData;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
