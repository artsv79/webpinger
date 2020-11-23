package com.artsv.webpinger;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Sergey Artamonov
 */
public class Storage {

    Map<String, HostResult> lastResultsPerHost = Collections.synchronizedMap(new HashMap<>());

    public void storeICMPResults(String host, String result) {
        lastResultsPerHost.compute(
                host,
                (s, oldResult) -> {
                    if (oldResult != null) {
                        oldResult.ICMPResult = result;
                        return oldResult;
                    } else {
                        HostResult newResult = new HostResult();
                        newResult.Host = host;
                        newResult.ICMPResult = result;
                        return newResult;
                    }
                });
    }
    public void storeTCPResults(String host, String result) {
        lastResultsPerHost.compute(
                host,
                (s, oldResult) -> {
                    if (oldResult != null) {
                        oldResult.TCPResults = result;
                        return oldResult;
                    } else {
                        HostResult newResult = new HostResult();
                        newResult.Host = host;
                        newResult.TCPResults = result;
                        return newResult;
                    }
                });
    }
    public void storeTracertResults(String host, String result) {
        lastResultsPerHost.compute(
                host,
                (s, oldResult) -> {
                    if (oldResult != null) {
                        oldResult.TracertResults = result;
                        return oldResult;
                    } else {
                        HostResult newResult = new HostResult();
                        newResult.Host = host;
                        newResult.TracertResults = result;
                        return newResult;
                    }
                });
    }

    public HostResult getLatestForHost(String host) {
        return lastResultsPerHost.computeIfAbsent(
                host,
                s -> {
                    HostResult hostResult = new HostResult();
                    hostResult.Host = host;
                    return hostResult;
                }
        );
    }
}
