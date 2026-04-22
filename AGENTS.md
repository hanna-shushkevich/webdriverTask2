# AGENTS.md - WebDriver Automation Project Guide

## Project Overview
A Selenium WebDriver automation framework for testing web interactions across Google Search and YouTube. Uses Page Object Model (POM) pattern with JUnit 5 and remote Selenium Grid. Java 21, Maven build system.

**Stack**: Java 21 • Selenium 4.15.0 • JUnit 5 • Selenium Grid (Remote) • Maven 4.0.0

---

## Architecture & Key Components

### Page Object Model (POM)
Three-tier hierarchy: `BasePage` → specialized pages → test code

- **BasePage.java** (src/main/java/pages/): Abstract base with shared webdriver operations
  - Provides `WebDriverWait` (10s default timeout) via constructor injection
  - Key utilities: `waitForElement()`, `navigateTo()`, `switchToNewWindow()`, `waitForPageTitle()`
  - All page classes extend this; never instantiate directly

- **GoogleSearchPage.java**: Google Search page object
  - Locators use static `By` constants (not inline)
  - Critical method: `searchFor(String)` - clears input, sends keys, submits
  - Handles Google cookies modal via `acceptCookiesIfPresent()` with exception fallback
  - Dynamic XPath: `clickYouTubeLinkWithText(String)` builds XPath with link text + youtube.com href

- **YouTubePage.java**: YouTube operations, most complex page
  - **View count parsing**: `parseViewCount(String)` handles multiple formats: "116M", "116,747,332", "500K" (case-insensitive)
  - Video interactions: `pauseVideo()` (spacebar), `clickAndHoldToFastForward()` (Actions), `scrollPage()` (JS)
  - Multi-fallback pattern for clickable elements: direct click → JS click → focus+Enter (see `clickSearchButton()`)
  - Handles video ads: `skipAdIfPresent()` waits 2s before attempting skip

### Driver Management
**DriverManager.java** (src/main/java/utils/): Singleton webdriver instance

- Uses Selenium Grid (default: `http://localhost:4444/wd/hub`)
- Static `getDriver()`: lazy initialization with browser system property ("chrome" default)
- `createRemoteDriver()`: builds RemoteWebDriver via DesiredCapabilities
  - Platform hardcoded to **MAC** (line 49) - update if testing on Windows/Linux
  - DesiredCapabilities approach (deprecated in Selenium 4, consider Options migration)
- `quitDriver()`: nullifies driver, cleanup in `@AfterEach`

### Test Execution
**BananaSearchTest.java** (src/test/java/): JUnit 5 test class

- Two test methods: one commented (Google search flow), one active (Selenium Grid demo)
- Active test `seleniumGridTest()` directly opens YouTube video (avoids Google captcha)
- Assertions verify page state after each interaction

---

## Critical Developer Workflows

### Build & Run Tests
```bash
# Build project
mvn clean compile

# Run all tests (parallel: 2 threads via surefire)
mvn test

# Run specific test
mvn test -Dtest=BananaSearchTest#seleniumGridTest

# Specify browser (chrome/firefox)
mvn test -Dbrowser=firefox
```

### Selenium Grid Setup (Required)
Tests require Selenium Grid running locally:
```bash
# Selenium Standalone Grid via Docker (recommended)
docker run -d -p 4444:4444 selenium/standalone-chrome

# Or locally (requires Grid download)
java -jar selenium-server.jar standalone
```

### Debugging
- Waits default 10s (`DEFAULT_TIMEOUT_SECONDS` in BasePage)
- Test timeouts: 600s per method (surefire config)
- Exception patterns: `try/catch` with silent fallback is intentional (handles missing cookies modals, ads)
- Use `Thread.sleep()` for deliberate pauses (not best practice but present)

---

## Project-Specific Patterns & Conventions

### Locator Strategy
- All UI elements as static `By` fields in page classes (e.g., `SEARCH_INPUT = By.cssSelector(...)`)
- Prefer `By.id` > `By.cssSelector` > `By.xpath` (XPath last resort)
- Dynamic XPath: built inline when parameterized (e.g., link text matching in `clickYouTubeLinkWithText()`)

### Wait & Synchronization
- `wait.until(ExpectedConditions.*)` for visibility/clickability (always precedes interaction)
- `Thread.sleep()` for deterministic pauses (ads, animations)
- No explicit waits for page navigation (use title checks via `waitForPageTitle()`)

### Error Handling
- Graceful degradation: try element interaction, catch Exception, return boolean/null
- Cookies & ads: expected to sometimes not appear (handled silently)
- **No error logging** - tests fail silently if assertions fail

### Multi-Window Handling
- `switchToNewWindow()`: finds first non-original window handle
- Used when search link opens new tab (YouTube scenario)

---

## Integration & Dependency Notes

### External Dependencies
- **Selenium WebDriver 4.15.0**: core browser automation
- **JUnit 5.10.1**: test runner with parameterized test support (unused here)
- **Selenium Grid**: remote browser execution (hardcoded localhost:4444)

### Platform Specifics
- **Java 21**: modern language features supported
- **macOS focus**: Platform.MAC hardcoded in DriverManager (update for CI/Windows)
- **Parallel execution**: Maven Surefire runs 2 test threads (tunable)

### Known Limitations
- Single test actually runs (other commented out)
- DesiredCapabilities deprecated in Selenium 4 (use ChromeOptions/FirefoxOptions for future)
- No configuration externalization (hardcoded GRID_URL, YouTube video URL)

---

## When Adding Tests or Pages

1. **New page class**: extend `BasePage`, inject `WebDriver` in constructor
2. **New locators**: add static `By` fields at class top, use descriptive names
3. **New assertions**: leverage page object methods returning boolean/String, assert in test
4. **Multi-step flows**: compose methods (e.g., `openGoogle()` + `searchFor()` + navigation)
5. **Flaky elements**: add try/catch with fallback method or increase wait timeout in method

---

## Quick Reference: Key Files
- **pom.xml**: Maven config, dependency versions, Surefire parallel settings
- **DriverManager.java**: Grid URL, driver lifecycle
- **BasePage.java**: shared wait/navigation logic (10s timeout)
- **BananaSearchTest.java**: test entry point, setup/teardown
- **YouTubePage.java**: complex parsing logic for view counts + multi-fallback clicks

