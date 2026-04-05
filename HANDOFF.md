# Session Handoff

## 2026-04-05 - Scene2D Custom Puzzle Editor Parity Pass

### Summary
This session focused on the newer Scene2D `CustomGameEditor` so the Java port has a genuinely interactive custom piece-editing surface instead of placeholder buttons.

### What Changed
- Rewrote `src/main/java/com/bobsgame/client/engine/game/gui/customGameEditor/CustomGameEditor.java` into a live editor panel backed by a real `GameType` instance.
- Added controls for:
  - add piece type
  - add rotation
  - clear current rotation
  - previous / next piece navigation
  - previous / next rotation navigation
- Wired the 4x4 button grid to real `Piece.Rotation` `blockOffsets` so clicks add/remove occupied cells.
- Added summary labels showing:
  - selected piece
  - selected rotation
  - total piece count
  - total rotation count
  - filled cell count in the current rotation
  - current mode / grid dimensions

### Validation
- `./gradlew compileJava` was first run to validate the editor work and revealed pre-existing failures in:
  - `src/main/java/com/bobsgame/client/engine/nd/NDPuzzleGame.java`
  - `libs/twl-lwjgl3/src/de/matthiasmann/twl/input/lwjgl/LWJGLInput.java`
- Those follow-up blockers were then fixed in the same session.
- Final result: `./gradlew compileJava` ✅

### Additional Build-Recovery Work Completed
- Updated `NDPuzzleGame.java` to use a local `GameManager`/`Room` wrapper and the current `GameLogic(GameManager, long)` constructor.
- Switched nD puzzle updates to `puzzleGame.update(0, 1)`.
- Added a tracked parent-repo compatibility override at `src/main/java/de/matthiasmann/twl/input/lwjgl/LWJGLInput.java` and excluded the conflicting nested-submodule source path in Gradle so TWL input forwarding no longer breaks on compile-time API drift against `TWL.jar`.

### Additional Follow-Up - 2026-04-05
- Extended the Scene2D editor with piece removal and rotation removal.
- Disabled grid buttons automatically when no piece is selected so empty-state editing is clearer.
- Bumped the Java repo version again to `2.0.3`.

### Additional Follow-Up - 2026-04-05 (Delete Confirmations)
- Routed Scene2D piece/rotation deletes through `Scene2DYesNoDialog` confirmations.
- Bumped the Java repo version again to `2.0.4`.

### Additional Follow-Up - 2026-04-05 (Duplication Workflow)
- Added duplicate-piece and duplicate-rotation actions to the Scene2D custom editor.
- Bumped the Java repo version again to `2.0.5`.

### Additional Follow-Up - 2026-04-05 (Rotation Overview)
- Added clickable rotation overview buttons with occupied-cell counts to the Scene2D custom editor.
- Bumped the Java repo version again to `2.0.6`.

### Additional Follow-Up - 2026-04-05 (Advanced Rule Toggles)
- Added Scene2D checkboxes for cascade gravity, disconnected-only gravity, row/column/diagonal chain checks, and recursive search.
- Expanded the Java editor summary to report enabled advanced rules.
- Bumped the Java repo version again to `2.0.7`.

### Additional Follow-Up - 2026-04-05 (Movement / Randomizer Toggles)
- Added Scene2D checkboxes for next-piece preview, hold piece, bag randomizer, hard-drop punch-through, and movement/kick options.
- Expanded the Java editor summary so those movement/randomizer rules are visible immediately.
- Bumped the Java repo version again to `2.0.8`.

### Additional Follow-Up - 2026-04-05 (Preset Slots)
- Added in-session preset save/load slots to the Scene2D custom editor using deep-cloned `GameType` snapshots.
- Bumped the Java repo version again to `2.0.9`.

### Additional Follow-Up - 2026-04-05 (Preset Shortcut Buttons)
- Added one-click Classic Drop, Cascade Puzzle, and Stack Arcade preset buttons on top of the Scene2D preset-slot workflow.
- Bumped the Java repo version again to `2.0.10`.

