@echo off

set EXIT=0
set JAR_ARGS=""
:loop
IF NOT "%1"=="" (
    IF "%1"=="--sda-home" (
        SET SDA_HOME=%2
        SHIFT
    )
	IF "%1"=="--from" (
        SET FROM=%2
        SHIFT
    )
	IF "%1"=="--to" (
        SET TO=%2
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
set CONF_FILE=%SDA_HOME%\scripts\consumer-tw-tot\consumer-tw-tot-confs.cfg
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

set  SDA_CONF=%SDA_HOME%\confs
set PATH_TO_JAR_FILE=%SDA_HOME%\bin\consumers\batch\%JAR_FILE_NAME_BATCH%
echo. Submitting tw tot batch application...
echo. Spark Home is %SPARK_HOME%
echo. SDA_CONF is %SDA_CONF%
echo. JAR_FILE_NAME is %JAR_FILE_NAME_BATCH%

set TOT_TW=consumers\consumer-tw-tot

%SPARK_HOME%/bin/spark-submit.cmd --class %TOT_TW_BATCH_MAIN_CLASS% --master %MASTER% --deploy-mode client %PATH_TO_JAR_FILE% --from %FROM% --to %TO% -I %INPUT_DATA_PATH%
)