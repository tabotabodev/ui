---
name: tabotabo-ui
description: Build Compose Multiplatform or Jetpack Compose UIs with the Tabotabo UI design system (io.github.tabotabodev:tabotabo-ui). Use when adding the dependency, theming an app with TabotaboTheme, or building screens from its emphasis text, outlined buttons, glow cards, pills, ScreenScaffold layouts, motion and settings components, and whenever a project already depends on tabotabo-ui, even if the request doesn't name the library.
---

# Tabotabo UI

Tabotabo UI is a minimal, editorial design system for Compose Multiplatform (Android, iOS, Desktop, JS, Wasm) and plain Jetpack Compose apps. Type, one random hue, and spacing carry the design. **Build screens from the library's components instead of restyling Material ones by hand.** The components already apply the rules below.

- Website: https://tabotabodev.github.io/ui/
- API reference: https://tabotabodev.github.io/ui/api/
- Source: https://github.com/tabotabodev/ui

## Install

```kotlin
// KMP: shared module build.gradle.kts
kotlin { sourceSets { commonMain.dependencies { implementation("io.github.tabotabodev:tabotabo-ui:0.2.1") } } }

// Android only: app/build.gradle.kts
dependencies { implementation("io.github.tabotabodev:tabotabo-ui:0.2.1") }
```

The repository needs `mavenCentral()`. Android `minSdk` is 29. Compose runtime, foundation, ui and material3 come in as `api` dependencies, so don't add them again for the library's sake.

## Set up the root

```kotlin
enum class AppearanceChoice { Light, Dark, System }

@Composable
fun App() {
    var appearance by rememberSaveable { mutableStateOf(AppearanceChoice.System) } // the app persists this
    val hueState = rememberHueState()                                              // new random hue per launch
    TabotaboTheme(
        darkTheme = when (appearance) {
            AppearanceChoice.Light -> false
            AppearanceChoice.Dark -> true
            AppearanceChoice.System -> isSystemInDarkTheme()
        },
        hueState = hueState,
        systemBars = rememberSystemBarsAppearance(), // platform hook, or null
    ) { /* screens */ }
}
```

- **`darkTheme` is required.** The library never reads the OS setting and has no "System" value. The app resolves the user's choice.
- **Persist the appearance choice, not the hue.** Every launch gets a new hue; `hueState.shuffle()` re-rolls it.
- **System bars:** implement `SystemBarsAppearance` per platform. On Android, flip the light/dark status- and navigation-bar icons from `colors.isDark`. Pass `null` where there's nothing to tint.
- **Theme values** come from `TabotaboTheme.colors` / `.typography` / `.dimensions` / `.motion`, like `MaterialTheme`. A restyled `MaterialTheme` is installed underneath, so stray M3 components still follow the scheme.

## Design rules (follow them in custom UI too)

1. **Montserrat only.** Weight and color create hierarchy. Use `TabotaboTheme.typography` roles (`heroTitle`, `headline`, `screenTitle`, `body`, `caption`, `label`, `buttonLabel`, `footnote`, `factTitle`); never set a font per element.
2. **Headlines emphasise a key phrase:** every hero, headline and title is Light + Bold: baseline words in Light and the accent color, the key phrase in Bold and primary. A single-word title is emphasised whole.
3. **One hue.** Use the color roles: `background`, `surface`, `primary`, `accent` (baseline text), `muted` (subtitles), `outline` (soft card / dialog / tag borders), `outlineStrong` (3:1 borders of interactive controls), `disabled`. Error colors are Material's own (`MaterialTheme.colorScheme.error`). Don't hard-code colors. For things that must be told apart (people, chart series), use `colors.distinct(index)`.
4. **Outlined, not elevated.** No resting shadows, gradients, illustrations or decorative icons. Glow is reserved for the one thing that needs attention.
5. **Left-aligned** text, titles, cards and lists. Center only buttons and a screen's single focal object.
6. **No layout shifts.** Reserve space for text that changes length (`minLines`) and for things that come and go.
7. **Motion is expected but purposeful:** typewriter headlines, pop-in values, animated highlights, a pulse while working, and a bounce only for celebrations.

## Which API for which need

