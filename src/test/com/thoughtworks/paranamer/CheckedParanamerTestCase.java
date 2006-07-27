package com.thoughtworks.paranamer;

import java.io.IOException;
import java.lang.reflect.Method;

public class CheckedParanamerTestCase extends AbstractParanamerTestCase {


    public void testCheckedMethodRetrievalFailure() throws IOException {
        try {
            new CheckedParanamer().checkedMethodLookup(Paranamer.class.getClassLoader(), "com.thoughtworks.paranamer.ParanamerGeneration","generate","hello,goodbye");
            fail("shoulda barfed");
        } catch (ParanamerException e) {
            // expected
        }
    }

    public void testMethodCanBeRetrievedByParameterNamesViaCheckedLookup() throws IOException, NoSuchMethodException, ParanamerException {
        Method method = new CheckedParanamer().checkedMethodLookup(Paranamer.class.getClassLoader(), "com.thoughtworks.paranamer.ParanamerImpl", "lookupMethod", "classLoader,className,methodName,paramNames");
        assertEquals(ParanamerImpl.class.getMethod("lookupMethod", new Class[]{ClassLoader.class, String.class, String.class, String.class}), method);
    }

}
