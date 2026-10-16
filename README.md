<img src="branding/icon.png" alt="Tabotabo UI mascot" width="96" align="right">

# Tabotabo UI

A Compose Multiplatform design system: one random hue per session, Montserrat type with bold-in-primary key phrases, outlined cards that glow when highlighted, and typewriter headlines.

**[Website](https://tabotabodev.github.io/ui/)** · **[Live showcase](https://tabotabodev.github.io/ui/showcase/)** · **[API reference](https://tabotabodev.github.io/ui/api/)** (always the latest `main`)

Targets **Android, iOS, Desktop (JVM), JS and Wasm**. It works in plain Android (Jetpack Compose) apps too.

The library is platform-free. It never reads OS settings or touches platform classes like `Context`. Your app owns the appearance choice (Light / Dark / System), persists it, and tints system bars through a small hook.

---

## Install

**Coordinates:** `io.github.tabotabodev:tabotabo-ui`. Releases are listed on [Maven Central](https://central.sonatype.com/artifact/io.github.tabotabodev/tabotabo-ui).

Make sure `mavenCentral()` is in your repositories (`settings.gradle.kts`):

```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}
```

### Kotlin Multiplatform

```kotlin
// build.gradle.kts of your shared module
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("io.github.tabotabodev:tabotabo-ui:0.1.0")
        }
    }
}
```

### Android only

```kotlin
// app/build.gradle.kts
dependencies {
    implementation("io.github.tabotabodev:tabotabo-ui:0.1.0")
}
```

Gradle picks the Android variant automatically.

### Version catalog

```toml
[libraries]
tabotabo-ui = { module = "io.github.tabotabodev:tabotabo-ui", version = "0.1.0" }
```

### Requirements

| | Minimum |
|---|---|
| Android `minSdk` | 29 |
| Kotlin | 2.4 |
| Compose Multiplatform | 1.12 |

The library exposes Compose `runtime`, `foundation`, `ui` and `material3` as `api` dependencies, so you don't need to add them yourself.

---

## Quick start

Wrap your UI in `TabotaboTheme`. Resolve the user's appearance choice into `darkTheme` yourself:

```kotlin
enum class AppearanceChoice { Light, Dark, System }

@Composable
fun App() {
    var appearance by rememberSaveable { mutableStateOf(AppearanceChoice.System) } // persist it your way
    val hueState = rememberHueState() // a fresh random hue on every launch / page load

    TabotaboTheme(
        darkTheme = when (appearance) {
            AppearanceChoice.Light -> false
            AppearanceChoice.Dark -> true
            AppearanceChoice.System -> isSystemInDarkTheme()
        },
        hueState = hueState,
        systemBars = null, // see "Platform hooks"
    ) {
        HomeScreen()
    }
}
```

`TabotaboTheme` also installs a restyled `MaterialTheme`, so stock Material3 components follow the scheme.

```kotlin
TabotaboTheme.colors      // TabotaboColors
TabotaboTheme.typography  // Material Typography
TabotaboTheme.dimensions  // spacing, corners, strokes, margins
TabotaboTheme.motion      // animation tokens
```

---

## API usage

Packages live under `com.tabotabo.ui`.

### Color: `color`

All roles come from one hue, and every text role is nudged until it meets WCAG 4.5:1 contrast against both the background and the surface.

```kotlin
val colors = tabotaboColors(Hue(210f), isDark = true)
colors.background; colors.surface; colors.primary; colors.accent; colors.muted; colors.outline; colors.disabled

colors.distinct(index = 2)          // a distinct, readable color for people / series / tags (golden-angle steps)

hueState.shuffle()                  // "Shuffle colors"
Hue.random()                        // a random hue
Hue(-30f).degrees                   // 330.0: any angle is normalised

// Material3 ColorSchemes, if you only want the colors
tabotaboDarkColorScheme(hue); tabotaboLightColorScheme(hue)
randomDarkColorScheme(); randomLightColorScheme()  // each picks its own hue

// Contrast helpers
contrastRatio(foreground, background)
color.ensureContrast(ContrastTargets.Text, listOf(background, surface))
```

> To switch between light and dark, build both from the **same** `Hue` (`TabotaboTheme` does this). Two separate `random*ColorScheme()` calls give unrelated colors.

### Typography: `typography`

Montserrat is bundled and is the default. To use your own fonts:

```kotlin
TabotaboTheme(
    darkTheme = dark,
    typography = tabotaboTypography(
        displayFamily = myDisplayFont, // hero, headline, screen title
        bodyFamily = myBodyFont,       // body, caption, footnote
        labelFamily = myLabelFont,     // buttons, labels, tags
    ),
) { … }
```

Role accessors on `Typography`: `heroTitle` (42sp), `headline` (34sp), `screenTitle` (32sp), `buttonLabel` (16sp Bold), `body` (14sp), `factTitle` (14sp Medium), `caption` (13sp), `label` (13sp Bold), `footnote` (11sp).

### Emphasis and typewriter text: `text`

A headline is built from segments and renders as a **single text**, so it wraps naturally. Focused phrases are Bold and drawn in the primary color.

```kotlin
TypewriterText {              // types in once, letter by letter
    UnfocusedText("Hello")
    FocusedText("User")       // starts after "Hello" finishes
}

EmphasisText {                // the same, without animation
    UnfocusedText("Where to")
    FocusedText("next")
    UnfocusedText("?")        // no space is added before punctuation
}

// Translations: keep the focused phrase separate
TypewriterText { Formatted(stringResource(Res.string.plan_title), "trip") } // "Plan the %s"

// Spacing control
EmphasisText { UnfocusedText("Note"); NoSpace(); FocusedText("book") }
```

Controlling the reveal:

```kotlin
val state = rememberTypewriterState(
    key = statusId,                     // a new key restarts the reveal
    startDelay = 400.milliseconds,      // e.g. chain after a title
    onFinished = { /* … */ },
)
TypewriterText(state = state, minLines = 2) { … } // reserve lines so the layout never shifts
state.skip(); state.restart(); state.isFinished
```

A typed-in text doesn't replay on recomposition or configuration changes. Changing the text types it in again. Typing only redraws the text; it never recomposes or re-measures it.

Ready-made roles: `ScreenTitle { … }` (Light + Bold), `HeroTitle { … }`, `Subtitle("…")`, `Fact(title = "App version", value = "1.2.0")`.

### Buttons: `button`

Buttons are outlined, with a surface fill. **Primary** uses the primary color for its border and label. **Secondary** has an outline border and an accent label. Disabled buttons use the `disabled` color.

```kotlin
ActionStack {                                   // full width, stacked, centred
    PrimaryButton("Get started", onClick = ::start)
    SecondaryButton("Settings", onClick = ::openSettings, enabled = canOpen)
}

PrimaryButton(onClick = ::share, leadingIcon = { MyShareIcon() }, text = "Share") // icons are tinted automatically

PrimaryButton(onClick = ::start) {              // slot API
    Text("Custom content")
}

IconAction(onClick = ::back, contentDescription = "Back") { MyArrowIcon() }       // bare icon, 48dp target
```

Long labels scroll as a marquee instead of wrapping. Override colors with `TabotaboButtonDefaults.primaryColors().copy(…)`.

### Surfaces: `surface`

```kotlin
GlowCard(highlighted = isSelected, onClick = { select() }) {   // outline turns primary and glows when highlighted
    Text("Card content")
}

SelectablePillRow(
    options = listOf("Daily", "Weekly", "Monthly"),
    selected = period,
    onSelect = { period = it },
    label = { it },
)

Tag("Beta")                                        // tiny display-only pill
Modifier.glowOutline(color, 2.dp, shape, glowRadius = 16.dp)

TabotaboDialog(
    onDismissRequest = { show = false },
    confirmText = "Got it",
    onConfirm = { show = false },
    title = { UnfocusedText("Changes"); FocusedText("saved") },
) { Subtitle("Your list is up to date.") }
```

### Layout: `layout`

`ScreenScaffold` handles both orientations from its own constraints:

- **Portrait:** header on top, content centred, actions pinned to the bottom.
- **Landscape:** content scrolls on the left; header and actions sit on the right, vertically centred and scrollable.
- **Web** (`webLayout = true`): one centred column (max 1080dp), with actions capped at 480dp.

```kotlin
ScreenScaffold(
    webLayout = isBrowser,
    header = {
        ScreenHeader(
            onBack = ::back, backContentDescription = "Back",
            subtitle = "Pick a destination and dates.",
        ) { UnfocusedText("Plan the"); FocusedText("trip") }
    },
    actions = { PrimaryButton("Continue", onClick = ::next) },
) {
    CardList(items = destinations, key = { it.id }) { place -> DestinationCard(place) } // scrolling column; a grid on web
}
```

`LocalScreenLayoutInfo.current` exposes `isLandscape`, `sizeClass`, `horizontalMargin` and `isWebLayout` inside the scaffold. For tablet spacing, pass `TabotaboDimensions.default(SizeClass.Expanded)`.

### Motion: `motion`

```kotlin
PopInContent(targetState = count) { Text("$it") }   // fade + scale-in when the value changes
Text("Thinking…", Modifier.pulse(active = isThinking))
Box(Modifier.celebrate(trigger = milestone))          // bouncy scale-up when the trigger changes

TabotaboTheme(darkTheme = dark, motion = TabotaboMotion.Reduced) { … } // honour reduce-motion
```

### Settings: `settings`

```kotlin
AppearanceSetting(
    options = AppearanceChoice.entries,
    selected = appearance,
    onSelect = { appearance = it /* and persist */ },
    label = { it.name },
    title = "Appearance",
    description = "Pick light or dark, or follow your device.",
)
SettingsSection(title = "Colors", description = "Every launch picks a new color.") {
    ShuffleColorsButton(hueState)
}
SettingsSection(title = "About") { Fact("App version", "1.0.0") }
```

---

## Platform hooks

The library leaves platform work to you through `SystemBarsAppearance`, which is called whenever the resolved colors change:

```kotlin
// androidMain: light/dark system-bar icons for an edge-to-edge activity
@Composable
fun rememberSystemBarsAppearance(): SystemBarsAppearance {
    val view = LocalView.current
    return remember(view) {
        SystemBarsAppearance { colors ->
            val window = (view.context as Activity).window
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !colors.isDark
                isAppearanceLightNavigationBars = !colors.isDark
            }
        }
    }
}
```

On other platforms, pass `systemBars = null` or set a web `<meta name="theme-color">` from `colors.background`.

---

## Repository layout

| Module | Purpose |
|---|---|
| `ui-lib` (Gradle project `:tabotabo-ui`) | The published library |
| `shared` | Showcase app UI consuming the library |
| `website` | The project website, a Kotlin/Wasm app built with the library |
| `skill/tabotabo-ui` | The Claude skill, published at `/ui/skill/` |
| `androidApp`, `desktopApp`, `webApp`, `iosApp` | Platform entry points for the showcase |

```shell
./gradlew :desktopApp:run                       # showcase on desktop
./gradlew :webApp:wasmJsBrowserDevelopmentRun   # showcase in the browser
./gradlew :website:wasmJsBrowserDevelopmentRun  # website in the browser
./gradlew :androidApp:assembleDebug             # showcase on Android
./gradlew :tabotabo-ui:jvmTest -q               # library tests (common + Compose UI tests)
```

---

## License

The library is licensed under the [Apache License 2.0](LICENSE). Montserrat is bundled under the [SIL Open Font License 1.1](https://openfontlicense.org); its license ships with the fonts at `ui-lib/src/commonMain/composeResources/files/fonts/OFL.txt`.
