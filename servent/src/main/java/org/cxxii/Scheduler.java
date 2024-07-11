package org.cxxii;

import org.cxxii.messages.PingMessage;
import org.cxxii.utils.FileManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Scheduler {

    private static final Logger LOGGER = LoggerFactory.getLogger(Scheduler.class);
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public static void startPingCacheUpdates(long initialDelay, long period, TimeUnit unit) {
        scheduler.scheduleAtFixedRate(PingMessage::pingPongCache, initialDelay, period, unit);
    }

    public static void stopPingCacheUpdates() {
        scheduler.shutdown();
    }


    public static void startPongCacheUpdates(long initialDelay, long period, TimeUnit unit) {
        scheduler.scheduleAtFixedRate(FileManager::pongCacheGenerator, initialDelay, period, unit);
    }

    public static void stopPongCacheUpdates() {
        scheduler.shutdown();
    }


    // Need to change method logging - pings hosts cache
    public static void startPingHostCache(long initialDelay, long period, TimeUnit unit) {
        try {
            scheduler.scheduleAtFixedRate(() -> {
                try {
                    PingMessage.startPings();
                } catch (SocketException | UnknownHostException e) {
                    LOGGER.error("An error occurred during startPings execution", e);
                }
            }, initialDelay, period, unit);
        } catch (Exception e) {
            LOGGER.error("An error occurred while scheduling the startPings task", e);
        }
    }

    public static void stopPingHostCache() {
        scheduler.shutdown();
    }



}
