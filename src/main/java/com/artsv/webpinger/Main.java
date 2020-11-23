package com.artsv.webpinger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Sergey Artamonov
 */
public class Main {

    public static void main(String[] args) {
        final Logger logger = LoggerFactory.getLogger(Main.class);

        logger.info("Simple Ping Application");

        Config config = Config.getConfig("pinger.yml");
        logger.info("Config: {}", config);

        List<String> hosts = config.hosts;

        Storage storage = new Storage();
        Reporter reporter = new Reporter();
        reporter.configure(config);

        List<HostChecker> pingers = Arrays.asList(
                new ICMPPinger(),
                new TCPPinger(),
                new Tracerouter()
        );
        pingers.forEach(pinger -> pinger.configure(config));

        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(hosts.size() * pingers.size());

        logger.info("Start pinging {}", hosts.toString());

        pingers.forEach(pinger -> {
            hosts.forEach(host -> executor.scheduleAtFixedRate(
                    () -> {
                        String result = pinger.checkHost(host);

                        pinger.saveToStorage(storage, host, result);

                        if (pinger.shouldReport(result)) {
                            reporter.report(host, storage.getLatestForHost(host));
                        }
                    },
                    0,
                    pinger.checkInterval(),
                    TimeUnit.MILLISECONDS
            ));
        });
    }

}
