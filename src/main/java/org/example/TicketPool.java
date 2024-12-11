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

    // Method to add tickets to the pool, synchronized to handle concurrency
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

        // Add tickets to the pool if there is enough capacity
        for (int i = 0; i < count && tickets.size() < maxCapacity; i++) {
            tickets.add("Ticket");
        }

        System.out.println("Tickets added. Total tickets: " + tickets.size());
        notifyAll(); // Notify any waiting customers that tickets may now be available
    }

    // Method to remove a ticket from the pool, synchronized to handle concurrency
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

        // Remove a ticket from the pool (remove from the beginning of the vector)
        tickets.removeFirst();
        System.out.println("Ticket sold. Tickets remaining: " + tickets.size());
        notifyAll(); // Notify vendors that there is now space to add tickets
    }

    // Method to get the current number of tickets in the pool
    public synchronized int getTicketCount() {
        return tickets.size();
    }
}
