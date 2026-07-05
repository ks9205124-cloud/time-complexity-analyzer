# Time Complexity Analyzer

## About

This project analyzes pasted code and detects its time complexity — not by calling an existing parsing library, but by building the entire pipeline from scratch: a hand-written lexer, a custom stack-based parser, and a complexity-detection engine written on top of it. It started as a way to actually understand how compilers reason about code (scanning, tokens, nesting) rather than just using a tool like ANTLR without knowing what's happening underneath. It's also a full-stack Spring Boot project end to end — REST API, MySQL persistence, and a plain HTML/CSS/JS frontend with submission history — deployed live rather than left running only on localhost.

**Live demo:** [https://time-complexity-analyzer-kaos.onrender.com](https://time-complexity-analyzer-kaos.onrender.com)
*(hosted on Render's free tier — the backend may take 30–60 seconds to wake up if it's been idle)*

## What it does

Paste a code snippet into the web UI, and the backend:
1. Tokenizes it with a custom lexer
2. Parses the tokens with a stack-based parser, tracking `{ }` nesting as it goes
3. Classifies time complexity based on loop nesting depth (`for` loops currently; `while` support in progress)
4. Stores every submission in MySQL
5. Displays results instantly, with a full submission history page

## Tech Stack

| Layer | Technology |
|---|---|
| Backend | Spring Boot (REST API) |
| Database | MySQL + Spring Data JPA |
| Frontend | HTML + CSS + vanilla JS |
| Analysis Engine | Hand-written Java lexer + stack-based parser + brace-depth complexity classifier |
| Build Tool | Maven |
| Hosting | [Render](https://render.com) (backend, Docker-based deploy) + [filess.io](https://filess.io) (free-tier MySQL) |

## Architecture

```
Raw code string
      ↓
   Lexer.java        → tokenizes source into a List<Token>
      ↓
   Parser.java        → stack-based parser; pushes a new block context on "{"
                         and pops/attaches it on "}", tracking brace nesting
                         depth directly during this process
      ↓
   ComplexityAnalyzer  → reads the resulting depth to classify complexity
      ↓
   ComplexityResult    → { complexity, depth }
```

**Current known limitations (being addressed):**
- Complexity is currently derived from brace-nesting depth tracked during parsing itself, rather than a separate walk over a fully-built AST. This works for the common cases but is being refactored so `ComplexityAnalyzer` walks the tree independently — this will make consecutive-vs-nested loop detection more reliable.
- Only `for` loops are currently detected. `while` loop support is planned next.
- O(log n) and O(n log n) detection are not yet implemented — a loop with a dividing/shifting update (e.g. `i /= 2`) is currently still classified by nesting depth alone.
- Recursive complexity detection (e.g. Fibonacci-style O(2ⁿ)) is planned as a future upgrade, along with multi-language support (C, JavaScript, Python).

## API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/analyze` | Accepts `{ "code": "..." }`, returns `{ complexity, depth }` and saves the submission |
| `GET` | `/api/submissions` | Returns all past submissions |

## Deployment

The app is deployed as a Docker container on **Render**, connected to a free-tier **MySQL instance on filess.io**. A few real constraints from getting this working, worth knowing if you're deploying your own fork:

- **`mvnw` executable bit** — Git doesn't always preserve the Unix executable permission when committed from Windows. Fixed with `git update-index --chmod=+x mvnw` and a `.gitattributes` rule (`mvnw text eol=lf`) to keep line endings consistent. The Dockerfile also runs `chmod +x mvnw` as a backup.
- **DB credentials via environment variables** — `application.properties` references `${DB_URL}`, `${DB_USERNAME}`, `${DB_PASSWORD}` rather than hardcoded values; the real values are set in Render's Environment tab, not committed to Git.
- **Hibernate dialect** — explicitly set `spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect` since auto-detection failed against filess.io without it.
- **SSL connection params** — filess.io's JDBC URL needs `?useSSL=true&requireSSL=false&serverTimezone=UTC` appended.
- **Connection pool size** — filess.io's free tier caps connections at 5 total for the DB user. Spring Boot's default HikariCP pool (10 connections) exceeds that immediately. Capped with:
  ```properties
  spring.datasource.hikari.maximum-pool-size=2
  spring.datasource.hikari.minimum-idle=1
  ```
- **Frontend pointed at localhost** — easy to miss: the deployed JS files must point `fetch()` calls at the live backend URL, not `http://localhost:8080`, or the live site will silently talk to whatever's running on your own machine instead of the deployed server.

## Project Structure

```
src/main/java/com/shaurya/spring/timecomplexityanalyzer/
├── engine/
│   ├── Lexer.java
│   ├── Token.java
│   ├── TokenType.java
│   ├── Parser.java
│   ├── ast/
│   │   ├── Node.java
│   │   ├── ForNode.java
│   │   └── BlockNode.java
│   └── ComplexityAnalyzer.java
├── controller/
│   └── AnalysisController.java
├── service/
│   └── AnalysisService.java
├── repository/
│   └── SubmissionRepository.java
├── model/
│   └── Submission.java
└── dto/
    ├── AnalysisRequest.java
    └── ComplexityResult.java
src/main/resources/
├── static/
│   ├── index.html
│   ├── history.html
│   ├── styles.css
│   ├── script.js
│   └── script_history.js
└── application.properties.example
Dockerfile
```

## Running Locally

### Prerequisites
- Java 17+ (project uses Java 25 features in places — check your local JDK version matches what's in `pom.xml`)
- Maven (or use the included `mvnw`/`mvnw.cmd` wrapper — no separate install needed)
- MySQL Server, running locally
- IntelliJ IDEA (recommended) or any IDE with Spring Boot support

### 1. Clone the repository
```bash
git clone https://github.com/ks9205124-cloud/time-complexity-analyzer.git
cd time-complexity-analyzer
```

### 2. Create the database
Open MySQL Workbench (or the MySQL CLI) and run:
```sql
CREATE DATABASE tca_db;
```

### 3. Configure your local credentials
Copy the example properties file:
```bash
cp src/main/resources/application.properties.example src/main/resources/application.properties
```
Then open `application.properties` and fill in your actual MySQL username and password:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/tca_db
spring.datasource.username=root
spring.datasource.password=YOUR_PASSWORD_HERE
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```
This file is git-ignored on purpose — your real credentials never get committed.

### 4. Run the backend
Using the Maven wrapper:
```bash
./mvnw spring-boot:run
```
On Windows:
```powershell
mvnw.cmd spring-boot:run
```
Or just hit **Run** on `TimeComplexityAnalyzerApplication.java` in IntelliJ.

The backend starts at `http://localhost:8080`. Hibernate will auto-create the table in `tca_db` on first run.

### 5. Open the frontend
Since the frontend lives in `src/main/resources/static/`, Spring Boot serves it directly. With the app running, open:
```
http://localhost:8080/index.html
```
in your browser. Paste code into the text area, hit **Analyze**, and check `http://localhost:8080/history.html` to see past submissions.

### 6. Test the API directly (optional)
Using Postman or curl:
```bash
curl -X POST http://localhost:8080/api/analyze \
  -H "Content-Type: application/json" \
  -d '{"code": "for(int i=0;i<n;i++){ for(int j=0;j<n;j++){ } }"}'
```

## Example Inputs to Try

**Nested loop — expect O(n²), depth 2:**
```java
for(int i = 0; i < n; i++){
    for(int j = 0; j < n; j++){
        sum = sum + 1;
    }
}
```

**Consecutive loops — expect O(n), depth 1:**
```java
for(int i = 0; i < n; i++){
    sum = sum + 1;
}
for(int j = 0; j < n; j++){
    sum = sum + 1;
}
```

**Triple nested — expect O(n³), depth 3:**
```java
for(int i = 0; i < n; i++){
    for(int j = 0; j < n; j++){
        for(int k = 0; k < n; k++){
            sum = sum + 1;
        }
    }
}
```

> Note: loops with dividing/shifting updates (e.g. `i /= 2`) will currently still be classified by nesting depth alone — O(log n) detection is a planned v2 addition, not yet implemented.

## Roadmap (v2)

- Refactor `ComplexityAnalyzer` to walk a fully-built AST independently, rather than deriving depth during parsing
- `while` loop detection and analysis
- O(log n) / O(n log n) detection (loop update patterns like `i /= 2`, `i *= 2`, bit shifts)
- Recursion detection (method declarations + self-referencing calls → O(2ⁿ), O(log n), etc.)
- Multi-language support: hand-written lexers and parsers for C, JavaScript, and Python
- Python indentation-based block detection (no `{ }` to rely on)

## Status

✅ Deployed and live at [time-complexity-analyzer-kaos.onrender.com](https://time-complexity-analyzer-kaos.onrender.com) — full pipeline (lexer → parser → complexity classification → REST API → MySQL → frontend with history) working end to end for `for`-loop nesting detection. `while` loop support and an independent AST-walking analyzer are the next things planned before calling this v1 complete.