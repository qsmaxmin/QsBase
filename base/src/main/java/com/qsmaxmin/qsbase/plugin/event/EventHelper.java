package com.qsmaxmin.qsbase.plugin.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * @CreateBy qsmaxmin
 * @Date 2020/8/18 11:55
 * @Description
 */
@SuppressWarnings("rawtypes")
public class EventHelper {
    private static final HashMap<Class, HashSet<EventItem>> holder = new HashMap<>();

    public static void register(Object target, String[] methodNames, Class[] paramClasses) {
        if (target == null || methodNames == null || paramClasses == null
                || methodNames.length == 0 || methodNames.length != paramClasses.length) return;

        for (int i = 0; i < methodNames.length; i++) {
            String methodName = methodNames[i];
            Class paramClass = paramClasses[i];
            HashSet<EventItem> eventItems = holder.get(paramClass);
            if (eventItems == null) {
                eventItems = new HashSet<>();
                holder.put(paramClass, eventItems);
            }
            EventItem eventItem = new EventItem(target, methodName, paramClass);
            eventItems.add(eventItem);
        }
    }

    public static void unregister(Object target, Class... paramClasses) {
        if (target == null || paramClasses == null || paramClasses.length == 0) return;
        List<EventItem> list = null;
        for (Class paramClass : paramClasses) {
            HashSet<EventItem> eventItems = holder.get(paramClass);
            if (eventItems == null || eventItems.isEmpty()) return;

            for (EventItem item : eventItems) {
                if (item.getTarget() == target) {
                    if (list == null) list = new ArrayList<>();
                    list.add(item);
                }
            }

            if (list != null) {
                eventItems.removeAll(list);
                list.clear();
            }
        }
    }

    public static void eventPost(Object data) {
        if (data == null) return;
        HashSet<EventItem> eventItems = holder.get(data.getClass());
        if (eventItems == null || eventItems.isEmpty()) return;
        for (EventItem item : eventItems) {
            item.execute(data);
        }
    }
}
