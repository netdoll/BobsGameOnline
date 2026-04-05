# Changelog

All notable changes to this project will be documented in this file.

## [2.0.15] - 2026-04-05

### Added
- **Scene2D Normalize-All Helper:** `CustomGameEditor.java` now provides a one-click helper to normalize every rotation in the selected piece, making full-set cleanup much faster.

### Changed
- Bumped `VERSION.md` to `2.0.15`.

### Validation
- `./gradlew compileJava` ✅

## [2.0.14] - 2026-04-05

### Added
- **Scene2D Duplicate Cleanup Helper:** `CustomGameEditor.java` now provides a one-click duplicate-rotation cleanup action that removes redundant rotations after confirmation.

### Changed
- Bumped `VERSION.md` to `2.0.14`.

### Validation
- `./gradlew compileJava` ✅

## [2.0.13] - 2026-04-05

### Added
- **Scene2D Normalize / Center Helpers:** `CustomGameEditor.java` now provides one-click normalize and center helpers for the current rotation, making custom piece cleanup much faster.

### Changed
- Bumped `VERSION.md` to `2.0.13`.

### Validation
- `./gradlew compileJava` ✅

## [2.0.12] - 2026-04-05

### Added
- **Scene2D Symmetry & Duplicate Hints:** `CustomGameEditor.java` now reports current-rotation symmetry and highlights duplicate rotations in the rotation overview, giving authors direct feedback about redundant states.

### Changed
- Bumped `VERSION.md` to `2.0.12`.

### Validation
- `./gradlew compileJava` ✅

## [2.0.11] - 2026-04-05

### Added
- **Scene2D Rotation Analytics:** `CustomGameEditor.java` now reports current rotation bounding-box size plus unique-vs-duplicate rotation counts for the selected piece, and the rotation overview buttons include bounding-box information.

### Changed
- Bumped `VERSION.md` to `2.0.11`.

### Validation
- `./gradlew compileJava` ✅

## [2.0.10] - 2026-04-05

### Added
- **Scene2D Preset Shortcut Buttons:** `CustomGameEditor.java` now includes one-click Classic Drop, Cascade Puzzle, and Stack Arcade preset buttons layered on top of the in-session preset-slot workflow.

### Changed
- Bumped `VERSION.md` to `2.0.10`.

### Validation
- `./gradlew compileJava` ✅

## [2.0.9] - 2026-04-05

### Added
- **Scene2D Preset Slots:** `CustomGameEditor.java` now supports quick in-session preset save/load slots using deep-cloned `GameType` snapshots, enabling fast iteration without replacing the active draft permanently.

### Changed
- Bumped `VERSION.md` to `2.0.9`.

### Validation
- `./gradlew compileJava` ✅

## [2.0.8] - 2026-04-05

### Added
- **Scene2D Movement / Randomizer Toggles:** `CustomGameEditor.java` now exposes Scene2D checkboxes for next-piece preview, hold piece, bag randomizer, hard-drop punch-through, and multiple kick/movement options.

### Changed
- The Java editor summary now includes enabled movement/randomizer rules together with the earlier advanced rule set.
- Bumped `VERSION.md` to `2.0.8`.

### Validation
- `./gradlew compileJava` ✅

## [2.0.7] - 2026-04-05

### Added
- **Scene2D Advanced Rule Toggles:** `CustomGameEditor.java` now exposes Scene2D checkboxes for cascade gravity, disconnected-only gravity, row/column/diagonal chain checks, and recursive chain search.

### Changed
- The Java editor summary now reports enabled advanced rules explicitly.
- Bumped `VERSION.md` to `2.0.7`.

### Validation
- `./gradlew compileJava` ✅

## [2.0.6] - 2026-04-05

### Added
- **Scene2D Rotation Overview:** `CustomGameEditor.java` now builds a clickable rotation overview strip with per-rotation occupied-cell counts, allowing direct selection instead of only cycling next/previous.

### Changed
- Bumped `VERSION.md` to `2.0.6`.

### Validation
- `./gradlew compileJava` ✅

## [2.0.5] - 2026-04-05

### Added
- **Scene2D Duplication Workflow:** `CustomGameEditor.java` now supports duplicating the selected piece and duplicating the selected rotation, making iterative ruleset authoring much faster.

### Changed
- Bumped `VERSION.md` to `2.0.5`.

### Validation
- `./gradlew compileJava` ✅

## [2.0.4] - 2026-04-05

### Changed
- **Safer Scene2D Deletes:** Piece and rotation removals in `CustomGameEditor.java` now go through `Scene2DYesNoDialog` confirmations instead of deleting immediately.
- Bumped `VERSION.md` to `2.0.4`.

### Validation
- `./gradlew compileJava` ✅

## [2.0.3] - 2026-04-05

### Added
- **Scene2D Delete Controls:** `CustomGameEditor.java` now supports removing the selected piece and removing the selected rotation, extending the Scene2D editor beyond pure additive editing.

### Changed
- **Editor Guardrails:** Grid buttons are disabled when no piece is selected, and the editor now recovers selection cleanly after deleting a piece or rotation.
- Bumped `VERSION.md` to `2.0.3`.

### Validation
- `./gradlew compileJava` ✅

## [2.0.2] - 2026-04-05

