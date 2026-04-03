package com.bobsgame.client.engine.ecs.components;

import com.bobsgame.client.engine.ecs.Component;
import com.bobsgame.shared.eventsheet.EventSheet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EventSheetComponent extends Component {
    public final String typeName = "EventSheet";
    public ArrayList<EventSheet> eventSheets = new ArrayList<>();
    public Map<String, Object> variables = new HashMap<>();

    @Override
    public String getTypeName() {
        return typeName;
    }
}
