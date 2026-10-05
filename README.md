# 🛰️ Room Radar

**Room Radar** is a full-stack web application designed for college students to quickly locate empty classrooms, labs, and seminar halls during free periods or study slots.

Students pick a day and a time window (e.g., Monday 10:00 to 11:00), apply optional filters (building, floor, room type, minimum capacity), and Room Radar immediately lists every free room on campus—along with a **"Free until..."** badge and full day schedule inspection.

---

## 🏗️ Architecture & Tech Stack

- **Frontend:** Semantic HTML5, Modern CSS3 (CSS Grid, Flexbox, native `<dialog>`, variables, animations), Vanilla JavaScript ES6+ (Zero external UI/JS dependencies).
- **Backend:** Spring Boot 3 (Java 17+), REST API, Spring Data JPA, Hibernate, Bean Validation.
- **Database:** PostgreSQL (with included schema and comprehensive seed dataset).
- **Build & Test:** Apache Maven, JUnit 5, AssertJ, Mockito.

---

## 📁 Project Structure

```
Room Radar/
├── backend/
│   ├── pom.xml
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/roomradar/
│   │   │   │   ├── RoomRadarApplication.java
│   │   │   │   ├── config/
│   │   │   │   │   ├── WebConfig.java             # Global CORS mapping
│   │   │   │   │   └── DataInitializer.java       # Automatic DB seeder if empty
│   │   │   │   ├── controller/
│   │   │   │   │   └── RoomController.java        # REST endpoints (/api/rooms/**)
│   │   │   │   ├── dto/
│   │   │   │   │   ├── FreeRoomResponse.java      # Room with freeUntil badge
│   │   │   │   │   ├── RoomDto.java               # Room entity DTO
│   │   │   │   │   ├── TimetableDto.java          # Schedule slot DTO
│   │   │   │   │   └── ErrorResponse.java         # Uniform REST error model
│   │   │   │   ├── entity/
│   │   │   │   │   ├── Room.java                  # JPA entity for rooms table
│   │   │   │   │   ├── Timetable.java             # JPA entity for timetable table
│   │   │   │   │   ├── RoomType.java              # Enum: CLASSROOM, LAB, SEMINAR_HALL
│   │   │   │   │   └── DayOfWeekEnum.java         # Enum: MON, TUE, WED, THU, FRI, SAT
│   │   │   │   ├── exception/
│   │   │   │   │   ├── BadRequestException.java
│   │   │   │   │   ├── ResourceNotFoundException.java
│   │   │   │   │   └── GlobalExceptionHandler.java# Centralized @RestControllerAdvice
│   │   │   │   ├── repository/
│   │   │   │   │   ├── RoomRepository.java        # Dynamic criteria queries
│   │   │   │   │   └── TimetableRepository.java   # Day & slot overlap queries
│   │   │   │   └── service/
│   │   │   │       └── RoomService.java           # Core overlap & free-until logic
│   │   │   └── resources/
│   │   │       ├── application.properties         # Default PostgreSQL config
│   │   │       ├── application-h2.properties      # Zero-setup standalone demo profile
│   │   │       └── static/                        # Bundled frontend (served on port 8080)
│   │   │           ├── index.html
│   │   │           ├── style.css
│   │   │           └── app.js
│   │   └── test/
│   │       ├── java/com/roomradar/service/
│   │       │   └── RoomServiceTest.java           # 17 Unit tests for overlap & free-until
│   │       └── resources/
│   │           └── application.properties         # Test-scoped H2 configuration
├── frontend/
│   ├── index.html                                 # Standalone single-page interface
│   ├── style.css                                  # Responsive design & modal styles
│   └── app.js                                     # Fetch API & interactive schedule logic
├── database/
│   ├── schema.sql                                 # PostgreSQL DDL table definitions
│   └── seed.sql                                   # Realistic weekly schedule for 10 rooms
└── README.md
```

---

## ⏱️ Core Overlap & "Free Until" Logic

### 1. Overlap Rule
A room is considered **FREE** during a requested interval `[requestedStart, requestedEnd)` on a given day if and only if **NO** scheduled class in that room satisfies:
```sql
entry.start_time < requested_end AND entry.end_time > requested_start
```
This bidirectional overlap rule handles all time intersection cases:
- **Full match:** Class from `10:00-11:00` blocks a `10:00-11:00` request.
- **Enclosing slot:** Class from `09:00-12:00` blocks a `10:00-11:00` request.
- **Enclosed slot:** Class from `10:15-10:45` blocks a `10:00-11:00` request.
- **Partial overlap (start before, end inside):** Class `09:30-10:30` blocks `10:00-11:00` (`09:30 < 11:00 AND 10:30 > 10:00` is true).
- **Partial overlap (start inside, end after):** Class `10:30-11:30` blocks `10:00-11:00` (`10:30 < 11:00 AND 11:30 > 10:00` is true).
- **Adjacent boundaries:** Class `09:00-10:00` ends right when the window starts -> **No overlap** (free). Class `11:00-12:00` starts right when the window ends -> **No overlap** (free).

