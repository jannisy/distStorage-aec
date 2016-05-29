package de.tuberlin.aec;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import de.tuberlin.aec.communication.PutResponse;
import fi.iki.elonen.NanoHTTPD;

/**
 * Very simple web server which takes HTTP requests for the REST API,
 * passes them to this node of the Key-Value store and returns a response.
 *
 */
public class RestServer extends NanoHTTPD {
	

	/** the node api */
    private DistoNodeApi api;
    private static int NANOHTTPD_SOCKET_READ_TIMEOUT = 20000;
    
	public RestServer(int port, DistoNodeApi api) throws IOException {
        super(port);
        this.api = api;
        start(NANOHTTPD_SOCKET_READ_TIMEOUT, false);
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
    	} else if(uri.startsWith("/paths")) {
    		return servePaths(session);
    	} else if(uri.startsWith("/servers")) {
    		return serveServerList(session);
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

		PutResponse response = this.api.put(key, value);
		String json;
		if(response.isSuccess()) {
			json = "{key: '" + key + "', status: 'success'}";
		} else {
			json = "{key: '" + key + "', status: 'error', errorMsg: '" + response.getErrorMessage() + "'}";
		}
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
		String json = "{key: '" + key + "', status: 'success'}";
        return newFixedLengthResponse(json);
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
    private Response servePaths(IHTTPSession session) {
    	String file;
		try {
			file = new String(Files.readAllBytes(Paths.get("paths.xml")));
		} catch (IOException e) {
			file = "An IOException occurred.";
			e.printStackTrace();
		}
        return newFixedLengthResponse(file);
    }
    private Response serveServerList(IHTTPSession session) {
    	String file;
		try {
			file = new String(Files.readAllBytes(Paths.get("serverlist")));
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
