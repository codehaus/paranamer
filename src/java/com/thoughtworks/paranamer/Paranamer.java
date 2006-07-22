package com.thoughtworks.paranamer;

import java.lang.reflect.Method;

public interface Paranamer {

    public Method checkedLookup(ClassLoader classLoader, String className, String methodName, String paramNames) throws ParanamerException;

    public Method uncheckedLookup(ClassLoader classLoader, String className, String methodName, String paramNames);

    /**
     * Copy the body of the method to wherever you want to - it means you won't have to rely on
     * one more jar in your app.
     *
     * @previousParamNames classLoader,c,m,p
     */
    public Method lookup(ClassLoader classLoader, String className, String methodName, String paramNames);
}
