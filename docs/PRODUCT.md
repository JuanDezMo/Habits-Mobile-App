# Product

<!-- impeccable:product-schema 1 -->

## Platform

android

Compose Multiplatform with one shared UI for Android and iOS. The same visual language (Material 3 based) ships on both systems; it does not switch to iOS-native components on iPhone.

## Users

A person building daily routines for personal use. They open the app once or twice a day in short moments (often at night) to mark what they did, for example "Leer" and "Gym", in a few seconds, and to check that their streak continues.

## Product Purpose

Track simple daily habits (done / not done) and show progress through streaks, completion rate and a monthly calendar. Success: marking the day takes seconds and the user keeps coming back.

## Positioning

Private and simple. Everything stays on the phone behind a 4-digit PIN: no server, no cloud account, no ads. One check per habit per day; no counters, quantities or complex goals.

## Operating Context

- Quick daily check-in from the Today screen; occasional habit management (create, edit, archive).
- Optional local reminder per habit (iteration 6).
- App locks with the PIN after 5 minutes in background.

## Capabilities and Constraints

- Modules: registration (name, email, PIN, free security question), PIN login and recovery, Today, Habits, Statistics, Reminders, Settings (language, theme, change PIN).
- Business rules A–R in `reglas-negocio-mvp.md`; architecture in `habit-tracker-arquitectura.html`; iteration plan in `habit-tracker-plan.html`.
- Spanish and English (in-app selector); light and dark mode (System / Light / Dark).
- Habits are never deleted, only archived (C5). Habit options: fixed list of 12 emojis and 8 colors.
- Every interactive element and key state carries a `testTag` (Maestro / Appium); identifiers never contain user data.
- The project is also a learning vehicle for a junior developer new to Kotlin: code stays simple and explained.

## Evidence on Hand

- Navigable HTML prototype: `habit-tracker-prototipo.html` (flows, copy and screen structure; its look is a reference, not a commitment).
- No real users, testimonials or metrics exist; none may be invented.

## Product Principles

1. Seconds, not minutes: the daily check-in is the core path and must stay effortless.
2. Private by default: nothing leaves the phone.
3. Honest progress: streaks and completion are calculated, never inflated.
4. Simple over configurable: one habit type, few options.

## Accessibility & Inclusion

Light and dark mode with AA contrast in both (iteration 3 "done when"). Screen-reader labels on icon-only controls (days, colors, add button).
