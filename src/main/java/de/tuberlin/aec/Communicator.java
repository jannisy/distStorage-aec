package de.tuberlin.aec;

import de.tub.ise.hermes.*;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by joh-mue on 24/05/16.
 */
public class Communicator implements IRequestHandler {
    private String handlerName;

    public Communicator(int port, String name) {
        RequestHandlerRegistry reg = RequestHandlerRegistry.getInstance();
        reg.registerHandler(name, this);

        try {
            Receiver receiver = new Receiver(port);
            receiver.start();
            System.out.println("Receiver started.");
        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    /** IRequestHandler implementation **/

    @Override
    public Response handleRequest(Request request) {
        System.out.println("Incoming Request.");
        request.getItems().forEach( item -> System.out.println(item));

        return new Response("Response", false, request, new ArrayList<>());
    }

    @Override
    public boolean requiresResponse() {
        return true;
    }

    /** functionality **/

    public void sendMessage(String host, int port, Request request) {
        Sender sender = new Sender(host, port);
        sender.sendMessage(request, 2000);
        System.out.println("Message sent.");
    }

    /** Accessors **/

    public String getHandlerName() {
        return handlerName;
    }

}
