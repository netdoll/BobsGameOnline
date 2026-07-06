package api

import (
	"encoding/json"
	"net/http"

	"bobsgameonlinejava/backend-go/services"
)

func HandleAutofix(w http.ResponseWriter, r *http.Request) {
	if r.Method != http.MethodPost {
		http.Error(w, "Method not allowed", http.StatusMethodNotAllowed)
		return
	}

	w.Header().Set("Content-Type", "application/json")

	// Trigger the CI auto-fix logic locally
	services.LogAnomaly("Manual autofix trigger from UI", map[string]interface{}{})

	response := map[string]interface{}{
		"status": "Autofix triggered via Shadow Pilot orchestration",
	}

	json.NewEncoder(w).Encode(response)
}
