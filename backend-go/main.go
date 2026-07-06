package main

import (
	"fmt"
	"log"
	"net/http"
	"os"

	"bobsgameonlinejava/backend-go/api"
	"bobsgameonlinejava/backend-go/services"
)

func main() {
	services.GlobalDiffMonitor.Start()
	services.GlobalSubmoduleMonitor.Start()

	http.HandleFunc("/api/system/status", api.HandleSystemStatus)
	http.HandleFunc("/api/system/ws", api.HandleWebSocket)
	http.HandleFunc("/api/system/autofix", api.HandleAutofix)

	port := os.Getenv("PORT")
	if port == "" {
		port = "8080"
	}

	fmt.Printf("Server starting on port %s...\n", port)
	if err := http.ListenAndServe(":"+port, nil); err != nil {
		log.Fatalf("Server failed to start: %v", err)
	}
}
