package com.thoughtworks.paranamer;

import junit.framework.TestCase;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;

import com.thoughtworks.paranamer.Paranamer;

public class ParanamerExampleTestCase extends TestCase {


    //Ignore this. You, the end user, will use the Ant task to generate parameter names.
    protected void setUp() throws Exception {
        Paranamer paranamer = new Paranamer();
        String parameterSignatures = paranamer.generate(new File(".").getAbsolutePath() + "/src/java");
        paranamer.write(new File(".").getAbsolutePath() + "/classes/", parameterSignatures);
    }


    // An example of a test that looks something up by it's parameter names

    public void testMethodCanBeRetrievedByParameterNames() throws IOException, NoSuchMethodException {
        Method method = new Paranamer().lookup(Paranamer.class.getClassLoader(), "com.thoughtworks.paranamer.Paranamer", "lookup", "classLoader,className,methName,paramNames");
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

    // You also have checkedLookup() and uncheckedLookup() for variants of lookup that throw, rather than
    // return null


}
