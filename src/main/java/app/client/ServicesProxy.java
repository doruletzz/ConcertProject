package app.client;

import app.request.DataRequest;
import app.request.Request;
import app.request.SaleRequest;
import app.request.UpdateRequest;
import app.response.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ServicesProxy {
    private final String host;
    private final Integer port;
    //private IJocObserver client;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private Socket connection;
    private List<String> shows;
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

    private Integer noOfSeats;

    private final BlockingQueue<Response> qresponses;

    public boolean isFinished() {
        return finished;
    }

    private volatile boolean finished;

    public ServicesProxy(String host, Integer port) {
        this.host = host;
        this.port = port;
        qresponses = new LinkedBlockingQueue<>();
    }

    private void closeConnection() {
        finished = true;
        try {
            input.close();
            output.close();
            connection.close();
            //client = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendRequest(Request request) throws Exception {
        try {
            output.writeObject(request);
            output.flush();
        } catch (IOException e) {
            throw new Exception("error sending request");
        }
    }

    private Response readResponse() {
        Response response = null;
        try {
            response = qresponses.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return response;
    }

    public List<Integer> getFreeSeats() {
        return freeSeats;
    }

    public void setFreeSeats(List<Integer> freeSeats) {
        this.freeSeats = freeSeats;
    }

    private class ReaderThread implements Runnable {

        @Override
        public void run() {
            while (!finished) {
                try {
                    Object response = input.readObject();
                    System.out.println("response received " + response);
                    if (response instanceof CloseResponse) {
                        closeConnection();
                    } else {
                        try {
                            qresponses.put((Response) response);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void startReader() {
        Thread tw = new Thread(new ReaderThread());
        tw.start();
    }

    public void initializeConnection() {
        try {
            finished = false;
            connection = new Socket(host, port);
            output = new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input = new ObjectInputStream(connection.getInputStream());
            startReader();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Boolean buySeats(String show, Integer noOfTickets, List<Integer> seats) throws Exception {
        //initializeConnection();
        sendRequest(new SaleRequest(show,noOfTickets,seats));
        Response response = readResponse();
        if(response instanceof SaleResponse){
            return((SaleResponse) response).getRaspuns();
        }
        else {
            return null;
        }
    }

    public Boolean checkConsistency() throws Exception {
        sendRequest(new UpdateRequest());
        Response response = readResponse();
        if(response instanceof checkResponse){
            return ((checkResponse) response).getReaspuns();
        }
        else {
            return null;
        }
    }

    public void bringDatas() throws Exception {
        sendRequest(new DataRequest());
        Response response = readResponse();
        if(response instanceof dataResponse){
            this.shows = ((dataResponse) response).getShows();
            this.noOfSeats = ((dataResponse) response).getNoOfSeats();
            this.freeSeats = ((dataResponse) response).getFreeSeats();
        }
    }

}
