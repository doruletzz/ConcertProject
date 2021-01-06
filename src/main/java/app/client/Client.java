package app.client;


import app.Application;
import com.google.common.base.Stopwatch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.swing.text.html.parser.Parser;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.*;

public class Client extends Application {

    public static void main(String[] args) throws Exception {
        launch(args);
    }

    private static void launch(String[] args) throws Exception {
        ServicesProxy server = new ServicesProxy("localhost",5055);
        server.initializeConnection();
        server.bringDatas();
        Random rand = new Random();
        Stopwatch saleTimer =  Stopwatch.createStarted();
        Stopwatch checkTimer =  Stopwatch.createStarted();

        while (!server.isFinished()) {
            if(saleTimer.elapsed().toSeconds() % 2==0){
                String show = server.getShows().get(rand.nextInt(server.getShows().size()));
                Integer noOfSeats = rand.nextInt() % server.getFreeSeats().size();

                List<Integer> newList = new ArrayList<>();
                for (int i = 0; i < server.getFreeSeats().size(); i++) {

                    // take a random index between 0 to size
                    // of given List
                    int randomIndex = rand.nextInt(server.getFreeSeats().size());

                    // add element in temporary list
                    newList.add(server.getFreeSeats().get(randomIndex));

                    // Remove selected element from orginal list
                    server.getFreeSeats().remove(randomIndex);
                }

                System.out.println("Vanzare reusita: "+server.buySeats(show,noOfSeats,newList));
                server.bringDatas();
            }
            if(checkTimer.elapsed().toSeconds() % 5==0){
                System.out.println("Consistenta: " + server.checkConsistency());
                server.bringDatas();
            }

        }
    }



}