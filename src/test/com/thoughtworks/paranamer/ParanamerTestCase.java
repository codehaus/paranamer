package com.thoughtworks.paranamer;

import junit.framework.TestCase;

import java.io.File;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.FileReader;

import com.thoughtworks.paranamer.Paranamer;
import com.thoughtworks.paranamer.ParanamerException;
import com.thoughtworks.paranamer.ParanamerRuntimeException;

public class ParanamerTestCase extends TestCase {

    String allParameters =
            "com.thoughtworks.paranamer.Paranamer generate sourcePath java.lang.String\n" +
            "com.thoughtworks.paranamer.Paranamer checkedLookup classLoader,className,methName,paramNames java.lang.ClassLoader,java.lang.String,java.lang.String,java.lang.String\n" +
            "com.thoughtworks.paranamer.Paranamer lookup classLoader,c,m,p java.lang.ClassLoader,java.lang.String,java.lang.String,java.lang.String\n" +
            "com.thoughtworks.paranamer.Paranamer lookup classLoader,className,methName,paramNames java.lang.ClassLoader,java.lang.String,java.lang.String,java.lang.String\n" +
            "com.thoughtworks.paranamer.Paranamer uncheckedLookup classLoader,className,methName,paramNames java.lang.ClassLoader,java.lang.String,java.lang.String,java.lang.String\n" +
            "com.thoughtworks.paranamer.Paranamer write outputPath,parameterText java.lang.String,java.lang.String\n" +
            "com.thoughtworks.paranamer.ParanamerException ParanamerException string java.lang.String\n" +
            "com.thoughtworks.paranamer.ParanamerRuntimeException ParanamerRuntimeException string java.lang.String\n" +
            "com.thoughtworks.paranamer.ParanamerTask execute  \n" +
            "com.thoughtworks.paranamer.ParanamerTask setOutputPath outputPath java.lang.String\n" +
            "com.thoughtworks.paranamer.ParanamerTask setSourcePath sourcePath java.lang.String\n";
    String expected1 = "com.thoughtworks.paranamer.Paranamer lookup clazz,classMethodAndParamNames java.lang.Class,java.lang.String\n";

    private String parameterSignatures;

    protected void setUp() throws Exception {
        parameterSignatures = new Paranamer().generate(new File(".").getAbsolutePath() + "/src/java");
    }

    public void testGenerationOfParamNameDataDoesSo() {
        assertEquals(allParameters, parameterSignatures);
    }

    public void testWritingOfParamNameDataWorks() throws IOException {
        new Paranamer().write(new File(".").getAbsolutePath() + "/classes/", allParameters);
        String file = new File(".").getAbsolutePath() + "/classes/ParameterList.txt";
        assertTrue(new File(file).exists());
        assertEquals("com.thoughtworks.paranamer.Paranamer generate sourcePath java.lang.String", new LineNumberReader(new FileReader(file)).readLine());
    }

    public void testMethodCantBeRetrievedIfItAintThere() throws IOException {
        Object method = new Paranamer().lookup(Paranamer.class.getClassLoader(), "com.thoughtworks.paranamer.Paranamer generate hello,goodbye", null, null);
        assertNull(method);
    }

    public void testMethodCantBeRetrievedForClassThatAintThere() throws IOException {
        Object method = new Paranamer().lookup(Paranamer.class.getClassLoader(), "paranamer.Footle generate sourcePath,rootPackage", null, null);
        assertNull(method);
    }

    public void testCheckedMethodRetrievalFailure() throws IOException {
        try {
            new Paranamer().checkedLookup(Paranamer.class.getClassLoader(), "com.thoughtworks.paranamer.Paranamer","generate","hello,goodbye");
            fail("shoulda barfed");
        } catch (ParanamerException e) {
            // expected
        }
    }

    public void testUncheckedMethodRetrievalFailure() throws IOException {
        try {
            new Paranamer().uncheckedLookup(Paranamer.class.getClassLoader(), "com.thoughtworks.paranamer.Paranamer", "generate", "hello,goodbye");
            fail("shoulda barfed");
        } catch (ParanamerRuntimeException e) {
            // expected
        }
    }

    public void testMethodRetrievalFailureIfNoParametersTextFile() throws IOException {
        new File("/Users/paul/scm/oss/Paranamer/classes/ParameterList.txt").delete();
        Object method = new Paranamer().lookup(Paranamer.class.getClassLoader(), "com.thoughtworks.paranamer.Paranamer", "generate", "hello,goodbye");
        assertNull(method);
    }


}
