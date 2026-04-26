# MEMORY: Observations & Design Preferences

- **Architecture:** The project is deeply modular, with distinct `client`, `server`, and `shared` modules.
- **Language:** Migrating towards modern Java 21 features. Strong preference for clean, well-structured, straightforward code.
- **UI/UX:** Every feature must be comprehensively represented in the UI. No hidden functionality. Must have exhaustive tooltips, labels, and documentation.
- **Submodules:** The project aggressively incorporates external open-source projects as git submodules in `libs/` and `references/` to study their features, algorithms, and workflows.
- **Versioning:** A single `VERSION.md` file serves as the universal source of truth for the project version. Every build/session requires a version increment and a corresponding `CHANGELOG.md` entry.
- **AI Collaboration:** Continuous, iterative development through agent handoffs (Gemini -> Claude -> GPT), with strict requirements for updating `HANDOFF.md` and following `UNIVERSAL_LLM_INSTRUCTIONS.md`.
- **Refactoring:** Legacy code (e.g., Swing-based editors) is being actively modernized, ported to JavaFX/Web/C++ (Qt6), and refined for extreme robustness.
