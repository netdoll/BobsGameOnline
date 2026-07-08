package api

import (
	"log"
	"net/http"
	"time"

	"bobsgameonlinejava/backend-go/services"
	"github.com/gorilla/websocket"
)

var upgrader = websocket.Upgrader{
	CheckOrigin: func(r *http.Request) bool {
		return true // Allow all origins for the dashboard
	},
}

func HandleWebSocket(w http.ResponseWriter, r *http.Request) {
	conn, err := upgrader.Upgrade(w, r, nil)
	if err != nil {
		log.Printf("WebSocket upgrade failed: %v", err)
		return
	}
	defer conn.Close()

	// Stream diff updates periodically
	ticker := time.NewTicker(2 * time.Second)
	defer ticker.Stop()

	for {
		select {
		case <-ticker.C:
			diffs := services.GlobalDiffMonitor.GetRecentDiffs()
			submodules := services.GlobalSubmoduleMonitor.GetRecentStatuses()
			if err := conn.WriteJSON(map[string]interface{}{
				"type": "diff_update",
				"data": diffs,
				"submodules": submodules,
			}); err != nil {
				log.Printf("WebSocket write failed: %v", err)
				return
			}
		}
	}
}