### Additional Follow-Up - 2026-04-05 (Rotation Analytics)
- Added current-rotation bounding-box size and unique-vs-duplicate rotation counts to the Scene2D custom editor summary.
- Expanded rotation overview buttons to include bounding-box information.
- Bumped the Java repo version again to `2.0.11`.

### Additional Follow-Up - 2026-04-05 (Symmetry & Duplicate Hints)
- Added current-rotation symmetry reporting and duplicate-rotation warnings to the Scene2D custom editor analytics.
- Bumped the Java repo version again to `2.0.12`.

### Additional Follow-Up - 2026-04-05 (Normalize / Center Helpers)
- Added one-click normalize and center helpers for the current rotation in the Scene2D custom editor.
- Bumped the Java repo version again to `2.0.13`.

### Additional Follow-Up - 2026-04-05 (Duplicate Cleanup)
- Added a one-click duplicate-rotation cleanup action to the Scene2D custom editor.
- Bumped the Java repo version again to `2.0.14`.

### Additional Follow-Up - 2026-04-05 (Normalize-All Helper)
- Added a one-click helper to normalize every rotation in the selected piece.
- Bumped the Java repo version again to `2.0.15`.

### Additional Follow-Up - 2026-04-05 (Empty Rotation Cleanup)
- Added a one-click helper to remove empty rotations after confirmation.
- Bumped the Java repo version again to `2.0.16`.

### Additional Follow-Up - 2026-04-05 (Recent Share / Import History)
- Added Scene2D import/share entry points backed by the existing string-dialog UI.
- Added a bounded recent-history list with quick load/copy actions for recently imported or shared rulesets.
- Added clipboard-first share behavior with a manual-copy dialog fallback.
- Bumped the Java repo version again to `2.0.17`.

### Recommended Next Steps
1. Add color/block-type controls to the Scene2D editor.
2. Bridge Scene2D custom editor state into the older TWL editor stack where useful.
3. Replace the temporary reflection bridge with a direct adapter once the exact TWL/LWJGL API contract is fully normalized.
4. Add a targeted Java regression test or smoke harness for nD puzzle bootstrap + editor launch.

## Summary
This session focused on modernizing the internal Swing-based Editor tools (`SpriteEditor`, `MapCanvas`, `DialogueEditor`) to include features found in industry-standard tools like Aseprite and Pyxel Edit.

## Key Changes

### 1. Editor Improvements
*   **Layer System:** Refactored `Sprite.java` to support multiple layers (Reference, Normal). Implemented `SELayerPanel` for UI management. Export methods flatten layers for backward compatibility.
*   **Project Persistence:** Added `.sprproj` format (JSON/GZIP) to save editor state (layers, visibility, opacity) without data loss.
*   **Universal Brushes:** Refactored `SECanvas` to use a `Brush` interface. Implemented `Pencil` (with Pixel Perfect mode), `Eraser`, `Fill`, and `Magic Wand` brushes.
*   **Selection Tools:** Added non-rectangular selection support using `boolean[][] mask` in `SelectionArea`.
*   **Pixel Perfect Drawing:** Added algorithm to `PixelBrush` to remove L-shaped corners during freehand drawing.
*   **Dialogue Editor:** Added a live `DialoguePreviewPanel` to visualize text rendering (color tags, pauses, page breaks) in real-time.
*   **Map Editor:** Implemented "Tile Instancing" (editing tiles directly on the map) and "Auto-tiling" (4-bit edge masking).

### 2. Infrastructure
*   **Submodules:** Added ~30 submodules in `references/` for research. Updated `libs/` submodules.
*   **Dashboard:** Created `DASHBOARD.md` listing all submodules and project structure.
*   **Roadmap:** Updated `ROADMAP_EDITOR.md` and `FEATURES_RESEARCH.md` with findings and status.

## Directory Structure
*   `client/`: Main game code.
*   `server/`: Game server.
*   `shared/`: Shared logic.
*   `references/`: External research repos.
*   `libs/`: Dependencies.

## Next Steps
*   **Undo System:** The undo system works (`CompoundEdit`) but could be more robust (limit stack size, memory usage).
*   **Animation Features:** Timeline view, Tags (Idle, Walk).
*   **Visual Dialogue Graph:** The current editor is text-based with preview. A node-based editor would be a massive improvement.
