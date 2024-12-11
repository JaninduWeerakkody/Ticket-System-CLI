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

    // The run method executed by the vendor thread
    @Override
    public void run() {
        // Continuously add tickets until the thread is interrupted
        while (!Thread.currentThread().isInterrupted()) {
            try {
                // Attempt to add one ticket to the pool
                ticketPool.addTickets(1);
                logger.info("Vendor added a ticket. Checking availability...");
                // Sleep for the specified release rate (in milliseconds)
                Thread.sleep(ticketReleaseRate);
            } catch (InterruptedException e) {
                // If the thread is interrupted, stop the loop and set the interrupt flag
                Thread.currentThread().interrupt();
                logger.warn("Vendor interrupted and stopping ticket addition.");
                break;
            }
        }
    }
}
