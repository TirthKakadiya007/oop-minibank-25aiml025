@echo off
echo =========================================
echo    Running MiniBank JUnit 5 Test Suite   
echo =========================================

if not exist bin (
    call build.bat
)

if exist bin-test rmdir /s /q bin-test
mkdir bin-test

for /r test %%i in (*.java) do (
    javac -cp "bin;lib/junit-platform-console-standalone.jar" -d bin-test "%%i"
)

java -jar lib/junit-platform-console-standalone.jar execute -cp "bin;bin-test" --select-class minibank.MiniBankTest
