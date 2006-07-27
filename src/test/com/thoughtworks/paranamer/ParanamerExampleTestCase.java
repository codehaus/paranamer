package com.thoughtworks.paranamer;

import junit.framework.TestCase;

import java.io.IOException;
import java.lang.reflect.Method;


public class ParanamerExampleTestCase extends TestCase {

    // An example of a test that looks something up by it's parameter names

    public void testMethodCanBeRetrievedByParameterNames() throws IOException, NoSuchMethodException {
        Method method = new ParanamerImpl().lookupMethod(Paranamer.class.getClassLoader(), "com.thoughtworks.paranamer.ParanamerImpl", "lookup", "classLoader,className,methodName,paramNames");
        assertEquals(ParanamerImpl.class.getMethod("lookup", new Class[]{ClassLoader.class, String.class, String.class, String.class}), method);
    }

    // An example of a test that looks ParameterNames up based on class/method only

    public void testParamerNameChoicesCanBeRetrievedForAMethodName() throws IOException, NoSuchMethodException {
        String[] paramNames = new ParanamerImpl().lookupParameterNames(Paranamer.class.getClassLoader(), "com.thoughtworks.paranamer.ParanamerImpl", "lookup");
        assertEquals(2, paramNames.length);
        assertEquals("classLoader,c,m,p", paramNames[0]);
        assertEquals("classLoader,className,methodName,paramNames", paramNames[1]);
    }



    // An example of a test that looks something up by it's OLD parameter names
    // These were encoded via a doclet tag on the method in question:
    //
    //   @previousParamNames clazz,cmapn
    public void testMethodCanBeRetrievedByParameterNamesPreviouslyUsed() throws IOException, NoSuchMethodException {
        Method method = new ParanamerImpl().lookupMethod(Paranamer.class.getClassLoader(), "com.thoughtworks.paranamer.ParanamerImpl", "lookup", "classLoader,c,m,p");
        assertEquals(ParanamerImpl.class.getMethod("lookup", new Class[]{ClassLoader.class, String.class, String.class, String.class}), method);
    }

    // don't forget, you can copy the lookup() code into your project as it's public domain, if you want to use
    // the Paranamer technology without an extra jar.

}
