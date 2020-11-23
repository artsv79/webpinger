package com.artsv.webpinger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author Sergey Artamonov
 */
public class TCPPinger extends HostChecker {
    private final Logger logger = LoggerFactory.getLogger(TCPPinger.class);

    private int pingInterval = 5000;
    private int timeout = 5000;
    @Override
    public void configure(Config config) {
        if (config.tcp != null) {
            pingInterval = config.tcp.delay;
            timeout = config.tcp.timeout;
        }

        if (pingInterval <= 0)
            pingInterval = 5000;
        if (timeout <= 0)
            timeout = 5000;
    }

    @Override
    public String checkHost(String host) {
        try {
            return ping(host);
        } catch (IOException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    @Override
    public void saveToStorage(Storage storage, String host, String result) {
        storage.storeTCPResults(host, result);
    }

    @Override
    public boolean shouldReport(String result) {
        return !result.equals("200");
    }

    @Override
    public int checkInterval() {
        return pingInterval;
    }

    String ping(String host) throws IOException {

        String protocol = "https";

        URL url = new URL(String.format("%s://%s/", protocol, host));
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(timeout);
            conn.setReadTimeout(timeout);
            conn.setUseCaches(false);
            conn.setRequestMethod("GET");

            conn.connect();
            int httpCode = conn.getResponseCode();

            logger.debug("Http ping {}: {}", url, httpCode);
            return String.format("%d", httpCode);
        } catch (IOException e) {
            logger.error("Failed to http ping {}: {}", url, e.getMessage());
            return e.getMessage();
        } finally {
            if (conn != null)
                conn.disconnect();
        }
    }
}
