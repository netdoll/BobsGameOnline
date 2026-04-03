package com.bobsgame.shared.eventsheet;

import java.util.ArrayList;
import java.util.Map;

public class EventSheet {
    public static class EventCondition {
        public String type;
        public Map<String, Object> params;
    }

    public static class EventAction {
        public String type;
        public Map<String, Object> params;
    }

    public static class EventBlock {
        public ArrayList<EventCondition> conditions = new ArrayList<>();
        public ArrayList<EventAction> actions = new ArrayList<>();
        public ArrayList<EventBlock> subEvents = new ArrayList<>();
    }

    public String name = "New Event Sheet";
    public ArrayList<EventBlock> blocks = new ArrayList<>();
}
