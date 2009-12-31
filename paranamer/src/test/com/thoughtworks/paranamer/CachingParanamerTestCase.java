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

import java.lang.reflect.Method;
import java.lang.reflect.AccessibleObject;
import java.util.Arrays;

import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class CachingParanamerTestCase {
    private Paranamer paranamer;
    private int count = 0;

    @Before
    public void setUp() throws Exception {
        paranamer = new Paranamer() {

            public String[] lookupParameterNames(AccessibleObject methodOrConstructor) {
                return lookupParameterNames(methodOrConstructor, true);
            }

            public String[] lookupParameterNames(AccessibleObject methodOrCtor, boolean throwExceptionIfMissing) {
                count++;
                return new String[]{"foo","bar"};
            }

        };
    }


     @Test
     public void testLookupOfParameterNamesForMethod() {
        Paranamer cachingParanamer = new CachingParanamer(paranamer);
        String[] paramNames = cachingParanamer.lookupParameterNames((Method)null);
        Assert.assertEquals(Arrays.asList(new String[]{"foo", "bar"}), Arrays.asList(paramNames));
        Assert.assertEquals(1, count);

        // cache hit
        paramNames = cachingParanamer.lookupParameterNames((Method)null);
        Assert.assertEquals(Arrays.asList(new String[]{"foo", "bar"}), Arrays.asList(paramNames));
        Assert.assertEquals(1, count);
    }
    
}
