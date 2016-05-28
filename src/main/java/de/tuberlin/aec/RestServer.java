package de.tuberlin.aec;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

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

		String json = "{status: 'TODO'}";
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

		String json = "{\n\tkey: '" + key + "',\n\tvalue: '" + value + "'\n}";
        return newFixedLengthResponse(json);
    }
    private Response serveHelp(IHTTPSession session) {
    	String file;
		try {
			file = new String(Files.readAllBytes(Paths.get("rest.html")));
		} catch (IOException e) {
			file = "An IOException occurred.";
			e.printStackTrace();
		}
        return newFixedLengthResponse(file);
    }
    
    private Response malformedRequest(IHTTPSession session) {
        String msg = "<html><body><h1>Malformed Request</h1>\n";
        return newFixedLengthResponse(msg + "</body></html>\n");
    }
}
