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

package com.thoughtworks.paranamer;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import junit.framework.TestCase;

/**
 * 
 * @author Paul Hammant
 * @author Mauro Talevi
 * @author Guilherme Silveira
 */
public abstract class AbstractParanamerTestCase extends TestCase {

    protected Paranamer paranamer;

    public void testLookupParanamerCanIndicateAbleToGetParameterNames()
            throws IOException {
        int x = paranamer.areParameterNamesAvailable(
                DefaultParanamer.class, "lookupParameterNames");
        assertEquals(Paranamer.PARAMETER_NAMES_FOUND, x);
    }

    public void testLookupParanamerCanIndicateThatUnableToGetParameterNamesForRealClassButBogusMethod()
            throws IOException {
        int x = paranamer.areParameterNamesAvailable(
                DefaultParanamer.class, "fooo");
        assertEquals(Paranamer.NO_PARAMETER_NAMES_FOR_CLASS_AND_MEMBER, x);
    }

    public void testLookupParameterNamesForMethodWhenNoArg() throws Exception {
        Method method = DefaultParanamer.class.getMethod("toString", new Class[0]);
        String[] names = paranamer.lookupParameterNames(method);
        assertThatParameterNamesMatch("", names);
    }

    public void testLookupParameterNamesForConstructorWithStringArg() throws Exception {
        Constructor ctor = ParameterNamesNotFoundException.class.getConstructor(new Class[] {String.class});
        String[] names = paranamer.lookupParameterNames(ctor);
        assertThatParameterNamesMatch("message", names);
    }

    public void testLookupParameterNamesForPrivateMethod() throws Exception {
        Method m = DefaultParanamer.class.getDeclaredMethod("getParameterTypeName", new Class[] {Class.class});
        String[] names = paranamer.lookupParameterNames(m);
        assertThatParameterNamesMatch("cls", names);
    }

    public void testLookupParameterNamesForInterfaceMethod() throws Exception {
        Method m = Paranamer.class.getDeclaredMethod("areParameterNamesAvailable", new Class[] {Class.class, String.class});
        String[] names = paranamer.lookupParameterNames(m);
        assertThatParameterNamesMatch("clazz,constructorOrMethodName", names);
    }

    protected void assertThatParameterNamesMatch(String csv, String[] names) {
        assertEquals(csv, toCSV(names));
    }

    private String toCSV(String[] names) {
        assertNotNull(names);
        StringBuffer sb = new StringBuffer();
        for ( int i = 0; i < names.length; i++ ){
            sb.append(names[i]);
            if ( i < names.length -1 ){
                sb.append(",");
            }            
        }
        return sb.toString();
    }
}
