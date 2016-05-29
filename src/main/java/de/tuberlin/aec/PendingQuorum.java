package de.tuberlin.aec;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * Created by joh-mue on 29/05/16.
 */
public class PendingQuorum extends PendingRequest {
    private int qsize;
    private int numberOfParticipants;

    public PendingQuorum(String startNode, String key, String value, boolean expectResponse, int qsize) {
        super(startNode, key, value, expectResponse);
        this.qsize = qsize;
    }

    @Override
    public void addNodesToNecessaryResponses(List<String> list) {
        super.addNodesToNecessaryResponses(list);
        this.numberOfParticipants = list.size();
    }

    @Override
    public void removeNodeFromNecessaryResponses(InetSocketAddress a) {
        super.removeNodeFromNecessaryResponses(a);
    }

    /**
     * Returns true if the quorum is concluded (when enough nodes have responded).
     * @return true if quorum conclued and false otherwise
     */
    @Override
    public boolean responsesPending() {
        return !(numberOfParticipants - getNumberOfNecessaryResponses() >= qsize);
    }
}
