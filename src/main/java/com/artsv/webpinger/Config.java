package com.artsv.webpinger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * @author Sergey Artamonov
 */
public class Config {
    public static final String DefaultconfigFileName = "pinger.yml";

    public Config() {
    }

    public List<String> hosts;
    public ICMPConfig icmp;
    public TCPConfig tcp;
    public TracertConfig tracert;
    public ReporterConfig report;


    public static class ICMPConfig {
        public int delay;
        public String command;

        @Override
        public String toString() {
            return "ICMPConfig{" +
                    "delay=" + delay +
                    ", command='" + command + '\'' +
                    '}';
        }
    }

    public static class TCPConfig {
        public int delay;
        public int timeout;

        @Override
        public String toString() {
            return "TCPConfig{" +
                    "delay=" + delay +
                    ", timeout=" + timeout +
                    '}';
        }
    }

    public static class TracertConfig {
        public int delay;
        public String command;

        @Override
        public String toString() {
            return "TracertConfig{" +
                    "delay=" + delay +
                    ", command='" + command + '\'' +
                    '}';
        }
    }

    public static class ReporterConfig {
        public String postUrl;

        @Override
        public String toString() {
            return "ReporterConfig{" +
                    "postUrl='" + postUrl + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "Config{" +
                "hosts=" + hosts +
                ", icmp=" + icmp +
                ", tcp=" + tcp +
                ", tracert=" + tracert +
                ", report=" + report +
                '}';
    }

    public static Config getConfig(String configFilePathName) {
        final Logger logger = LoggerFactory.getLogger(Config.class);

        ObjectMapper om = new ObjectMapper(new YAMLFactory());

        Config config = new Config();
        URL configFileUrl = null;
        try {
            File configFile = new File(configFilePathName);
            if (!configFile.exists()) {
                logger.info("Can't find config file {}. Looking up for config in resources..", configFile.getAbsolutePath());
                configFileUrl = Config.class.getClassLoader().getResource(DefaultconfigFileName);
            } else {
                configFileUrl = new File(configFilePathName).toURI().toURL();
            }
            logger.debug("Reading config from {}", configFile.getAbsolutePath());
            config = om.readValue(configFileUrl, Config.class);
        } catch (IOException e) {
            logger.error("Error reading config file {}: {}", configFileUrl, e);
        }
        return config;
    }
}

