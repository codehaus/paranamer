package com.thoughtworks.paranamer.generator;

import junit.framework.TestCase;

import java.io.File;

public abstract class AbstractQDoxParanamerTestCase extends TestCase {

    //Ignore this. You, the end user, will use the Ant task to generate parameter names.
    protected void setUp() throws Exception {
        ParanamerGenerator generator = new QdoxParanamerGenerator();
        String parameterSignatures = generator.generate(new File(".").getAbsolutePath() + "/src/java");
        generator.write(new File(".").getAbsolutePath() + "/target/test-classes/", parameterSignatures);
    }

}