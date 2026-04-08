# Features Research and Analysis

This document compiles comprehensive research on features from various sprite and tile editing tools to inform the development of the internal tools. The goal is to ensure every single implemented and planned feature and functionality is very well represented in full detail in the UI with all possible functionality, very well documented in the UI via labels, descriptions, and tooltips, and also fully documented with high-quality comprehensive documentation in the manual, help files, and so forth.

## Referenced Submodules & Systems Analysis

We have analyzed an extensive list of tools and submodules located in `references/` to build the ultimate sprite and tile editor.

### Pixel Art & Sprite Editors
*   **Aseprite & LibreSprite:** The industry standards. Key features include an advanced timeline, cel linking, onion skinning, pixel-perfect drawing, symmetry, advanced layer blending modes, reference layers, color palette management (indexed vs RGB), shading inks, and contour filling.
*   **Pixelorama & PixiEditor:** Modern, feature-rich tools offering excellent layer management, robust undo/redo (Command pattern), custom brushes, dynamic UI layouts, and sophisticated export options (spritesheets, GIFs).
*   **Grafx2 & Piskel & Csprite:** Focus on retro workflows. Grafx2 offers deep indexed color manipulation (color cycling, palette ramps). Piskel excels at quick, web-friendly animations with live previews and straightforward frame management.
*   **Stipple-effect & Rx & Raster-Master:** These tools highlight unique texturing, stippling brushes, and localized palette restrictions typical of older hardware restrictions.
*   **Sprite-Studio-64 & Retro-Game-Editor:** Focus on hardware-specific constraints (e.g., C64, NES), emphasizing strict palette enforcement, sprite-per-scanline limits, and tile character constraints.

### Tilemap & Level Editors
*   **Tiled:** The definitive tile editor. Features include orthogonal, isometric, and hexagonal grids. Object layers, image layers, custom properties (metadata), auto-tiling (Terrain/Wang sets), and collision shapes per tile.
*   **OgmoEditor3-CE:** Excellent for entity placement, distinct layer types (Decal, Tile, Entity, Grid), project-wide configurations, and clean JSON exports.
*   **Tile-Studio, Tilemap-Studio, Tilemap-Editor:** Focus on raw tile manipulation, stamp brushes, randomizing tile placement, and direct memory export for retro consoles.
*   **DTile & Bottled-up-tilemap & Tactile:** Modern, streamlined auto-tiling interfaces, allowing for rapid level design with minimal clicking.

### Voxel & 3D Tools
*   **Blockbench & Goxel & Cytopia:** While 3D/Voxel, they heavily influence 2D isometric workflows, texture mapping, UV painting, and generating 2D sprites from 3D models.

## Feature Analysis & UI/Documentation Strategy

### 1. Layers & Project Management
*   **Standard Layers:** Visibility, Opacity, Locking, Blending Modes.
*   **Reference Layers:** Layers visible during editing but excluded from export.
*   **Tilemap Layers:** Layers dedicated to tile indices rather than pixels.
*   **UI/Docs Strategy:** The Layer Panel must have clear iconography (eye for visibility, padlock for locking). Tooltips on hover must explain blending mode mathematics. A dedicated manual section will explain the difference between Reference and Standard layers, with GIF examples.

### 2. Drawing Tools
*   **Universal Brush:** Common interface for Pencil, Eraser, Fill, Shape, Custom Brushes.
*   **Pixel-Perfect:** Algorithm to remove "doubled" pixels on corners.
*   **Symmetry & Tile Instancing:** Real-time mirroring and automatic tile updates.
*   **UI/Docs Strategy:** The Toolbar will feature descriptive labels and hotkey hints in tooltips. The settings panel for each tool will include a "Help" button opening an inline documentation pane explaining concepts like "Pixel-Perfect" with visual "Before/After" diagrams.

### 3. Selection & Transformation
*   **Magic Wand & Color Select:** Connected pixels and global color selection.
*   **Transformations:** Rotations, scaling (lossy vs nearest-neighbor).
*   **UI/Docs Strategy:** Selection modes (Add, Subtract, Intersect) will have visual UI toggles. The manual will detail the exact flood-fill algorithms used for the Magic Wand and provide use-case tutorials.

### 4. Animation
*   **Timeline & Onion Skinning:** Frame thumbnails, tint/alpha skins.
*   **Tags/Loops:** Defining animation segments (Idle, Walk).
*   **UI/Docs Strategy:** The timeline UI will allow drag-and-drop frame reordering. Hovering over a frame will display its duration and assigned tags. The manual will include a comprehensive guide to building sprite animations from scratch using tags.

### 5. Color & Palette
*   **Palette Management:** Loading/Saving (.pal, .gpl), Rearranging.
*   **Color Replacement:** Global swap, gradient generation.
*   **UI/Docs Strategy:** The Palette UI will feature sort options (by Hue, Saturation). Tooltips will display RGB/Hex/Index values. Help files will explain how to import palettes from popular formats.

### 6. Tile Mapping
*   **Auto-Tiling:** Blob/Wang sets.
*   **Stamp Brush & Collision:** Multi-tile stamping, collision polygon definition.
*   **UI/Docs Strategy:** Auto-tiling will have a visual setup wizard in the UI, mapping tiles to 4-bit or 8-bit connection values. Comprehensive documentation will explain the bitmask logic behind auto-tiling with exhaustive examples.

### 7. Generative / AI
*   **Sprite Generation & Upscaling:** Text-to-Image, HQ2x/xBRZ.
*   **UI/Docs Strategy:** AI tools will have extensive prompt documentation, explaining how to write effective prompts, configure seed values, and understand upscaling algorithms.

## Priority Implementation List & Next Steps
1.  **Comprehensive UI Overhaul:** Ensure every tool has a settings pane, exhaustive tooltips, and clear labels.
2.  **Integrated Help System:** Build a searchable, offline-capable manual viewer directly into the editor UI.
3.  **Complete Submodule Feature Parity:** Systematically implement any remaining features found in Aseprite, Tiled, and Pixelorama, prioritizing a robust Command-pattern Undo system, advanced animation tagging, and multi-layer blending modes.
