package com.artsv.webpinger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author Sergey Artamonov
 */
public class ICMPPinger extends HostChecker {
    private final Logger logger = LoggerFactory.getLogger(ICMPPinger.class);

    public static final String[] DefaultCmd = {"ping", "-c", "5"};

    private int pingInterval = 5000;
    private String[] pingCmd = DefaultCmd;

    @Override
    public void configure(Config config) {
        if (config.icmp != null) {
            pingInterval = config.icmp.delay;
            pingCmd = config.icmp.command.split(" ");
        }

        if (pingInterval <=0)
            pingInterval = 5000;

        if (pingCmd == null || pingCmd.length == 0)
            pingCmd = DefaultCmd;

    }

    @Override
    public String checkHost(String host) {
        try {
            return ping(host);
        } catch (IOException | InterruptedException | ExecutionException e) {
            logger.error("Failed to ping {}: {}", host, e.getMessage());
            return e.getMessage();
        }
    }

    @Override
    public void saveToStorage(Storage storage, String host, String result) {
        storage.storeICMPResults(host, result);
    }

    @Override
    public boolean shouldReport(String result) {
        return !result.contains(" 0% packet loss");
    }

    @Override
    public int checkInterval() {
        return pingInterval;
    }

    public String ping(String host) throws IOException, InterruptedException, ExecutionException {
        List<String> args = new ArrayList<>();
        Collections.addAll(args, pingCmd);
        args.add(host);

        return executeCommand(args.toArray(new String[]{}));
    }

}
