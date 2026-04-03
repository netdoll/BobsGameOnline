package com.bobsgame.server;

import java.util.concurrent.ConcurrentHashMap;
import java.util.ArrayList;
import java.util.List;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

public class RoomManager {
    public static class Room {
        public String id;
        public String name;
        public ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
        public List<String> playerIds = new ArrayList<>();
        public String state = "LOBBY";
    }

    private static ConcurrentHashMap<String, Room> rooms = new ConcurrentHashMap<>();

    public static Room createRoom(String name) {
        Room room = new Room();
        room.id = java.util.UUID.randomUUID().toString().substring(0, 8);
        room.name = name;
        rooms.put(room.id, room);
        return room;
    }

    public static Room getRoom(String id) {
        return rooms.get(id);
    }

    public static void removeRoom(String id) {
        rooms.remove(id);
    }
}
