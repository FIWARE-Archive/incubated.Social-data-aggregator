@echo off

if "%1"=="" (
call:exit_with_usage
goto:eof 
)
	
set SDA_FOLDER_NAME=sda
set CONNECTOR_TW_FOLDER_NAME=connector-tw
set CONSUMER_TOT_TW_BATCH_PATH=consumer-tw-tot\consumer-tw-tot-batch
set CONSUMER_TOT_TW_STREAM_PATH=consumer-tw-tot\consumer-tw-tot-stream
set SCRIPTS_FOLDER_NAME=scripts
set CONFS_FOLDER_NAME=confs
set CONNNECTORS_ROOT=producers
set CONSUMERS_ROOT=consumers
set CONSUMERS_BIN_REL_PATH=bin\consumers
set BATCH_FOLDER=batch
set STREAM_FOLDER=stream

set TARGET_FOLDER=target
set CONNECTOR_BIN_REL_PATH=bin\connectors

REM Figure out where SDA is installed
set CURR_DIR_FNAME=%~f0
for %%F in (%CURR_DIR_FNAME%) do set CURR_DIR=%%~dpF
set SCRIPT_NAME=%~nx0
SET SDA_SRC_DIR=%CURR_DIR:~0,-17%

echo. SDA_SRC_DIR is %SDA_SRC_DIR%
echo. script name is %SCRIPT_NAME%

set OUTPUT_DIR=%1
set OUTPUT_PATH=%OUTPUT_DIR%\%SDA_FOLDER_NAME%
echo OUTPUT_PATH is %OUTPUT_PATH%

rd /s /q %OUTPUT_PATH% && echo. folder %OUTPUT_PATH% already present. Deleting..
md %OUTPUT_PATH%\%CONNECTOR_BIN_REL_PATH%
md %OUTPUT_PATH%\%CONSUMERS_BIN_REL_PATH%
md %OUTPUT_PATH%\%CONSUMERS_BIN_REL_PATH%\%BATCH_FOLDER%
md %OUTPUT_PATH%\%CONSUMERS_BIN_REL_PATH%\%STREAM_FOLDER%

echo.
xcopy /S /I "%SDA_SRC_DIR%\%SCRIPTS_FOLDER_NAME%\windows" "%OUTPUT_PATH%\%SCRIPTS_FOLDER_NAME%"
xcopy  "%SDA_SRC_DIR%\%CONNNECTORS_ROOT%\%CONNECTOR_TW_FOLDER_NAME%\%TARGET_FOLDER%\uber-*.jar" "%OUTPUT_PATH%\%CONNECTOR_BIN_REL_PATH%"
xcopy "%SDA_SRC_DIR%\%CONSUMERS_ROOT%\%CONSUMER_TOT_TW_BATCH_PATH%\%TARGET_FOLDER%\uber-*.jar" "%OUTPUT_PATH%\%CONSUMERS_BIN_REL_PATH%\%BATCH_FOLDER%"
xcopy "%SDA_SRC_DIR%\%CONSUMERS_ROOT%\%CONSUMER_TOT_TW_STREAM_PATH%\%TARGET_FOLDER%\uber-*.jar" "%OUTPUT_PATH%\%CONSUMERS_BIN_REL_PATH%\%STREAM_FOLDER%"

xcopy /S /I "%SDA_SRC_DIR%\%CONFS_FOLDER_NAME%" "%OUTPUT_PATH%\%CONFS_FOLDER_NAME%"

echo. removing make-dist script from path %OUTPUT_PATH%\%SCRIPTS_FOLDER_NAME%\%SCRIPT_NAME%
del /Q %OUTPUT_PATH%\%SCRIPTS_FOLDER_NAME%\%SCRIPT_NAME%
echo "End"

echo.&pause&goto:eof

:exit_with_usage
  echo. make-dist.bat - tool for making binary distributions of SocialDataAggregator
  echo. 
  echo. usage:
  echo. make-dist.bat "outputDir"
  echo. - "outputDir" output directory where to build sda
  echo.
goto:eof