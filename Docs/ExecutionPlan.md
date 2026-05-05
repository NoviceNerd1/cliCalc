# CLI-Based Modular Calculator — Complete Execution Plan

## Principal Engineer Edition | 6-Week Zero-to-Production Roadmap

---

# EXECUTIVE SUMMARY

| Metric | Target | Measurement |
|--------|--------|-------------|
| **Duration** | 6 weeks | 30 working days |
| **Effort** | 240 hours | 1 developer (principal) |
| **Deliverable** | Production JAR + Native Image | Working calculator |
| **Test Coverage** | >80% | Jacoco report |
| **Performance** | 10K expressions/sec | JMH benchmark |

---

# PHASE 0 — PREPARATION (Day 0)

## 0.1 Environment Setup

```bash
# Required installations (2 hours)
1. JDK 21+ (Eclipse Temurin recommended)
   https://adoptium.net/

2. IntelliJ IDEA Community Edition (free)
   OR VS Code with Java extensions

3. Gradle 8.5+ (or use wrapper)

4. Git

5. Docker (for containerized testing)

# Verify setup
java --version      # 21+
./gradlew --version  # 8.5+
git --version
docker --version
```

## 0.2 Project Initialization

```bash
# Create root directory (30 minutes)
mkdir calculator
cd calculator

# Initialize Git repository
git init
echo "# CLI Modular Calculator" > README.md
echo "*.class" > .gitignore
echo "build/" >> .gitignore
echo ".gradle/" >> .gitignore
echo "*.log" >> .gitignore

# Create Gradle wrapper
gradle wrapper --gradle-version 8.5
```

## 0.3 Directory Structure Creation

```bash
# Create all module directories (15 minutes)
mkdir -p core/src/{main,test}/java/com/calculator/core
mkdir -p operations/src/{main,test}/java/com/calculator/operations
mkdir -p parser/src/{main,test}/java/com/calculator/parser
mkdir -p evaluator/src/{main,test}/java/com/calculator/evaluator
mkdir -p history/src/{main,test}/java/com/calculator/history
mkdir -p config/src/{main,test}/java/com/calculator/config
mkdir -p cli/src/{main,test}/java/com/calculator/cli
mkdir -p app/src/{main,test}/java/com/calculator/app
mkdir -p launcher/src/{main,test}/java

# Create test resource directories
mkdir -p core/src/test/resources
mkdir -p parser/src/test/resources
mkdir -p cli/src/test/resources
```

---

# PHASE 1 — CORE INFRASTRUCTURE (Week 1)

## Day 1: Root Build Configuration (4 hours)

**Tasks:**
1. Create `settings.gradle.kts` with all modules
2. Create root `build.gradle.kts` with common configuration
3. Configure Java toolchain (JDK 21)
4. Setup test dependencies (JUnit 5, AssertJ, Mockito)
5. Configure Checkstyle (Google Java Format)

**Deliverable:** `settings.gradle.kts`, root `build.gradle.kts`

**Success Criteria:** `./gradlew tasks` shows all modules

```kotlin
// settings.gradle.kts
rootProject.name = "calculator"
include(
    "core", "operations", "parser", "evaluator",
    "history", "config", "cli", "app", "launcher"
)
```

## Day 2: Core Module - Abstractions (6 hours)

**Tasks:**
1. Create `Token` and `TokenType` (record, enum)
2. Create `Operation` interface (SPI)
3. Create `OperationRegistry` (ServiceLoader wrapper)
4. Create exception hierarchy (`ParseException`, `EvaluationException`)
5. Write unit tests for each class

**Files to create:**
```
core/src/main/java/com/calculator/core/model/
  ├── Token.java
  ├── TokenType.java
  └── Expression.java

core/src/main/java/com/calculator/core/spi/
  ├── Operation.java
  └── OperationRegistry.java

core/src/main/java/com/calculator/core/exception/
  ├── CalculatorException.java
  ├── ParseException.java
  ├── EvaluationException.java
  ├── DivisionByZeroException.java
  └── DomainException.java
```

**Unit Tests:**
- Token immutability
- OperationRegistry registration/lookup
- Exception messages contain context

**Deliverable:** Core module compiled, tests pass

**Success Criteria:** `./gradlew :core:test` → green

## Day 3: Operations Module - Basic Arithmetic (6 hours)

