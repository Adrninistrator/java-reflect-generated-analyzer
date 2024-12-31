# 1. 前言

在分析 Java Metaspace OOM 问题时，可能需要获取 Java 反射生成的 sun.reflect.GeneratedMethodAccessor...、sun.reflect.GeneratedConstructorAccessor...、sun.reflect.GeneratedSerializationConstructorAccessor... 类对应的原始方法

目前可以使用的方法步骤相对较多，耗时很长，因此开发了简化的工具快速获取

以下会使用 dumpclass（https://github.com/hengyunabc/dumpclass）来将 Java 进程中生成的以上类导出为 .class 文件

# 2. 使用方式

## 2.1. 下载地址

可通过以下地址下载工具对应的 Java 程序，之后可解压使用：

https://github.com/Adrninistrator/java-reflect-generated-analyzer/releases/

https://gitee.com/Adrninistrator/java-reflect-generated-analyzer/releases/

项目地址如下：

https://github.com/Adrninistrator/java-reflect-generated-analyzer/

https://gitee.com/Adrninistrator/java-reflect-generated-analyzer/

## 2.2. 支持的 JDK 版本

由于 JDK8 之后的版本没有提供 sa-jdi.jar，因此只支持 JDK8 及之前的版本

对于 JDK8 之后的版本，使用 jhsdb 命令还是能够人工 dump class

## 2.3. 公共参数

### 2.3.1. JAVA_HOME

在执行以下脚本时，需要使用 JAVA_HOME 环境变量配置需要使用的 JDK 的根目录，与被 dump 的 Java 应用使用的 JAVA_HOME 需要相同

或者在 JVM 参数中使用以下方式指定：

```
-DJAVA_HOME=xxx
```

在 Linux 类操作系统中，若未在操作系统环境变量中配置 JAVA_HOME，则可在执行脚本前执行以下命令指定会话级别变量：

```shell
export JAVA_HOME=xxx
```

## 2.4. 执行方式——Windows

在 Windows 操作系统下，通过以下方式执行

### 2.4.1. 指定进程 PID

打开 cmd 后进入工具解压后的目录，通过以下方式执行：

```shell
run.bat {PID}
```

参数 1 指定需要分析的 Java 进程 PID

### 2.4.2. 指定进程主类名

```shell
run_by_main_class.bat {Main Class}
```

参数 1 指定需要分析的 Java 进程的主类名

Java 进程的主类名可以执行以下命令查看

```shell
jps -l
```

输出结果示例如下：

```log
2912
19316 org.sonarsource.sonarlint.core.backend.cli.SonarLintServerCli
3748 sun.tools.jps.Jps
25532 org.gradle.launcher.daemon.bootstrap.GradleDaemon
43942 org.apache.catalina.startup.Bootstrap
```

PID 后面的就是对应 Java 进程的主类名

## 2.5. 执行方式——Linux

在 Linux 类操作系统下，通过以下方式执行

### 2.5.1. 指定进程 PID

```shell
sh run.sh {PID}
```

参数 1 指定需要分析的 Java 进程 PID

### 2.5.2. 指定进程主类名

```shell
sh run_by_main_class.sh {Main Class}
```

参数 1 指定需要分析的 Java 进程的主类名

## 2.6. 输出结果保存目录

若执行以上脚本时未指定参数 2，则会将输出结果保存在当前目录的子目录“rga_result/{当前时间}@{PID}”中

若执行以上脚本时有指定参数 2，则会将输出结果保存在参数 2 指定的目录中，对应的目录需要不存在，或目录存在且为空

在保存本次输出结果的目录中，GeneratedMethodAccessor、GeneratedConstructorAccessor、GeneratedSerializationConstructorAccessor 目录分别保存通过 dumpclass 导出的 sun.reflect.GeneratedMethodAccessor...、sun.reflect.GeneratedConstructorAccessor...、sun.reflect.GeneratedSerializationConstructorAccessor... 类的 .class 文件

GeneratedMethodAccessor.txt、GeneratedConstructorAccessor.txt、GeneratedSerializationConstructorAccessor.txt 文件分别保存 sun.reflect.GeneratedMethodAccessor...、sun.reflect.GeneratedConstructorAccessor...、sun.reflect.GeneratedSerializationConstructorAccessor... 类对应的原始方法

## 2.7. 输出结果文件格式

