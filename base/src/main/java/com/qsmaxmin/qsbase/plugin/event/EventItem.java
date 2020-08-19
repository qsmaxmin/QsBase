package com.qsmaxmin.qsbase.plugin.event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @CreateBy administrator
 * @Date 2020/8/18 14:11
 * @Description
 */
public class EventItem {
    private final Object target;
    private final String methodName;
    private final Class  paramClass;

    public EventItem(Object target, String methodName, Class paramClass) {
        this.target = target;
        this.methodName = methodName;
        this.paramClass = paramClass;
    }

    Object getTarget() {
        return target;
    }

    public void execute(Object data) {
        try {
            Method method = target.getClass().getDeclaredMethod(methodName, paramClass);
            method.invoke(target, data);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
