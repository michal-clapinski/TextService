package rtb;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.logging.Logger;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Response.Status;
import fi.iki.elonen.util.ServerRunner;


public class Server extends NanoHTTPD {

    private static final Logger LOG = Logger.getLogger(Server.class.getName());

    public void Start() {
        ServerRunner.run(Server.class);
    }

    public Server() {
        super(8000);
    }

    @Override
    public Response serve(IHTTPSession session) {
        Method method = session.getMethod();
        if (method != Method.GET) {
            return newFixedLengthResponse(Status.METHOD_NOT_ALLOWED, MIME_PLAINTEXT, "Only GET allowed");
        }

        Map<String, String> parms = session.getParms();
        if (parms.get("user") == null) {
            return newFixedLengthResponse(Status.BAD_REQUEST, MIME_PLAINTEXT, "Missing user param");
        }

        String uri = session.getUri();
        int lineNumber;
        try {
            lineNumber = Integer.parseInt(uri.substring(1));
        } catch (NumberFormatException e) {
            return newFixedLengthResponse(Status.BAD_REQUEST, MIME_PLAINTEXT, "Uri should be an integer");
        }

        if (lineNumber < 0) {
            return newFixedLengthResponse(Status.BAD_REQUEST, MIME_PLAINTEXT, "Uri should be a non-negative integer");
        }

        //Server.LOG.info(method + " '" + uri + "' ");
        //parms.get("user")

        FileTraverser traverser = new FileTraverser();
        String line;
        try {
            line = traverser.getLine(lineNumber);
        } catch(Exception e) {
            return newFixedLengthResponse(Status.BAD_REQUEST, MIME_PLAINTEXT, "Line number too big");
        }

        String msg = String.join(
            System.getProperty("line.separator"),
            "{",
            "\"date\": \"" + ZonedDateTime.now().format(DateTimeFormatter.ISO_INSTANT) + "\"",
            "\"line\": \"" + line + "\"",
            "}",
            ""
        );
        return newFixedLengthResponse(msg);
    }
}
