package com.bobsgame.shared.database;

import com.bobsgame.shared.AssetData;
import com.bobsgame.shared.MapData;
import com.bobsgame.shared.SkillData;
import java.util.ArrayList;

public class RPGDatabase {
    public static class ActorData {
        public int id;
        public String name;
        public int classId;
        public int initialLevel;
        public String faceName;
        public String characterName;
        public String description;
    }

    public static class ItemData {
        public int id;
        public String name;
        public String description;
        public int price;
        public boolean consumable;
        public int itypeId;
    }

    public static class EnemyData {
        public int id;
        public String name;
        public int mhp;
        public int atk;
        public int def;
        public int gold;
    }

    public ArrayList<ActorData> actors = new ArrayList<>();
    public ArrayList<SkillData> skills = new ArrayList<>();
    public ArrayList<ItemData> items = new ArrayList<>();
    public ArrayList<EnemyData> enemies = new ArrayList<>();
    public ArrayList<MapData> maps = new ArrayList<>();
    public ArrayList<AssetData> assets = new ArrayList<>();
}