### 2. "Free Until" Calculation
For each free room:
1. Examine all timetable entries for that room on that day starting at or after `requestedEnd` (`startTime >= requestedEnd`).
2. Pick the entry with the minimum start time.
3. If an entry is found, `freeUntil` is formatted as `HH:mm` (e.g., `11:00` or `13:00`).
4. If no further classes are scheduled on that day, `freeUntil` is `"Rest of the day"`.

---

## 🚀 Setup & Execution Guide

### Prerequisites
- **Java Development Kit (JDK):** Version 17 or higher (tested on Java 21)
- **Apache Maven:** Version 3.8+
- **PostgreSQL:** Version 12+ (or Docker)
- Modern web browser (Chrome, Edge, Firefox, Safari)

---

### Step 1: Create the PostgreSQL Database
Log in to your PostgreSQL instance using `psql` or pgAdmin:

```bash
# Using psql
psql -U postgres
```

Create the database:
```sql
CREATE DATABASE roomradar;
\q
```

*(Alternatively, run PostgreSQL in Docker)*:
```bash
docker run --name postgres-roomradar -e POSTGRES_DB=roomradar -e POSTGRES_PASSWORD=postgres -p 5432:5432 -d postgres:16
```

---

### Step 2: Initialize Schema and Seed Data
Execute `schema.sql` followed by `seed.sql`:

```bash
# Windows Command Prompt / PowerShell
psql -U postgres -d roomradar -f "database/schema.sql"
psql -U postgres -d roomradar -f "database/seed.sql"

# Linux / macOS
psql -U postgres -d roomradar -f database/schema.sql
psql -U postgres -d roomradar -f database/seed.sql
```

