@echo off
echo =========================================
echo    Building MiniBank Project (JDK 17+)   
echo =========================================

if exist bin rmdir /s /q bin
mkdir bin

echo [1/2] Compiling Java source files...
for /r src %%i in (*.java) do (
    javac -d bin "%%i"
)

echo [2/2] Packaging runnable minibank.jar...
jar --create --file minibank.jar --main-class minibank.MiniBank -C bin .

echo =========================================
echo    BUILD SUCCESSFUL: minibank.jar created
echo    Run with: run.bat or java -jar minibank.jar
echo =========================================
