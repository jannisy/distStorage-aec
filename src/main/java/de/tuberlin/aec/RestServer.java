package de.tuberlin.aec;

import java.io.IOException;
import fi.iki.elonen.NanoHTTPD;

/**
 * Very simple web server which takes HTTP requests for the REST API,
 * passes them to this node of the Key-Value store and returns a response.
 *
 */
public class RestServer extends NanoHTTPD {
	

    public RestServer(int port) throws IOException {
        super(port);
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        System.out.println("\nREST API server running: http://localhost:" + port + "/ \n");
    }

    @Override
    public Response serve(IHTTPSession session) {
    	String uri = session.getUri();
    	if(uri.startsWith("/put")) {
    		return servePut(session);
    	} else if(uri.startsWith("/get")) {
    		return serveGet(session);
    	} else if(uri.startsWith("/delete")) {
    		return serveDelete(session);
        } else {
        	return serveHelp(session);
        }
    }
    

    public Response servePut(IHTTPSession session) {
        return newFixedLengthResponse("TODO");
    }
    public Response serveDelete(IHTTPSession session) {
        return newFixedLengthResponse("TODO");
    }
    public Response serveGet(IHTTPSession session) {
        return newFixedLengthResponse("TODO");
    }
    public Response serveHelp(IHTTPSession session) {
        String msg = "<html><body><h1>Key-Value Store: REST API</h1>\n";
        msg += "Available functions:";
        msg += "<ul>";
        msg += "<li>/put/key/value<br><i>Stores the given key-value pair.</i></li>";
        msg += "<li>/get/key<br><i>Returns the value with the given key.</i></li>";
        msg += "<li>/delete/key<br><i>Deletes the value with the given key.</i></li>";
        msg += "</ul>";
        return newFixedLengthResponse(msg + "</body></html>\n");
    	/*
        Map<String, String> parms = session.getParms();
        if (parms.get("username") == null) {
            msg += "<form action='?' method='get'>\n  <p>Your name: <input type='text' name='username'></p>\n" + "</form>\n";
        } else {
            msg += "<p>Hello, " + parms.get("username") + "!</p>";
        }
        */
    }
}
