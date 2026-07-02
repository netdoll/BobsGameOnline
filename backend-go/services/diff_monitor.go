package services

import (
	"bytes"
	"log"
	"os/exec"
	"strings"
	"sync"
	"time"
)

type DiffEvent struct {
	Timestamp time.Time
	File      string
	Status    string // Added, Modified, Deleted
	Changes   int
}

type DiffMonitor struct {
	mu        sync.RWMutex
	lastDiffs []DiffEvent
	interval  time.Duration
}

var GlobalDiffMonitor *DiffMonitor

func init() {
	GlobalDiffMonitor = &DiffMonitor{
		lastDiffs: make([]DiffEvent, 0),
		interval:  10 * time.Second,
	}
}

func (dm *DiffMonitor) Start() {
	go func() {
		for {
			dm.checkDiffs()
			time.Sleep(dm.interval)
		}
	}()
}

func (dm *DiffMonitor) checkDiffs() {
	cmd := exec.Command("git", "diff", "--numstat")
	var out bytes.Buffer
	cmd.Stdout = &out
	if err := cmd.Run(); err != nil {
		log.Printf("DiffMonitor Error: %v", err)
		return
	}

	diffs := []DiffEvent{}
	lines := strings.Split(out.String(), "\n")
	for _, line := range lines {
		if strings.TrimSpace(line) == "" {
			continue
		}
		parts := strings.Fields(line)
		if len(parts) >= 3 {
			diffs = append(diffs, DiffEvent{
				Timestamp: time.Now(),
				File:      parts[2],
				Status:    "Modified",
			})
		}
	}

	dm.mu.Lock()
	dm.lastDiffs = diffs
	dm.mu.Unlock()

	// Notify Shadow Pilot if anomalies found
	if len(diffs) > 10 {
		LogAnomaly("High volume of uncommitted changes detected", map[string]interface{}{
			"count": len(diffs),
		})
	}
}

func (dm *DiffMonitor) GetRecentDiffs() []DiffEvent {
	dm.mu.RLock()
	defer dm.mu.RUnlock()
	return dm.lastDiffs
}
