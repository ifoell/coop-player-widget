# CO-OP Family Status Widget — Android

## 1. Project Overview

Build a native Android application that turns real-life family members into a dynamic video-game-style **CO-OP status widget**.

The visual direction is inspired by modern 3D fighting/RPG game HUDs:
- Dark cinematic/translucent panels
- Blue / red / green player accents
- Player labels such as `P1`, `P2`, `P3`
- `LV.` is calculated automatically from the person's current age
- Stats are configurable per person
- Number of players is fully dynamic
- The Android home-screen widget updates automatically as data changes

The goal is to make the family feel like a cooperative RPG party:

> SAME TEAM • SAME LIFE • HIGHER LEVELS

Reference image:
`Co-Op Family: Same Team, Bigger Adventures.png`

---

# 2. Core Requirements

## 2.1 Dynamic Players

The app MUST NOT hardcode Dad, Mom, Baby, or a fixed number of players.

Users can create:

```text
P1
P2
P3
...
Pn
```

Example:

```text
P1 — DAD
P2 — MOM
P3 — BABY
```

If only one player exists, the widget displays only P1.

If two players exist:

```text
P1 — DAD
P2 — MOM
```

If three:

```text
P1 — DAD
P2 — MOM
P3 — BABY
```

The widget layout must adapt to the number of players and available widget size.

---

# 3. Player Data Model

Each player should contain at minimum:

```kotlin
Player(
    id,
    name,
    role,
    birthDate,
    avatar,
    stats,
    accentColor,
    createdAt,
    updatedAt
)
```

Suggested fields:

| Field | Description |
|---|---|
| `id` | Unique player ID |
| `name` | Display name |
| `role` | DAD, MOM, BABY, etc. |
| `birthDate` | Used to calculate age/level |
| `avatar` | Optional local image/avatar |
| `stats` | Dynamic list of stats |
| `accentColor` | Player-specific UI accent |
| `createdAt` | Creation timestamp |
| `updatedAt` | Last modification timestamp |

Do not store `level` as the source of truth.

Level must be calculated from `birthDate`.

---

# 4. Level / Age System

## Rule

```text
LV = current age
```

Age must be calculated correctly based on the current date.

Example:

```text
Birth date: 1991-05-10
Current date: 2026-09-23

LV. 35
```

After the birthday:

```text
LV. 36
```

The user should never need to manually update the level.

For babies/children, the UI may display:

```text
LV. 0
```

or, if product design chooses a more granular system later:

```text
LV. 0 • 8M
```

The first implementation should keep the primary `LV` integer based on age.

---

# 5. Dynamic Stats

Stats must be configurable per player.

Do NOT assume every player has:

```text
STR
DEF
INT
SPD
LUK
```

Those are only examples.

Example Dad:

```text
STR 85
DEF 75
INT 90
SPD 70
LUK 80
```

Example Mom:

```text
CARE 95
SUPPORT 90
DEF 75
INT 85
LUCK 80
```

Example Baby:

```text
CUTE 100
JOY 100
HP 90
SPD 30
LUCK 99
```

## Stat model

```kotlin
Stat(
    id,
    name,
    value
)
```

Constraints:

- `name`: 2–12 characters
- `value`: 0–100
- Recommended maximum: 5 stats per player
- Stats can be added, edited, reordered, or removed

The UI should render a horizontal progress bar:

```text
STR  ████████░░ 80
```

---

# 6. Player Roles

Role is primarily cosmetic.

Suggested default roles:

```text
DAD
MOM
SON
DAUGHTER
BABY
GRANDPA
GRANDMA
BROTHER
SISTER
OTHER
```

Users may also enter a custom role.

The role should be displayed prominently below the player number.

Example:

```text
P1
DAD
LV. 35
```

---

# 7. Main App Screens

## 7.1 Home / Party Screen

Display all players as cards.

Example:

```text
CO-OP MODE

┌─────────────────────────────┐
│ P1  DAD                 LV35│
│ STR ████████░░              │
│ INT █████████░              │
│ DEF ███████░░░              │
└─────────────────────────────┘

┌─────────────────────────────┐
│ P2  MOM                 LV32│
│ CARE ██████████             │
│ SUPPORT █████████░          │
└─────────────────────────────┘

[ + ADD PLAYER ]
```

