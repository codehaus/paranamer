package com.thoughtworks.paranamer;

import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaParameter;
import com.thoughtworks.qdox.model.DocletTag;

import java.io.File;
import java.io.IOException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

public class ParanamerGeneration {

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


    public void write(String outputPath, String parameterText) throws IOException {
        new File(outputPath + File.separator + "META-INF" + File.separator).mkdirs();
        FileWriter fileWriter = new FileWriter(outputPath + File.separator + "META-INF" + File.separator + "ParameterNames.txt");
        PrintWriter pw = new PrintWriter(fileWriter);
        pw.println(parameterText);
        pw.close();
    }

    private String comma(int k, int size) {
        return (k + 1 < size) ? "," : "";
    }





}