| Need | Use |
|---|---|
| Screen skeleton (portrait / landscape / web) | `ScreenScaffold(header, actions, webLayout) { content }` |
| Screen title + subtitle + back arrow | `ScreenHeader(onBack, subtitle) { UnfocusedText("Plan the"); FocusedText("trip") }` |
| Headline that types in once | `TypewriterText { … }`; control it with `rememberTypewriterState(key, startDelay, onFinished)` |
| Static emphasis line | `EmphasisText { … }`, `ScreenTitle { … }`, `HeroTitle { … }` |
| Translatable emphasis | `Formatted(stringResource(Res.string.x), focused = "trip")` with a single `%s` |
| Muted line under a headline | `Subtitle(text, minLines = …)` |
| Title/value fact ("App version" / "1.2.0") | `Fact(title, value)` |
| Main / secondary actions | `ActionStack { PrimaryButton(text, onClick); SecondaryButton(text, onClick) }` |
| Bare icon action (back, info, gear) | `IconAction(onClick, contentDescription) { icon }` |
| Card | `GlowCard(highlighted, onClick) { … }` |
| 2–3 way choice | `SelectablePillRow(options, selected, onSelect, label)` |
| Small status label ("Beta", "New") | `Tag(text, color)` |
| Dialog | `TabotaboDialog(onDismissRequest, confirmText, onConfirm, dismissText) { title DSL } { content }` |
| List of cards | `CardList(items, key) { item -> GlowCard { … } }` inside the scaffold's content |
| Changing value that should pop in | `PopInContent(targetState = value) { Text("$it") }` |
| Working / thinking label | `Modifier.pulse(active)` |
| Celebration (a milestone) | `Modifier.celebrate(trigger)` |
| Settings screen pieces | `AppearanceSetting`, `ShuffleColorsButton(hueState)`, `SettingsSection`, `Fact` |
| Custom outlined / glowing shape | `Modifier.glowOutline(color, width, shape, glowRadius)` |
| Reduced motion | `TabotaboTheme(motion = TabotaboMotion.Reduced)` |
| Tablet spacing | `TabotaboTheme(dimensions = TabotaboDimensions.default(SizeClass.Expanded))` |

## Screen template

```kotlin
@Composable
fun PlanScreen(onBack: () -> Unit, onContinue: () -> Unit, isWeb: Boolean) {
    ScreenScaffold(
        webLayout = isWeb,
        header = {
            ScreenHeader(onBack = onBack, backContentDescription = "Back", subtitle = "Pick a destination and dates.") {
                UnfocusedText("Plan the")
                FocusedText("trip")
            }
        },
        actions = { PrimaryButton("Continue", onClick = onContinue) },
    ) {
        CardList(items = destinations, key = { it.id }) { place ->
            GlowCard(Modifier.fillMaxWidth(), highlighted = place == selected, onClick = { selected = place }) {
                Text(place.name, color = TabotaboTheme.colors.accent, style = TabotaboTheme.typography.labelLarge)
                Subtitle(place.summary)
            }
        }
    }
}
```

Every app ships a **Settings** screen with appearance (Light / Dark / System), **Shuffle colors**, and an **About** section of `Fact`s, each with a plain-language description.

## Gotchas

- Emphasis text is built from segments, not from strings with markup. Spacing between segments is automatic: no space goes before punctuation; use `NoSpace()` or `Space(n)` to override. `Formatted` requires exactly one `%s`.
- `TypewriterText` reveals each distinct text once per state. Recomposition and configuration changes don't replay it, but new text does. Use `state.restart()` to replay.
- To switch light and dark, keep one `HueState`. Two separate `randomDarkColorScheme()` / `randomLightColorScheme()` calls produce unrelated colors.
- Buttons fill the width only inside `ActionStack`; elsewhere, pass a modifier.
- With `webLayout = true` the scaffold already scrolls. Don't add `verticalScroll` to its content, and `CardList` switches to a responsive grid.
- In landscape the content pane and the header/actions pane scroll independently. Keep the content scrollable (e.g. `CardList`) rather than fixed-height.
- On the web, open Settings from a header `IconAction` (gear) instead of a full-width button.
