package com.github.adrninistrator.reflectgeneratedanalyzer.entry;

import com.github.adrninistrator.reflectgeneratedanalyzer.dto.Argument;
import com.github.adrninistrator.reflectgeneratedanalyzer.dto.ProcessExecResult;
import com.github.adrninistrator.reflectgeneratedanalyzer.dto.RawMethodInfo;
import com.github.adrninistrator.reflectgeneratedanalyzer.util.CommonUtil;
import io.github.hengyunabc.dumpclass.DumpMain;
import org.apache.bcel.Const;
import org.apache.bcel.classfile.ClassFormatException;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.Constant;
import org.apache.bcel.classfile.ConstantClass;
import org.apache.bcel.classfile.ConstantInterfaceMethodref;
import org.apache.bcel.classfile.ConstantMethodref;
import org.apache.bcel.classfile.ConstantNameAndType;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Utility;
import org.apache.commons.lang3.StringUtils;
import org.kohsuke.args4j.CmdLineParser;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author adrninistrator
 * @date 2024/12/15
 * @description:
 */
public class ReflectGeneratedAnalyzer {

    protected static final boolean IS_WINDOWS_OS = CommonUtil.isWindowsOS();

    protected static final String JAVA_HOME = CommonUtil.getJavaHome();

    public static final String[] GENERATED_CLASS_NAME_PREFIX_ARRAY = new String[]{
            "sun.reflect.GeneratedMethodAccessor",
            "sun.reflect.GeneratedConstructorAccessor",
            "sun.reflect.GeneratedSerializationConstructorAccessor"
    };

    public static void main(String[] args) {
        ReflectGeneratedAnalyzer reflectGeneratedAnalyzer = new ReflectGeneratedAnalyzer();
        reflectGeneratedAnalyzer.analyse(args);
    }

    public void analyse(String[] args) {
        if (JAVA_HOME == null) {
            return;
        }

        Argument argument = checkArgument(args);
        if (argument == null) {
            return;
        }

        int failTimes = 0;

        for (String generatedClassNamePrefix : GENERATED_CLASS_NAME_PREFIX_ARRAY) {
            if (!analyseClass(argument, generatedClassNamePrefix)) {
                failTimes++;
            }
        }

        if (failTimes > 0) {
            System.err.println("执行失败");
            return;
        }
        System.out.println("执行成功");
    }

    private Argument checkArgument(String[] args) {
        if (args.length < 1) {
            System.err.println("需要在参数1指定目标Java进程PID");
            return null;
        }
        if (args.length > 2) {
            System.err.println("最多接受2个参数 " + args.length);
            return null;
        }

        String pid = args[0];
        if (!StringUtils.isNumeric(pid)) {
            System.err.println("参数1指定的目标Java进程PID非法 " + pid);
            return null;
        }

        String dstDir;
        if (args.length == 2) {
            dstDir = args[1];
            if (!CommonUtil.checkDirExists(dstDir, true)) {
                return null;
            }
        } else {
            dstDir = "rga_result/" + CommonUtil.genCurrentTime4File() + "@" + pid;
            if (!CommonUtil.checkDirExists(dstDir, false)) {
                return null;
            }
        }
        String finalDstDir = CommonUtil.getCanonicalPath(dstDir);
        if (finalDstDir == null) {
            System.err.println("获取输出目录的绝对路径失败 " + dstDir);
            return null;
        }

        Argument argument = new Argument();
        argument.setPid(pid);
        argument.setDstDir(finalDstDir);
        System.out.println("当前需要处理的目标Java进程PID " + pid + " 输出目录 " + finalDstDir);
        return argument;
    }

    private boolean analyseClass(Argument argument, String generatedClassNamePrefix) {
        String generatedSimpleClassNamePrefix = StringUtils.substringAfterLast(generatedClassNamePrefix, ".");
        String dumpDir4GeneratedMethodAccessor = argument.getDstDir() + "/" + generatedSimpleClassNamePrefix;
        if (!CommonUtil.checkDirExists(dumpDir4GeneratedMethodAccessor, false)) {
            return false;
        }
        if (!dumpClass(argument.getPid(), generatedClassNamePrefix + "*", dumpDir4GeneratedMethodAccessor)) {
            return false;
        }
        System.out.println("dump class文件成功 " + generatedClassNamePrefix);
        String outputFilePath4GeneratedMethodAccessor = argument.getDstDir() + "/" + generatedSimpleClassNamePrefix + ".txt";
        return parseClass(dumpDir4GeneratedMethodAccessor, outputFilePath4GeneratedMethodAccessor, generatedClassNamePrefix);
    }

