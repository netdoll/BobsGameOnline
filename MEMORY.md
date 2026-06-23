# Memory Document: Ongoing Observations & Context

## Project State (v3.5.1)

Jules Autopilot has successfully pivoted to a **Go-first architecture**.
The `server/` directory and backend-only JS dependencies have been removed.

### Core Observations
1.  **Architecture:**
    -   Frontend: Vite SPA (React 19, Tailwind v4).
    -   Backend: Go runtime (Fiber) serving APIs, WebSockets, and static assets.
    -   Database: SQLite + GORM.
    -   Workspaces: PNPM workspaces manage packages like `@jules/shared` and `@jules/cli`.
2.  **Deployment Challenges:**
    -   The Go version in `backend-go/go.mod` must match the build environment exactly (currently pinned to `1.26.0` for Render).
3.  **Missing/Incomplete Features (Identified for Implementation):**
    -   **Git Diff Monitoring:** Background Shadow Pilot anomaly detection is missing native git diff monitoring.
    -   **CI Pipeline Auto-Fix:** Shadow Pilot has anomaly logging but the CI pipeline auto-fix is incomplete.
    -   **Submodule Status Check:** Real-time submodule git status checks in the Go backend are not fully wired to the `/system/status` UI.

### Design Preferences
-   **No SSR:** The frontend relies exclusively on Client-Side Rendering (SPA mode) to avoid Next.js overhead.
-   **Single Source of Truth:** `VERSION.md` is the absolute source of truth for versions, updated via `scripts/update-version.js`.
-   **Universal Instructions:** All AI interactions must refer back to `LLM_INSTRUCTIONS.md`.

## Agent Directives
-   Always check this file before altering the project's macro structure.
-   Prioritize Go runtime stability over Node.js fallback mechanisms.



JULES AGENT LAST 5 MESSAGES:

---

---

---

---


=== Recent Commits ===
3a494a0 fix: update LM Studio model to gemma-4-26b-a4b-it-qat-heretic (actually loaded)
0a950f0 feat: nudge sends instructions+docs+agent msgs+commits, no recovery guidance
03f22a3 feat: recovery prompt structured as instructions+docs+agent msgs+commits+instructions
ea9bfed feat: skip nudge if last message from user, include last 5 agent msgs + docs + commits in recovery
7b3bcf5 chore: register services, change port 8081->8082, add Windows service scripts
a3470e0 chore: add .suno_new_session/ to gitignore
06b74a2 chore: cleanup dirty state
4eb0a03 sec: upgrade axios@^1.12.0, esbuild@latest
4431d37 Merge branch 'feat-shadow-pilot-git-diff-ui-12323440949671972104'
68b0a18 chore: cleanup jules-autopilot dirty state\n\n- Untrack packages/shared/dist/ (built outputs), add to .gitignore\n- Commit security upgrades (package.json, pnpm-lock.yaml)
