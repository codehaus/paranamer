package com.thoughtworks.paranamer;

import junit.framework.TestCase;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.NoSuchElementException;

public class DefaultParanamerTestCase extends TestCase {
    private DefaultParanamer paranamer;
    
    protected void setUp() throws Exception {
        paranamer = new DefaultParanamer();
    }

    public void testLookupMethodReturnsNullIfMethodNotFound()
            throws IOException {
        Object method = paranamer.lookupMethod(
                Paranamer.class.getClassLoader(),
                "com.thoughtworks.paranamer.QdoxParanamerGenerator",
                "generate", "hello,goodbye");
        assertNull(method);
    }

    public void testLookupMethodReturnsNullIfClassNotFound()
            throws IOException {
        Object method = paranamer.lookupMethod(
                Paranamer.class.getClassLoader(), "paranamer.Footle",
                "generate", "sourcePath,rootPackage");
        assertNull(method);
    }

    public void testLookupMethodEndsWithUnknownClass() throws IOException {
        Object method = paranamer.lookupMethod(
                Paranamer.class.getClassLoader(), "foo.Bar", "generate",
                "hello,goodbye");
        assertNull(method);
    }

    public void testLookupFailsIfResourceMissing() throws IOException {
        Paranamer paranamer = new DefaultParanamer("/inexistent/resource");
        try {
            paranamer.lookupMethod(Paranamer.class.getClassLoader(),
                    "com.thoughtworks.paranamer.QdoxParanamerGenerator",
                    "generate", "sourcePath,rootPackage");
            fail("Expected NoSuchElementException");
        } catch (NoSuchElementException e) {
            // expected
        }
    }

    public void testMethodWithNoArgsCanBeRetrievedByParameterNames()
            throws IOException, NoSuchMethodException {
        Method method = paranamer.lookupMethod(
                Paranamer.class.getClassLoader(),
                "com.thoughtworks.paranamer.ParanamerGeneratorMojo", "execute", "");
        assertEquals(ParanamerGeneratorMojo.class.getMethod("execute", new Class[0]),
                method);
    }

    public void testMethodWithNoArgsCanBeRetrievedAndShowNoParameterNames()
            throws IOException, NoSuchMethodException {
        String[] choices = paranamer.lookupParameterNames(Paranamer.class
                .getClassLoader(), "com.thoughtworks.paranamer.ParanamerGeneratorMojo",
                "execute");
        assertEquals(0, choices.length);
    }

    public void testLookupParameterNamesForMethod() throws Exception {
        Method method = QdoxParanamerGenerator.class.getMethod("write", new Class[] {String.class, String.class});
        String parameters = paranamer.lookupParameterNamesForMethod(method);

        assertEquals("outputPath,content", parameters);
    }

    public void testLookupParameterNamesForMethodWhenNoArg() throws Exception {
        Method method = DefaultParanamer.class.getMethod("toString", new Class[0]);
        String parameters = paranamer.lookupParameterNamesForMethod(method);

        assertEquals("", parameters);
    }

}