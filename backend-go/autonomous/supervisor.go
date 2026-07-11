package autonomous

import (
	"log"
	"math/rand"
	"time"
)

type Plan struct {
	ID    string
	Risk  string // "LOW", "HIGH"
	Topic string
}

func EvaluatePlan(plan Plan) {
	if plan.Risk == "HIGH" {
		log.Printf("[COUNCIL SUPERVISOR] High-risk plan detected: %s. Initiating multi-agent debate.", plan.ID)

		session := InitiateDebate(plan.Topic)

		// Simulate Security Architect
		tokensArch := rand.Intn(100) + 50
		AddDebateMessage(session.ID, "Security Architect", "I have reviewed the plan. The anomaly vectors look safe, but we must verify the CI hooks.", tokensArch)
		time.Sleep(100 * time.Millisecond) // Simulated latency

		// Simulate Senior Engineer
		tokensEng := rand.Intn(100) + 50
		AddDebateMessage(session.ID, "Senior Engineer", "Agreed. The hooks are isolated from the main orchestrator loop. Proceeding with execution.", tokensEng)
		time.Sleep(100 * time.Millisecond)

		ResolveDebate(session.ID)
	} else {
		log.Printf("[COUNCIL SUPERVISOR] Low-risk plan %s auto-approved.", plan.ID)
	}
}
