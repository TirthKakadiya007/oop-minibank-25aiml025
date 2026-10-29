@echo off
if not exist minibank.jar (
    echo minibank.jar not found. Running build first...
    call build.bat
)
java -jar minibank.jar
