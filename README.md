# Disaster Coordination & Crisis Management System

This project is a comprehensive system developed to ensure effective coordination and facilitate crisis management during disasters. The system features a robust database infrastructure capable of spatial data analysis, a desktop interface for administrators, and a mobile application for field use.

## 🚀 Technologies Used

**Database & Backend:**
*   **PostgreSQL:** Relational database management.
*   **PostGIS:** Spatial data storage and management of map/location-based queries.


**Mobile Application:**
*   **Kotlin (Android):** An Android application developed for field teams or users.

## 📁 Project Structure

*   \`CrisisManagementApp/\`: Contains the source code for the mobile application (Kotlin).
*   \`CrisisManagementBackend/\`: Contains the source code for the desktop application (C# WinForms), database connections, and SQL configuration files.
*   \`Database/\`: `.sql` database backup and schema designs required for the system to run.

## ⚙️ Installation and Setup

### 1. Database Setup
1.  Ensure that **PostgreSQL** and the **PostGIS** extension are installed on your machine.
2.  Create a new database using **pgAdmin 4** (or a similar tool).
3.  Restore the `.sql` backup file (if provided) into this newly created database.

### 2. Running the Desktop Application (WinForms)
1.  Open the Solution (`.sln`) file located in the `CrisisManagementBackend` folder with Visual Studio.
2.  Update the username and password in the database connection strings to match your local database credentials.
3.  Build and run the project.

### 3. Running the Mobile Application (Android)
1.  Open the `CrisisManagementApp` folder with **Android Studio**.
2.  Wait for the necessary Gradle dependencies to sync and download.
3.  Run the application on an emulator or a physical Android device.
