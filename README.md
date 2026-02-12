# Supply Chain Resilience Management System

## Overview

The **Supply Chain Resilience Management System** is an intelligent, multi-agent application designed to enhance the resilience and efficiency of supply chain operations. Built with Java and Spring Boot, it leverages a custom agent-based framework to simulate, monitor, and respond to various supply chain events in real-time.

The system is capable of detecting disruptions (such as shipment delays), autonomously adjusting procurement strategies, and notifying relevant stakeholders to mitigate risks.

## Key Features

*   **Multi-Agent Architecture**: Decentralized agents handling specific domains (Logistics, Inventory, Procurement, etc.).
*   **Real-time Monitoring**: Continuous tracking of shipments and inventory levels.
*   **Automated Exception Handling**: Automatic cancellation of delayed orders and triggering of recovery purchase orders.
*   **Simulation Engine**: Built-in capability to trigger supply chain scenarios for testing and validation.
*   **Event Logging**: Comprehensive logging of all agent interactions and system decisions.

## Agent System

The core of the system relies on the collaboration of the following specialized agents:

1.  **Root Agent (`RootAgent`)**:
    *   Acts as the central orchestrator.
    *   Initializes the system and coordinates high-level workflows.
    *   Delegates tasks to other agents based on system state.

2.  **Logistics Agent (`LogisticsAgent`)**:
    *   Monitors active shipments.
    *   Detects delivery delays and signals potential disruptions.
    *   Communicates shipment status updates to the network.

3.  **Inventory Agent (`InventoryAgent`)**:
    *   Tracks current stock levels at various warehouses.
    *   Alerts when stock falls below safety thresholds.
    *   Validates stock availability for new orders.

4.  **Procurement Agent (`ProcurementAgent`)**:
    *   Manages Purchase Orders (POs).
    *   Handles the creation of new POs for replenishment.
    *   Executes recovery actions, such as finding alternative suppliers when primary shipments fail.

5.  **Notification Agent (`NotificationAgent`)**:
    *   The communication bridge to human stakeholders.
    *   Sends alerts regarding critical events (e.g., "Severe Delay Detected", "New PO Created").
    *   Logs notifications for audit trails.

## Technology Stack

*   **Language**: Java 17
*   **Framework**: Spring Boot 3.1.5
*   **Build Tool**: Maven, Google Cloud Platform, Docker
*   **Testing**: JUnit 4
*   **IDE**: Antigravity
*   **Development**: Google Agent Development Kit (ADK)


## Running the Application Locally

### Prerequisites

*   **Java Development Kit (JDK) 17** or higher.
*   **Maven** 3.6 or higher.
*   **Google API Key**: Set the `GOOGLE_API_KEY` environment variable to enable Gemini reasoning.

### Build and Run

1.  **Clone the repository** (if not already done).
2.  **Set the API Key**:
    ```bash
    export GOOGLE_API_KEY="your-api-key-here"
    ```
3.  **Build the project**:
    ```bash
    mvn clean install
    ```
4.  **Run the application**:
    ```bash
    mvn spring-boot:run
    ```

### Using the API

Once the application is running, you can interact with it using the following endpoints:

*   **Trigger Simulation**: `POST http://localhost:8080/api/trigger`
*   **View Logs**: `GET http://localhost:8080/api/logs`

## Deployment

Application is running on: https://supply-chain-resilance-307597295243.europe-west1.run.app


## PPT and Project Demo Recording

1. PPT:
2. Project Demo Recording: 

## Project Diagram




## Project Structure

```
src/main/java/com/supplychain/resilience
├── SupplyChainApplication.java   # Main entry point
├── agents                        # Agent implementations (Logistics, Inventory, etc.)
├── framework                     # Core interfaces (Agent, Message)
├── model                         # Domain models (PurchaseOrder, Shipment)
├── services                      # Business logic services (SimulationService, EventLogService)
└── controller                    # REST Controllers for API access
```
