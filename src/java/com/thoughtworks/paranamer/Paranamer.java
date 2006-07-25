package com.thoughtworks.paranamer;

import java.lang.reflect.Method;

public interface Paranamer {

    /**
     * Lookup a method, and throw a ParanamerException if its not there
     */
    public Method checkedLookup(ClassLoader classLoader, String className, String methodName, String paramNames) throws ParanamerException;

    /**
     * Lookup a method, and throw a ParanamerRuntimeException if its not there
     */
    public Method uncheckedLookup(ClassLoader classLoader, String className, String methodName, String paramNames);

    /**
     *
     * Lookup a method, and return null if its not there
     *
     */
    public Method lookup(ClassLoader classLoader, String className, String methodName, String paramNames);

    /**
     *
     * Lookup parameter names choices, for class/method only
     *
     */
    public String[] lookupParameterNames(ClassLoader classLoader, String className, String methodName);




}