### Fixed
- **nD Puzzle Bootstrap:** `NDPuzzleGame.java` now constructs `GameLogic` with a local `GameManager`/`Room` context, assigns the active `PuzzlePlayer`, and calls `update(0, 1)` with the modern signature.
- **TWL/LWJGL Input Bridge:** Added a tracked parent-repo override at `src/main/java/de/matthiasmann/twl/input/lwjgl/LWJGLInput.java` plus a Gradle exclude for the conflicting nested-submodule source file, avoiding the compile-time API drift that previously broke `compileJava`.

### Changed
- Bumped `VERSION.md` to `2.0.2`.

### Validation
- `./gradlew compileJava` ✅

## [2.0.1] - 2026-04-05

### Added
- **Scene2D Custom Piece Editing:** `CustomGameEditor.java` now maintains a live `GameType`, supports adding piece types and rotations, and exposes an interactive 4x4 rotation grid for sketching custom puzzle pieces.
- **Editor Summary Feedback:** Added current piece / rotation labels plus a summary line showing grid, mode, piece count, rotation count, and filled-cell count for the active rotation.

### Changed
- Bumped `VERSION.md` to `2.0.1`.
- The Scene2D custom editor now serves as a real editing surface instead of placeholder buttons.

### Validation
- `./gradlew compileJava` still fails, but the remaining failures are pre-existing and unrelated to this editor change:
  - `NDPuzzleGame.java` constructor/update mismatch with `GameLogic`
  - `libs/twl-lwjgl3` `GUI` API mismatches (`handleKey`, `handleMouse`, `handleMouseWheel`)


## [0.1.7] - 2026-02-07

### Fixed
- **Submodules**: Fixed `libs/aseprite-file` submodule pointer to a valid commit (`06b6189`) as the previous one was missing on the remote, causing clone failures.

## [0.1.6] - 2026-02-03

### Changed
- **Maintenance**: Automated cleanup and merge of feature branches.
- **Submodules**: Removed broken `GeoIP2-java` submodule.

## [0.1.5] - 2025-12-27

### Changed
- **Submodules**: Updated all submodules to latest upstream versions and merged upstream changes.
- **Dashboard**: Updated `docs/dashboard.md` with latest submodule status and project structure explanation.
- **Maintenance**: Merged all feature branches into `main` and ensured clean state.

## [0.1.4] - 2025-12-27

### Changed
- **Environment**: Standardized development environment on Java 21 to resolve Gradle 8.8 compatibility issues.
- **Documentation**: Consolidated and updated `LLM_INSTRUCTIONS.md` to serve as the single source of truth for all AI models.
- **Documentation**: Updated `docs/dashboard.md` with current submodule hashes and build status.

## [0.1.3] - 2025-12-26

### Added
- **Project Merging**: Implemented full project merging capabilities. Users can now merge maps, tilesets, palettes, sprites, and other data from another project zip file into the current project.
- **Documentation**: Updated `LLM_INSTRUCTIONS.md` with strict versioning protocols.
- **Dashboard**: Updated `docs/dashboard.md` with latest submodule status.

### Changed
- **Submodules**: Updated all submodules to latest upstream versions.
- **Branches**: Merged all local feature branches into `main`.
- **Versioning**: Incremented version to 0.1.3.

## [0.1.2] - 2025-12-25

### Added
- **Import Image to Tileset**: New feature in `Tileset Tools` menu allowing users to import BMP/PNG images directly into the tileset. Supports appending or overwriting, and automatically matches or adds colors to the palette.
- **Pattern Fill**: `MapCanvas` now supports filling a selection with a pattern from the tileset selection (tiling).
- **Random Sprite Export**: Added "Export Random Sprites" to Sprite Editor to export sprites marked as random.
- **Grid Layout**: Sprite Editor now displays frames in a responsive grid layout instead of a single line.
- **Palette Synchronization**: Implemented synchronized sorting and color addition across all tileset palettes to maintain consistency when modifying the master palette.

### Changed
- **Optimized Redo**: Improved performance of the Redo operation in `MapCanvas`.
- **Documentation**: Updated `dashboard.md`, `LLM_INSTRUCTIONS.md`, and `ROADMAP.md`.
- **Versioning**: Project now references `VERSION.md`.

### Fixed
- Fixed `GameSave.java` compilation errors (previous session).
- Fixed `SEFrameCanvas` painting logic.

## [0.1.1] - 2025-12-25

### Fixed
- Resolved compilation errors in `GameSave.java` by removing duplicate methods and fixing structure.
- Fixed duplicate field definitions in `CustomGameEditor.java`.
- Fixed missing method calls in `GLUtils.java` by correcting `BitmapFont` import.
- Fixed missing variables in `GameLogic.java`.

## [0.1.0] - 2025-12-25

### Added
- `docs/dashboard.md`: Project dashboard with submodule status and structure explanation.
- `VERSION.md`: Single source of truth for project version.
- `LLM_INSTRUCTIONS.md`: Unified instructions for AI assistants.

### Changed
- Merged feature branches:
    - `modernize-codebase-final-polish`
    - `modernize-codebase-polish`
    - `modernize-codebase-polish-2`
    - `modernize-java-project-part-2`
    - `new-feature-branch`
- Updated all submodules to latest upstream versions.
- Fixed `cpp_repo` submodule issue.
