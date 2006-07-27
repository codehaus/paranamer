package com.thoughtworks.paranamer;

import junit.framework.TestCase;

import java.io.IOException;
import java.lang.reflect.Method;

public class UncheckedParanamerTestCase extends AbstractParanamerTestCase {

    public void testUncheckedMethodRetrievalFailure() throws IOException {
        try {
            new UncheckedParanamer().uncheckedLookup(Paranamer.class.getClassLoader(), "com.thoughtworks.paranamer.ParanamerGeneration", "generate", "hello,goodbye");
            fail("shoulda barfed");
        } catch (ParanamerRuntimeException e) {
            // expected
        }
    }

    public void testMethodCanBeRetrievedByParameterNamesViaUnCheckedLookup() throws IOException, NoSuchMethodException, ParanamerException {
        Method method = new UncheckedParanamer().uncheckedLookup(Paranamer.class.getClassLoader(), "com.thoughtworks.paranamer.ParanamerImpl", "lookup", "classLoader,className,methodName,paramNames");
        assertEquals(ParanamerImpl.class.getMethod("lookup", new Class[]{ClassLoader.class, String.class, String.class, String.class}), method);
    }


}
