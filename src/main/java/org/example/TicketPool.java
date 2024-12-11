package org.example;

import java.util.Vector;

public class TicketPool {
    private final Vector<String> tickets;
    private final int maxCapacity;

    public TicketPool(int maxCapacity, int initialTickets) {
        this.tickets = new Vector<>();
        this.maxCapacity = maxCapacity;

        // Populate the pool with initial tickets
        for (int i = 0; i < initialTickets; i++) {
            tickets.add("Ticket");
        }
    }

    public synchronized void addTickets(int count) {
        while (tickets.size() + count > maxCapacity) {
            try {
                System.out.println("Max capacity reached. Vendor waiting to add tickets...");
                wait(); // Vendor waits if adding tickets would exceed max capacity
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Vendor interrupted while waiting.");
                return;
            }
        }

        for (int i = 0; i < count && tickets.size() < maxCapacity; i++) {
            tickets.add("Ticket");
        }

        System.out.println("Tickets added. Total tickets: " + tickets.size());
        notifyAll();
    }

    public synchronized void removeTicket() {
        while (tickets.isEmpty()) {
            try {
                System.out.println("No tickets available. Customer waiting for tickets...");
                wait(); // Customer waits if there are no tickets available
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Customer interrupted while waiting.");
                return;
            }
        }

        tickets.removeFirst(); // Remove one ticket from the beginning of the vector
        System.out.println("Ticket sold. Tickets remaining: " + tickets.size());
        notifyAll(); // Notify vendors if space is now available in the pool
    }

    public synchronized int getTicketCount() {
        return tickets.size();
    }
}
