package com.thoughtworks.paranamer;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

public class CachingParanamer implements Paranamer {

    private final Paranamer delegate;
    private WeakHashMap classLoaders = new WeakHashMap();

    public CachingParanamer(Paranamer paranamer) {
        delegate = paranamer;
    }

    public CachingParanamer() {
        delegate = new ParanamerImpl();
    }

    public synchronized Method checkedLookup(ClassLoader classLoader, String className, String methodName, String paramNames) throws ParanamerException {
        Method m = lookupMethod(classLoader, className, methodName, paramNames);
        if (m != null) {
            return m;
        } else {
            throw new ParanamerException("Paranamer could not find method signature");
        }
    }

    public Method uncheckedLookup(ClassLoader classLoader, String className, String methodName, String paramNames) {
        Method m = lookupMethod(classLoader, className, methodName, paramNames);
        if (m != null) {
            return m;
        } else {
            throw new ParanamerRuntimeException("Paranamer could not find method signature");
        }
    }

    public Method lookupMethod(ClassLoader classLoader, String className, String methodName, String paramNames) {
        String key = className + " " + methodName + " " + paramNames;
        Map map = (Map) classLoaders.get(classLoader);
        Method method;
        if (map != null) {
            method = (Method) map.get(key);
            if (method != null) {
                return method;
            }
        }
        if (map == null) {
            map = new HashMap();
            classLoaders.put(classLoader, map);
        }
        method = delegate.lookupMethod(classLoader, className, methodName, paramNames);
        map.put(key,method);
        return method;
    }

    public String[] lookupParameterNames(ClassLoader classLoader, String className, String methodName) {
        return delegate.lookupParameterNames(classLoader, className, methodName);
    }

}
