@echo off
cd /d "%~dp0"
if not exist out mkdir out
if exist sources.tmp del sources.tmp
for /r busbooking %%f in (*.java) do echo "%%f">> sources.tmp
for /r login %%f in (*.java) do echo "%%f">> sources.tmp
javac -encoding UTF-8 -d out @sources.tmp
set ERR=%ERRORLEVEL%
del sources.tmp
if not "%ERR%"=="0" (pause & exit /b %ERR%)
java -cp out;. busbooking.Main
pause
