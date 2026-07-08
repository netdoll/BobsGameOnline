package api

import (
	"encoding/json"
	"net/http"

	"bobsgameonlinejava/backend-go/services"
)

func HandleSystemStatus(w http.ResponseWriter, r *http.Request) {
	w.Header().Set("Content-Type", "application/json")
	diffs := services.GlobalDiffMonitor.GetRecentDiffs()
	submodules := services.GlobalSubmoduleMonitor.GetRecentStatuses()

	status := map[string]interface{}{
		"diffLogs": diffs,
		"submodules": submodules,
		"shadowPilot": map[string]interface{}{
			"ciAutoFix": "READY",
			"submoduleSync": "ACTIVE",
			"diffMonitor": "ACTIVE",
		},
	}

	json.NewEncoder(w).Encode(status)
}