**Tasks:**
1. Implement `AddOperation`, `SubtractOperation`
2. Implement `MultiplyOperation`, `DivideOperation`
3. Create service files in `META-INF/services/`
4. Write property-based tests (commutative, associative)
5. Handle edge cases (division by zero)

**Files to create:**
```
operations/src/main/java/com/calculator/operations/
  ├── AddOperation.java
  ├── SubtractOperation.java
  ├── MultiplyOperation.java
  └── DivideOperation.java

operations/src/main/resources/META-INF/services/
  └── com.calculator.core.spi.Operation
```

**Property Tests:**
- `a + b == b + a` (commutative)
- `(a + b) + c == a + (b + c)` (associative)
- `a * 0 == 0`

**Deliverable:** Four basic operations working

**Success Criteria:** `OperationRegistry.get("+")` returns `AddOperation`

## Day 4: Core Module - Expression Model (4 hours)

**Tasks:**
1. Create `Expression` class (immutable)
2. Add validation (balanced parentheses check)
3. Add caching (LRU, max 1000 expressions)
4. Write tests for expression validation

**Files to create/modify:**
```
core/src/main/java/com/calculator/core/model/Expression.java
core/src/main/java/com/calculator/core/cache/ExpressionCache.java
```

**Tests:**
- Valid expressions: `"2+2"`, `"(3+4)*5"`
- Invalid: `"(2+2"`, `"2++2"`

**Deliverable:** Expression parsing framework (without actual parser)

## Day 5: Module Integration & Polish (4 hours)

**Tasks:**
1. Ensure all modules compile together
2. Run full test suite
3. Fix any dependency issues
4. Update README with module structure

**Deliverable:** Week 1 complete, all modules compile

**Success Criteria:** `./gradlew build` → BUILD SUCCESSFUL

---

# PHASE 2 — PARSER IMPLEMENTATION (Week 2)

## Day 6: Tokenizer (6 hours)

**Tasks:**
1. Create `Tokenizer` class with regex patterns
2. Support numbers (integers, decimals, scientific notation)
3. Support operators (+ - * / ^)
4. Support functions (sqrt, sin, cos, tan)
5. Support parentheses
6. Track token positions for error messages

**Files to create:**
```
parser/src/main/java/com/calculator/parser/
  ├── Tokenizer.java
  └── TokenizerException.java
```

**Regex Pattern:**
```java
Pattern.compile(
    "(?<NUMBER>\\d+(?:\\.\\d+)?(?:[eE][+-]?\\d+)?)|" +
    "(?<OPERATOR>[+\\-*/^])|" +
    "(?<FUNCTION>sqrt|sin|cos|tan)|" +
    "(?<LPAREN>\\()|" +
    "(?<RPAREN>\\))|" +
    "(?<WHITESPACE>\\s+)|" +
    "(?<INVALID>.)"
);
```

**Tests:**
- `"2+2"` → `[2, +, 2]`
- `"3.14 * 2"` → `[3.14, *, 2]`
- `"sqrt(16)"` → `[sqrt, (, 16, )]`
- `"2e-5"` → scientific notation
- Invalid char `"2$2"` → throws TokenizerException

**Deliverable:** Tokenizer converts string to tokens

## Day 7: Precedence Table (4 hours)

**Tasks:**
1. Create `PrecedenceTable` class
2. Define default precedence (PEMDAS)
3. Support right-associativity (for `^`)
4. Make configurable via builder
5. Write validation (cycle detection)

**Files to create:**
```
parser/src/main/java/com/calculator/parser/
  ├── PrecedenceTable.java
  └── Associativity.java (enum)
```

**Default Configuration:**
| Operator | Precedence | Associativity |
|----------|------------|---------------|
| +, - | 2 | LEFT |
| *, /, % | 3 | LEFT |
| ^ | 4 | RIGHT |
| unary_plus, unary_minus | 5 | RIGHT |

**Tests:**
- `getPrecedence("+")` → 2
- `isRightAssociative("^")` → true
- Cycle detection: circular precedence → throws

**Deliverable:** Precedence table ready for Shunting-Yard

## Day 8: Shunting-Yard Algorithm (6 hours)

**Tasks:**
1. Implement `ShuntingYardParser` class
2. Convert infix tokens to RPN (postfix)
3. Handle operator precedence and associativity
4. Handle functions (push to output when closing paren)
5. Handle unary operators (detect `-` as unary)
6. Add error detection (missing operands, mismatched parens)

