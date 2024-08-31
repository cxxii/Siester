package org.cxxii.json;

public class PongHitJson {

    private String ip;
    private int port;
    private int hits;

    public PongHitJson(String ip, int port, int hits) {
        this.ip = ip;
        this.port = port;
        this.hits = hits;
    }
}
