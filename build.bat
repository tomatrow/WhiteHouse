@echo off
if exist "%PROGRAMFILES(X86)%" (set swt="lib\swt-win64.jar") else (set swt="lib\swt-win.jar")
echo Using %swt% ...
if %1==compile javac -cp .;%swt% -d . *.java
if %1==run java -cp .;%swt% com.github.redhatter.whitehouse.WhiteHouse