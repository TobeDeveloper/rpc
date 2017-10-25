package org.myan.io;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by myan on 2017/10/25.
 * Intellij IDEA
 */
public class TimeServer {

    public static void main(String[] args) {

        try (ServerSocket server = new ServerSocket(8000)){

            while(true) {
                Socket socket = server.accept();
                ExecutorService executor = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), 50,
                        120, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10000));
                executor.execute(new ServerHandler(socket));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

class ServerHandler implements Runnable {
    private Socket socket;

    ServerHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
                PrintWriter out = new PrintWriter(this.socket.getOutputStream(), true)
        ) {
            String body;
            while(true) {
                body = reader.readLine();
                if(body == null)
                    break;
                String result = "QUERY TIME".equalsIgnoreCase(body) ? new Date(System.currentTimeMillis()).toString() : "INVALID QUERY";
                out.println(result);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
