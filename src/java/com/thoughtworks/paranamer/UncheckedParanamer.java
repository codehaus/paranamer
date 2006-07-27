package com.thoughtworks.paranamer;

import java.lang.reflect.Method;

public class UncheckedParanamer {

    private Paranamer delegate;

    public UncheckedParanamer(Paranamer delegate) {
        this.delegate = delegate;
    }

    public UncheckedParanamer() {
        this.delegate = new ParanamerImpl();
    }


    public Method uncheckedMethodLookup(ClassLoader classLoader, String className, String methodName, String paramNames) {
        Method method = delegate.lookupMethod(classLoader, className, methodName, paramNames);
        if (method == null) {
            throw new ParanamerRuntimeException("Paranamer could not find method signature");
        }
        return method;
    }



}
