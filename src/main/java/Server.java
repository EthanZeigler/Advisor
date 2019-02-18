import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

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
            InputStream responseBody = t.getRequestBody();
            String input = new String(responseBody.readAllBytes());

            String output = ScheduleMasterCLI.main(new String[]{input});
            System.out.println(output.length());

            t.sendResponseHeaders(200, output.length());
            try (BufferedOutputStream out = new BufferedOutputStream(t.getResponseBody())) {
                try (ByteArrayInputStream bis = new ByteArrayInputStream(output.getBytes())) {
                    byte [] buffer = new byte [1024];
                    int count;
                    while ((count = bis.read(buffer)) != -1) {
                        out.write(buffer, 0, count);
                    }
                }
            }


//            OutputStream os = t.getResponseBody();
//            try {
//                os.write(output.getBytes());
//            } catch(Exception e) {
//                e.printStackTrace();
//            }
        }
    }
}
