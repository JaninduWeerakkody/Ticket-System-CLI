package org.example;

import org.apache.log4j.Logger;

public class Vendor implements Runnable {
    private static final Logger logger = Logger.getLogger(Vendor.class);

    private final TicketPool ticketPool;
    private final int ticketReleaseRate;

    public Vendor(TicketPool ticketPool, int ticketReleaseRate) {
        this.ticketPool = ticketPool;
        this.ticketReleaseRate = ticketReleaseRate;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                ticketPool.addTickets(1); // Add one ticket to the pool
                logger.info("Vendor added a ticket. Checking availability...");
                Thread.sleep(ticketReleaseRate); // Sleep for the specified rate in milliseconds
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.warn("Vendor interrupted and stopping ticket addition.");
                break;
            }
        }
    }
}