Actions:

- Add player
- Edit player
- Delete player
- Reorder players
- Preview widget
- Widget settings

---

# 8. Add / Edit Player Screen

Fields:

```text
Name
Role
Birthday
Avatar
Accent Color
Stats
```

Example:

```text
ADD PLAYER

Name
[ Dad                    ]

Role
[ DAD ▼                  ]

Birthday
[ 10 May 1991            ]

Avatar
[ Choose Photo ]

Stats

STR       85
████████▌░

DEF       75
███████▌░░

INT       90
█████████░

SPD       70
███████░░░

LUK       80
████████░░

[ + ADD STAT ]

[ SAVE PLAYER ]
```

---

# 9. Widget Requirements

The widget is the main feature.

Use:

**Jetpack Glance**

instead of legacy `RemoteViews` where practical.

The widget must support Android home-screen placement and resizing.

## Supported size classes

### Small

Designed for approximately 2x2.

Show:

```text
CO-OP MODE

P1 DAD
LV. 35

STR ████████
INT █████████
```

### Medium

Approximately 4x2.

Show 1–2 players depending on layout.

### Large

Approximately 4x4 or larger.

Show multiple players.

### Dynamic overflow

If there are more players than fit:

```text
P1 DAD
P2 MOM
P3 BABY

+2 MORE
```

Never allow the widget to become unreadable.

---

# 10. Widget Layout Strategy

The widget must dynamically determine the layout based on:

```text
number of players
widget width
widget height
number of stats
```

Suggested rules:

### 1 player

Large player card.

### 2 players

Two-column layout when width permits.

Otherwise vertical layout.

### 3 players

Three-column layout for wide widgets.

Otherwise compact vertical cards.

### 4+ players

Compact grid.

Example:

```text
┌─────────────────────────────┐
│ CO-OP MODE                  │
│                             │
│ P1 DAD       P2 MOM         │
│ LV35         LV32           │
│ STR ████     CARE █████     │
│                             │
│ P3 BABY      P4 SIS         │
│ LV0          LV12           │
│ CUTE █████   INT ████       │
└─────────────────────────────┘
```

---

# 11. Widget Visual Design

The widget should visually match the provided reference image.

## Style

- Cinematic game HUD
- Dark navy / black translucent background
- Subtle gradient
- Thin borders
- Slight glow
- Sharp game UI typography
- Minimal decoration
- High readability on Android home screen

## Player colors

Default:

```text
P1 → Blue
P2 → Red/Pink
P3 → Green
P4 → Purple
P5+ → rotating accent colors
```

Users may customize accent colors.

---

# 12. Widget Header

Default:

```text
CO-OP MODE
SAME TEAM • SAME LIFE
```

Optionally allow a custom family/team name.

Example:

```text
THE FURUQI FAMILY
CO-OP MODE
```

This should be configurable.

---

# 13. Widget Footer

Optional small footer:

```text
NEXT STAGE
A BRIGHTER TOMORROW
```

or:

```text
SAME TEAM
BIGGER ADVENTURES
```

Keep it subtle and hide it automatically on small widgets.

---

# 14. Add Player From Widget

If technically feasible with Glance/app widget limitations, clicking:

```text
+ ADD PLAYER
```

should open the main app directly to the Add Player screen.

The widget itself does not need to provide a full player-editing UI.

---

# 15. Data Storage

Use:

**Jetpack DataStore**

for the first implementation.

Suggested structure:

```text
DataStore
   |
   └── PartySettings
       |
       ├── familyName
       ├── players[]
       └── widgetSettings
```

JSON serialization may be used for the player collection.

No backend is required.

No account/login is required.

No internet connection is required.

The application should work completely offline.

---

# 16. Widget Update Strategy

Widget must refresh when:

1. A player is added
2. A player is edited
3. A player is deleted
4. A stat changes
5. Player ordering changes
6. Family/team name changes
7. Widget settings change
8. The date changes enough to affect age/level

After app data changes:

```text
DataStore update
      ↓
Widget refresh
      ↓
Glance recomposes
```

For age changes, use an appropriate Android scheduling mechanism so the widget can update around birthdays without requiring the user to open the app.

Do not use an unnecessarily frequent background job.

---

