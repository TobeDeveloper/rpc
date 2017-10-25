package org.myan.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by myan on 2017/10/25.
 * Intellij IDEA
 */
public class TimeClient {

    public static void main(String[] args) {
        try (
                Socket socket = new Socket("127.0.0.1", 8000);
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
        ) {
            out.println("QUERY TIME");
            String resp = reader.readLine();
            System.out.println("Now time:" + resp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
