# Time Complexity Analyzer

## About

This project analyzes pasted code and detects its time complexity — not by calling an existing parsing library, but by building the entire pipeline from scratch: a hand-written lexer, a custom stack-based parser, and a complexity-detection engine written on top of it. It started as a way to actually understand how compilers reason about code (scanning, tokens, nesting) rather than just using a tool like ANTLR without knowing what's happening underneath. It's also a full-stack Spring Boot project end to end — REST API, MySQL persistence, and a plain HTML/CSS/JS frontend with submission history — deployed live rather than left running only on localhost.

**Live demo:** [https://time-complexity-analyzer-kaos.onrender.com](https://time-complexity-analyzer-kaos.onrender.com)
*(hosted on Render's free tier — the backend may take 30–60 seconds to wake up if it's been idle)*

## What it does

Paste a code snippet into the web UI, and the backend:
1. Tokenizes it with a custom lexer
2. Parses the tokens with a stack-based parser, tracking `{ }` nesting as it goes
3. Classifies time complexity based on loop nesting depth (`for` and `while` loops supported) and loop update patterns (`i *= 2`, `i /= 2`, bit shifts)
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
   Lexer.java         → tokenizes source into a List<Token>
      ↓
   Parser.java         → stack-based parser; pushes a new block context on "{"
                          and pops/attaches it on "}", tracking brace nesting
                          depth for both for and while loops; also detects
                          log n operators (*=, /=, <<, >>) inside loop bodies
      ↓
   ComplexityAnalyzer  → reads depth and logN flags to classify complexity
      ↓
   ComplexityResult    → { complexity, depth }
```

**Current known limitations (being addressed):**
- Complexity is currently derived from brace-nesting depth tracked during parsing itself, rather than a separate walk over a fully-built AST. This works for the common cases but is being refactored so `ComplexityAnalyzer` walks the tree independently.
- Log n detection is token-based — any `*=`, `/=`, `<<`, `>>` inside a loop body is flagged, even if it's not the loop variable being modified. More precise detection planned for v2.
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
- **Frontend pointed at localhost** — easy to miss: the deployed JS files must point `fetch()` calls at the live backend URL, not `http://localhost:8080`.

## Project Structure

```
src/main/java/com/shaurya/spring/timecomplexityanalyzer/
├── engine/
│   ├── Lexer.java
│   ├── Token.java
│   ├── TokenType.java
│   ├── Parser.java
│   ├── ComplexityAnalyzer.java
│   └── nodes/
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
- Java 25
- Maven (or use the included `mvnw`/`mvnw.cmd` wrapper)
- MySQL Server running locally
- IntelliJ IDEA (recommended)

### 1. Clone the repository
```bash
git clone https://github.com/ks9205124-cloud/time-complexity-analyzer.git
cd time-complexity-analyzer
```

### 2. Create the database
```sql
CREATE DATABASE tca_db;
```

### 3. Configure credentials
```bash
cp src/main/resources/application.properties.example src/main/resources/application.properties
```
Fill in your MySQL credentials. This file is git-ignored.

### 4. Run
```powershell
mvnw.cmd spring-boot:run
```
Backend starts at `http://localhost:8080`.

### 5. Open the frontend
```
http://localhost:8080/index.html
```

### 6. Test the API
```bash
curl -X POST http://localhost:8080/api/analyze \
  -H "Content-Type: application/json" \
  -d '{"code": "for(int i=0;i<n;i++){ for(int j=0;j<n;j++){ } }"}'
```

## Example Inputs

**O(n²) — nested loops:**
```java
for(int i = 0; i < n; i++){
    for(int j = 0; j < n; j++){
        sum = sum + 1;
    }
}
```

**O(n) — consecutive loops:**
```java
for(int i = 0; i < n; i++){ sum = sum + 1; }
for(int j = 0; j < n; j++){ sum = sum + 1; }
```

**O(n³) — triple nested:**
```java
for(int i = 0; i < n; i++){
    for(int j = 0; j < n; j++){
        for(int k = 0; k < n; k++){
            sum = sum + 1;
        }
    }
}
```

**O(log n) — dividing loop:**
```java
for(int i = 1; i < n; i *= 2) { }
```

**O(n log n) — outer linear, inner logarithmic:**
```java
for(int i = 0; i < n; i++){
    for(int j = 1; j < n; j *= 2){ }
}
```

## Roadmap (v2)

- Refactor `ComplexityAnalyzer` to walk a fully-built AST independently
- Precise log n detection — identify the actual loop variable instead of any `*=` in the body
- Recursion detection (method declarations + self-referencing calls → O(2ⁿ), O(log n))
- Multi-language support: hand-written lexers and parsers for C, JavaScript, and Python
- Python indentation-based block detection

## Status

✅ Deployed and live at [time-complexity-analyzer-kaos.onrender.com](https://time-complexity-analyzer-kaos.onrender.com) — full pipeline working end to end. Detects O(1), O(n), O(n²), O(n³), O(log n), O(n log n) for `for` and `while` loops.