    private boolean dumpClass(String pid, String className, String dumpClassDir) {
        try {
            long startTime = System.currentTimeMillis();
            String javaPath = JAVA_HOME + "/bin/java";
            if (IS_WINDOWS_OS) {
                javaPath += ".exe";
            }
            String dumpClassJarPath = CommonUtil.getClassJarPath(DumpMain.class);
            if (dumpClassJarPath == null) {
                System.err.println("获取 dumpclass jar包路径失败");
                return false;
            }
            String args4jJarPath = CommonUtil.getClassJarPath(CmdLineParser.class);
            if (args4jJarPath == null) {
                System.err.println("获取 args4j jar包路径失败");
                return false;
            }
            String jarSeparator = IS_WINDOWS_OS ? ";" : ":";
            String javaClassPath = StringUtils.joinWith(jarSeparator, dumpClassJarPath, args4jJarPath, JAVA_HOME + "/lib/sa-jdi.jar");

            ProcessExecResult processExecResult = CommonUtil.execProcess(
                    javaPath,
                    "-cp", javaClassPath,
                    DumpMain.class.getName(), "-p", pid, "-c", className, "-o", dumpClassDir, "--noStat"
            );
            if (processExecResult == null) {
                return false;
            }

            // 打印输出结果
            System.out.println("调用 dumpclass " + className + " 返回码 " + processExecResult.getExitCode());
            System.out.println("调用 dumpclass " + className + " 输出\n【\n" + processExecResult.getOutput() + "】");
            System.out.println("调用 dumpclass " + className + " 耗时（秒） " + CommonUtil.getSpendSeconds(startTime));
            return processExecResult.getExitCode() == 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean parseClass(String dumpClassDir, String dstFilePath, String classNamePrefix) {
        List<String> classPathList = new ArrayList<>();
        CommonUtil.searchDir(dumpClassDir, classPathList, ".class");
        if (classPathList.isEmpty()) {
            System.out.println("目录中未找到.class文件 " + dumpClassDir);
            return true;
        }
        List<RawMethodInfo> rawMethodInfoList = new ArrayList<>();
        long startTime = System.currentTimeMillis();
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dstFilePath), StandardCharsets.UTF_8))) {
            for (String classPath : classPathList) {
                RawMethodInfo rawMethodInfo = parseOneClass(writer, classPath, classNamePrefix);
                rawMethodInfoList.add(rawMethodInfo);
            }

            System.out.println("解析class文件成功，数量 " + classPathList.size() + " " + classNamePrefix + " 耗时（秒） " + CommonUtil.getSpendSeconds(startTime));
            rawMethodInfoList.sort(Comparator.comparing(RawMethodInfo::getClassSeq));
            for (RawMethodInfo rawMethodInfo : rawMethodInfoList) {
                writer.write(StringUtils.joinWith(" ", rawMethodInfo.getClassSeq(), rawMethodInfo.getClassName(), rawMethodInfo.getRawClassName(),
                        rawMethodInfo.getRawMethodName()) + "\n");
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private RawMethodInfo parseOneClass(Writer writer, String filePath, String classNamePrefix) throws IOException {
        JavaClass javaClass = new ClassParser(filePath).parse();
        String className = javaClass.getClassName();
        String classSeq = StringUtils.substringAfterLast(className, classNamePrefix);
        ConstantPool constantPool = javaClass.getConstantPool();
        String rawClassName = "";
        String rawMethodName = "";
        for (int i = 0; i < constantPool.getLength(); i++) {
            Constant constant = null;
            try {
                constant = constantPool.getConstant(i);
            } catch (ClassFormatException e) {
                // 正常情况，不需要处理
            }
            if (constant == null) {
                continue;
            }
            int classIndex = -1;
            int nameAndTypeIndex = -1;
            if (constant.getTag() == Const.CONSTANT_Methodref) {
                ConstantMethodref constantMethodref = (ConstantMethodref) constant;
                classIndex = constantMethodref.getClassIndex();
                nameAndTypeIndex = constantMethodref.getNameAndTypeIndex();
            } else if (constant.getTag() == Const.CONSTANT_InterfaceMethodref) {
                ConstantInterfaceMethodref constantInterfaceMethodref = (ConstantInterfaceMethodref) constant;
                classIndex = constantInterfaceMethodref.getClassIndex();
                nameAndTypeIndex = constantInterfaceMethodref.getNameAndTypeIndex();
            }
            if (classIndex != -1 && nameAndTypeIndex != -1) {
                ConstantClass constantClass = constantPool.getConstant(classIndex);

                rawClassName = constantPool.constantToString(constantClass.getNameIndex(), Const.CONSTANT_Utf8);
                rawClassName = Utility.compactClassName(rawClassName, false);

                ConstantNameAndType constantNameAndType = constantPool.getConstant(nameAndTypeIndex);
                rawMethodName = constantPool.constantToString(constantNameAndType.getNameIndex(), Const.CONSTANT_Utf8);
                break;
            }
        }
        RawMethodInfo rawMethodInfo = new RawMethodInfo();
        rawMethodInfo.setClassSeq(Integer.parseInt(classSeq));
        rawMethodInfo.setClassName(className);
        rawMethodInfo.setRawClassName(rawClassName);
        rawMethodInfo.setRawMethodName(rawMethodName);
        return rawMethodInfo;
    }
}
