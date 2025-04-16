[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/6xRviSdT)
# Welcome to the course CSYE6200- Concepts of Object Oriented Design
> Northeastern University, College of Engineering


## Professor: Daniel Peters

### Requirements
1. Eclipse or VS Code or IntelliJ.

Note: If you are using Eclipse, please have git CLI installed on your system or GitHub Desktop to commit the code in this repository

### SetUp Instructions
1. Please clone the repository on your local system
2. For Eclipse Import the project as Existing Maven Project, For IntelliJ you can directlty open it using 'Get from VCS'.
4. All code should be pushed to the main branch
3. Ensure the GitHub actions are successful post push

Submissions will have deadlines, failed GitHub Actions would result in point deductions.

### References
1. Cloning a Repository: <https://docs.github.com/en/repositories/creating-and-managing-repositories/cloning-a-repository>
2. Any GitHub Setup: Please refer to the Git & GitHub Fundamentals Repository shared to you by your respective TA and refer the README.md section

Please reach out to your respective TA if you need any help in regards with submission/ GitHub

Author:
- Rohan Vasudev Ginde (ginde.r@northeastern.edu)
- Yesha Joshi (joshi.ye@northeastern.edu)





**Presentation Video Link-**
[VideoLink](https://northeastern-my.sharepoint.com/:v:/r/personal/jadhav_sani_northeastern_edu/Documents/Recordings/Meeting%20in%20TripSync-20241209_130227-Meeting%20Recording.mp4?csf=1&web=1&e=9zDyUE)- https://northeastern-my.sharepoint.com/:v:/r/personal/jadhav_sani_northeastern_edu/Documents/Recordings/Meeting%20in%20TripSync-20241209_130227-Meeting%20Recording.mp4?csf=1&web=1&e=9zDyUE


# Project Summary for TripSync
**Project Overview:**
TripSync is an intuitive and centralized travel itinerary management platform designed to address the complexities of planning and managing travel. The platform simplifies travel planning by providing users with tools to organize trips, track activities, and manage accommodations efficiently. By combining a clean user interface with robust backend functionality, TripSync aims to offer a seamless travel experience, saving users time and effort in organizing their itineraries.


**Key challenges addressed include:**
1. Centralizing trip data such as costs, schedules, and accommodations.
2. Providing CRUD (Create, Read, Update, Delete) functionality for trip components.
3. Enabling advanced filtering to refine and personalize trip management.
4. The platform ensures efficient organization through a user-friendly interface and backend support, making it an indispensable tool for travelers.


## Figma for wireframing
https://www.figma.com/design/IDfLFyugiYzF1mzYiE27UO/TripSync?node-id=0-1&node-type=canvas&t=dvn8fMlCT0EpCcNu-0

## Features

- User Authentication and Security
- Trip Management
- Activity and Accommodation Management
- Dynamic User Interface
- Local Storage Utilization
- Error Handling and Validation
- Filtering and Retrieval


## Tech Stack

**Frontend:** HTML, CSS, React, and TypeScript

**Backend:** Spring Boot, Spring Data JPA

**Database:** MySQL, Oracle DB

**Build Tool:** Maven





## Object-Oriented Programming Principles

**1. Encapsulation:** Fields in entity classes such as User, Trip, Activity, and Accommodation are private, with controlled access through public getter and setter methods.

**2. Inheritance:** Interfaces such as UserService and TripService ensure consistent structure and enforce a contract for implementation.

**3. Polymorphism:** Service classes override generic methods to include entity-specific logic.

**4. Abstraction:** Layers such as controllers, services, and repositories abstract business logic, enabling modular development.

**5. Composition and Aggregation:** Composition: Trips are composed of Activities and Accommodations as integral components Aggregation: A user can manage multiple trips with independent lifecycles.

**6. Dependency Injection:** Utilizes Spring’s DI framework for loose coupling between components like controllers and services.

## Contributions

1. Aditi Bailur:
    i. Implement Website NavigationBar Design, ii. Configure Database Connection, iii. Trip Model, iv. UI for Activities and Accommodations Page, v. Integration with backend for Activities and Accommodations Page
2. Atharva Hankare:
    i. Class Diagram, ii. Wireframes for Profile page and Home page, iii. User Model, UI for Home Page, iv. User Registration and Authentication, v.  Integration with backend for Home Page
3. Pratham Rathod:
    i. Trip Creation Page Wireframe, ii. Login Page Wireframe, iii. Spring Boot Project Configuration, iv. UI for Trips Page, v. Integration with backend for Trips Page
4. Saniya Jadhav:
    i. Design Website Wireframe for Trip Details Page, ii. Design Website Wireframe for Activity Management Page, iii. Accommodation Model, iv. UI for Login and Sign Up, v. Integration with backend for Login and Sign Up Page
5. Tejas Tawde:
    i. Sequence Diagram, ii. Wireframe for Accommodation Management Page, iii. Activity Model, iv. UI for Profile Page, v. Integration with backend for Profile Page

## Project Milestones

**Milestone 1:** Established project specifications and defined technical stack and object-oriented principles.
Developed wireframes for core pages.


**Milestone 2:** Implemented user authentication and home page display. Created static components for trip and activity management.


**Milestone 3** Integrated backend services with the frontend for activities and accommodations.
Displayed dynamic content from the database on the homepage and profile page.

## Installation

Install my-project with npm

```bash
  brew install mysql
  brew services start mysql
  mysql_secure_installation
```
    
## Run Locally

Clone the project

```bash
  git clone https://github.com/CSYE-6200-Concepts-of-OOD-Fall-2024-S3/final-project-group-4.git
```

Go to the project directory

```bash
  cd final-project-group-4
```

Navigate to the frontend directory and install dependencies

```bash
    cd frontend
    npm install
```
Navigate to the frontend directory and install dependencies

```bash
    cd final-project-group-4
    mvn clean install
```

Install MySQL using Homebrew

```bash
    brew update
    brew install mysql
    brew services start mysql
    mysql_secure_installation
```


To start the frontend development server

```bash
  cd final-project-group-4/frontend
  npm run dev

```

To start the backend server

```bash
  cd final-project-group-4/backend
  mvn spring-boot:run

```


## Authors

- [@aditibailur2024](https://github.com/aditibailur2024)

- [@atharvah009](https://github.com/atharvah009)

- [@tejastawde28](https://github.com/tejastawde28)

- [@saniyajadhav12](https://github.com/saniyajadhav12)

- [@rathodpratham15](https://github.com/rathodpratham15)