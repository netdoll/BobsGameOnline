package services

import (
	"bytes"
	"log"
	"os/exec"
	"strings"
	"sync"
	"time"
)

type SubmoduleStatus struct {
	Name   string
	Path   string
	Status string // "up-to-date", "modified", "untracked"
	Commit string
}

type SubmoduleMonitor struct {
	mu          sync.RWMutex
	lastStatuses []SubmoduleStatus
	interval    time.Duration
}

var GlobalSubmoduleMonitor *SubmoduleMonitor

func init() {
	GlobalSubmoduleMonitor = &SubmoduleMonitor{
		lastStatuses: make([]SubmoduleStatus, 0),
		interval:     30 * time.Second,
	}
}

func (sm *SubmoduleMonitor) Start() {
	go func() {
		for {
			sm.checkSubmodules()
			time.Sleep(sm.interval)
		}
	}()
}

func (sm *SubmoduleMonitor) checkSubmodules() {
	cmd := exec.Command("git", "submodule", "status")
	var out bytes.Buffer
	cmd.Stdout = &out
	if err := cmd.Run(); err != nil {
		log.Printf("SubmoduleMonitor Error: %v", err)
		return
	}

	statuses := []SubmoduleStatus{}
	lines := strings.Split(out.String(), "\n")
	for _, line := range lines {
		if strings.TrimSpace(line) == "" {
			continue
		}

		statusIndicator := line[0:1]
		parts := strings.Fields(line[1:])

		if len(parts) >= 2 {
			statusStr := "up-to-date"
			if statusIndicator == "+" {
				statusStr = "modified"
			} else if statusIndicator == "-" {
				statusStr = "uninitialized"
			} else if statusIndicator == "U" {
				statusStr = "merge-conflict"
			}

			statuses = append(statuses, SubmoduleStatus{
				Commit: parts[0],
				Path:   parts[1],
				Name:   parts[1],
				Status: statusStr,
			})
		}
	}

	sm.mu.Lock()
	sm.lastStatuses = statuses
	sm.mu.Unlock()
}

func (sm *SubmoduleMonitor) GetRecentStatuses() []SubmoduleStatus {
	sm.mu.RLock()
	defer sm.mu.RUnlock()
	return sm.lastStatuses
}
