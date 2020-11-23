package com.artsv.webpinger;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Sergey Artamonov
 */
class StorageTest {

    @Test
    void storeICMPResults() {
        HostResult expected = new HostResult();
        expected.Host = "yahoo.com";
        expected.ICMPResult =  "ping 10 ms";

        Storage storage = new Storage();

        storage.storeICMPResults("yahoo.com", "ping 10 ms");
        assertEquals(expected.toString(), storage.getLatestForHost("yahoo.com").toString());

        storage.storeICMPResults("yahoo.com", "ping 100 ms");
        expected.ICMPResult = "ping 100 ms";
        assertEquals(expected.toString(), storage.getLatestForHost("yahoo.com").toString());
    }

    @Test
    void storeTCPResults() {
        HostResult expected = new HostResult();
        expected.Host = "yahoo2.com";
        expected.TCPResults =  "Http 200 OK";

        Storage storage = new Storage();

        storage.storeTCPResults("yahoo2.com", "Http 200 OK");
        assertEquals(expected.toString(), storage.getLatestForHost("yahoo2.com").toString());

        storage.storeTCPResults("yahoo2.com", "Http 200 OK");
        expected.TCPResults = "Http 200 OK";
        assertEquals(expected.toString(), storage.getLatestForHost("yahoo2.com").toString());
    }

    @Test
    void storeTracertResults() {
        HostResult expected = new HostResult();
        expected.Host = "yahoo.com";
        expected.TracertResults =  "ping 10 ms";

        Storage storage = new Storage();

        storage.storeTracertResults("yahoo.com", "ping 10 ms");
        assertEquals(expected.toString(), storage.getLatestForHost("yahoo.com").toString());

        storage.storeTracertResults("yahoo.com", "ping 100 ms");
        expected.TracertResults = "ping 100 ms";
        assertEquals(expected.toString(), storage.getLatestForHost("yahoo.com").toString());
    }

    @Test
    void getLatestForHost() {
        HostResult expected = new HostResult();
        expected.Host = "none.com";
        Storage storage = new Storage();

        storage.storeICMPResults("yahoo.com", "ping 10 ms");
        storage.storeTCPResults("yahoo2.com", "ping 100 ms");
        storage.storeTracertResults("yahoo3.com", "ping 1000 ms");
        assertEquals(expected.toString(), storage.getLatestForHost("none.com").toString());
    }
}