# 17. Architecture

Use clean, maintainable Android architecture.

Suggested:

```text
UI
 └── Jetpack Compose

Widget
 └── Jetpack Glance

Presentation
 └── ViewModel

Domain
 ├── Player
 ├── Stat
 ├── AgeCalculator
 └── PartyRepository

Data
 ├── DataStore
 └── PlayerSerializer
```

Suggested package structure:

```text
com.example.coopwidget/

├── data/
│   ├── datastore/
│   ├── model/
│   └── repository/
│
├── domain/
│   ├── model/
│   ├── repository/
│   └── usecase/
│
├── ui/
│   ├── home/
│   ├── player/
│   ├── settings/
│   └── theme/
│
├── widget/
│   ├── CoOpWidget.kt
│   ├── CoOpWidgetReceiver.kt
│   ├── WidgetState.kt
│   └── components/
│
└── MainActivity.kt
```

---

# 18. Recommended Technology Stack

Use current stable Android tooling available in the development environment.

Preferred:

```text
Kotlin
Jetpack Compose
Jetpack Glance
AndroidX
DataStore Preferences or Proto DataStore
Kotlin Serialization
Material 3
```

Avoid adding third-party libraries unless they provide a clear benefit.

---

# 19. Theme

App theme:

```text
Dark cinematic
```

Suggested palette:

```text
Background:
#090D16

Panel:
#111827

P1:
#2196F3

P2:
#FF4F7B

P3:
#31D17C

Text:
#F5F7FA

Secondary:
#9CA3AF

Border:
#334155
```

These are starting values only. The implementation can tune them for readability.

---

# 20. Avatar Support

Version 1:

- Optional profile image
- Pick image from device
- Store a local URI/reference
- Widget uses a cropped circular/square avatar where space permits

If avatar rendering causes complexity in the first implementation, the widget may initially use initials/player icons and avatar support can be implemented immediately afterward.

---

# 21. Player Ordering

P numbers are generated from ordering.

Example:

```text
players[0] → P1
players[1] → P2
players[2] → P3
```

If P2 is deleted:

Before:

```text
P1 Dad
P2 Mom
P3 Baby
```

After:

```text
P1 Dad
P2 Baby
```

Do not preserve gaps such as P1/P3.

The player ID remains stable internally; P number is a display index.

---

# 22. Validation

Player:

- Name required
- Birthday required
- Birthday cannot be in the future
- Role required
- Maximum reasonable number of players should be enforced to prevent unusable widgets

Stats:

- Name required
- Value 0–100
- Duplicate stat names should be prevented within the same player
- Maximum 5 stats initially

---

# 23. Settings

Provide:

```text
Family / Team Name
Widget Theme
Show Footer
Show Stats
Maximum Stats Per Player
Compact Mode
```

Potential future settings:

```text
Show Age Instead of Level
Show Birthday Countdown
Show XP
Show Relationship
Show Custom Tagline
```

Do not overbuild these in V1.

---

# 24. Widget Configuration

Prefer one global configuration initially.

Potential future support:

- Multiple widgets
- Different party selection per widget
- Different widget sizes/themes
- Individual player-only widget

V1 should focus on one party widget.

---

# 25. UX Goal

The app should feel like:

> "My family is a playable party."

Opening the app should be quick and fun.

The widget should provide useful information at a glance while still looking like a game HUD.

The user should be able to:

```text
Install
 ↓
Add Dad
 ↓
Add Mom
 ↓
Add Baby
 ↓
Add widget
 ↓
Done
```

No account creation.

No server.

No complicated setup.

---

# 26. Example Final Widget

Large widget:

```text
╭────────────────────────────────────╮
│ CO-OP MODE                         │
│ SAME TEAM • SAME LIFE              │
│                                    │
│ P1                    P2           │
│ DAD                   MOM          │
│ LV. 35                LV. 32       │
│                                    │
│ STR ████████░         CARE ██████  │
│ DEF ███████░░         SUPPORT ████ │
│ INT █████████         INT ███████  │
│ SPD ███████░░         LUK ███████  │
│                                    │
│ P3                                 │
│ BABY                               │
│ LV. 0                              │
│ CUTE ██████████                    │
│ JOY  ██████████                    │
│ HP   █████████                     │
│                                    │
│ SAME TEAM • BIGGER ADVENTURES      │
╰────────────────────────────────────╯
```

