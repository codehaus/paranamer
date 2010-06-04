/***
 *
 * Copyright (c) 2007 Paul Hammant
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holders nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.thoughtworks.paranamer.generator;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;

import junit.framework.TestCase;

public class QDoxParanamerTestCase extends TestCase {

    private String root;

    protected void setUp() throws Exception {
        ParanamerGenerator generator = new QdoxParanamerGenerator();
        root = new File(".").getAbsolutePath();
        generator.processSourcePath(root + "/src/test", root + "/target/test-classes/");
    }

    public void testFoo() throws IOException, NoSuchFieldException, IllegalAccessException {

        FileInputStream fis = new FileInputStream(root + "/target/test-classes/com/thoughtworks/paranamer/generator/Elephant.class");
        byte[] bytes = new byte[4000];
        int read = fis.read(bytes);
        byte[] bytes2 = new byte[read];
        System.arraycopy(bytes,0,bytes2,0,read);

        MyClassLoader cl = new MyClassLoader();

        Class enhancedClazz = cl.defineEnhancerClass(bytes2, read);
        Field f = enhancedClazz.getField("__PARANAMER_DATA");
        assertNotNull(f);
        String s1 = ((String) f.get(null));
        String s2 = ("<init> java.util.Map map \n" +
                        "setMap java.util.Map map \n");
        assertEquals(s2, s1);
    }


    private static class MyClassLoader extends ClassLoader {
        public Class defineEnhancerClass(byte[] bytes, int length) {
            return defineClass("com.thoughtworks.paranamer.generator.Elephant", bytes, 0, bytes.length);
        }
    }
}