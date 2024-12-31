package com.github.adrninistrator.reflectgeneratedanalyzer.dto;

/**
 * @author adrninistrator
 * @date 2024/12/15
 * @description:
 */
public class RawMethodInfo {

    private int classSeq;
    private String className;
    private String rawClassName;
    private String rawMethodName;

    public int getClassSeq() {
        return classSeq;
    }

    public void setClassSeq(int classSeq) {
        this.classSeq = classSeq;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getRawClassName() {
        return rawClassName;
    }

    public void setRawClassName(String rawClassName) {
        this.rawClassName = rawClassName;
    }

    public String getRawMethodName() {
        return rawMethodName;
    }

    public void setRawMethodName(String rawMethodName) {
        this.rawMethodName = rawMethodName;
    }
}
