package com.github.adrninistrator.reflectgeneratedanalyzer.util;

import com.github.adrninistrator.reflectgeneratedanalyzer.dto.ProcessExecResult;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author adrninistrator
 * @date 2024/12/15
 * @description:
 */
public class CommonUtil {

    public static String getCanonicalPath(String filePath) {
        File dstDirFile = new File(filePath);
        try {
            return dstDirFile.getCanonicalPath();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getClassJarPath(Class<?> clazz) {
        String jarFilePath = clazz.getProtectionDomain().getCodeSource().getLocation().getFile();
        return getCanonicalPath(jarFilePath);
    }

    public static boolean isWindowsOS() {
        return StringUtils.startsWithIgnoreCase(System.getProperty("os.name"), "Windows");
    }

    public static boolean checkDirExists(String dirPath, boolean shouldBeEmpty) {
        File dir = new File(dirPath);
        if (dir.exists()) {
            if (dir.isFile()) {
                System.err.println("存在与目标目录同名的文件 " + dirPath);
                return false;
            }
            if (shouldBeEmpty) {
                File[] files = dir.listFiles();
                if (ArrayUtils.isNotEmpty(files)) {
                    System.err.println("指定的目录存在且非空 " + dirPath);
                    return false;
                }
            }
            return true;
        }
        if (!dir.mkdirs()) {
            System.err.println("创建目录失败 " + dirPath);
            return false;
        }
        return true;
    }

    public static void searchDir(String dirPath, List<String> subFilePathList, String... fileExts) {
        File dir = new File(dirPath);
        File[] files = dir.listFiles();
        if (files == null) {
            return;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                searchDir(file.getAbsolutePath(), subFilePathList, fileExts);
            } else {
                String filePath = file.getAbsolutePath();
                if (fileExts == null || checkFileExt(filePath, fileExts)) {
                    subFilePathList.add(filePath);
                }
            }
        }
    }

    public static boolean checkFileExt(String filePath, String... fileExts) {
        if (fileExts == null) {
            return true;
        }

        for (String fileExt : fileExts) {
            if (StringUtils.endsWithIgnoreCase(filePath, fileExt)) {
                return true;
            }
        }
        return false;
    }

    public static String genCurrentTime4File() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS");
        return sdf.format(new Date());
    }

    public static Double getSpendSeconds(long startTime) {
        long spendTime = System.currentTimeMillis() - startTime;
        return spendTime / 1000.0D;
    }

    public static String getJavaHome() {
        String javaHome = System.getProperty("JAVA_HOME");
        if (StringUtils.isBlank(javaHome)) {
            javaHome = System.getenv("JAVA_HOME");
        }

        if (StringUtils.isBlank(javaHome)) {
            System.err.println("请先通过 JAVA_HOME 环境变量或JVM参数指定需要使用的JDK根目录");
            return null;
        }
        return javaHome;
    }

    public static ProcessExecResult execProcess(String... args) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(args);

            Process process = processBuilder.start();
            int exitCode = process.waitFor();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            StringBuilder output = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            ProcessExecResult processExecResult = new ProcessExecResult();
            processExecResult.setExitCode(exitCode);
            processExecResult.setOutput(output.toString());
            return processExecResult;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private CommonUtil() {
        throw new IllegalStateException("illegal");
    }
}
