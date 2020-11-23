package com.artsv.webpinger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

/**
 * @author Sergey Artamonov
 */
public abstract class HostChecker {

    public abstract void configure(Config config);

    public abstract String checkHost(String host);

    public abstract void saveToStorage(Storage storage, String host, String result);

    /**
     *
     * @param result command output to test for possible problems
     * @return does result signals about eny errors which soudl be reported to report server
     */
    public abstract boolean shouldReport(String result);

    public abstract int checkInterval();

    protected static String executeCommand(String[] args) throws IOException, InterruptedException, ExecutionException {
        final Logger logger = LoggerFactory.getLogger(HostChecker.class);

        ExecutorService pipeReaders = Executors.newFixedThreadPool(2);

        ProcessBuilder processBuilder = new ProcessBuilder(args);
        Process process = processBuilder.start();

        BufferedReader inputs = new BufferedReader(new InputStreamReader(process.getInputStream()));
        BufferedReader errors = new BufferedReader(new InputStreamReader(process.getErrorStream()));

        FutureTask<String> procOut = wrapOutputStream(inputs);
        pipeReaders.submit(procOut);

        FutureTask<String> procErr = wrapOutputStream(errors);
        pipeReaders.submit(procErr);

        process.waitFor();

        int exitValue = process.exitValue();
        String err = procErr.get();
        String out = procOut.get();

        if (exitValue != 0) {
            logger.error("Command '{}' exits: {}. Error text: {}", args, exitValue, err);
            return err;
        } else {
            logger.debug("Command '{}' exits: {}", args[0], exitValue);
            return out;
        }
    }

    protected static FutureTask<String> wrapOutputStream(BufferedReader inputs) {
        return new FutureTask<>(() -> {
            StringBuilder result = new StringBuilder();
            for (String s = inputs.readLine(); s != null; s = inputs.readLine()) {
                result.append(s);
                result.append("\n");
            }
            return result.toString();
        });
    }

}
