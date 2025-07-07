package org.example;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;

public class Main {
    private static final String MODULE_PATH = "simple-http-server";

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8080);
        System.out.println("Server started on port 8080");

        while(true) {
            Socket clientSocket = serverSocket.accept();
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

            String request = in.readLine();
            System.out.println("request: " + request);
            String fileStr = request.substring(request.indexOf('/') + 1,request.indexOf(' ', request.indexOf('/')));
            System.out.println("file: " + fileStr);

            File file = new File(MODULE_PATH + "/static/" + fileStr);

            if(file.exists() && file.isFile()) {

                String contentType = Files.probeContentType(file.toPath());
                out.write("HTTP/1.1 200 (OK)\r\n");
                out.write("Content-Type: " + contentType + "; charset=UTF-8\r\n");
                out.write("Content-Length: " + file.length() + "\r\n");
                out.flush();

                System.out.println("Content-Type: " + contentType);
                System.out.println("Content-Length: " + file.length() + "bytes");
                System.out.println("HTTP/1.1 200 (OK)");
            }
            else {
                out.write("HTTP/1.1 404 (Not Found)\r\n");
                System.out.println("HTTP/1.1 404 (Not Found)");
            }
            clientSocket.close();
        }

    }
}