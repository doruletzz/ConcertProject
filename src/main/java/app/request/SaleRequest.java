package app.request;


import java.util.List;

public class SaleRequest implements Request {
    private String show;
    private Integer noOfTickets;
    private List<Integer> seats;

    public SaleRequest(String show, Integer noOfTickets, List<Integer> seats) {
        this.show = show;
        this.noOfTickets = noOfTickets;
        this.seats = seats;
    }

    public String getShow() {
        return show;
    }

    public void setShow(String show) {
        this.show = show;
    }

    public Integer getNoOfTickets() {
        return noOfTickets;
    }

    public void setNoOfTickets(Integer noOfTickets) {
        this.noOfTickets = noOfTickets;
    }

    public List<Integer> getSeats() {
        return seats;
    }

    public void setSeats(List<Integer> seats) {
        this.seats = seats;
    }
}
