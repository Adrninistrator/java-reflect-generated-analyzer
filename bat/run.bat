SETLOCAL ENABLEDELAYEDEXPANSION
set CLASSPATH=
FOR %%C IN (lib\*.jar) DO set CLASSPATH=!CLASSPATH!;%%C
echo %CLASSPATH%

java -Dfile.encoding=UTF-8 -cp .;./jar/java-reflect-generated-analyzer.jar;%CLASSPATH% com.github.adrninistrator.reflectgeneratedanalyzer.entry.ReflectGeneratedAnalyzer %*