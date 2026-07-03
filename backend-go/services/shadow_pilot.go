package services

import (
	"log"
	"os/exec"
)

func LogAnomaly(message string, details map[string]interface{}) {
	log.Printf("[SHADOW PILOT ANOMALY] %s: %v", message, details)

	// Trigger CI auto-fix logic locally
	cmd := exec.Command("go", "run", "autofix/autofix.go", "--diff-source=native-diff-monitor", "--apply-heuristics")
	cmd.Dir = ".." // Run from backend-go

	// Start it in background to not block
	if err := cmd.Start(); err != nil {
		log.Printf("[SHADOW PILOT] Failed to trigger autofix: %v", err)
	}
}
