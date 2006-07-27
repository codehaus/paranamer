package com.thoughtworks.paranamer;

import junit.framework.TestCase;

import java.io.File;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.FileReader;

public class ParanamerTestCase extends TestCase {

    String allParameters =
            "com.thoughtworks.paranamer.CachingParanamer CachingParanamer\n" +
                    "com.thoughtworks.paranamer.CachingParanamer CachingParanamer paranamer com.thoughtworks.paranamer.Paranamer\n" +
                    "com.thoughtworks.paranamer.CachingParanamer lookupParameterNames classLoader,className,methodName java.lang.ClassLoader,java.lang.String,java.lang.String\n" +
                    "com.thoughtworks.paranamer.CachingParanamer checkedLookup classLoader,className,methodName,paramNames java.lang.ClassLoader,java.lang.String,java.lang.String,java.lang.String\n" +
                    "com.thoughtworks.paranamer.CachingParanamer lookup classLoader,className,methodName,paramNames java.lang.ClassLoader,java.lang.String,java.lang.String,java.lang.String\n" +
                    "com.thoughtworks.paranamer.CachingParanamer uncheckedLookup classLoader,className,methodName,paramNames java.lang.ClassLoader,java.lang.String,java.lang.String,java.lang.String\n" +
                    "com.thoughtworks.paranamer.CheckedParanamer CheckedParanamer\n" +
                    "com.thoughtworks.paranamer.CheckedParanamer CheckedParanamer delegate com.thoughtworks.paranamer.Paranamer\n" +
                    "com.thoughtworks.paranamer.CheckedParanamer checkedLookup classLoader,className,methodName,paramNames java.lang.ClassLoader,java.lang.String,java.lang.String,java.lang.String\n" +
                    "com.thoughtworks.paranamer.ParanamerException ParanamerException message java.lang.String\n" +
                    "com.thoughtworks.paranamer.ParanamerGeneration generate sourcePath java.lang.String\n" +
                    "com.thoughtworks.paranamer.ParanamerGeneration write outputPath,parameterText java.lang.String,java.lang.String\n" +
                    "com.thoughtworks.paranamer.ParanamerImpl lookupParameterNames classLoader,className,methodName java.lang.ClassLoader,java.lang.String,java.lang.String\n" +
                    "com.thoughtworks.paranamer.ParanamerImpl lookup classLoader,c,m,p java.lang.ClassLoader,java.lang.String,java.lang.String,java.lang.String\n" +
                    "com.thoughtworks.paranamer.ParanamerImpl lookup classLoader,className,methodName,paramNames java.lang.ClassLoader,java.lang.String,java.lang.String,java.lang.String\n" +
                    "com.thoughtworks.paranamer.ParanamerRuntimeException ParanamerRuntimeException message java.lang.String\n" +
                    "com.thoughtworks.paranamer.ParanamerTask execute\n" +
                    "com.thoughtworks.paranamer.ParanamerTask setOutputPath outputPath java.lang.String\n" +
                    "com.thoughtworks.paranamer.ParanamerTask setSourcePath sourcePath java.lang.String\n" +
                    "com.thoughtworks.paranamer.UncheckedParanamer UncheckedParanamer\n" +
                    "com.thoughtworks.paranamer.UncheckedParanamer UncheckedParanamer delegate com.thoughtworks.paranamer.Paranamer\n" +
                    "com.thoughtworks.paranamer.UncheckedParanamer uncheckedLookup classLoader,className,methodName,paramNames java.lang.ClassLoader,java.lang.String,java.lang.String,java.lang.String\n";
    String expected1 = "com.thoughtworks.paranamer.Paranamer lookup clazz,classMethodAndParamNames java.lang.Class,java.lang.String\n";

    private String parameterSignatures;

    protected void setUp() throws Exception {
        parameterSignatures = new ParanamerGeneration().generate(new File(".").getAbsolutePath() + "/src/java");
    }

    public void testGenerationOfParamNameDataDoesSo() {
        assertEquals(allParameters, parameterSignatures);
    }

    public void testWritingOfParamNameDataWorks() throws IOException {
        File dir = new File("target/classes/");
        dir.mkdirs();
        new ParanamerGeneration().write(dir.getAbsolutePath(), allParameters);
        String file = new File("target/classes/META-INF/ParameterNames.txt").getAbsolutePath();
        assertTrue(new File(file).exists());
        assertEquals("format version 1.0",
                new LineNumberReader(new FileReader(file)).readLine());
    }

    public void testMethodCantBeRetrievedIfItAintThere() throws IOException {
        Object method = new ParanamerImpl().lookupMethod(Paranamer.class.getClassLoader(), "com.thoughtworks.paranamer.ParanamerGeneration", "generate", "hello,goodbye");
        assertNull(method);
    }

    public void testBogusClassEndsLookup() throws IOException {
        Object method = new ParanamerImpl().lookupMethod(Paranamer.class.getClassLoader(), "foo.Bar", "generate", "hello,goodbye");
        assertNull(method);
    }

    public void testMissingMetaInfEndsLookup() throws IOException {
        File file = new File("target/classes/META-INF/ParameterNames.txt");
        file.delete();
        assertFalse(file.exists());
        Object method = new ParanamerImpl().lookupMethod(Paranamer.class.getClassLoader(), "com.thoughtworks.paranamer.ParanamerGeneration", "generate", "sourcePath,rootPackage");
        assertNull(method);
    }

    public void testMethodCantBeRetrievedForClassThatAintThere() throws IOException {
        Object method = new ParanamerImpl().lookupMethod(Paranamer.class.getClassLoader(), "paranamer.Footle", "generate", "sourcePath,rootPackage");
        assertNull(method);
    }

    public void testMethodRetrievalFailureIfNoParametersTextFile() throws IOException {
        new File("/Users/paul/scm/oss/Paranamer/classes/META-INF/ParameterNames.txt").delete();
        Object method = new ParanamerImpl().lookupMethod(Paranamer.class.getClassLoader(), "com.thoughtworks.paranamer.ParanamerGeneration", "generate", "hello,goodbye");
        assertNull(method);
    }


}
