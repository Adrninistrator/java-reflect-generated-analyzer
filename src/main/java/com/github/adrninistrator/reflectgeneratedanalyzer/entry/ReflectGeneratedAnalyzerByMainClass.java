package com.github.adrninistrator.reflectgeneratedanalyzer.entry;

import com.github.adrninistrator.reflectgeneratedanalyzer.dto.ArgumentByMainClass;
import com.github.adrninistrator.reflectgeneratedanalyzer.dto.ProcessExecResult;
import com.github.adrninistrator.reflectgeneratedanalyzer.util.CommonUtil;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author adrninistrator
 * @date 2024/12/16
 * @description:
 */
public class ReflectGeneratedAnalyzerByMainClass extends ReflectGeneratedAnalyzer {

    public static void main(String[] args) {
        ReflectGeneratedAnalyzerByMainClass reflectGeneratedAnalyzerByMainClass = new ReflectGeneratedAnalyzerByMainClass();
        reflectGeneratedAnalyzerByMainClass.analyse(args);
    }

    public void analyse(String[] args) {
        if (JAVA_HOME == null) {
            return;
        }
        ArgumentByMainClass argumentByMainClass = checkArgument(args);
        if (argumentByMainClass == null) {
            return;
        }

        String jpsPath = JAVA_HOME + "/bin/jps";
        if (IS_WINDOWS_OS) {
            jpsPath += ".exe";
        }
        ProcessExecResult processExecResult = CommonUtil.execProcess(jpsPath, "-l");
        if (processExecResult == null) {
            return;
        }
        if (processExecResult.getExitCode() != 0) {
            System.err.println("调用jps失败 " + jpsPath);
            return;
        }
        if (StringUtils.isBlank(processExecResult.getOutput())) {
            System.err.println("调用jps获取到的结果为空 " + jpsPath);
            return;
        }
        List<String> pidList = new ArrayList<>();
        String[] jpsExecResults = StringUtils.split(processExecResult.getOutput(), "\n");
        for (String jpsExecResult : jpsExecResults) {
            String[] jpsExecResultArray = StringUtils.split(jpsExecResult, " ");
            if (jpsExecResultArray.length != 2) {
                continue;
            }
            if (argumentByMainClass.getMainClass().equals(jpsExecResultArray[1])) {
                pidList.add(jpsExecResultArray[0]);
            }
        }
        if (pidList.isEmpty()) {
            System.err.println("调用jps未获取到指定主类进程 " + argumentByMainClass.getMainClass());
            return;
        }
        if (pidList.size() > 1) {
            System.err.println("调用jps获取到多个指定主类进程 " + argumentByMainClass.getMainClass() + " " + StringUtils.join(" ", pidList));
            return;
        }
        String pid = pidList.get(0);
        System.out.println("调用jps获取到指定主类进程 " + argumentByMainClass.getMainClass() + " 对应的进程PID " + pid);

        List<String> argList = new ArrayList<>();
        argList.add(pid);
        if (StringUtils.isNotBlank(argumentByMainClass.getDstDir())) {
            argList.add(argumentByMainClass.getDstDir());
        }
        String[] args4Super = argList.toArray(new String[]{});
        super.analyse(args4Super);
    }

    private ArgumentByMainClass checkArgument(String[] args) {
        if (args.length < 1) {
            System.err.println("需要在参数1指定目标Java进程主类名，需要指定完整类名，使用 jps -l 查看");
            return null;
        }
        if (args.length > 2) {
            System.err.println("最多接受2个参数 " + args.length);
            return null;
        }
        String dstDir = null;
        if (args.length == 2) {
            dstDir = args[1];
        }

        String mainClass = args[0];
        ArgumentByMainClass argumentByMainClass = new ArgumentByMainClass();
        argumentByMainClass.setMainClass(mainClass);
        argumentByMainClass.setDstDir(dstDir);
        System.out.println("当前需要处理的目标Java进程主类名 " + mainClass + " 输出目录 " + dstDir);
        return argumentByMainClass;
    }
}
