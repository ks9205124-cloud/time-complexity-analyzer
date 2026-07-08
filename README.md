# Time Complexity Analyzer

## About

This project analyzes pasted code and detects its time complexity — not by calling an existing parsing library, but by building the entire pipeline from scratch: a hand-written lexer, a recursive descent parser that builds a real AST, and a tree-walking complexity engine on top of it. It started as a way to actually understand how compilers reason about code (scanning, tokens, parsing, tree walking) rather than just using a tool like ANTLR without knowing what's happening underneath. It's also a full-stack Spring Boot project end to end — REST API, MySQL persistence, and a plain HTML/CSS/JS frontend with submission history — deployed live rather than left running only on localhost.

**Live demo:** [https://time-complexity-analyzer-kaos.onrender.com](https://time-complexity-analyzer-kaos.onrender.com)
*(hosted on Render's free tier — the backend may take 30–60 seconds to wake up if it's been idle)*

## What it does

Paste a code snippet into the web UI, and the backend:
1. Tokenizes it with a custom hand-written lexer
2. Parses the token stream with a recursive descent parser that builds a real AST (`ForNode`, `WhileNode`, `BlockNode`)
3. Walks the AST to calculate maximum loop nesting depth and detect logarithmic update patterns
4. Classifies time complexity — O(1), O(n), O(n²), O(n³), O(log n), O(n log n) — for `for` and `while` loops
5. Stores every submission in MySQL
6. Displays results instantly, with a full submission history page

## Tech Stack

| Layer | Technology |
|---|---|
| Backend | Spring Boot (REST API) |
| Database | MySQL + Spring Data JPA |
| Frontend | HTML + CSS + vanilla JS |
| Analysis Engine | Hand-written lexer → recursive descent AST parser → tree-walking complexity analyzer |
| Build Tool | Maven |
| Hosting | [Render](https://render.com) (backend, Docker-based deploy) + [filess.io](https://filess.io) (free-tier MySQL) |

## Architecture

```
Raw code string
      ↓
   Lexer.java          → tokenizes source into List<Token>
                          strips comments, handles two-char operators with peek-ahead
                          wraps source in { } so parser always has a root block
      ↓
   Parser.java          → recursive descent parser
                          parseBlock() → parseFor() / parseWhile() → parseBlock() (recursive)
                          builds a real AST: ForNode, WhileNode, BlockNode
                          detects log n operators (*=, /=, <<, >>) and stores flag on each node
      ↓
   Parser.walk()        → tree walker, separate from parsing
                          tracks currDepth and maxDepth as it recurses into the AST
                          depth++ on entering ForNode/WhileNode, depth-- on exit
                          consecutive loops handled correctly — depth resets after each
      ↓
   ComplexityAnalyzer   → reads maxDepth and logN flags to classify complexity
      ↓
   ComplexityResult     → { complexity, depth }
```

**Current known limitations:**
- Log n detection is token-based — any `*=`, `/=`, `<<`, `>>` inside a loop body is flagged, even if it's not the loop variable. Precise detection planned for v2.
- Only iterative complexity is detected. Recursive complexity (e.g. Fibonacci O(2ⁿ)) requires method declaration and call tracking — planned for v2.
- Only Java syntax supported. Multi-language support (C, JavaScript, Python) planned for v2.

## API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/analyze` | Accepts `{ "code": "..." }`, returns `{ complexity, depth }` and saves the submission |
| `GET` | `/api/submissions` | Returns all past submissions |

## Deployment

The app is deployed as a Docker container on **Render**, connected to a free-tier **MySQL instance on filess.io**. A few real constraints from getting this working:

- **`mvnw` executable bit** — Git doesn't always preserve the Unix executable permission when committed from Windows. Fixed with `git update-index --chmod=+x mvnw` and a `.gitattributes` rule (`mvnw text eol=lf`). The Dockerfile also runs `chmod +x mvnw` as a backup.
- **DB credentials via environment variables** — `application.properties` references `${DB_URL}`, `${DB_USERNAME}`, `${DB_PASSWORD}`; real values set in Render's Environment tab, never committed to Git.
- **Hibernate dialect** — explicitly set `spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect` since auto-detection failed against filess.io.
- **SSL connection params** — filess.io JDBC URL needs `?useSSL=true&requireSSL=false&serverTimezone=UTC`.
- **Connection pool size** — filess.io free tier caps at 5 connections. Capped HikariCP:
  ```properties
  spring.datasource.hikari.maximum-pool-size=2
  spring.datasource.hikari.minimum-idle=1
  ```
- **Frontend fetch URL** — deployed JS must point to the live backend URL, not `http://localhost:8080`.

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
│       ├── rootNode.java
│       ├── ForNode.java
│       ├── WhileNode.java
│       └── BlockNode.java
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
- Maven (or use `mvnw`/`mvnw.cmd` wrapper)
- MySQL Server running locally
- IntelliJ IDEA (recommended)

### 1. Clone
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
Fill in your MySQL credentials. File is git-ignored.

### 4. Run
```powershell
mvnw.cmd spring-boot:run
```
Backend at `http://localhost:8080`.

### 5. Open frontend
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

**O(1) — no loops:**
```java
int x = 5;
int y = x + 10;
```

**O(n) — single loop:**
```java
for(int i = 0; i < n; i++) {
    System.out.println(i);
}
```

**O(n) — consecutive loops (correctly detected, not O(n²)):**
```java
for(int i = 0; i < n; i++) { }
for(int j = 0; j < n; j++) { }
```

**O(n²) — nested loops:**
```java
for(int i = 0; i < n; i++){
    for(int j = 0; j < n; j++){
        sum = sum + 1;
    }
}
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

**O(log n) — dividing for loop:**
```java
for(int i = 1; i < n; i *= 2) { }
```

**O(log n) — dividing while loop:**
```java
while(i < n) { i *= 2; }
```

**O(n log n) — outer linear, inner logarithmic:**
```java
for(int i = 0; i < n; i++){
    for(int j = 1; j < n; j *= 2){ }
}
```

## Roadmap (v2)

- Precise log n detection — check actual loop variable, not any `*=` in the body
- Recursion detection — method declarations + self-referencing calls → O(2ⁿ), O(log n)
- Multi-language support — hand-written lexers and parsers for C, JavaScript, and Python
- Python indentation-based block detection (no `{ }` to rely on)

## Status

✅ Deployed and live at [time-complexity-analyzer-kaos.onrender.com](https://time-complexity-analyzer-kaos.onrender.com)

Full pipeline working end to end: hand-written lexer → recursive descent AST parser → tree-walking complexity analyzer → REST API → MySQL → frontend with submission history.

Detects O(1), O(n), O(n²), O(n³), O(log n), O(n log n) for `for` and `while` loops. Consecutive vs nested loops handled correctly via proper AST tree walking. Recursive complexity detection planned for v2.