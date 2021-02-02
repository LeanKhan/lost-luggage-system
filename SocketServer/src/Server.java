import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public static final int port = 12345;
    private ServerSocket server;
    private static App app;

    public static void log(String message) {
        System.out.println(message);
    }

    public static void send(PrintWriter writer, String status, String message, String data) {
        writer.println(status + " | " + message + " | " + data);
    }

    public void listen() {
        try {
            server = new ServerSocket(5555);
            Server.app = new App();
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
                    // line += br.readLine();
                    // log(line);
                    handleRequest(line, writer);
                }

            } catch (IOException e) {
                System.out.println("Accept failed: 5555");
                System.exit(-1);
            }
        }
    }

    private static void handleRequest(String request, PrintWriter writer) {
        // Because "|" is a special character in .split() I have to use \\ to escape it.
        // The frontend sends requests in this format => METHOD | ACTION/ENTITY sha more
        // detail | PAYLOAD
        String[] requestLines = request.split(" \\| ");

        String method = requestLines[0]; // e.g GET
        String resource = requestLines[1]; // e.g person
        String query = requestLines[2]; // e.g or payload {name: 'Emmanuel'}

        log(method);
        log(resource);
        log(query);

        if (method.equals("GET")) {
            if (resource.equals("message")) {
                send(writer, "SUCCESS", "message recieved :)", "-");
                return;
            }

            if (resource.equals("person")) {
                String p = Server.app.getPersonJSON();

                send(writer, "SUCCESS", "Person fetched sucessffully!", p);
                return;
            }

            if (resource.equals("luggages")) {
                String ls = Server.app.getLuggages();
                int count = Server.app.luggagesCount();

                send(writer, "SUCCESS", "Luggages fetched sucessffully!",
                        "{ \"luggages\" :" + ls + ", \"count\" :" + count + "}");
                return;
            }

            send(writer, "ERROR", "Unknown function!", "-");
            return;
        } else if (method.equals("POST")) {
            if (resource.equals("person")) {
                log(query);
                Server.app.createPerson(query);
                send(writer, "SUCCESS", "Person created sucessffully!", "-");
                return;
            }

            if (resource.equals("luggage")) {
                log(query);
                Boolean res = Server.app.addLuggage(query);
                send(writer, "SUCCESS", "Luggage created sucessffully!", res.toString());
                return;
            }

            // TODO: you can add more methods!
            // for example...
            if (resource.equals("checkout-luggage")) {
                log(query);

                int luggageId;
                Boolean res = false;
                try {
                    luggageId = Integer.parseInt(query);
                    log("Id => " + luggageId);
                    res = Server.app.checkoutLuggage(luggageId);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }

                writer.println("SUCCESS | " + "Luggage Checked Out successfully!" + res);
                return;
            }

            if (resource.equals("search-luggages")) {
                log(query);

                int luggageIndex;
                String luggage;
                try {
                    luggageIndex = Server.app.searchLuggages(query);
                    luggage = Server.app.getLuggageJSON(luggageIndex);

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
            send(writer, "ERROR", "Method not supported!", "-");
            return;
        }
    }

    /* creating new server and call it's listen method */
    public static void main(String[] args) {
        new Server().listen();
    }
}
