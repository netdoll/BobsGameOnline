package api

import (
	"encoding/json"
	"net/http"

	"bobsgameonlinejava/backend-go/autonomous"
)

func HandleFleetSummary(w http.ResponseWriter, r *http.Request) {
	w.Header().Set("Content-Type", "application/json")

	// Get ongoing active debate telemetry
	debatesJSON := autonomous.GetFleetDebateSummary()

	var debates []interface{}
	json.Unmarshal(debatesJSON, &debates)

	summary := map[string]interface{}{
		"status": "ONLINE",
		"active_jobs": 0, // Placeholder
		"council_debates": debates,
	}

	json.NewEncoder(w).Encode(summary)
}