*(Note: If you run without manual SQL execution, Room Radar's built-in `DataInitializer` will automatically seed the 10 rooms and realistic timetable upon first boot).*

---

### Step 3: Configure Database Connection
Review `backend/src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/roomradar
spring.datasource.username=postgres
spring.datasource.password=postgres
```
*You can customize these by setting environment variables `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD` or directly updating `application.properties`.*

---

### Step 4: Run the Backend Tests
Run the comprehensive test suite verifying the overlap logic and service constraints:

```bash
cd backend
mvn test
```
All 17 unit tests run against an isolated in-memory test database and will output `BUILD SUCCESS`.

---

### Step 5: Start the Spring Boot Backend

#### Standard Mode (PostgreSQL):
```bash
cd backend
mvn spring-boot:run
```

#### Quick Standalone Demo Mode (Zero Prerequisites - In-Memory H2 DB):
If you do not have PostgreSQL installed and want to run the full application instantly:
```bash
cd backend
mvn spring-boot:run "-Dspring-boot.run.profiles=h2"
```

The Spring Boot server will start on **`http://localhost:8080`**.

---

### Step 6: Open the Frontend

You have two convenient ways to access the frontend:

1. **Integrated Web App (Recommended):**
   Open your browser to:
   ```
   http://localhost:8080/
   ```
   Spring Boot serves the static HTML/CSS/JS interface directly from the classpath!

2. **Standalone Frontend File:**
   Open `frontend/index.html` directly in your web browser, or use VS Code Live Server / Python HTTP Server:
   ```bash
   cd frontend
   python -m http.server 3000
   ```
   *(CORS is enabled on all backend endpoints, so the standalone frontend connects seamlessly to `http://localhost:8080/api/rooms`).*

---

## 📡 REST API Reference

| Method | Endpoint | Query Parameters | Description |
|---|---|---|---|
| `GET` | `/api/rooms/free` | `day`, `start`, `end`, *(optional)*: `building`, `floor`, `roomType`, `minCapacity` | Lists free rooms during requested time window with `freeUntil`. |
| `GET` | `/api/rooms/free-now` | *(optional)*: `building`, `floor`, `roomType`, `minCapacity` | Lists rooms free right now for the next 1-hour window. |
| `GET` | `/api/rooms` | *(none)* | Returns all 10 registered campus rooms. |
| `GET` | `/api/rooms/{id}/schedule` | `day` (`MON`-`SAT`) | Returns the chronological timetable for that room on that day. |

---

## 🧪 Demo Script (3 Example Searches with Seed Data)

Use these three demo searches to verify the application behavior against the seed data:

### Search 1: Monday Peak Morning Window (Standard Search)
- **Input:** `day=MON`, `start=10:00`, `end=11:00` (no filters)
- **API Call:**
  ```http
  GET http://localhost:8080/api/rooms/free?day=MON&start=10:00&end=11:00
  ```
- **Expected Results (4 Free Rooms Found):**
  1. **A102** (Main Block, Floor 1, Capacity 60, CLASSROOM) &rarr; **Free until 11:00** (Next class: *Operating Systems Principles* at 11:00)
  2. **A202** (Main Block, Floor 2, Capacity 50, CLASSROOM) &rarr; **Free until 13:00** (Next class: *Linear Algebra* at 13:00)
  3. **B102** (Science Block, Floor 1, Capacity 40, LAB) &rarr; **Free until 13:00** (Next class: *Chemistry Lab* at 13:00)
  4. **C202** (Tech Block, Floor 2, Capacity 45, CLASSROOM) &rarr; **Free until 14:00** (Next class: *Cloud Computing* at 14:00)
- **Busy Rooms Filtered Out:**
  - `A101`: Occupied by *Operating Systems Principles* (10:00-11:00)
  - `A201`: Occupied by *Computer Organization & Architecture* (10:00-11:00)
  - `B101`: Occupied by 2-hour *Advanced Java Programming Lab* (09:00-11:00)
  - `B201`: Occupied by *Engineering Chemistry* (10:00-11:00)
  - `C101`: Occupied by *Technical Communication & Soft Skills* (10:00-11:00)
  - `C201`: Occupied by *Machine Learning Foundations* (10:00-11:00)

---

### Search 2: Monday Morning Science Block Filter
- **Input:** `day=MON`, `start=10:00`, `end=11:00`, `building=Science Block`
- **API Call:**
  ```http
  GET http://localhost:8080/api/rooms/free?day=MON&start=10:00&end=11:00&building=Science%20Block
  ```
- **Expected Result (Exactly 1 Room Found):**
  - **B102** (Science Block, Floor 1, LAB, Capacity 40) &rarr; **Free until 13:00**
- **Explanation:**
  - `B101` (Lab) is busy with a 2-hour Java Lab block from `09:00 - 11:00`.
  - `B201` (Classroom) is busy with Engineering Chemistry from `10:00 - 11:00`.
  - Only `B102` is free during this slot.

---

### Search 3: Partial Overlap Verification Window
- **Input:** `day=MON`, `start=10:30`, `end=11:30` (no filters)
- **API Call:**
  ```http
  GET http://localhost:8080/api/rooms/free?day=MON&start=10:30&end=11:30
  ```
- **Expected Results (3 Free Rooms Found):**
  1. **A202** (Main Block, Floor 2, Capacity 50) &rarr; **Free until 13:00**
  2. **B102** (Science Block, Floor 1, Capacity 40) &rarr; **Free until 13:00**
  3. **C202** (Tech Block, Floor 2, Capacity 45) &rarr; **Free until 14:00**
- **Verification of Partial Overlap Exclusion:**
  - **A102 is BLOCKED:** It has a class from `11:00 - 12:00`. Even though it was free from `10:00-11:00`, the `11:00-12:00` class partially overlaps with `10:30-11:30` (`11:00 < 11:30` and `12:00 > 10:30`).
  - **B101 is BLOCKED:** Its morning lab runs `09:00 - 11:00`, which partially overlaps with `10:30-11:30` (`09:00 < 11:30` and `11:00 > 10:30`).

---

## 🛡️ Input Validation & Error Handling

- **Invalid Time Order:** Requesting `start=11:00` and `end=10:00` yields `HTTP 400 Bad Request`:
  ```json
  {
    "timestamp": "2026-10-05T22:17:36",
    "status": 400,
    "error": "Bad Request",
    "message": "Invalid time window: end time (10:00) must be strictly after start time (11:00)",
    "path": "/api/rooms/free"
  }
  ```
- **Invalid Day:** Passing `day=SUNDAY_FUNDAY` yields `HTTP 400 Bad Request`:
  ```json
  {
    "timestamp": "2026-10-05T22:18:08",
    "status": 400,
    "error": "Bad Request",
    "message": "Invalid day of week: 'SUNDAY_FUNDAY'. Valid options are MON, TUE, WED, THU, FRI, SAT",
    "path": "/api/rooms/free"
  }
  ```
- **Resource Not Found:** Querying `/api/rooms/999/schedule?day=MON` returns `HTTP 404 Not Found`.
- **CORS Enabled:** Cross-Origin Resource Sharing is configured to allow queries from any browser origin.
