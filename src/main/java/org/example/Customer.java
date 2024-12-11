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

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                ticketPool.removeTicket(); // Attempt to purchase a ticket
                logger.info("Customer purchased a ticket. Remaining tickets: " + ticketPool.getTicketCount());
                Thread.sleep(customerRetrievalRate); // Sleep for the specified retrieval rate in milliseconds
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.warn("Customer interrupted and stopping ticket retrieval.");
                break;
            }
        }
    }
}
