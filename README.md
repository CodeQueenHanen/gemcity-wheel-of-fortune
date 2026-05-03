# GemCity TECH — Wheel of Fortune: Coder Edition

A developer-themed Wheel of Fortune built with Angular 17 (frontend) and Java Spring Boot (backend, coming soon).

## Frontend Quick Start

```bash
cd frontend
npm install
npm start
# → http://localhost:4200
```

> The frontend ships with a **mock mode** enabled by default — no backend required to run it.  
> When the Java backend is ready, open `src/app/services/game.service.ts` and set `useMock = false`.

## Project Structure

```
frontend/
├── src/app/
│   ├── models/
│   │   └── game.models.ts          # Shared interfaces (Puzzle, WheelSegment, etc.)
│   ├── services/
│   │   └── game.service.ts         # All HTTP calls + mock fallback
│   ├── components/
│   │   ├── wheel/                  # SVG spinning wheel
│   │   ├── board/                  # Letter tile display
│   │   ├── keyboard/               # A–Z letter picker
│   │   ├── score-panel/            # Score + current segment display
│   │   ├── toast/                  # Feedback messages
│   │   └── solve-modal/            # Full-puzzle guess dialog
│   └── game/
│       └── game.component.ts       # Main game orchestrator
├── proxy.conf.json                 # Dev proxy → Java on :8080
└── angular.json
```

## Java Backend API Contract

The frontend expects these endpoints on `http://localhost:8080`:

| Method | Path | Description |
|--------|------|-------------|
| `GET`  | `/api/puzzle/random` | Returns a `Puzzle` object |
| `GET`  | `/api/wheel/spin`    | Returns a `WheelSegment` |
| `POST` | `/api/game/guess`    | Body: `{ puzzleId, letter, pointsPerHit }` → `GuessResult` |
| `POST` | `/api/game/solve`    | Body: `{ puzzleId, attempt }` → `GuessResult` |

See `src/app/models/game.models.ts` for the full TypeScript interfaces that mirror the Java DTOs.

## Puzzle Types

- **CS CONCEPT** — phrase like `BINARY SEARCH` with all letters hidden
- **CODE SNIPPET** — method body with `{ } ( ) ; .` always visible, letters hidden

## Tech Stack

- Angular 17 (standalone components, signals-ready)
- TypeScript 5.2
- SCSS
- Fonts: Syne (display) + JetBrains Mono (code/labels)
