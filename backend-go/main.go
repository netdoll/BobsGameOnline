package main

import (
	"encoding/json"
	"fmt"
	"log"
	"net/http"
	"os"

	"bobsgameonlinejava/backend-go/services"
)

func main() {
	services.GlobalDiffMonitor.Start()

	http.HandleFunc("/api/system/diff-status", func(w http.ResponseWriter, r *http.Request) {
		w.Header().Set("Content-Type", "application/json")
		diffs := services.GlobalDiffMonitor.GetRecentDiffs()
		json.NewEncoder(w).Encode(diffs)
	})

	port := os.Getenv("PORT")
	if port == "" {
		port = "8080"
	}

	fmt.Printf("Server starting on port %s...\n", port)
	if err := http.ListenAndServe(":"+port, nil); err != nil {
		log.Fatalf("Server failed to start: %v", err)
	}
}
