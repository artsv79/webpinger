package com.artsv.webpinger;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Sergey Artamonov
 */
public class Reporter {
    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");

    private final Logger logger = LoggerFactory.getLogger(Reporter.class);

    private String postUrlString;

    public void configure(Config config) {
        if (config.report != null) {
            postUrlString = config.report.postUrl;
        }
        logger.info("reports will be posted to {}", postUrlString);
    }

    public void report(String host, HostResult hostResult) {
        logger.debug("report on {}", host);

        try {
            ObjectMapper om = new ObjectMapper(new JsonFactory());
            String jsonResults = om.writeValueAsString(hostResult);
            postResults(host, jsonResults);
            log(host, jsonResults);
        } catch (JsonProcessingException e) {
            logger.error("Error serializing to json: {}", e.getMessage());
        }
    }

    private void log(String host, String json) {
        logger.warn("Host {} has connection troubles. Last result: {}", host, json);
    }

    private void postResults(String host, String jsonResults) {

        if (postUrlString == null || postUrlString.isEmpty())
            return;

        logger.info("Posting to report server...");

        try {
            OkHttpClient client = new OkHttpClient();

            RequestBody body = RequestBody.create(jsonResults, JSON);
            Request request = new Request.Builder()
                    .url(postUrlString)
                    .post(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.body() != null) {
                    String serverResponse = response.body().string();
                    if (response.code() != 200) {
                        logger.error("Error posting report. Http status: {}\n{}",response.code(), serverResponse);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error reporting to server: {}", e.getMessage());
        }
    }
}
