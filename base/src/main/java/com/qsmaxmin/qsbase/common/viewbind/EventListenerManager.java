/*
 * Copyright (c) 2013. wyouflf (wyouflf@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.qsmaxmin.qsbase.common.viewbind;

import android.text.TextUtils;
import android.view.View;

import com.qsmaxmin.qsbase.common.viewbind.annotation.OnClick;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

final class EventListenerManager {

    private final static long            QUICK_EVENT_TIME_SPAN = 300;
    private final static HashSet<String> AVOID_QUICK_EVENT_SET = new HashSet<>(2);

    static {
        AVOID_QUICK_EVENT_SET.add("onClick");
        AVOID_QUICK_EVENT_SET.add("onItemClick");
    }

    private EventListenerManager() {
    }

    /**
     * k1: viewInjectInfo
     * k2: interface Type
     * value: listener
     */
    private final static DoubleKeyValueMap<ViewInfo, Class<?>, Object> listenerCache = new DoubleKeyValueMap<>();


    static void addEventMethod(ViewFinder finder, ViewInfo info, OnClick annotation, Object handler, String className, Method method) {
        View view = finder.findViewByInfo(info);
        if (view != null) {
            Class<?> listenerType = annotation.type();
            String listenerSetter = annotation.setter();
            if (TextUtils.isEmpty(listenerSetter)) {
                listenerSetter = "set" + listenerType.getSimpleName();
            }
            String methodName = annotation.method();
            boolean addNewMethod = false;
            Object listener = listenerCache.get(info, listenerType);
            DynamicHandler dynamicHandler;
            if (listener != null) {
                dynamicHandler = (DynamicHandler) Proxy.getInvocationHandler(listener);
                addNewMethod = handler.equals(dynamicHandler.getHandler());
                if (addNewMethod) {
                    dynamicHandler.addMethod(methodName, method);
                }
            }
            if (!addNewMethod) {
                dynamicHandler = new DynamicHandler(handler);
                dynamicHandler.addMethod(methodName, method);
                listener = Proxy.newProxyInstance(listenerType.getClassLoader(), new Class<?>[]{listenerType}, dynamicHandler);
                listenerCache.put(info, listenerType, listener);
            }
            Method setEventListenerMethod = null;
            try {
                setEventListenerMethod = view.getClass().getMethod(listenerSetter, listenerType);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            try {
                if (setEventListenerMethod != null) setEventListenerMethod.invoke(view, listener);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            throw new RuntimeException("Invalid @OnClick class name:" + className + "  method name:" + method.getName() + "." + info.value);
        }
    }

    private static class DynamicHandler implements InvocationHandler {
        private WeakReference<Object> handlerRef;
        private final  HashMap<String, Method> methodMap     = new HashMap<>(1);
        private static long                    lastClickTime = 0;

        DynamicHandler(Object handler) {
            this.handlerRef = new WeakReference<>(handler);
        }

        void addMethod(String name, Method method) {
            methodMap.put(name, method);
        }

        Object getHandler() {
            return handlerRef.get();
        }

        @Override public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Object handler = handlerRef.get();
            if (handler != null) {
                String eventMethod = method.getName();
                if ("toString".equals(eventMethod)) {
                    return DynamicHandler.class.getSimpleName();
                }
                method = methodMap.get(eventMethod);
                if (method == null && methodMap.size() == 1) {
                    for (Map.Entry<String, Method> entry : methodMap.entrySet()) {
                        if (TextUtils.isEmpty(entry.getKey())) {
                            method = entry.getValue();
                        }
                        break;
                    }
                }

                if (method != null) {
                    if (AVOID_QUICK_EVENT_SET.contains(eventMethod)) {
                        long timeSpan = System.currentTimeMillis() - lastClickTime;
                        if (timeSpan < QUICK_EVENT_TIME_SPAN) {
                            return null;
                        }
                        lastClickTime = System.currentTimeMillis();
                    }
                    try {
                        return method.invoke(handler, args);
                    } catch (Throwable ex) {
                        throw new RuntimeException("invoke method error:" + handler.getClass().getName() + "#" + method.getName(), ex);
                    }
                }
            }
            return null;
        }
    }
}
