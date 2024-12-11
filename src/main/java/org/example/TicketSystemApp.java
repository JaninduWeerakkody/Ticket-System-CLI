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

        for (int i = 0; i < 3; i++) { // Three vendors
            Thread vendorThread = new Thread(new Vendor(ticketPool, config.getTicketReleaseRate()));
            vendors.add(vendorThread);
            vendorThread.start();
        }

        for (int i = 0; i < 5; i++) { // Five customers
            Thread customerThread = new Thread(new Customer(ticketPool, config.getCustomerRetrievalRate()));
            customers.add(customerThread);
            customerThread.start();
        }

        Runtime.getRuntime().addShutdownHook(new Thread(TicketSystemApp::stopAllThreads));
    }

    private static void stopAllThreads() {
        for (Thread vendor : vendors) vendor.interrupt();
        for (Thread customer : customers) customer.interrupt();
        logger.info("All threads stopped.");
    }

    private static Configuration loadConfiguration() {
        Gson gson = new Gson();
        String filePath = "config.json";

        try (FileReader reader = new FileReader(filePath)) {
            return gson.fromJson(reader, Configuration.class);
        } catch (FileNotFoundException e) {
            logger.warn("Configuration file not found. Using default configuration.");
        } catch (IOException e) {
            logger.error("Error reading configuration file: " + e.getMessage());
        }

        Configuration defaultConfig = new Configuration();
        defaultConfig.setTotalTickets(20);
        defaultConfig.setTicketReleaseRate(300);
        defaultConfig.setCustomerRetrievalRate(1500);
        defaultConfig.setMaxTicketCapacity(5);
        return defaultConfig;
    }
}
