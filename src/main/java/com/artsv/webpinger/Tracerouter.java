package com.artsv.webpinger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @author Sergey Artamonov
 */
public class Tracerouter extends HostChecker {
    private final Logger logger = LoggerFactory.getLogger(Tracerouter.class);

    public static final String[] DefaultCmd = {"traceroute"};
    private int pingInterval = 5000;
    private String[] traceCmd = DefaultCmd;

    @Override
    public void configure(Config config) {
        if (config.tracert != null) {
            pingInterval = config.tracert.delay;
            traceCmd = config.tracert.command.split(" ");
        }

        if (pingInterval <=0)
            pingInterval = 5000;

        if (traceCmd == null || traceCmd.length == 0)
            traceCmd = DefaultCmd;

    }

    @Override
    public String checkHost(String host) {
        try {
            return trace(host);
        } catch (IOException | InterruptedException | ExecutionException e) {
            logger.error("Failed to trace {}: {}", host, e.getMessage());
            return e.getMessage();
        }
    }

    @Override
    public void saveToStorage(Storage storage, String host, String result) {
        storage.storeTracertResults(host, result);
    }

    @Override
    public boolean shouldReport(String result) {
        return false;
    }

    @Override
    public int checkInterval() {
        return pingInterval;
    }

    public String trace(String host) throws IOException, InterruptedException, ExecutionException {

        List<String> args = new ArrayList<>();
        Collections.addAll(args, traceCmd);
        args.add(host);

        return executeCommand(args.toArray(new String[]{}));
    }
}
