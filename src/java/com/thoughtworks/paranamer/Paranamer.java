package com.thoughtworks.paranamer;

import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaParameter;
import com.thoughtworks.qdox.model.DocletTag;

import java.io.*;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.StringTokenizer;
import java.util.List;

public class Paranamer {
    public String generate(String sourcePath) {
        String retval = "";
        JavaDocBuilder builder = new JavaDocBuilder();
        builder.addSourceTree(new File(sourcePath));
        JavaClass[] classes = builder.getClasses();
        Arrays.sort(classes);
        for (int i = 0; i < classes.length; i++) {
            JavaClass clazz = classes[i];
            String pkgName = clazz.getPackage() + ".";
            String className = pkgName + clazz.getName();
            JavaMethod[] methods = clazz.getMethods();
            Arrays.sort(methods);
            for (int j = 0; j < methods.length; j++) {
                JavaMethod method = methods[j];
                List methodList = Arrays.asList(method.getModifiers());
                if (methodList.contains("public")) {
                    JavaParameter[] parms = method.getParameters();
                    DocletTag[] alsoKnownAs = method.getTagsByName("previousParamNames");
                    for (int k = 0; k < alsoKnownAs.length; k++) {
                        String value = alsoKnownAs[k].getValue();
                        retval = retval + className + " " + method.getName() + " " + value + " " + getTypes(parms) + "\n";
                    }
                    String meth = method.getName() + " ";
                    meth = meth + getParamNames(parms) + " ";
                    String types = getTypes(parms);
                    meth = meth + types + "\n";
                    retval = retval + className + " " + meth;
                }
            }
        }
        return retval;
    }

    private String getParamNames(JavaParameter[] parms) {
        String meth = "";
        for (int k = 0; k < parms.length; k++) {
            meth = meth + parms[k].getName();
            meth = meth + comma(k, parms.length);
        }
        return meth;
    }

    private String getTypes(JavaParameter[] parms) {
        String types = "";
        for (int k = 0; k < parms.length; k++) {
            types = types + parms[k].getType();
            types = types + comma(k, parms.length);
        }
        return types;
    }

    private String comma(int k, int size) {
        if (k + 1 < size) {
            return ",";
        }
        return "";
    }

    public void write(String outputPath, String parameterText) throws IOException {
        new File(outputPath + File.separator + "META-INF" + File.separator).mkdirs();
        FileWriter fileWriter = new FileWriter(outputPath + File.separator + "META-INF" + File.separator + "ParameterNames.txt");
        PrintWriter pw = new PrintWriter(fileWriter);
        pw.println(parameterText);
        pw.close();
    }


    public Method checkedLookup(ClassLoader classLoader, String className, String methName, String paramNames) throws ParanamerException {
        Method method = lookup(classLoader, className , methName, paramNames);
        if (method == null) {
            throw new ParanamerException("Paranamer could not find method signature");
        }
        return null;
    }

    public Method uncheckedLookup(ClassLoader classLoader, String className, String methName, String paramNames) {
        Method method = lookup(classLoader, className , methName, paramNames);
        if (method == null) {
            throw new ParanamerRuntimeException("Paranamer could not find method signature");
        }
        return null;
    }

    /**
     *
     * @previousParamNames classLoader,c,m,p
     */
    public Method lookup(ClassLoader classLoader, String className, String methName, String paramNames) {
        InputStream resourceAsStream = classLoader.getResourceAsStream("META-INF/ParameterNames.txt");
        if (resourceAsStream == null) {
            return null;
        }
        InputStreamReader inputStreamReader = new InputStreamReader(resourceAsStream);
        LineNumberReader lineReader = new LineNumberReader(inputStreamReader);
        String line = readLine(lineReader);
        String classAndMethodAndParamNames = className + " " + methName + " " + paramNames;
        while (line != null) {
            if (line.startsWith(classAndMethodAndParamNames)) {
                StringTokenizer st = new StringTokenizer(line);
                String clazzName = st.nextToken();
                String methodName = st.nextToken();
                st.nextToken(); // parameter names - not needed again
                String methodParamTypes = st.nextToken();
                Class loadedClazz = null;
                try {
                    loadedClazz = classLoader.loadClass(clazzName);
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
                            paramTypes = paramTypes + comma(k, parameters.length);
                        }
                        if (paramTypes.equals(methodParamTypes)) {
                            return method;
                        }
                    }
                }
            }
            line = readLine(lineReader);
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
