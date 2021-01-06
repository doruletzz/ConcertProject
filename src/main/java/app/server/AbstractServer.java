package app.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.ServerException;

public abstract class AbstractServer {
    private int port;
    private ServerSocket server = null;

    public AbstractServer(int port) {
        this.port = port;
    }

    public void start() throws ServerException {
        try {
            server = new ServerSocket(port);
            while (true) {
                System.out.println("waiting for clients...");
                Socket client = server.accept();
                System.out.println("client connected...");
                processRequest(client);
            }
        } catch (IOException e) {
            throw new ServerException("starting server error ", e);
        } finally {
            stop();
        }
    }

    protected abstract void processRequest(Socket client);

    public void stop() throws ServerException {
        try {
            server.close();
        } catch (IOException e) {
            throw new ServerException("closing server error ", e);
        }
    }
}
