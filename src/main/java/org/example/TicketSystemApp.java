package org.example;

import com.google.gson.Gson;
import org.apache.log4j.Logger;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

public class TicketSystemApp {
    private static final Logger logger = Logger.getLogger(TicketSystemApp.class);

    private static final Vector<Thread> vendors = new Vector<>();
    private static final Vector<Thread> customers = new Vector<>();

    public static void main(String[] args) {
        Configuration config = loadConfiguration();
        logger.info("Configuration loaded: " + config);

        TicketPool ticketPool = new TicketPool(config.getMaxTicketCapacity(), config.getTotalTickets());

        // Create and start threads for vendors (3 vendors)
        for (int i = 0; i < 3; i++) {
            Thread vendorThread = new Thread(new Vendor(ticketPool, config.getTicketReleaseRate()));
            vendors.add(vendorThread);
            vendorThread.start();
        }

        // Create and start threads for customers (5 customers)
        for (int i = 0; i < 5; i++) {
            Thread customerThread = new Thread(new Customer(ticketPool, config.getCustomerRetrievalRate()));
            customers.add(customerThread);
            customerThread.start();
        }

        // Register a shutdown hook to stop all threads gracefully when the application is terminated
        Runtime.getRuntime().addShutdownHook(new Thread(TicketSystemApp::stopAllThreads));
    }

    // Method to stop all vendor and customer threads when the application is shutdown
    private static void stopAllThreads() {
        // Interrupt each vendor and customer thread to stop them
        for (Thread vendor : vendors) vendor.interrupt();
        for (Thread customer : customers) customer.interrupt();
        logger.info("All threads stopped.");
    }

    // Method to load configuration from a JSON file
    private static Configuration loadConfiguration() {
        Gson gson = new Gson();
        String filePath = "config.json";

        try (FileReader reader = new FileReader(filePath)) {
            // Deserialize the JSON configuration into a Configuration object
            return gson.fromJson(reader, Configuration.class);
        } catch (FileNotFoundException e) {
            logger.warn("Configuration file not found. Using default configuration.");
        } catch (IOException e) {
            logger.error("Error reading configuration file: " + e.getMessage());
        }

        // Default configuration values
        Configuration defaultConfig = new Configuration();
        defaultConfig.setTotalTickets(20);
        defaultConfig.setTicketReleaseRate(300);
        defaultConfig.setCustomerRetrievalRate(1500);
        defaultConfig.setMaxTicketCapacity(5);
        return defaultConfig;
    }
}