GeneratedMethodAccessor.txt、GeneratedConstructorAccessor.txt、GeneratedSerializationConstructorAccessor.txt 文件格式相同，文件每行有 4 列，使用空格分隔，每一列的含义如下：

|列序号|含义|
|---|---|
|1|反射生成的类名中的序号|
|2|反射生成的类名|
|3|类对应的原始类名|
|4|类对应的原始方法名|

GeneratedMethodAccessor.txt 文件内容示例如下：

```log
1 sun.reflect.GeneratedMethodAccessor1 org.apache.tomcat.util.modeler.FeatureInfo setName
2 sun.reflect.GeneratedMethodAccessor2 org.apache.tomcat.util.modeler.FeatureInfo setDescription
3 sun.reflect.GeneratedMethodAccessor3 org.apache.tomcat.util.modeler.FeatureInfo setType
4 sun.reflect.GeneratedMethodAccessor4 org.apache.tomcat.util.modeler.OperationInfo addParameter
5 sun.reflect.GeneratedMethodAccessor5 org.apache.tomcat.util.modeler.OperationInfo setImpact
6 sun.reflect.GeneratedMethodAccessor6 org.apache.tomcat.util.modeler.OperationInfo setReturnType

28 sun.reflect.GeneratedMethodAccessor28 sun.reflect.Reflection getCallerClass
29 sun.reflect.GeneratedMethodAccessor29 java.lang.reflect.ParameterizedType getRawType
30 sun.reflect.GeneratedMethodAccessor30 java.lang.reflect.ParameterizedType getActualTypeArguments
```

GeneratedConstructorAccessor.txt 文件内容示例如下：

```log
1 sun.reflect.GeneratedConstructorAccessor1 org.apache.tomcat.util.modeler.ParameterInfo <init>
2 sun.reflect.GeneratedConstructorAccessor2 org.apache.tomcat.util.modeler.OperationInfo <init>
3 sun.reflect.GeneratedConstructorAccessor3 org.apache.tomcat.util.modeler.AttributeInfo <init>
4 sun.reflect.GeneratedConstructorAccessor4 org.apache.tomcat.util.modeler.ManagedBean <init>
5 sun.reflect.GeneratedConstructorAccessor5 org.apache.tomcat.util.modeler.modules.MbeansDescriptorsDigesterSource <init>
6 sun.reflect.GeneratedConstructorAccessor6 com.sun.proxy.$Proxy5 <init>
7 sun.reflect.GeneratedConstructorAccessor7 org.apache.tomcat.util.descriptor.tld.TagXml <init>
8 sun.reflect.GeneratedConstructorAccessor8 com.sun.proxy.$Proxy7 <init>
9 sun.reflect.GeneratedConstructorAccessor9 com.sun.proxy.$Proxy8 <init>
10 sun.reflect.GeneratedConstructorAccessor10 com.sun.proxy.$Proxy1 <init>
```

GeneratedSerializationConstructorAccessor.txt 文件内容示例如下：

```log
14 sun.reflect.GeneratedSerializationConstructorAccessor14 java.lang.Object <init>
15 sun.reflect.GeneratedSerializationConstructorAccessor15 java.lang.Object <init>
16 sun.reflect.GeneratedSerializationConstructorAccessor16 java.lang.Object <init>
17 sun.reflect.GeneratedSerializationConstructorAccessor17 java.lang.Object <init>
```

## 2.8. 输出日志示例 

执行 run_by_main_class.sh 脚本根据主类名获取反射生成的类信息时，在控制台输出的日志如下所示：

