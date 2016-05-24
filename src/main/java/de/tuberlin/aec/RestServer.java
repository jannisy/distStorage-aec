package de.tuberlin.aec;

import java.io.IOException;
import fi.iki.elonen.NanoHTTPD;

/**
 * Very simple web server which takes HTTP requests for the REST API,
 * passes them to this node of the Key-Value store and returns a response.
 *
 */
public class RestServer extends NanoHTTPD {
	

	/** the node api */
    private DistoNodeApi api;
    
	public RestServer(int port, DistoNodeApi api) throws IOException {
        super(port);
        this.api = api;
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


    private Response servePut(IHTTPSession session) {
    	String uri = session.getUri();
    	String[] uriParts = uri.split("/");
    	if(uriParts.length < 4) {
    		return this.malformedRequest(session);
    	}
    	
		String key = uriParts[2];
		String value = uriParts[3];
		// TODO status
		this.api.put(key, value);

		String json = "{status: 'OK'}";
        return newFixedLengthResponse(json);
    }
    private Response serveDelete(IHTTPSession session) {
    	String uri = session.getUri();
    	String[] uriParts = uri.split("/");
    	if(uriParts.length < 3) {
    		return this.malformedRequest(session);
    	}
		String key = uriParts[2];
		this.api.delete(key);
        return newFixedLengthResponse("TODO");
    }
    private Response serveGet(IHTTPSession session) {
    	String uri = session.getUri();
    	String[] uriParts = uri.split("/");
    	if(uriParts.length < 3) {
    		return this.malformedRequest(session);
    	}
		String key = uriParts[2];
		String value = this.api.get(key);
        return newFixedLengthResponse("TODO");
    }
    private Response serveHelp(IHTTPSession session) {
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
    
    private Response malformedRequest(IHTTPSession session) {
        String msg = "<html><body><h1>Malformed Request</h1>\n";
        return newFixedLengthResponse(msg + "</body></html>\n");
    }
}
