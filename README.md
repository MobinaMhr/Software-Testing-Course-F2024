# Software-Testing-Course-S2025

This course focuses on writing and implementing various types of tests for two systems: Mizdooni, a reservation system, and Fasedyab, a transaction engine. Both systems are developed using Spring Boot with a RESTful API.
Each computer assignment (CA) involves writing different tests for Mizdooni and Fasedyab, accompanied by explanatory questions on relevant software testing topics. Answers to these questions are provided in corresponding report files.

## Assignments

### CA1: Unit Testing (JUnit)
Unit tests were written for the `User`, `Table`, `Rating`, and `Restaurant` models.  
The test files are located in `Mizdooni/src/test/java/model`. Some of the tests utilize parameterized testing for efficiency and coverage.  
The explanatory questions cover topics such as private method testing, multi-threaded testing, and identifying problems in a sample test code.

### CA2: Mock Testing (Mockito)
Tests were implemented for the `ReservationController`, `ReviewController`, and `AuthenticationController` classes.  
These test files can be found in `Mizdooni/src/test/java/controllers`. The tests use the **Mockito** framework to mock Mizdooni’s service classes effectively.  
The explanatory questions discuss dependency injection, the types of test doubles, and the differences between classical and mockist testing strategies.

### CA3: Graph Coverage (JaCoCo)
Graph coverage tests were created for the `TransactionEngine` and `Transaction` classes in Fasedyab.  
The test files are located in `Fasedyab/src/test/java/domain`, aiming to maximize branch and statement coverage, as calculated using the **JaCoCo** library.  
The explanatory questions explore the feasibility of achieving 100% branch or statement coverage, drawing control flow graphs, and understanding graph coverage criteria such as prime paths and DU paths.

### CA4: API Testing (MockMvc)
API tests were written for the `TableController` and `RestaurantController` classes in Mizdooni.  
These test files, with names ending in `ApiTest`, are located in `Mizdooni/src/test/java/controllers`. The tests use the `@SpringBootTest` annotation along with **MockMvc** to perform API calls on a running instance of the application and validate JSON responses.  
The explanatory questions address logic coverage and input space partitioning in API testing.

### CA5: Mutation Testing (PITest)
Mutation testing was conducted on the tests from CA3 (Fasedyab) using the **PITest** library to evaluate the quality of the written tests.  
The results, including the analysis of mutants that could not be killed, are documented in the report.  
Additionally, a GitHub Actions workflow (`.github/workflows/maven.yml`) was created to automate the building and testing of Mizdooni and Fasedyab projects after every push.

### CA6: Behavior Driven Development (Cucumber)
Behavior-driven tests were developed for methods such as `AddReservation`, `AddReview`, and `GetAverageRating` in the `User` and `Restaurant` models.  
The test scenarios are written using **Cucumber**’s Gherkin language and stored in `Mizdooni/src/test/resources`, with the implementation in `Mizdooni/src/test/java/mizdooni/CucumberTest.java`.  
In addition, GUI testing was recorded using **Katalon Recorder** on **Swagger UI**, which visualizes Mizdooni's API.

---
![structure](https://github.com/MobinaMhr/Software-Testing-Course-S2025/blob/main/assets/app-struct.png)
