# **💰 ExpenseTracker API (Core Java Edition)**

**"Before mastering the Framework, you must master the Language."**

This project is a raw, high-performance REST API built entirely in **Core Java** without relying on heavy frameworks like Spring Boot. It demonstrates a deep understanding of HTTP protocols, Token-based Authentication (JWT), and Data Persistence using file handling.

## **🚀 Why This Project?**

In a world of "Annotation Magic" (Spring Boot, Hibernate), many developers forget how things work under the hood. I built this to:

* **Manually handle HTTP Requests/Responses** using Java's HttpServer.  
* Implement **JWT Authentication & Authorization** from scratch.  
* Design a custom **File-Based Database System** (JSON persistence).  
* Manage **Password Hashing** and security best practices manually.

## **⚡ Features**

* **🔐 Secure Authentication:** User Registration & Login with BCrypt Password Hashing.  
* **🛡️ JWT Authorization:** Stateless session management using JSON Web Tokens.  
* **📝 CRUD Operations:** Create, Read, Update, and Delete expenses.  
* **🔍 Advanced Filtering:** \- Filter by **Category** (e.g., Food, Travel).  
  * Filter by **Date Range** (Last Week, Month, 3 Months).  
* **💾 Persistence:** Custom JSON file storage engine (Expenses.json, Users.json) ensuring data survives server restarts.  
* **🚫 Error Handling:** Robust exception handling for Malformed URLs, Invalid Tokens, and Bad Requests.

## **🛠️ Tech Stack**

* **Language:** Java 21  
* **Server:** com.sun.net.httpserver (Native HTTP Server)  
* **Security:** JJWT (JSON Web Tokens), BCrypt (JBCrypt)  
* **Data Parsing:** Jackson (JSON Serialization/Deserialization)  
* **Build Tool:** Maven

## **📂 Project Structure**

src/main/java/com/saksham\_kumar/  
├── Server.java           \# Entry Point & Request Routing (The "Traffic Controller")  
├── JwtUtil.java          \# JWT Generation & Validation Logic  
├── UserManager.java      \# User Logic (Auth, Registration)  
├── ExpenseManager.java   \# Expense Logic (CRUD, Filtering, File I/O)  
├── Expense.java          \# POJO for Expense Data  
└── Users.java            \# POJO for User Data

## **🔌 API Endpoints**

### **1\. Authentication**

| Method | Endpoint | Description | Auth Required |
| :---- | :---- | :---- | :---- |
| POST | /request | Register a new user | ❌ No |
| POST | /login | Login and receive a Bearer Token | ❌ No |

### **2\. Expense Management**

*(All endpoints below require header: Authorization: Bearer \<your\_token\>)*

| Method | Endpoint | Description |
| :---- | :---- | :---- |
| POST | /expenses | Add a new expense |
| GET | /expenses | Get all expenses for the logged-in user |
| GET | /expenses?type=category\&value=Food | Get expenses filtered by Category |
| GET | /expenses?type=filter\&value=week | Get expenses for the last week |
| PUT | /expenses?id={id} | Update an expense (Price, Desc, etc.) |
| DELETE | /expenses?id={id} | Delete an expense |

## **🏃‍♂️ How to Run**

### **Prerequisites**

* Java JDK 21+  
* Maven installed and configured

### **Installation**

1. **Clone the repository:**  
   git clone \[https://github.com/yourusername/expense-tracker-core.git\](https://github.com/yourusername/expense-tracker-core.git)  
   cd expense-tracker-core

2. **Clean & Compile:**  
   mvn clean compile

3. **Start the Server:**  
   mvn exec:java

   *The server will start at http://localhost:8000*

## **🧪 Testing (Postman Workflow)**

1. **Register:** Send a POST to /request with JSON body {"userName": "admin", "password": "123"}.  
2. **Login:** Send a POST to /login with the same credentials.  
3. **Get Token:** Copy the token string from the response.  
4. **Authorize:** For all subsequent requests, go to the **Headers** tab in Postman and add:  
   * Key: Authorization  
   * Value: Bearer \<paste\_your\_token\_here\>  
5. **Operations:** Now you can Add, List, or Delete expenses securely.

## **🔮 Future Roadmap**

* \[ \] Migration to **MySQL/PostgreSQL** for relational database storage.  
* \[ \] Refactoring to **Spring Boot** for scalability and dependency injection.  
* \[ \] Adding Unit Tests using **JUnit 5**.

### **👨‍💻 Author**

**Saksham Kumar** *Backend Developer | Java Enthusiast*