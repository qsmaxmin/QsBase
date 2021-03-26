package com.qsmaxmin.qsbase.plugin.event;

import java.util.HashMap;
import java.util.HashSet;

/**
 * @CreateBy qsmaxmin
 * @Date 2020/8/18 11:55
 * @Description
 */
public class EventHelper {
    private static final HashMap<Class, HashSet<EventHandler>> itemHolder   = new HashMap<>();
    private static final HashMap<Object, EventHandler[]>       targetHolder = new HashMap<>();

    public static void register(Object target, EventHandler[] items) {
        if (target == null || items == null || items.length == 0) {
            return;
        }
        targetHolder.put(target, items);
        for (EventHandler item : items) {
            Class paramClass = item.getParamsClass();
            HashSet<EventHandler> hashSet = itemHolder.get(paramClass);
            if (hashSet == null) {
                hashSet = new HashSet<>();
                itemHolder.put(paramClass, hashSet);
            }
            item.setParent(hashSet);
            hashSet.add(item);
        }
    }

    public static void unregister(Object target) {
        if (target == null) return;
        EventHandler[] items = targetHolder.remove(target);
        if (items != null && items.length > 0) {
            for (EventHandler item : items) {
                HashSet<EventHandler> set = item.getParent();
                set.remove(item);
                if (set.isEmpty()) {
                    itemHolder.remove(item.getParamsClass());
                }
            }
        }
    }

    public static void eventPost(Object data) {
        if (data == null) return;
        HashSet<EventHandler> eventItems = itemHolder.get(data.getClass());
        if (eventItems == null || eventItems.isEmpty()) return;
        for (EventHandler item : eventItems) {
            item.execute(data);
        }
    }
}
