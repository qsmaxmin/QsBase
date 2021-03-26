package com.qsmaxmin.qsbase.plugin.event;

import java.util.HashSet;

/**
 * @CreateBy qsmaxmin
 * @Date 2021/3/25 17:03
 * @Description
 */
public abstract class EventHandler {
    private HashSet<EventHandler> parent;

    protected abstract void execute(Object data);

    protected abstract Class getParamsClass();

    final void setParent(HashSet<EventHandler> hashSet) {
        parent = hashSet;
    }

    final HashSet<EventHandler> getParent() {
        return parent;
    }
}
