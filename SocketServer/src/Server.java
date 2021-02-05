
/***
 * SEN306 Group  7 Project. 
 * February 4th 2021
 * - Emmanuel Segun-Lean
 * - Maxwell Ogalabu
 * - Shugaba Wuta
 * - Judith Ogoh
 * - Lloyd Ochukenyi
 * School: American University of Nigeria.
 * Instructor: Dr. Ignace Djitog
 */
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private ServerSocket server;
    private static LuggageSystem luggageSystem;

    public static void log(String message) {
        System.out.println(message);
    }

    /**
     * Send stuff to Client
     * 
     * @param writer  PrintWriter
     * @param status  SUCCESS or ERROR
     * @param message the message like 'Luggage fetched successfully!'
     * @param data    if there's any payload this is JSON of the requested data.
     */
    public static void send(PrintWriter writer, String status, String message, String data) {
        writer.println(status + " | " + message + " | " + data);
    }

    public void listen() {
        try {
            server = new ServerSocket(5555);

            Server.luggageSystem = LuggageSystem.getLuggageSystem();

            Server.luggageSystem.loadData();
        } catch (IOException e) {
            System.out.println("Could not listen on port 5555 =>" + e);
            System.exit(-1);
        }
        while (true) {

            try {
                System.out.println("Waiting for connection");
                final Socket socket = server.accept();

                final InputStream inputStream = socket.getInputStream();
                final InputStreamReader streamReader = new InputStreamReader(inputStream);

                OutputStream output = socket.getOutputStream();
                PrintWriter writer = new PrintWriter(output, true);

                BufferedReader br = new BufferedReader(streamReader);

                // readLine blocks until line arrives or socket closes, upon which it returns
                // null
                String line = null;
                while ((line = br.readLine()) != null) {
                    log(line);
                    handleRequest(line, writer);
                }

            } catch (IOException e) {
                LuggageSystem.getLuggageSystem().saveJsonData();
                System.out.println("Accept failed: 5555; Client disconnected or something :/");
                System.exit(-1);
            }
        }
    }

    private static void handleRequest(String request, PrintWriter writer) {
        // Because "|" is a special character in .split() I have to use \\ to escape it.
        // The Client sends requests in this format => ACTION | RESOURCE | PAYLOAD/QUERY
        String[] requestLines = request.split(" \\| ");

        String method = requestLines[0]; // e.g GET
        String resource = requestLines[1]; // e.g person
        String query = "-";

        if (requestLines.length > 2) {
            query = requestLines[2]; // e.g or payload {name: 'Emmanuel'}
        }

        log("Action: " + method);
        log("Resource: " + resource);
        log("Payload/Query: " + query);

        if (method.equals("GET")) {
            if (resource.equals("say-hello")) {
                send(writer, "SUCCESS", "i see you! :)", "-");
                return;
            }

            if (resource.equals("luggages")) {
                String ls = Server.luggageSystem.getLuggages();
                int count = Server.luggageSystem.luggagesCount();

                send(writer, "SUCCESS", "Luggages fetched sucessffully!",
                        "{ \"luggages\" :" + ls + ", \"count\" :" + count + "}");
                return;
            }

            if (resource.equals("report")) {
                String report = Server.luggageSystem.printReport();

                log(report);

                send(writer, "SUCCESS", "Report Fetched Successfully!", report);
                return;
            }

            send(writer, "ERROR", "Unknown function!", "-");
            return;
        } else if (method.equals("POST")) {
            if (resource.equals("luggage")) {
                log(query);
                Boolean res = Server.luggageSystem.checkinLuggage(query);
                send(writer, "SUCCESS", "Luggage created sucessffully!", res.toString());
                return;
            }

            if (resource.equals("checkout-luggage")) {
                log(query);

                int luggageId;
                Boolean res = false;
                try {
                    luggageId = Integer.parseInt(query);
                    log("Id => " + luggageId);
                    res = Server.luggageSystem.checkoutLuggage(luggageId);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

                writer.println("SUCCESS | " + "Luggage Checked Out successfully!" + res);
                return;
            }

            if (resource.equals("search-luggages")) {
                log(query);

                String luggage;
                try {
                    luggage = Server.luggageSystem.findLuggageByOwnerlastname(query);

                    send(writer, "SUCCESS", "Luggage searched successfully!", luggage);
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                    send(writer, "ERROR", "Luggage not found!", "{}");
                    return;
                }
            }

            send(writer, "ERROR", "Unknown function!", "-");
            return;
        } else {
            send(writer, "ERROR", "Action/Method not supported!", "-");
            return;
        }
    }

    /* creating new server and call it's listen method */
    public static void main(String[] args) {
        new Server().listen();

        // Add shutdown hook...

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                log("Gracefully shutting down and saving Luggage System Data :)");
                LuggageSystem.getLuggageSystem().saveJsonData();
            }
        });
    }
}