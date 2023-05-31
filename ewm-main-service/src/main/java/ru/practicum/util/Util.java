package ru.practicum.util;

import org.apache.commons.lang3.EnumUtils;
import ru.practicum.event.model.EventState;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.math.NumberUtils.toLong;

public class Util {
    public static List<Long> getListLongFromString(String inputString) {
        if (inputString == null || inputString.isBlank()) {
            return null;
        }
        List<Long> longList = new ArrayList<>();

        for (String s : inputString.split(",")) {
            if (toLong(s.trim()) != 0) {
                longList.add(toLong(s.trim()));
            }
        }
        return longList;
    }

    public static List<EventState> getListEventStateFromString(String inputString) {
        if (inputString == null || inputString.isBlank()) {
            return null;
        }
        List<EventState> eventStateList = new ArrayList<>();
        for (String es : inputString.split(",")) {
            if (EnumUtils.getEnum(EventState.class, es.trim()) != null) {
                eventStateList.add(EnumUtils.getEnum(EventState.class, es.trim()));
            }
        }
        return eventStateList;
    }
}
