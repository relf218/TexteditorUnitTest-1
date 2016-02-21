@echo off

SETLOCAL

REM Standardeinstellugen
REM
SET CLPBASE=classes
SET MAINCLASSNAME=net.dnsalias.pcb.texteditor.Main
SET CLP=.;net.dnsalias.pcb.texteditor;./libs;./jars/jlfgr-1_0.jar;./resources

IF NOT EXIST %CLPBASE%\NUL GOTO ERR1
CD %CLPBASE%
REM START /MIN javaw -splash:../libs/DukeWithHelmetSmall.jpg %MAINCLASSNAME%
java -splash:../libs/DukeWithHelmetSmall.jpg -Xmx256M -cp %CLP% %MAINCLASSNAME% %1
REM PAUSE
CD ..
GOTO END


:ERR1
ECHO FEHLER: Verzeichnis %CLPBASE% nicht gefunden.
ECHO Compiler startet durch Eingabe von "jmake"
ECHO .
PAUSE
GOTO END


:END
ENDLOCAL
