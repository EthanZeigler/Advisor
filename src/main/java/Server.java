import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpsServer;

import java.io.*;
import java.net.InetSocketAddress;

public class Server {
    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/test", new MyHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
    }

    static class MyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {

            Thread thread = new Thread(() -> {
                try {
                    t.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                    t.getResponseHeaders().add("Content-Type", "text/plain");

                    if (t.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                        t.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, OPTIONS");
                        t.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type,Authorization");
                        t.sendResponseHeaders(204, -1);
                        return;
                    }

                    InputStream responseBody = t.getRequestBody();
                    String input = new String(responseBody.readAllBytes());

                    String output = ScheduleMasterCLI.main(new String[]{input});
                    output = output.replace("\n", "<br>");
                    //output = output.replace(" ", "-");
                    System.out.println(output.getBytes().length);

                    byte[] bs = output.getBytes("UTF-8");
                    t.sendResponseHeaders(200, bs.length);
                    OutputStream os = t.getResponseBody();
                    os.write(bs);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }); // myRunnable does your calculations

            long startTime = System.currentTimeMillis();
            long endTime = startTime + 7000L;

            thread.start(); // Kick off calculations

            while (System.currentTimeMillis() < endTime) {
                // Still within time theshold, wait a little longer
                try {
                    Thread.sleep(500L);  // Sleep 1/2 second
                } catch (InterruptedException e) {
                    // Someone woke us up during sleep, that's OK
                }
            }

            thread.interrupt();  // Tell the thread to stop
            try {
                thread.join();       // Wait for the thread to cleanup and finish
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Cleaning up");
        }
    }
}
