package org.example;

import org.apache.log4j.Logger;

public class Customer implements Runnable {
    private static final Logger logger = Logger.getLogger(Customer.class);

    private final TicketPool ticketPool;

    private final int customerRetrievalRate;

    public Customer(TicketPool ticketPool, int customerRetrievalRate) {
        this.ticketPool = ticketPool;
        this.customerRetrievalRate = customerRetrievalRate;
    }

    // The run method that is executed when the thread starts
    @Override
    public void run() {
        // Loop that runs until the thread is interrupted
        while (!Thread.currentThread().isInterrupted()) {
            try {
                // Attempt to purchase a ticket from the ticket pool
                ticketPool.removeTicket();

                // Log the ticket purchase and the remaining tickets in the pool
                logger.info("Customer purchased a ticket. Remaining tickets: " + ticketPool.getTicketCount());

                // Sleep for the specified retrieval rate before trying again
                Thread.sleep(customerRetrievalRate);
            } catch (InterruptedException e) {
                // If interrupted, set the thread's interrupt flag and stop ticket retrieval
                Thread.currentThread().interrupt();
                logger.warn("Customer interrupted and stopping ticket retrieval.");
                break;
            }
        }
    }
}
