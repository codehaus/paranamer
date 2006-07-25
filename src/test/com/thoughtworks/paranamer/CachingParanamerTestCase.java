package com.thoughtworks.paranamer;

import junit.framework.TestCase;

import java.lang.reflect.Method;
import java.io.File;
import java.io.IOException;

public class CachingParanamerTestCase extends TestCase {

    Method method;
    Paranamer paranamer;
    int count = 0;

    protected void setUp() throws Exception {
        method = String.class.getMethod("toString", new Class[0]);

        paranamer = new Paranamer() {
            public Method checkedLookup(ClassLoader classLoader, String className, String methodName, String paramNames) throws ParanamerException {
                count++;
                return method;
            }

            public Method uncheckedLookup(ClassLoader classLoader, String className, String methodName, String paramNames) {
                count++;
                return method;
            }

            public Method lookup(ClassLoader classLoader, String className, String methodName, String paramNames) {
                count++;
                return method;
            }

            public String[] lookupParameterNames(ClassLoader classLoader, String className, String methodName) {
                return new String[] {"foo,bar"};
            }
        };

    }

    public void testCachedOnLookup() {
        Paranamer cachingParanamer = new CachingParanamer(paranamer);
        Method m = cachingParanamer.lookup(this.getClass().getClassLoader(), "huey", "duey", "luis");
        assertEquals(method, m);
        assertEquals(1, count);
        m = cachingParanamer.lookup(this.getClass().getClassLoader(), "huey", "duey", "luis");
        assertEquals(method, m);
        assertEquals(1, count);
    }

    public void testNotCachedIfDiffeent() {
        Paranamer cachingParanamer = new CachingParanamer(paranamer);
        Method m = cachingParanamer.lookup(this.getClass().getClassLoader(), "huey", "duey", "luis");
        assertEquals(method, m);
        assertEquals(1, count);
        m = cachingParanamer.lookup(this.getClass().getClassLoader(), "huey", "duey", "horatio");
        assertEquals(method, m);
        assertEquals(2, count);
    }

    public void testForcedNullOnCheckedLookup() {
        method = null;
        Paranamer cachingParanamer = new CachingParanamer(paranamer);
        Method m = cachingParanamer.lookup(this.getClass().getClassLoader(), "huey", "duey", "luis");
        assertNull(m);
    }



    public void testCachedOnCheckedLookup() throws ParanamerException {
        Paranamer cachingParanamer = new CachingParanamer(paranamer);
        Method m = cachingParanamer.checkedLookup(this.getClass().getClassLoader(), "huey", "duey", "luis");
        assertEquals(method, m);
        assertEquals(1, count);
        m = cachingParanamer.lookup(this.getClass().getClassLoader(), "huey", "duey", "luis");
        assertEquals(method, m);
        assertEquals(1, count);
    }

    public void testNotCachedIfDiffeentOnCheckedLookup() throws ParanamerException {
        Paranamer cachingParanamer = new CachingParanamer(paranamer);
        Method m = cachingParanamer.checkedLookup(this.getClass().getClassLoader(), "huey", "duey", "luis");
        assertEquals(method, m);
        assertEquals(1, count);
        m = cachingParanamer.lookup(this.getClass().getClassLoader(), "huey", "duey", "horatio");
        assertEquals(method, m);
        assertEquals(2, count);
    }


    public void testForcedBarfOnCheckedLookup() {
        method = null;
        Paranamer cachingParanamer = new CachingParanamer(paranamer);
        try {
            cachingParanamer.checkedLookup(this.getClass().getClassLoader(), "huey", "duey", "luis");
            fail("shoulda barfed");
        } catch (ParanamerException e) {
            // expected
        }
    }

    public void testCachedOnUncheckedLookup() {
        Paranamer cachingParanamer = new CachingParanamer(paranamer);
        Method m = cachingParanamer.uncheckedLookup(this.getClass().getClassLoader(), "huey", "duey", "luis");
        assertEquals(method, m);
        assertEquals(1, count);
        m = cachingParanamer.lookup(this.getClass().getClassLoader(), "huey", "duey", "luis");
        assertEquals(method, m);
        assertEquals(1, count);
    }

    public void testNotCachedIfDiffeentOnUncheckedLookup() {
        Paranamer cachingParanamer = new CachingParanamer(paranamer);
        Method m = cachingParanamer.uncheckedLookup(this.getClass().getClassLoader(), "huey", "duey", "luis");
        assertEquals(method, m);
        assertEquals(1, count);
        m = cachingParanamer.lookup(this.getClass().getClassLoader(), "huey", "duey", "horatio");
        assertEquals(method, m);
        assertEquals(2, count);
    }

    public void testForcedBarfOnUncheckedLookup() {
        method = null;
        Paranamer cachingParanamer = new CachingParanamer(paranamer);
        try {
            cachingParanamer.uncheckedLookup(this.getClass().getClassLoader(), "huey", "duey", "luis");
            fail("shoulda barfed");
        } catch (ParanamerRuntimeException e) {
            // expected
        }
    }


    public void testCanChainToDefaultImpl() throws IOException {
        //setup
        ParanamerGeneration paranamerGeneration = new ParanamerGeneration();
        String parameterSignatures = paranamerGeneration.generate(new File(".").getAbsolutePath() + "/src/java");
        paranamerGeneration.write(new File(".").getAbsolutePath() + "/target/classes/", parameterSignatures);

        Paranamer cachingParanamer = new CachingParanamer();
        Method m = cachingParanamer.lookup(Paranamer.class.getClassLoader(), "com.thoughtworks.paranamer.ParanamerImpl", "lookup", "classLoader,className,methodName,paramNames");
        assertNotNull(m);
    }

    public void testLookupOfParameterNames() {

        Paranamer cachingParanamer = new CachingParanamer(paranamer);
        String[] paramNameChoices = cachingParanamer.lookupParameterNames(Paranamer.class.getClassLoader(), "com.thoughtworks.paranamer.ParanamerImpl", "lookup");
        assertEquals(1, paramNameChoices.length);
        assertEquals("foo,bar", paramNameChoices[0]);
    }


}
