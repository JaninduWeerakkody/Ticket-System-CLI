package org.example;

import com.google.gson.Gson;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

public class TicketSystemCLI {
    private static final Logger logger = Logger.getLogger(TicketSystemCLI.class);

    private static TicketPool ticketPool;
    private static Configuration config = new Configuration();

    private static final Vector<Thread> vendors = new Vector<>();
    private static final Vector<Thread> customers = new Vector<>();

    private static boolean systemRunning = false;

    // Static block to configure logging properties from a file
    static {
        PropertyConfigurator.configure("log4j.properties");
    }

    public static void main(String[] args) {
        logger.info("Starting Ticket System CLI application...");

        loadConfiguration();

        ticketPool = new TicketPool(config.getMaxTicketCapacity(), config.getTotalTickets());

        Scanner scanner = new Scanner(System.in);
        String command;

        // Menu loop for handling user commands
        do {
            displayMenu();
            command = scanner.nextLine();

            switch (command) {
                case "1":
                    startSystem();
                    break;
                case "2":
                    stopSystem();
                    break;
                case "3":
                    configureSystem(scanner);
                    ticketPool = new TicketPool(config.getMaxTicketCapacity(), config.getTotalTickets());
                    break;
                case "4":
                    stopSystem();
                    logger.info("Exiting application...");
                    break;
                default:
                    logger.warn("Invalid command. Try again.");
            }
        } while (!command.equals("4"));

        scanner.close();
    }

    // Method to load configuration from a JSON file
    private static void loadConfiguration() {
        Gson gson = new Gson();
        String filePath = "config.json";
        try (FileReader reader = new FileReader(filePath)) {
            // Deserialize the JSON configuration into a Configuration object
            config = gson.fromJson(reader, Configuration.class);
            logger.info("Configuration loaded from file.");
        } catch (IOException e) {
            // If file loading fails, inform the user and use manual configuration
            logger.error("Error loading configuration file: " + e.getMessage());
            logger.info("Switching to manual configuration.");
            configureSystem(new Scanner(System.in));
        }
    }

    // Method to display the menu of options in the CLI
    private static void displayMenu() {
        System.out.println("\n--- Ticket System CLI ---");
        System.out.println("1. Start System");
        System.out.println("2. Stop System");
        System.out.println("3. Configure Settings");
        System.out.println("4. Exit");
        System.out.print("Enter command: ");
    }

    // Method to save the current configuration to a file
    public static void saveConfiguration() {
        Gson gson = new Gson();
        String filePath = "config.json";
        try (FileWriter writer = new FileWriter(filePath)) {
            // Serialize the configuration object to JSON and save it to the file
            gson.toJson(config, writer);
            logger.info("Configuration saved to file.");
        } catch (IOException e) {
            logger.error("Error saving configuration file: " + e.getMessage());
        }
    }

    // Method to configure system settings through user input
    private static void configureSystem(Scanner scanner) {
        if (systemRunning) {
            // Prevent configuring the system while it's running
            logger.warn("System must be stopped before configuring settings.");
            return;
        }

        // Prompt for each configuration setting and validate input
        System.out.print("Enter Total Tickets: ");
        config.setTotalTickets(validateInput(scanner, Integer.MAX_VALUE));

        System.out.print("Enter Ticket Release Rate (in minutes): ");
        int releaseRateInMinutes = validateInput(scanner, Integer.MAX_VALUE);
        config.setTicketReleaseRate(releaseRateInMinutes * 60000);

        System.out.print("Enter Customer Retrieval Rate (in minutes): ");
        int retrievalRateInMinutes = validateInput(scanner, Integer.MAX_VALUE);
        config.setCustomerRetrievalRate(retrievalRateInMinutes * 60000);

        System.out.print("Enter Max Ticket Capacity: ");
        config.setMaxTicketCapacity(validateInput(scanner, config.getTotalTickets()));

        // Save the configuration after input is collected
        saveConfiguration();
        logger.info("Configuration saved.");
    }

    // Method to validate user input
    private static int validateInput(Scanner scanner, int max) {
        int value;
        while (true) {
            if (scanner.hasNextInt()) {
                value = scanner.nextInt();
                scanner.nextLine();
                if (value >= 1 && value <= max) {
                    return value;
                }
            } else {
                scanner.nextLine(); // Consume the invalid input
            }
            logger.warn("Invalid input. Please enter a valid number:");
        }
    }

    // Method to display the current ticket availability in the system
    private static void displayTicketAvailability() {
        logger.info("Current ticket availability: " + ticketPool.getTicketCount());
    }

    // Method to start the system and initialize vendor and customer threads
    private static void startSystem() {
        if (systemRunning) {
            logger.warn("System is already running.");
            return;
        }

        logger.info("Starting system...");
        systemRunning = true;

        // Start vendor threads (3 vendors)
        for (int i = 0; i < 3; i++) {
            Thread vendorThread = new Thread(new Vendor(ticketPool, config.getTicketReleaseRate()));
            vendors.add(vendorThread);
            vendorThread.start();
        }

        // Start customer threads (5 customers)
        for (int i = 0; i < 5; i++) {
            Thread customerThread = new Thread(new Customer(ticketPool, config.getCustomerRetrievalRate()));
            customers.add(customerThread);
            customerThread.start();
        }

        // Start a separate thread to display ticket availability in real-time
        new Thread(() -> {
            while (systemRunning) {
                displayTicketAvailability();
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }).start();

        logger.info("System started.");
    }

    // Method to stop the system and interrupt all vendor and customer threads
    private static void stopSystem() {
        if (!systemRunning) {
            logger.warn("System is not running.");
            return;
        }

        logger.info("Stopping system...");
        systemRunning = false;

        // Interrupt all vendor and customer threads to stop them
        for (Thread vendor : vendors) {
            vendor.interrupt();
        }
        for (Thread customer : customers) {
            customer.interrupt();
        }
        vendors.clear();
        customers.clear();

        logger.info("System stopped.");
    }
}