**Files to create:**
```
parser/src/main/java/com/calculator/parser/
  └── ShuntingYardParser.java
```

**Algorithm Implementation:**
```java
public Queue<Token> parse(List<Token> tokens) {
    Deque<Token> stack = new ArrayDeque<>();
    Queue<Token> output = new ArrayDeque<>();
    
    for (Token token : tokens) {
        switch (token.type()) {
            case NUMBER -> output.add(token);
            case FUNCTION -> stack.push(token);
            case OPERATOR -> {
                while (!stack.isEmpty() && 
                       stack.peek().type() == OPERATOR &&
                       (precedence(token) < precedence(stack.peek()) ||
                        (precedence(token) == precedence(stack.peek()) &&
                         associativity(token) == LEFT))) {
                    output.add(stack.pop());
                }
                stack.push(token);
            }
            case LPAREN -> stack.push(token);
            case RPAREN -> {
                while (!stack.isEmpty() && stack.peek().type() != LPAREN) {
                    output.add(stack.pop());
                }
                stack.pop(); // remove LPAREN
                if (!stack.isEmpty() && stack.peek().type() == FUNCTION) {
                    output.add(stack.pop());
                }
            }
        }
    }
    while (!stack.isEmpty()) output.add(stack.pop());
    return output;
}
```

**Tests:**
- `"3+4"` → `[3, 4, +]`
- `"3+4*2"` → `[3, 4, 2, *, +]`
- `"(3+4)*2"` → `[3, 4, +, 2, *]`
- `"2^3^2"` → `[2, 3, 2, ^, ^]` (right-assoc)
- `"sin(30)"` → `[30, sin]`

**Deliverable:** Infix to RPN conversion working

## Day 9: Parser Integration & Error Handling (4 hours)

**Tasks:**
1. Integrate Tokenizer + ShuntingYard into single parser
2. Add position tracking for errors
3. Create detailed error messages with caret indicators
4. Add parse tree visualization (debug mode)

**Files to create:**
```
parser/src/main/java/com/calculator/parser/CalculatorParser.java
parser/src/main/java/com/calculator/parser/ParseError.java
```

**Error Message Example:**
```
calc> 2 + * 3
           ^
Error: Unexpected operator '*' at position 4. Expected operand.
```

**Deliverable:** User-friendly parse errors

## Day 10: Parser Testing & Polish (4 hours)

**Tasks:**
1. Add 50+ parser test cases
2. Property tests: random expressions vs expected
3. Performance test: 10K expressions <100ms
4. Documentation: parser architecture

**Tests to Add:**
- Nested parentheses: `(1+(2+3))`
- Multiple operators: `1+2+3+4`
- Unary minus: `-5+3`, `5+-3`
- Complex: `(3+4*2)/(1-5)^2^3`

**Deliverable:** Week 2 complete, parser fully tested

**Success Criteria:** `./gradlew :parser:test` → green, 90% coverage

---

# PHASE 3 — EVALUATOR & REPL BASICS (Week 3)

## Day 11: Postfix Evaluator (6 hours)

**Tasks:**
1. Create `PostfixEvaluator` class
2. Implement stack machine (Deque<BigDecimal>)
3. Integrate with OperationRegistry
4. Handle binary and unary operations
5. Add overflow protection (max stack depth 1000)

**Files to create:**
```
evaluator/src/main/java/com/calculator/evaluator/
  ├── PostfixEvaluator.java
  ├── StackMachine.java
  └── EvaluationContext.java
```

**Evaluation Loop:**
```java
public BigDecimal evaluate(Queue<Token> postfix, EvaluationContext context) {
    Deque<BigDecimal> stack = new ArrayDeque<>();
    
    for (Token token : postfix) {
        if (token.type() == NUMBER) {
            stack.push(new BigDecimal(token.value(), context.mathContext()));
        } else if (token.type() == OPERATOR || token.type() == FUNCTION) {
            Operation op = registry.get(token.value())
                .orElseThrow(() -> new EvaluationException("Unknown operator: " + token.value()));
            
            if (op.arity() == 2) {
                BigDecimal right = stack.pop();
                BigDecimal left = stack.pop();
                stack.push(op.apply(left, right));
            } else if (op.arity() == 1) {
                BigDecimal operand = stack.pop();
                stack.push(op.apply(operand));
            }
        }
    }
    return stack.pop();
}
```

