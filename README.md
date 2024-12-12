README for Ticket System

1. Introduction

The Ticket System is a multithreaded application that simulates ticket management processes between vendors and customers. Vendors release tickets at a configurable rate, and customers retrieve tickets at a separate rate. The system supports real-time ticket pool monitoring, dynamic configuration, and graceful shutdown. This project demonstrates key concepts of concurrency, synchronization, and resource management.

2. Setup Instructions

Prerequisites:

Java Development Kit (JDK) v17 or higher: Ensure that JDK 17 or later is installed. Verify installation by running java -version in the command prompt or terminal.

Integrated Development Environment (IDE) (Optional): IntelliJ IDEA, Eclipse, or Visual Studio Code with Java support are recommended for development and debugging.

How to Build and Run the Application:

Clone the Repository:

git clone https://github.com/your-username/ticket-system.git
cd ticket-system

Compile the Application:

javac -d bin src/*.java

Run the Application:

java -cp bin TicketSystem

3. Usage Instructions

System Configuration:

Manual Configuration: Use the "Configure" option in the main menu to set ticket pool capacity, vendor release rate, and customer retrieval rate.

Load from File: If a config.json file is available, the system will load configuration settings from it at startup.

Save to File: Changes to the system configuration can be saved back to the config.json file for future use.

System Controls:

Start: Begins ticket operations with vendors releasing tickets and customers retrieving them.

Stop: Halts ticket activity, pausing vendor and customer threads.

Configure: Allows manual entry of ticket capacity, vendor release rate, and customer retrieval rate.

Exit: Safely shuts down the system, ensuring that all threads are stopped and system resources are released.

Explanation of UI Controls:

Menu Navigation: Users navigate the menu using numerical inputs (e.g., "1" to Start, "2" to Stop, etc.).

Console Outputs: Real-time ticket pool information, thread activity, and system logs are displayed in the console.

Error Handling: If invalid inputs are provided, the system prompts for re-entry. Missing files (like config.json) trigger notifications and options for manual configuration.

