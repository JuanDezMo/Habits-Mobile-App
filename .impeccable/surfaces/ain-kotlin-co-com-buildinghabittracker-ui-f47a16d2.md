---
version: 1
slug: "ain-kotlin-co-com-buildinghabittracker-ui-f47a16d2"
primary_target: "BuildingHabitTracker/shared/src/commonMain/kotlin/co/com/buildinghabittracker/ui"
related_targets: []
---

# App surface: Habit Tracker (all screens)

Mode: Operate. Audience: one person building daily routines, short check-ins (often at night). Task: mark today's habits in seconds, manage the habit list, read progress. Constraints: Compose Multiplatform, same look on Android and iOS, Material 3 components themed (no Dynamic Color), testTags on every interactive element, AA contrast in light and dark. Avoid: generic health-app look, cold/technical, pressure or guilt (missed days in neutral grey, never red).

## Direction contract

THESIS: A park guide map for your day. One flat-colour lobe dominates each screen and carries that screen's headline (today's "2 de 3", the habit name, the screen title); its coastline does the dividing. Refuses the category default of white cards, green check and flame on a grey ground.

OWN-WORLD: Flat unmodulated colour, no gradients, no shadows. Cream water #F5F1E4 ground, ink #2C2E2A, land green #8ED462. Habit colours are the map's capsule inks: coral #FF705D, blue #2BA0FF, lilac #EBC1FF, yellow #F5E211, green #8ED462, orange #FFA24C, teal #3FD0B6, pink #FF9FC8, always with ink text. One grotesk (Bricolage Grotesque) at one bold weight; display tight (-0.05em, 0.95 leading). Controls are full-radius capsules and circles; the primary action is an ink capsule with a yellow round chip.

STORY: The user sees at once how much of today is done, marks what is left, and trusts that missed days are shown plainly and kindly.

FIRST VIEWPORT (Hoy): lobe in land green across the top ~45%, date and "Hola, Ana" small, then "2 de 3" at ~118sp; a yellow pill "Te falta Meditar"; below, one capsule per habit in its colour; ink capsule bottom navigation.

FORM: candidate "aww2-zoo-gardens-guide-map" (bolder re-roll, round 3, lead was centre-rail; chosen by user). Signature interaction: when a habit is done its round chip crosses from the trailing to the leading edge of its capsule with a short overshoot. seed:c8d86e77819d2322

FINISH: unreviewed and undocumented is unfinished; this build ends with the finish review, the verdict, DESIGN.md, and every shipping raster carrying its provenance
