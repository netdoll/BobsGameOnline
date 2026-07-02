package services

import (
	"log"
)

func LogAnomaly(message string, details map[string]interface{}) {
	log.Printf("[SHADOW PILOT ANOMALY] %s: %v", message, details)
}
