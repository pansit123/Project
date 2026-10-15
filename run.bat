@echo off
rem Always run from the project folder: data\ and images\ are found relative to it
cd /d "%~dp0"
if not exist out mkdir out
rem -sourcepath lets javac find and compile every class that Main uses
javac -encoding UTF-8 -d out -sourcepath src src\busbooking\Main.java
if errorlevel 1 (pause & exit /b 1)
java -cp out busbooking.Main
pause
