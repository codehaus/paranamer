package com.thoughtworks.paranamer;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.BuildException;

import java.io.IOException;

import com.thoughtworks.paranamer.Paranamer;

public class ParanamerTask extends Task {

    private String sourcePath;
    private String outputPath;

    public void execute() throws BuildException {
        Paranamer paranamer = new Paranamer();
        String parameterText = paranamer.generate(sourcePath);
        try {
            paranamer.write(outputPath, parameterText);
        } catch (IOException e) {
            throw new BuildException("Paranamer encountered an IOException", e);
        }
    }

    public void setSourcePath(String sourcePath) {
        this.sourcePath = sourcePath;
    }

    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
    }
}
