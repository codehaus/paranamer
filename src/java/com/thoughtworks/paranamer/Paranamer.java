package com.thoughtworks.paranamer;

import java.io.*;
import java.lang.reflect.Method;
import java.util.StringTokenizer;

public class Paranamer {

    public Method checkedLookup(ClassLoader classLoader, String className, String methodName, String paramNames) throws ParanamerException {
        Method method = lookup(classLoader, className , methodName, paramNames);
        if (method == null) {
            throw new ParanamerException("Paranamer could not find method signature");
        }
        return method;
    }

    public Method uncheckedLookup(ClassLoader classLoader, String className, String methodName, String paramNames) {
        Method method = lookup(classLoader, className , methodName, paramNames);
        if (method == null) {
            throw new ParanamerRuntimeException("Paranamer could not find method signature");
        }
        return method;
    }

    /**
     * Copy the body of the method to wherever you want to - it means you won't have to rely on
     * one more jar in your app.
     *
     * @previousParamNames classLoader,c,m,p
     */
    public Method lookup(ClassLoader classLoader, String className, String methodName, String paramNames) {
        InputStream resourceAsStream = classLoader.getResourceAsStream("META-INF/ParameterNames.txt");
        try {
            if (resourceAsStream == null) {
                return null;
            }
            InputStreamReader inputStreamReader = new InputStreamReader(resourceAsStream);
            LineNumberReader lineReader = new LineNumberReader(inputStreamReader);
            String line = readLine(lineReader);
            String classAndMethodAndParamNames = className + " " + methodName + " " + paramNames;
            Class loadedClazz = null;
            try {
                loadedClazz = classLoader.loadClass(className);
            } catch (ClassNotFoundException e) {
                return null; // or could throw a/the exception
            }
            while (line != null) {
                if (line.startsWith(classAndMethodAndParamNames)) {
                    StringTokenizer st = new StringTokenizer(line);
                    st.nextToken(); // className
                    st.nextToken(); // methodName
                    st.nextToken(); // parameter names - not needed again
                    String methodParamTypes = st.nextToken();
                    Method methods[] = loadedClazz.getMethods();
                    for (int i = 0; i < methods.length; i++) {
                        Method method = methods[i];
                        Class[] parameters = method.getParameterTypes();
                        String paramTypes = "";
                        if (method.getName().equals(methodName)) {
                            for (int k = 0; k < parameters.length; k++) {
                                paramTypes = paramTypes + parameters[k].getName();
                                paramTypes = paramTypes + ((k + 1 < parameters.length) ? "," : "");
                            }
                            if (paramTypes.equals(methodParamTypes)) {
                                return method;
                            }
                        }
                    }
                }
                line = readLine(lineReader);
            }
        } finally {
            try {
                if (resourceAsStream != null) {
                    resourceAsStream.close();
                }
            } catch (IOException e) {
            }
        }
        return null;
    }

    private String readLine(LineNumberReader lineReader) {
        try {
            return lineReader.readLine();
        } catch (IOException e) {
            return null;
        }
    }
}
