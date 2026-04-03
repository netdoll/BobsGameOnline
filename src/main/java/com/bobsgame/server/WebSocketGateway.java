package com.bobsgame.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.slf4j.LoggerFactory;
import ch.qos.logback.classic.Logger;

/**
 * WebSocket Gateway for the Java Server.
 * 
 * Maps incoming WebSocket JSON events (Socket.io-like) 
 * to the internal GameServerTCP logic.
 */
public class WebSocketGateway extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    public static Logger log = (Logger) LoggerFactory.getLogger(WebSocketGateway.class);
    private static final Gson gson = new Gson();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame frame) throws Exception {
        String text = frame.text();
        log.debug("WebSocket Message received: " + text);

        // Simple protocol: ["eventName", {data}]
        // This is a minimal bridge to match the Node.js server behavior
        try {
            if (text.startsWith("42[")) { // Socket.io protocol prefix
                String json = text.substring(2);
                Object[] eventArr = gson.fromJson(json, Object[].class);
                String eventName = (String) eventArr[0];
                JsonObject data = (eventArr.length > 1) ? gson.toJsonTree(eventArr[1]).getAsJsonObject() : null;

                handleEvent(ctx, eventName, data);
            }
        } catch (Exception e) {
            log.error("Failed to parse WebSocket message", e);
        }
    }

    private void handleEvent(ChannelHandlerContext ctx, String eventName, JsonObject data) {
        log.info("Handling WebSocket event: " + eventName);

        switch (eventName) {
            case "createRoom":
                String roomName = data.has("name") ? data.get("name").getAsString() : "New Room";
                RoomManager.Room newRoom = RoomManager.createRoom(roomName);
                newRoom.channels.add(ctx.channel());
                
                JsonObject response = new JsonObject();
                response.addProperty("id", newRoom.id);
                response.addProperty("name", newRoom.name);
                ctx.writeAndFlush(new TextWebSocketFrame("42[\"roomCreated\"," + gson.toJson(response) + "]"));
                break;

            case "joinRoom":
                String roomId = data.get("id").getAsString();
                RoomManager.Room room = RoomManager.getRoom(roomId);
                if (room != null) {
                    room.channels.add(ctx.channel());
                    JsonObject joinInfo = new JsonObject();
                    joinInfo.addProperty("id", room.id);
                    joinInfo.addProperty("name", room.name);
                    ctx.writeAndFlush(new TextWebSocketFrame("42[\"joinedRoom\"," + gson.toJson(joinInfo) + "]"));
                }
                break;

            case "frame":
                // Broadcast frame to all other players in the room
                // Note: Logic to find the current room for this channel needed
                break;

            case "saveCharacter":
                saveCharacter(data);
                ctx.writeAndFlush(new TextWebSocketFrame("42[\"characterSaved\",{\"success\":true}]"));
                break;
            case "loadCharacter":
                String charName = data.getAsString();
                String charJson = loadCharacter(charName);
                ctx.writeAndFlush(new TextWebSocketFrame("42[\"characterLoaded\",{\"success\":true,\"charData\":" + charJson + "}]"));
                break;
            case "saveMap":
                saveMap(data);
                ctx.writeAndFlush(new TextWebSocketFrame("42[\"mapSaved\",{\"success\":true}]"));
                break;
            case "loadMap":
                String mapId = data.getAsString();
                String mapJson = loadMap(mapId);
                ctx.writeAndFlush(new TextWebSocketFrame("42[\"mapLoaded\",{\"success\":true,\"mapData\":" + mapJson + "}]"));
                break;
            case "playerMove":
            case "playerAction":
                // Broadcast to world
                break;

            case "saveRPGDatabase":
                saveDatabase(data);
                ctx.writeAndFlush(new TextWebSocketFrame("42[\"rpgDatabaseSaved\",{\"success\":true}]"));
                break;

            case "loadRPGDatabase":
                String dbJson = loadDatabase();
                ctx.writeAndFlush(new TextWebSocketFrame("42[\"rpgDatabaseLoaded\",{\"success\":true,\"db\":" + dbJson + "}]"));
                break;

            default:
                log.warn("Unhandled WebSocket event: " + eventName);
        }
    }

    private void saveDatabase(JsonObject data) {
        try (java.io.FileWriter writer = new java.io.FileWriter("rpg_database.json")) {
            gson.toJson(data, writer);
        } catch (Exception e) {
            log.error("Failed to save RPG Database", e);
        }
    }

    private String loadDatabase() {
        try {
            return new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get("rpg_database.json")));
        } catch (Exception e) {
            return "{}";
        }
    }

    private void saveCharacter(JsonObject data) {
        try {
            String name = data.get("name").getAsString();
            JsonObject charData = data.get("charData").getAsJsonObject();
            java.nio.file.Files.createDirectories(java.nio.file.Paths.get("characters"));
            try (java.io.FileWriter writer = new java.io.FileWriter("characters/" + name.toLowerCase() + ".json")) {
                gson.toJson(charData, writer);
            }
        } catch (Exception e) {
            log.error("Failed to save character", e);
        }
    }

    private String loadCharacter(String name) {
        try {
            return new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get("characters/" + name.toLowerCase() + ".json")));
        } catch (Exception e) {
            return "{}";
        }
    }

    private void saveMap(JsonObject data) {
        try {
            String mapId = data.get("mapId").getAsString();
            JsonObject mapData = data.get("mapData").getAsJsonObject();
            java.nio.file.Files.createDirectories(java.nio.file.Paths.get("maps"));
            try (java.io.FileWriter writer = new java.io.FileWriter("maps/map_" + mapId + ".json")) {
                gson.toJson(mapData, writer);
            }
        } catch (Exception e) {
            log.error("Failed to save map", e);
        }
    }

    private String loadMap(String mapId) {
        try {
            return new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get("maps/map_" + mapId + ".json")));
        } catch (Exception e) {
            return "{}";
        }
    }
}
