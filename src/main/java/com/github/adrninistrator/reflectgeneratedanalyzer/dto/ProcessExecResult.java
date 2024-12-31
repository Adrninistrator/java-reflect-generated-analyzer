package com.github.adrninistrator.reflectgeneratedanalyzer.dto;

/**
 * @author adrninistrator
 * @date 2024/12/16
 * @description:
 */
public class ProcessExecResult {

    private int exitCode;
    private String output;

    public int getExitCode() {
        return exitCode;
    }

    public void setExitCode(int exitCode) {
        this.exitCode = exitCode;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }
}