```log
$ export JAVA_HOME=/opt/java/openjdk8
$ sh run_by_main_class.sh org.apache.catalina.startup.Bootstrap

当前需要处理的目标 Java 进程主类名 org.apache.catalina.startup.Bootstrap 输出目录 null
调用 jps 获取到指定主类进程 org.apache.catalina.startup.Bootstrap 对应的进程 PID 583
当前需要处理的目标 Java 进程 PID 583 输出目录 /tmp/jar_output_dir/rga_result/20241217_094808_113@583
调用 dumpclass sun.reflect.GeneratedMethodAccessor* 返回码 0
调用 dumpclass sun.reflect.GeneratedMethodAccessor* 输出
【
Attaching to process ID 583, please wait...
Debugger attached successfully.
Server compiler detected.
JVM version is 25.382-b05
】
调用 dumpclass sun.reflect.GeneratedMethodAccessor* 耗时（秒） 44.237
dump class 文件成功 sun.reflect.GeneratedMethodAccessor
解析 class 文件成功，数量 3193 sun.reflect.GeneratedMethodAccessor 耗时（秒） 0.478
调用 dumpclass sun.reflect.GeneratedConstructorAccessor* 返回码 0
调用 dumpclass sun.reflect.GeneratedConstructorAccessor* 输出
【
Attaching to process ID 583, please wait...
Debugger attached successfully.
Server compiler detected.
JVM version is 25.382-b05
】
调用 dumpclass sun.reflect.GeneratedConstructorAccessor* 耗时（秒） 35.955
dump class 文件成功 sun.reflect.GeneratedConstructorAccessor
解析 class 文件成功，数量 198 sun.reflect.GeneratedConstructorAccessor 耗时（秒） 0.006
调用 dumpclass sun.reflect.GeneratedSerializationConstructorAccessor* 返回码 0
调用 dumpclass sun.reflect.GeneratedSerializationConstructorAccessor* 输出
【
Attaching to process ID 20712, please wait...
Debugger attached successfully.
Server compiler detected.
JVM version is 25.144-b01
】
调用 dumpclass sun.reflect.GeneratedSerializationConstructorAccessor* 耗时（秒） 3.767
dump class 文件成功 sun.reflect.GeneratedSerializationConstructorAccessor
解析 class 文件成功，数量 4 sun.reflect.GeneratedSerializationConstructorAccessor 耗时（秒） 0.006
执行成功
```

# 3. 输出结果文件处理脚本

对生成的 GeneratedMethodAccessor.txt、GeneratedConstructorAccessor.txt、GeneratedSerializationConstructorAccessor.txt 文件可以使用以下脚本进行处理，可在 Linux shell 或 Windows Git Bash 中执行

- 检查是否有某个文件解析失败

在生成的文件中，若解析对应的原始方法成功，每行应为 4 列非空值，以下脚本检查是否存在列数不为 4 列的行

```shell
cat GeneratedMethodAccessor.txt | awk '{if (NF!=4){print"!!!error!!!"$0}}'
cat GeneratedConstructorAccessor.txt | awk '{if (NF!=4){print"!!!error!!!"$0}}'
cat GeneratedSerializationConstructorAccessor.txt | awk '{if (NF!=4){print"!!!error!!!"$0}}'
```

- 查找生成了多个类的方法

Java 反射生成的类的并发情况下可能对同一个方法生成多个类，以下脚本用于找出相关的方法

```shell
cat GeneratedMethodAccessor.txt | awk '{print $3"@"$4}' | sort | uniq -c | awk '{if ($1>1){print $0}}' | sort -r -n -k 1
cat GeneratedConstructorAccessor.txt | awk '{print $3"@"$4}' | sort | uniq -c | awk '{if ($1>1){print $0}}' | sort -r -n -k 1
cat GeneratedSerializationConstructorAccessor.txt | awk '{print $3"@"$4}' | sort | uniq -c | awk '{if ($1>1){print $0}}' | sort -r -n -k 1
```

# 4. 其他获取 Java 反射生成类的方式

以下方式也能获取 Java 反射生成类的方式，但步骤相对较多，耗时很长，因此不使用以下方式

## 4.1. 使用 dumpclass 导出类

### 4.1.1. 解压 dumpclass

解压 dumpclass-0.0.2.jar 中的 BOOT-INF 目录，只需要执行一次

```shell
jar -xvf dumpclass-0.0.2.jar BOOT-INF
```

### 4.1.2. 执行 dumpclass

执行 dumpclass 导出类，导出到 output 目录

Windows 环境使用以下脚本执行：

```shell
set pid=xxx
java -cp ./BOOT-INF/classes/;BOOT-INF/lib/*;"%JAVA_HOME%/lib/sa-jdi.jar" io.github.hengyunabc.dumpclass.DumpMain -p %pid% -c sun.reflect.GeneratedMethodAccessor* -o ./output
```

Linux 环境使用以下脚本执行：

```shell
pid=$(jps | grep {类名关键字} | awk '{print $1}')
java -cp ./BOOT-INF/classes/:BOOT-INF/lib/*:"$JAVA_HOME/lib/sa-jdi.jar" io.github.hengyunabc.dumpclass.DumpMain -p $pid -c sun.reflect.GeneratedMethodAccessor* -o ./output
```

