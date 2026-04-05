# Changelog

All notable changes to this project will be documented in this file.

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