**Tests:**
- Simple: `[2, 2, +]` → `4`
- Compound: `[3, 4, 2, *, +]` → `11`
- Unary: `[5, unary_minus]` → `-5`

**Deliverable:** RPN evaluator working

## Day 12: Advanced Operations (6 hours)

**Tasks:**
1. Implement `PowerOperation` (binary)
2. Implement `SqrtOperation` (unary)
3. Implement `UnaryMinusOperation`
4. Implement `PercentOperation`
5. Add tests for edge cases (sqrt negative, pow overflow)

**Files to create:**
```
operations/src/main/java/com/calculator/operations/
  ├── PowerOperation.java
  ├── SqrtOperation.java
  ├── UnaryMinusOperation.java
  └── PercentOperation.java
```

**Edge Case Tests:**
- `2^10` → `1024`
- `2^-2` → `0.25`
- `sqrt(16)` → `4`
- `sqrt(-1)` → DomainException
- `-5` → `-5`
- `50%` → `0.5`

**Deliverable:** All specified operations working

## Day 13: Evaluation Context & Configuration (4 hours)

**Tasks:**
1. Create `EvaluationContext` with MathContext
2. Support pluggable precision (default 16 digits)
3. Support configurable rounding modes
4. Add thread-local context for concurrent evaluation
5. Wire up with Config module (Week 4)

**Files to create/modify:**
```
evaluator/src/main/java/com/calculator/evaluator/EvaluationContext.java
core/src/main/java/com/calculator/core/math/MathConfig.java
```

**Deliverable:** Configurable precision (16/32/64 digits)

## Day 14: CLI - Basic REPL (6 hours)

**Tasks:**
1. Setup JLine3 dependency in `cli` module
2. Create `CalculatorCLI` class
3. Implement basic read-eval-print loop
4. Handle expression evaluation path
5. Handle exit command (/exit, Ctrl+C)
6. Format output with colors

**Files to create:**
```
cli/src/main/java/com/calculator/cli/
  ├── CalculatorCLI.java
  ├── OutputFormatter.java
  └── repl/ReplLoop.java
```

**REPL Loop Pseudo-code:**
```java
public void run() {
    try (Terminal terminal = TerminalBuilder.builder().build();
         LineReader reader = LineReaderBuilder.builder()
            .terminal(terminal)
            .history(new DefaultHistory())
            .build()) {
        
        while (true) {
            String line = reader.readLine("calc> ");
            if (line == null || line.equalsIgnoreCase("/exit")) break;
            
            if (line.startsWith("/")) {
                handleCommand(line);
            } else {
                evaluateAndPrint(line);
            }
        }
    }
}
```

**Deliverable:** Interactive calculator working

## Day 15: CLI Polish & Commands (4 hours)

