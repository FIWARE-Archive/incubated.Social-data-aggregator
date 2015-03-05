@echo off

set EXIT=0

:loop
IF NOT "%1"=="" (
    IF "%1"=="--sda-home" (
        SET SDA_HOME=%2
        SHIFT
    )
    IF "%1"=="--with-master" (
        SET MASTER=%2
        SHIFT
    )
	IF "%1"=="--spark-home" (
        SET SPARK_HOME=%2
        SHIFT
    )
	IF "%1"=="--help" (
        call:exit_with_usage
		set EXIT=1
    )
    SHIFT
    GOTO :loop
)
if %EXIT%==0 (
set CONF_FILE=%SDA_HOME%\scripts\connector-tw\tw-connector-confs.cfg
echo. conf file path:%CONF_FILE%
for /f "delims=" %%x in (%CONF_FILE%) do (set "%%x")

if "%SDA_HOME%"=="" (
echo Missing SDA_HOME
call:exit_with_usage
goto:eof )
if "%MASTER%"=="" (
echo. Missing MASTER
call:exit_with_usage
goto:eof )
if "%SPARK_HOME%"=="" (
echo. Missing SPARK_HOME
call:exit_with_usage
goto:eof )

set  SDA_CONF=%SDA_HOME%\confs\connector-tw
set PATH_TO_JAR_FILE=%SDA_HOME%\bin\connectors\%JAR_FILE_NAME%
echo. Submitting tw-connector application...
echo. Spark Home is %SPARK_HOME%
echo. SDA_CONF is %SDA_CONF%
echo. JAR_FILE_NAME is %JAR_FILE_NAME%
echo. %SPARK_HOME%/bin/spark-submit.cmd --class %TW_CONN_MAIN_CLASS% --master %MASTER% --deploy-mode client %PATH_TO_JAR_FILE%

%SPARK_HOME%/bin/spark-submit.cmd --class %TW_CONN_MAIN_CLASS% --master %MASTER% --deploy-mode client %PATH_TO_JAR_FILE% %SDA_CONF%
echo. Done
)

echo.&pause&goto:eof

:exit_with_usage
  echo. start-tw-connector.bat - submit tw-connector on Spark
  echo. 
  echo. usage:
  echo. start-tw-connector.bat --sda-home "sda_home" --with-master "master" --spark-home "spark_home"
  echo.
  echo. --sda-home. The path of social-data-aggregator folder. Optional. Provide from command line if SDA_HOME is not setted in your environment
  echo. --with-master. master name (eg local,spark://xxx.xxx). Optional. Provide from command line if MASTER is not setted in your environment
  echo. --spark-home. The path of spark folder. Optional. Provide from command line if SPARK_HOME is not setted in your environment
  echo.
goto:eof