## 4.2. 使用 javap 反编译类并获得原始方法

### 4.2.1. 获得原始方法脚本

在 Linux 环境打开 shell，或 Windows 环境，打开 Git Bash，进入以上 output 目录，执行以下脚本，使用 javap 反编译类并获得原始方法

```shell
cd output

JAVAP_RESULT=javap_result
[-d $JAVAP_RESULT] || mkdir $JAVAP_RESULT
for class_file in $(find sun -type f -name \*.class); do
    echo $class_file
    file_name=$(echo $class_file | awk -F '/' '{print $NF'})
    javap -l -v -p $class_file > $JAVAP_RESULT/${file_name}.java
done

CLASS_METHOD_RESULT=class_method_result.txt
> $CLASS_METHOD_RESULT
for java_file in $(find $JAVAP_RESULT -type f -name \*.java); do
    echo $java_file
    file_name=$(echo $java_file | awk -F '/' '{print $NF'})
    class_method=$(grep -E '= Methodref | = InterfaceMethodref' $java_file | awk '{print $6}')
    class_name=$(echo $class_method | awk -F '.' '{print $1}' | sed's#/#.#g')
    method_name=$(echo $class_method | awk -F '.' '{print $2}' | awk -F ':' '{print $1}')
    echo "$file_name $class_name $method_name" >> $CLASS_METHOD_RESULT
done
```

### 4.2.2. 输出结果文件格式

输出结果文件 class_method_result.txt 每行有 3 列，使用空格分隔，每一列的含义如下：

|列序号|含义|
|---|---|
|1|反射生成的类名|
|2|对应的原始类名|
|3|对应的原始方法名|

### 4.2.3. 输出结果文件处理脚本

- 检查是否有某个文件解析失败

```shell
cat $CLASS_METHOD_RESULT | awk '{if (NF!=3){print"!!!error!!!"$0}}'
```

- 查找生成了多个类的方法

```shell
cat $CLASS_METHOD_RESULT | awk '{print $2"2"$3}' | sort | uniq -c | sort -r -n -k 1 | awk '{if ($1>1){print $0}}'
```

# 5. 以上工具与 javap 执行耗时对比

在同一台电脑上进行验证，对比以上工具与 javap 执行耗时如下

使用 javap 反编译一个反射生成的类时，第一次耗时约 0.9 秒，后续约 0.4~0.5 秒（还需要额外使用 grep 命令处理反编译结果）

使用以上工具解析一个反射生成的类时，第一次耗时约 0.00054 秒，后续约 0.00028 秒

以上工具执行多次的日志中显示的耗时如下：

```
解析 class 文件成功，数量 3193 sun.reflect.GeneratedMethodAccessor 耗时（秒） 1.733
解析 class 文件成功，数量 3193 sun.reflect.GeneratedMethodAccessor 耗时（秒） 0.905
解析 class 文件成功，数量 3193 sun.reflect.GeneratedMethodAccessor 耗时（秒） 0.949
```

作为对比，在解析反射生成的类的原始方法这一步，以上工具的执行速度比 javap 快约 1600 多倍

以以上日志为例，解析 3193 个反射生成的类时，使用以上工具耗时约 0.9~1.7 秒，使用 javap 耗时约 24~48 分钟

# 6. 其他不使用方式的原因说明

## 6.1. 不使用 Arthas 导出类的原因

Arthas 的 dump 命令导出类存在数量限制，且需要先 attach 到对应 Java 进程上，因此不使用 Arthas 导出类

## 6.2. 不使用 dumpclass 提供的命令导出类的原因

dumpclass 提供的导出类的命令如下

```shell
java -cp ./dumpclass-0.0.2.jar;"%JAVA_HOME%/lib/sa-jdi.jar" io.github.hengyunabc.dumpclass.DumpMain {pid} sun.reflect.GeneratedMethodAccessor* .
```

执行时出现以下错误，因此不使用以上命令

```log
错误：找不到或无法加载主类 io.github.hengyunabc.dumpclass.DumpMain
```

在以下 issue 中也有人反馈：[https://github.com/hengyunabc/dumpclass/issues/13](https://github.com/hengyunabc/dumpclass/issues/13)
