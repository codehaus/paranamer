package com.thoughtworks.paranamer;

import junit.framework.TestCase;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;

import com.thoughtworks.paranamer.Paranamer;

public class ParanamerExampleTestCase extends TestCase {


    //Ignore this. You, the end user, will use the Ant task to generate parameter names.
    protected void setUp() throws Exception {
        ParanamerGeneration paranamerGeneration = new ParanamerGeneration();
        String parameterSignatures = paranamerGeneration.generate(new File(".").getAbsolutePath() + "/src/java");
        paranamerGeneration.write(new File(".").getAbsolutePath() + "/target/classes/", parameterSignatures);
    }


    // An example of a test that looks something up by it's parameter names

    public void testMethodCanBeRetrievedByParameterNames() throws IOException, NoSuchMethodException {
        Method method = new Paranamer().lookup(Paranamer.class.getClassLoader(), "com.thoughtworks.paranamer.Paranamer", "lookup", "classLoader,className,methodName,paramNames");
        assertEquals(Paranamer.class.getMethod("lookup", new Class[]{ClassLoader.class, String.class, String.class, String.class}), method);
    }

    // An example of a test that looks something up by it's OLD parameter names
    // These were encoded via a doclet tag on the method in question:
    //
    //   @previousParamNames clazz,cmapn


    public void testMethodCanBeRetrievedByParameterNamesPreviouslyUsed() throws IOException, NoSuchMethodException {
        Method method = new Paranamer().lookup(Paranamer.class.getClassLoader(), "com.thoughtworks.paranamer.Paranamer", "lookup", "classLoader,c,m,p");
        assertEquals(Paranamer.class.getMethod("lookup", new Class[]{ClassLoader.class, String.class, String.class, String.class}), method);
    }

    // don't forget, you can copy the lookup() code into your project as it's public domain, if you want to use
    // the Paranamer technology without an extra jar.


    // An example of a test that looks something up by it's parameter names

    public void testMethodCanBeRetrievedByParameterNamesViaCheckedLookup() throws IOException, NoSuchMethodException, ParanamerException {
        Method method = new Paranamer().checkedLookup(Paranamer.class.getClassLoader(), "com.thoughtworks.paranamer.Paranamer", "lookup", "classLoader,className,methodName,paramNames");
        assertEquals(Paranamer.class.getMethod("lookup", new Class[]{ClassLoader.class, String.class, String.class, String.class}), method);
    }

    // An example of a test that looks something up by it's parameter names

    public void testMethodCanBeRetrievedByParameterNamesViaUncheckedLookup() throws IOException, NoSuchMethodException {
        Method method = new Paranamer().uncheckedLookup(Paranamer.class.getClassLoader(), "com.thoughtworks.paranamer.Paranamer", "lookup", "classLoader,className,methodName,paramNames");
        assertEquals(Paranamer.class.getMethod("lookup", new Class[]{ClassLoader.class, String.class, String.class, String.class}), method);
    }


}