---

# 27. Acceptance Criteria

The implementation is considered complete when:

- [ ] Android project builds successfully
- [ ] App launches without crash
- [ ] User can add a player
- [ ] User can edit a player
- [ ] User can delete a player
- [ ] User can reorder players
- [ ] Player number automatically becomes P1, P2, P3, etc.
- [ ] Level is automatically calculated from birthday
- [ ] Level updates correctly as age changes
- [ ] User can create custom stats
- [ ] Stats support 0–100 values
- [ ] User can configure up to 5 stats per player
- [ ] Data survives app restart
- [ ] Data survives device reboot
- [ ] Widget can be added from Android launcher
- [ ] Widget displays the current players
- [ ] Widget dynamically adapts to 1 player
- [ ] Widget dynamically adapts to 2 players
- [ ] Widget dynamically adapts to 3 players
- [ ] Widget handles 4+ players gracefully
- [ ] Widget refreshes after player changes
- [ ] Widget refreshes when relevant age/level changes
- [ ] Widget supports resizing
- [ ] Widget remains readable at supported sizes
- [ ] App works offline
- [ ] No login/backend is required
- [ ] UI follows the cinematic fighting-game HUD direction
- [ ] Reference image is used as visual inspiration, not as a static widget background

---

# 28. Development Order

Agent should implement in this order:

## Phase 1 — Project Setup

- Create Android project
- Kotlin
- Compose
- Material 3
- Glance
- DataStore
- Serialization
- Establish package structure

## Phase 2 — Data Layer

- Player model
- Stat model
- DataStore repository
- Serialization
- CRUD operations

## Phase 3 — Age/Level

- Implement reliable age calculator
- Unit tests around birthdays
- Unit tests around leap years/date boundaries

## Phase 4 — App UI

- Party screen
- Add player
- Edit player
- Delete player
- Reorder players
- Dynamic stat editor

## Phase 5 — Widget

- Basic Glance widget
- Dynamic player count
- Player cards
- Dynamic stats
- Responsive layouts
- Small/medium/large sizing

## Phase 6 — Widget Refresh

- Refresh after CRUD
- Refresh after settings changes
- Date-based age refresh

## Phase 7 — Visual Polish

- Cinematic dark theme
- Player accent colors
- HUD borders
- Progress bars
- Typography
- Animations where Android widget limitations allow

## Phase 8 — Testing

- Unit tests
- Widget tests
- Different screen sizes
- Different player counts
- Rotation/configuration changes
- Reboot persistence
- Birthday/age transitions

---

# 29. Important Implementation Constraints

1. Do not hardcode exactly three players.
2. Do not hardcode Dad/Mom/Baby.
3. Do not store level as permanent data.
4. Do not require internet.
5. Do not introduce authentication.
6. Do not introduce a backend.
7. Do not make the widget a static image.
8. Do not make the widget dependent on the reference image.
9. Keep the widget readable at small sizes.
10. Prioritize a working MVP before visual polish.

---

# 30. Future Ideas — NOT V1

Potential future features:

- XP system
- Level-up animation
- Birthday notification
- Birthday countdown
- Daily family quest
- Family achievements
- Mood/status
- HP/energy
- Relationship bonuses
- Family party buffs
- Multiple widget themes
- Individual player widgets
- Cloud backup
- Import/export JSON
- Share party card as an image
- Lock-screen widget where supported
- Wear OS companion
- Dynamic "Daily Quest"

Example:

```text
DAILY QUEST

☐ Help Mom
☐ Play with Baby
☐ Finish Work
☐ Family Dinner

+500 FAMILY XP
```

These should remain outside the initial MVP.

---

# 31. Definition of Done

The final product should feel like a small real Android game companion rather than a generic utility widget.

The core experience is:

```text
Real Family
     ↓
Create Players
     ↓
Age → Automatic Level
     ↓
Custom Stats
     ↓
CO-OP Party
     ↓
Android Home Screen Widget
     ↓
Dynamic P1 / P2 / P3 / ... / Pn
```

The most important principle:

> **The widget represents the current family party dynamically.**
> Adding or removing a family member must automatically change the widget layout.