**Tasks:**
1. Add `/help` command
2. Add `/clear` command
3. Add error recovery (don't crash on invalid input)
4. Add startup banner
5. Test across terminals (Windows, macOS, Linux)

**Commands to Implement:**
- `/help`, `/?` → show help
- `/exit`, `/quit` → exit
- `/clear` → clear screen
- `/version` → show version
- `/debug` → toggle debug mode

**Deliverable:** Week 3 complete, working REPL

**Success Criteria:** User can type `2+2` and see `4`

---

# PHASE 4 — HISTORY & CONFIGURATION (Week 4)

## Day 16: SQLite Integration (6 hours)

**Tasks:**
1. Add SQLite JDBC dependency
2. Add HikariCP connection pool
3. Create `SqliteHistoryRepository` class
4. Implement schema creation on startup
5. Add insert and query methods

**Files to create:**
```
history/src/main/java/com/calculator/history/
  ├── HistoryService.java (interface)
  ├── SqliteHistoryRepository.java
  └── model/HistoryEntry.java
```

**Schema:**
```sql
CREATE TABLE IF NOT EXISTS history (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    expression TEXT NOT NULL,
    result TEXT NOT NULL,
    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
    duration_ms INTEGER
);
CREATE INDEX IF NOT EXISTS idx_timestamp ON history(timestamp);
```

**Deliverable:** History persists across sessions

## Day 17: History Service Implementation (6 hours)

**Tasks:**
1. Implement `save(CalculationResult)`
2. Implement `getLastN(int n)`
3. Implement `search(String pattern)`
4. Implement `export(Path, Format)`
5. Implement `clear()`
6. Add error handling (disk full, locked)

**History Commands:**
- `/history` → show last 20 calculations
- `/history export csv` → export to CSV
- `/history export json` → export to JSON
- `/history clear` → clear all history
- `/history search "2+2"` → search pattern

**Deliverable:** Full history management

## Day 18: Configuration Module (6 hours)

**Tasks:**
1. Add Typesafe Config dependency
2. Create `CalculatorConfig` class
3. Implement default configuration
4. Add file loading (multiple locations: /etc, ~/.calculator, .)
5. Add environment variable overrides
6. Add `/config show` command

**Files to create:**
```
config/src/main/java/com/calculator/config/
  ├── CalculatorConfig.java
  ├── ConfigLoader.java
  └── PrecedenceConfig.java

launcher/src/main/resources/application.conf
```

**application.conf:**
```hocon
calculator {
  precision = 16
  rounding-mode = "HALF_EVEN"
  
  precedence {
    "+" = 2, "-" = 2
    "*" = 3, "/" = 3, "%" = 3
    "^" = 4
  }
  
  history {
    max-entries = 10000
    database-path = "~/.calculator/history.db"
  }
  
  repl {
    prompt = "calc> "
    history-file = "~/.calculator/repl_history.txt"
  }
}
```

**Deliverable:** Configurable calculator

## Day 19: Dynamic Configuration Reload (4 hours)

**Tasks:**
1. Add `/config set key value` command
2. Validate new values before applying
3. Hot-reload precedence table (rebuild parser)
4. Hot-reload precision (reset MathContext)
5. Persist changes to user config file

**Validation:**
- precision: 1-100, integer
- rounding-mode: enum values only
- precedence values: 1-4, no cycles

**Deliverable:** Runtime configuration changes

## Day 20: Integration & Testing (4 hours)

**Tasks:**
1. Wire all modules together in `app` module
2. Create `CalculatorApplication` main class
3. Test end-to-end: startup → eval → history → config → exit
4. Fix any dependency issues
5. Run full integration test suite

**Files to create:**
```
app/src/main/java/com/calculator/app/
  ├── CalculatorApplication.java
  └── ModuleConfigurer.java
```

**Deliverable:** Week 4 complete, fully integrated app

**Success Criteria:** `./gradlew :app:run` starts calculator

---

# PHASE 5 — ADVANCED FEATURES (Week 5)

## Day 21: Command Completion & History (6 hours)

**Tasks:**
1. Implement tab completion for commands and functions
2. Add JLine3 history (up/down arrows)
3. Implement reverse search (Ctrl+R)
4. Add syntax highlighting (ANSI colors)
5. Add parentheses matching (highlight matching pair)

**Files to modify:**
```
cli/src/main/java/com/calculator/cli/repl/
  ├── CommandCompleter.java
  ├── SyntaxHighlighter.java
  └── ParenthesesMatcher.java
```

**Deliverable:** Full-featured REPL with readline support

## Day 22: Batch Mode & File Input (6 hours)

**Tasks:**
1. Add `--file` command line option
2. Implement batch processing (read expressions from file)
3. Add summary statistics (total, succeeded, failed)
4. Add error file output (expressions that failed)
5. Add exit code (0 if all succeeded, 1 if any failed)

**Implementation:**
```java
// BatchFileProcessor.java
public BatchResult process(Path filePath) {
    List<String> lines = Files.readAllLines(filePath);
    List<CalculationResult> results = new ArrayList<>();
    
    for (String line : lines) {
        if (line.trim().isEmpty() || line.startsWith("#")) continue;
        results.add(evaluate(line));
    }
    
    printSummary(results);
    return new BatchResult(results);
}
```

**Deliverable:** Batch processing mode

## Day 23: JSON Mode (6 hours)

**Tasks:**
1. Add `--json` command line option
2. Implement JSON input parsing (single expression or array)
3. Implement JSON output formatting
4. Add streaming mode (one JSON object per line)
5. Support for tool integration (IDE plugins, CI/CD)

**JSON Input Format:**
```json
{"expression": "2+2", "id": "req-001"}
[{"expression": "2+2"}, {"expression": "3*4"}]
```

**JSON Output Format:**
```json
{"id": "req-001", "expression": "2+2", "result": "4", "status": "SUCCESS"}
```

**Deliverable:** JSON mode for scripting

## Day 24: Metrics & Observability (6 hours)

**Tasks:**
1. Add Dropwizard Metrics dependency
2. Create `MetricsRegistry` singleton
3. Add timers for parsing, evaluation
4. Add counters for expressions (total, succeeded, failed)
5. Add health check endpoint
6. Add `--metrics` flag to print metrics on exit

**Files to create:**
```
core/src/main/java/com/calculator/core/metrics/MetricsRegistry.java
app/src/main/java/com/calculator/app/HealthChecker.java
```

**Metrics Output:**
```
=== Calculator Metrics ===
Expressions evaluated: 1234
Expressions succeeded: 1230 (99.68%)
Expressions failed: 4 (0.32%)
Average evaluation time: 1.23 ms
P95 evaluation time: 4.56 ms
Parse time P99: 2.34 ms
```

**Deliverable:** Production observability

## Day 25: Advanced Functions & Polish (4 hours)

**Tasks:**
1. Add trigonometric functions (sin, cos, tan)
2. Add constants (pi, e)
3. Add logarithm functions (log, ln)
4. Improve error messages with suggestions
5. Final polish of all features

**New Operations:**
- `sin(x)` → sine in radians
- `cos(x)` → cosine
- `tan(x)` → tangent
- `log(x)` → base-10 logarithm
- `ln(x)` → natural logarithm
- `pi` → 3.141592653589793
- `e` → 2.718281828459045

**Deliverable:** Week 5 complete, feature-complete calculator

---

# PHASE 6 — PRODUCTION HARDENING (Week 6)

## Day 26: Shadow JAR & Distribution (6 hours)

**Tasks:**
1. Configure shadow JAR plugin
2. Ensure ServiceLoader files are merged
3. Build fat JAR (`calculator-1.0.0-all.jar`)
4. Test JAR on clean machine (no Gradle)
5. Create startup scripts (bin/calculator)

**Shadow Configuration:**
```kotlin
tasks.shadowJar {
    mergeServiceFiles()
    archiveFileName.set("calculator-${project.version}-all.jar")
    manifest {
        attributes["Main-Class"] = "com.calculator.app.CalculatorApplication"
    }
}
```

**Deliverable:** Single-file distribution

## Day 27: GraalVM Native Image (6 hours)

**Tasks:**
1. Install GraalVM JDK 21
2. Add native-image plugin to launcher module
3. Configure reflection configuration (for ServiceLoader)
4. Build native executable
5. Test startup time (<50ms target)

**Native Build Command:**
```bash
./gradlew nativeCompile
# Output: launcher/build/native/nativeCompile/calculator
```

**Reflection Configuration (reflect-config.json):**
```json
[
  {
    "name": "com.calculator.operations.AddOperation",
    "allDeclaredConstructors": true
  }
]
```

**Deliverable:** Native executable (Windows/Linux/macOS)

## Day 28: Comprehensive Testing (6 hours)

**Tasks:**
1. Run all unit tests (JUnit)
2. Run integration tests (end-to-end)
3. Run property-based tests (jqwik, 10K iterations)
4. Run performance benchmarks (JMH)
5. Run platform tests (Windows, macOS, Linux)
6. Generate test coverage report (Jacoco >80%)

**Test Matrix:**
| Platform | JDK | Mode | Result |
|----------|-----|------|--------|
| Ubuntu 22.04 | JDK 21 | JAR | ✅ |
| Ubuntu 22.04 | GraalVM | Native | ✅ |
| macOS 14 | JDK 21 | JAR | ✅ |
| Windows 11 | JDK 21 | JAR | ✅ |

**Deliverable:** All tests passing, coverage >80%

## Day 29: Documentation & Packaging (4 hours)

**Tasks:**
1. Write README.md (installation, usage, examples)
2. Write ARCHITECTURE.md (design decisions)
3. Write API.md (command reference)
4. Write CONTRIBUTING.md (how to add operations)
5. Create GitHub release with assets
6. Build Debian/RPM packages (optional)
7. Push Docker image to Docker Hub

**Release Assets:**
- `calculator-1.0.0-all.jar` (fat JAR)
- `calculator-linux-x64` (native Linux)
- `calculator-macos-arm64` (native macOS ARM)
- `calculator.exe` (Windows native)
- `calculator-1.0.0.deb` (Debian package)

**Deliverable:** Complete documentation and release artifacts

## Day 30: Final Production Readiness (4 hours)

**Tasks:**
1. Run final sanity tests
2. Verify all commands work
3. Verify history persistence
4. Verify configuration loading
5. Run disaster recovery test (delete config, recreate)
6. Create post-launch issue tracker
7. Sign off on release

**Production Readiness Checklist:**
- [ ] All tests pass in CI
- [ ] Test coverage >80%
- [ ] No critical bugs open
- [ ] Documentation complete
- [ ] Native image builds successfully
- [ ] Docker image runs correctly
- [ ] Performance meets targets (<1ms simple ops)
- [ ] Error messages are user-friendly
- [ ] History survives restart
- [ ] Configuration hot-reload works

**Deliverable:** Week 6 complete, PRODUCTION RELEASE

---

# POST-LAUNCH (Week 7)

## Day 31-35: Monitoring & Feedback

**Tasks:**
1. Monitor for crash reports
2. Collect user feedback
3. Fix critical bugs (24h SLA)
4. Performance tuning based on metrics
5. Plan V2 features

**Success Metrics:**
- User satisfaction >90%
- Bug reports <5 in first week
- No data loss incidents
- Startup time <500ms on target platforms

---

# DAILY SCHEDULE

| Time | Activity | Duration |
|------|----------|----------|
| 09:00 - 09:30 | Standup + planning | 30 min |
| 09:30 - 12:30 | Deep work (coding) | 3 hours |
| 12:30 - 13:30 | Lunch | 1 hour |
| 13:30 - 15:30 | Deep work (testing) | 2 hours |
| 15:30 - 15:45 | Break | 15 min |
| 15:45 - 17:45 | Deep work (documentation) | 2 hours |
| 17:45 - 18:00 | Commit + daily log | 15 min |

**Total:** 8 focused hours/day

---

# DELIVERABLES TRACKING

| Week | Deliverable | Artifact |
|------|-------------|----------|
| 1 | Core infrastructure | 9 modules compiled |
| 2 | Parser | Infix→RPN conversion |
| 3 | Evaluator + REPL | Interactive calculator |
| 4 | History + Config | Persistence, customization |
| 5 | Advanced features | JSON, batch, metrics |
| 6 | Production release | JAR, native, Docker |

---

# RISK MANAGEMENT

| Risk | Probability | Impact | Mitigation |
|------|-------------|--------|------------|
| JDK 21 not available | Low | Medium | Use JDK 17 fallback (remove preview features) |
| GraalVM native build fails | Medium | Low | JAR as fallback distribution |
| JLine3 terminal detection fails | Low | Medium | Fallback to simple Scanner |
| SQLite native library missing | Low | Low | Pure Java fallback (H2) |
| Performance targets not met | Low | High | Optimize with caching, reduce BigDecimal use |
| ServiceLoader issues in shadow JAR | Low | Medium | Verify mergeServiceFiles() configured |

---

# SUCCESS CRITERIA

**Technical:**
- All 50+ parser tests pass
- 10K expressions/second on reference hardware
- Memory <100MB RSS
- Test coverage >80%

**User Experience:**
- Startup <500ms
- Error messages understandable
- History works as expected
- Configuration hot-reload works

**Release:**
- GitHub release with all assets
- Docker image on Docker Hub
- Documentation complete
- No critical bugs at launch

---

# PRINCIPAL ENGINEER SIGN-OFF

**Execution Plan Status:** ✅ APPROVED

**Start Date:** [Insert Date]

**End Date:** [Start Date + 6 weeks]

**Weekly Checkpoints:**
- Week 1: Module structure green
- Week 2: Parser passes all tests
- Week 3: REPL interactive
- Week 4: History and config working
- Week 5: All features complete
- Week 6: Production release

**Post-Launch Review:** Week 7, Day 35

**Recommendation:** Proceed. This plan is achievable by a single principal engineer in 6 weeks with focused effort.

---

**Next Action:** Create the root `settings.gradle.kts` file and run `./gradlew tasks` to verify setup.

**Good luck. Build something great.**