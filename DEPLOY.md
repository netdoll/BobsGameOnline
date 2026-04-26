# DEPLOY: Deployment Instructions

The project consists of multiple deployable artifacts:

## Server Deployment (Docker)
1. Ensure Docker and docker-compose are installed.
2. Configure `server.properties` or environment variables for database credentials.
3. Run `docker-compose up --build -d` to launch the Game Server, STUN Server, and MySQL database.

## Client Deployment
1. Build the client using Gradle: `./gradlew :client:build`
2. Run the client: `./gradlew :client:run`

## Web Deployment (Hetzner / bobsgameweb)
*(To be expanded as the web port is finalized)*
- Target: 30-player cross-platform multiplayer, leaderboards, and live editing.
- Ensure all WebSocket proxy layers are configured to bridge web traffic to the Netty TCP backend.
