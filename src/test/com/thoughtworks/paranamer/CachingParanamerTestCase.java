package com.thoughtworks.paranamer;

import junit.framework.TestCase;

import java.lang.reflect.Method;

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


}
