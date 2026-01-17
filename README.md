# Countdown Timer – Compose Task

## Author
4wl2d - Tomilov Vladislav Igorevich

---

# My Solution:
---

## Architecture

The project follows **Clean Architecture** principles with layer separation:

### Project Structure

```
app/src/main/java/ind/wldd/sportytimer/
├── domain/                    # Domain layer (business logic)
│   ├── model/
│   │   └── TimerState.kt     # Timer state model (domain layer)
│   └── usecase/
│       └── CountdownUseCase.kt # Countdown use case function
├── presentation/              # Presentation layer
│   ├── model/
│   │   └── TimerUiState.kt   # UI state model
│   ├── viewmodel/
│   │   └── TimerViewModel.kt # ViewModel for state management
│   └── ui/
│       └── TimerScreen.kt    # Compose UI components
└── MainActivity.kt            # Application entry point
```

### Architectural Layers

1. **Domain Layer**
   - Contains application business logic
   - Independent of Android frameworks
   - `countdownSecondsFlow()` — encapsulates countdown logic as a top-level function
   - `TimerState` — domain data model returned by the use case, converted to `TimerUiState` in the presentation layer

2. **Presentation Layer**
   - Manages UI and user interaction
   - `TimerViewModel` — manages UI state through StateFlow, converts `TimerState` to `TimerUiState` via `toUiState()` extension function
   - `TimerScreen` — Compose UI components with custom drawing implementation
   - `TimerUiState` — state model for UI (presentation layer)

3. **MainActivity**
   - Application entry point
   - Sets up Compose and theme
   - Uses Edge-to-Edge for modern design

---

## Application Logic

### Main Workflow

1. **Initialization**
   - When `MainActivity` starts, `TimerViewModel` is created
   - `TimerViewModel` automatically starts the timer in the `init` block by calling `startTimer()`
   - The timer starts counting down from 10 seconds (default parameter)

2. **Countdown**
   - `countdownSecondsFlow(totalSeconds)` creates a Flow that:
     - Uses `SystemClock.elapsedRealtime()` for precise time calculations
     - Calculates remaining time based on actual elapsed time, ensuring accuracy even if the app is paused/resumed
     - Prevents duplicate emissions by tracking `lastEmitted` value
     - Calculates delays to the next second boundary for efficiency (only delays until the next second change)
     - Emits `TimerState` objects with `currentValue`, `isRunning`, and `isFinished` properties
     - Emits the initial value (totalSeconds)
     - Emits updated remaining seconds as time progresses
     - Emits 0 when the countdown completes
     - Runs on `Dispatchers.Default` for non-blocking execution
   - The implementation uses time-based calculations rather than simple delays to maintain accuracy

3. **State Management**
   - `TimerViewModel` subscribes to the Flow through `collect` in `viewModelScope`
   - Converts `TimerState` (domain) to `TimerUiState` (presentation) using the `toUiState()` extension function
   - Updates `TimerUiState` on each new value from the Flow
   - Tracks state: `isRunning` and `isFinished`
   - The `startTimer(seconds: Int = 10)` function can be called with different values and prevents restart if already running
   - Cancels previous countdown job when starting a new timer

4. **Display**
   - `TimerScreen` uses `LaunchedEffect` to collect from `viewModel.uiState` Flow
   - Stores the current timer value in a `remember { mutableIntStateOf(0) }` state
   - UI automatically updates when state changes
   - Uses custom drawing with `drawWithContent` modifier for precise text rendering
   - Renders text using native Canvas (`drawContext.canvas.nativeCanvas.drawText()`) for pixel-perfect centering
   - Text is styled using Material theme typography (`displayLarge`) and colors
   - Text is centered both horizontally and vertically using precise Paint measurements

---

## Technologies and Libraries Used

### Core Technologies

1. **Jetpack Compose** (`compose-bom: 2026.01.00`)

2. **Kotlin Coroutines** (`kotlinx-coroutines-android: 1.10.2`)

3. **ViewModel** (`lifecycle-viewmodel-compose: 2.10.0`)

4. **Lifecycle Runtime Compose** (`lifecycle-runtime-compose: 2.10.0`)

### Versions and Configuration

- **Min SDK**: 26 (Android 8.0)
- **Target SDK**: 36
- **Compile SDK**: 36
- **Kotlin**: 2.3.0
- **AGP**: 9.0.0

---

## What Could Have Been Used But Wasn't

### 1. WorkManager

**Why it wasn't used:**
- Timer only works when the application is active
- No background work requirements
- Using coroutines in ViewModel is sufficient for the task

**When it should be used:**
- If a timer is needed that works in the background
- If a notification is required upon completion
- If the countdown needs to continue when the app is closed

### 2. RxJava / ReactiveX

**Why it wasn't used:**
- Kotlin Coroutines and Flow have native support in Kotlin
- Fewer dependencies: RxJava requires an additional library
- Simpler syntax for this task
- Better integration with Compose through Flow collection in `LaunchedEffect`
- Lower learning curve for Kotlin developers

**When it should be used:**
- In projects where RxJava is already used
- When complex transformation operators are needed (combineLatest, zip, etc.)
- When working with legacy RxJava code
- If the team is already familiar with ReactiveX

### 3. MVI Frameworks (FlowMVI, Orbit MVI, MVIKotlin)

**Why it wasn't used:**
- Task simplicity doesn't require complex state architecture
- ViewModel + StateFlow is sufficient for state management
- MVI adds an additional abstraction layer (Intent/Action → State)
- Increases code amount and complexity for a simple timer

**When it should be used:**
- In complex applications with multiple screens and states
- When predictable state changes are needed (unidirectional data flow)
- When working in a team where MVI is the standard
- If time-travel debugging or logging of all actions is required

**What MVI would provide:**
- Clear separation into Intent (actions) → State (state)
- Predictable data flow: all changes through one channel
- Easier to track the source of state changes
- Example structure: `TimerIntent.Start → TimerState.Running → TimerIntent.Tick → TimerState.Updated`

**Why the current approach is sufficient:**
- StateFlow already provides reactive UI updates
- ViewModel encapsulates state change logic
- Less boilerplate code
- Easier to understand and maintain for simple cases
