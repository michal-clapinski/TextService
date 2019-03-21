package TextService;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Response.Status;


public class Server extends NanoHTTPD {

    private final Logger LOG = Logger.getLogger(Server.class.getName());
    private static final String uriPrefix = "/fileservice/";
    private FileTraverser traverser;

    public Server(String path) throws Exception {
        super(8000);

        LOG.setUseParentHandlers(false);
        FileHandler fh = new FileHandler("FileService.log");
        fh.setFormatter(new SimpleFormatter());
        LOG.addHandler(fh);

        traverser = new FileTraverser(path);
    }

    public void StartServing() throws Exception {
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
    }

    public void StopServing() {
        stop();
    }

    @Override
    public Response serve(IHTTPSession session) {
        String uri = session.getUri();
        if (!uri.startsWith(uriPrefix)) {
            return newFixedLengthResponse(Status.NOT_FOUND, MIME_PLAINTEXT, "Not found");
        }

        Method method = session.getMethod();
        if (method != Method.GET) {
            return newFixedLengthResponse(Status.METHOD_NOT_ALLOWED, MIME_PLAINTEXT, "Only GET allowed");
        }

        Map<String, String> parms = session.getParms();
        String user = parms.get("user");
        if (user == null) {
            return newFixedLengthResponse(Status.BAD_REQUEST, MIME_PLAINTEXT, "Missing user param");
        }

        LOG.info("Request by " + user);

        long lineNumber;
        try {
            lineNumber = Long.parseLong(uri.substring(uriPrefix.length()));
        } catch (NumberFormatException e) {
            return newFixedLengthResponse(Status.BAD_REQUEST, MIME_PLAINTEXT, "Line number should be a 64bit integer");
        }

        if (lineNumber <= 0) {
            return newFixedLengthResponse(Status.BAD_REQUEST, MIME_PLAINTEXT, "Line number should be positive");
        }

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
