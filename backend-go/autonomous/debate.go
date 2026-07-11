package autonomous

import (
	"encoding/json"
	"log"
	"time"
)

type AgentMessage struct {
	Role      string    `json:"role"`
	Content   string    `json:"content"`
	Timestamp time.Time `json:"timestamp"`
	Tokens    int       `json:"tokens"`
}

type DebateSession struct {
	ID        string         `json:"id"`
	Topic     string         `json:"topic"`
	Status    string         `json:"status"` // "ongoing", "resolved", "escalated"
	Messages  []AgentMessage `json:"messages"`
	StartTime time.Time      `json:"startTime"`
	EndTime   time.Time      `json:"endTime,omitempty"`
}

var ActiveDebates = make(map[string]*DebateSession)

func InitiateDebate(topic string) *DebateSession {
	session := &DebateSession{
		ID:        "debate_" + time.Now().Format("20060102150405"),
		Topic:     topic,
		Status:    "ongoing",
		StartTime: time.Now(),
		Messages:  []AgentMessage{},
	}
	ActiveDebates[session.ID] = session
	log.Printf("[COUNCIL SUPERVISOR] Initiated debate session: %s on topic: %s", session.ID, topic)
	return session
}

func AddDebateMessage(sessionID, role, content string, tokens int) {
	if session, exists := ActiveDebates[sessionID]; exists {
		msg := AgentMessage{
			Role:      role,
			Content:   content,
			Timestamp: time.Now(),
			Tokens:    tokens,
		}
		session.Messages = append(session.Messages, msg)
		log.Printf("[DEBATE %s] %s: %s (%d tokens)", sessionID, role, content, tokens)
	}
}

func ResolveDebate(sessionID string) {
	if session, exists := ActiveDebates[sessionID]; exists {
		session.Status = "resolved"
		session.EndTime = time.Now()
		log.Printf("[COUNCIL SUPERVISOR] Resolved debate session: %s", sessionID)
	}
}

func GetFleetDebateSummary() []byte {
	summary := []map[string]interface{}{}
	for _, session := range ActiveDebates {
		summary = append(summary, map[string]interface{}{
			"id":       session.ID,
			"topic":    session.Topic,
			"status":   session.Status,
			"messages": len(session.Messages),
		})
	}
	bytes, _ := json.Marshal(summary)
	return bytes
}
