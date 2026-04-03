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
                // Map to GameServerTCP logic
                break;
            case "joinRoom":
                // Map to GameServerTCP logic
                break;
            case "frame":
                // High-frequency state sync
                break;
            case "chatMessage":
                break;
            default:
                log.warn("Unhandled WebSocket event: " + eventName);
        }
    }
}
