package com.thoughtworks.paranamer;

import java.lang.reflect.Method;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

public class ParanamerImpl implements Paranamer {

    public Method checkedLookup(ClassLoader classLoader, String className, String methodName, String paramNames) throws ParanamerException {
        Method method = lookup(classLoader, className, methodName, paramNames);
        if (method == null) {
            throw new ParanamerException("Paranamer could not find method signature");
        }
        return method;
    }

    public Method uncheckedLookup(ClassLoader classLoader, String className, String methodName, String paramNames) {
        Method method = lookup(classLoader, className, methodName, paramNames);
        if (method == null) {
            throw new ParanamerRuntimeException("Paranamer could not find method signature");
        }
        return method;
    }

    /**
     * Lookup a method, and return null if its not there
     *
     * Copy the body of the method to wherever you want to - it means you won't have to rely on
     * one more jar in your app.
     *
     * @previousParamNames classLoader,c,m,p
     */
    public Method lookup(ClassLoader classLoader, String className, String methodName, String paramNames) {
        String mappings = getMappingsFromResource(classLoader.getResourceAsStream("META-INF/ParameterNames.txt"));
        String classAndMethodAndParamNames = className + " " + methodName + " " + paramNames + " ";
        int ix = mappings.indexOf(classAndMethodAndParamNames);
        if (ix != -1) {
            int start = ix + classAndMethodAndParamNames.length();
            int end = mappings.indexOf("\n", start + 1);
            String methodParamTypes = mappings.substring(start, end).trim();
            Class loadedClazz;
            try {
                loadedClazz = classLoader.loadClass(className);
            } catch (ClassNotFoundException e) {
                return null; // or could throw a/the exception
            }
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
        return null;
    }

    private String getMappingsFromResource(InputStream resourceAsStream) {
        StringBuffer paramMappingsBuffer = new StringBuffer();
        try {
            if (resourceAsStream == null) {
                return "";
            }
            InputStreamReader inputStreamReader = new InputStreamReader(resourceAsStream);
            LineNumberReader lineReader = new LineNumberReader(inputStreamReader);
            String line = readLine(lineReader);
            while (line != null) {
                paramMappingsBuffer.append(line).append("\n");
                line = readLine(lineReader);
            }
            return paramMappingsBuffer.toString();
        } finally {
            try {
                if (resourceAsStream != null) {
                    resourceAsStream.close();
                }
            } catch (IOException e) {
            }
        }
    }

    private String readLine(LineNumberReader lineReader) {
        try {
            return lineReader.readLine();
        } catch (IOException e) {
            return null;
        }
    }

    public String[] lookupParameterNames(ClassLoader classLoader, String className, String methodName) {
        String mappings = getMappingsFromResource(classLoader.getResourceAsStream("META-INF/ParameterNames.txt"));
        if (mappings == null) {
            return new String[0];
        }
        String classAndMethodName = className + " " + methodName + " ";
        int ix = mappings.indexOf(classAndMethodName);
        List matches = new ArrayList();
        while (ix > 0) {
            matches.add(mappings.substring(ix + classAndMethodName.length(), mappings.indexOf(" ", ix + classAndMethodName.length()+1)).trim());
            ix = mappings.indexOf(classAndMethodName,ix+1);
        }
        return (String[]) matches.toArray(new String[matches.size()]);
    }
}
