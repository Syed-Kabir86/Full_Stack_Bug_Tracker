# Full Stack Bug & Defect Tracker

Full-stack ticket management web app.

**Tech Stack:** Java, Spring Boot, Angular, PostgreSQL, Docker, JUnit, Mockito

## Instructions for running the program

Start these in order: database, then backend, then frontend.

### 1. Database (PostgreSQL in Docker)

Make sure Docker Desktop is open and running.

Open a terminal in the backend folder and run:
`docker compose up -d`

This starts PostgreSQL in a container. To confirm it's running:
`docker ps`
You should see a container named `bugtracker-db` with status "Up".

To stop the database later:
`docker compose stop`

### 2. Backend

In the backend folder, run:
`.\mvnw.cmd spring-boot:run` (Windows)
`./mvnw spring-boot:run` (Mac/Linux)

Then open a new browser tab to `localhost:8080/api/tickets`.
If successful, it should show `[]` (or a list of existing tickets).

### 3. Frontend

Open a terminal in the frontend folder and run:
`ng serve`

Then open a new browser tab to `localhost:4200`.
If successful, it should show the form to create a ticket and the ticket table.

## Running the tests

In the backend folder, run:
`.\mvnw.cmd test` (Windows)
`./mvnw test` (Mac/Linux)
