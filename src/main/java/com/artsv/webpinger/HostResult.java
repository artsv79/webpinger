package com.artsv.webpinger;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * @author Sergey Artamonov
 * Data Object
 */

public class HostResult {
    @JsonProperty(value = "host")
    public String Host;
    @JsonProperty(value = "icmp_ping")
    public String ICMPResult;
    @JsonProperty(value = "tcp_ping")
    public String TCPResults;
    @JsonProperty(value = "trace")
    public String TracertResults;

    @Override
    public String toString() {
        return "HostResult{" +
                "Host='" + Host + '\'' +
                ", ICMPResult='" + ICMPResult + '\'' +
                ", TCPResults='" + TCPResults + '\'' +
                ", TracertResults='" + TracertResults + '\'' +
                '}';
    }
}
