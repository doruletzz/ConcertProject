package app.response;

import java.util.List;

public class dataResponse implements Response {
    private List<String> shows;
    private Integer noOfSeats;
    private List<Integer> freeSeats;

    public List<String> getShows() {
        return shows;
    }

    public void setShows(List<String> shows) {
        this.shows = shows;
    }

    public Integer getNoOfSeats() {
        return noOfSeats;
    }

    public void setNoOfSeats(Integer noOfSeats) {
        this.noOfSeats = noOfSeats;
    }

    public List<Integer> getFreeSeats() {
        return freeSeats;
    }

    public void setFreeSeats(List<Integer> freeSeats) {
        this.freeSeats = freeSeats;
    }
}
