@ECHO OFF

setlocal EnableDelayedExpansion
SET BINDIR=%~dp0
SET BATNAME=%0


:Loop
IF "%1"=="" GOTO Continue
    IF "%1"=="-g" (
		SET FLAG="-g"
	) ELSE IF "%1"=="-o" (
		SET FLAG="-o"
	) ELSE IF "%1"=="-h" (
		GOTO usage
	) ELSE (
		IF %FLAG%=="-g" (
			SET TARGET=%1
			SET FLAG=""
		) ELSE IF %FLAG%=="-o" (
			SET OUTDIR=%1
			SET FLAG=""
		) ELSE (
			IF %TARGET%=="" (GOTO Usage)
			IF %OUTDIR%=="" (GOTO Usage)
			GOTO Continue
		)
	)
SHIFT
GOTO Loop
:Continue

SHIFT /4
IF NOT "%TARGET%"=="java" (
	IF NOT "%TARGET%"=="objc" (
		IF NOT "%TARGET%"=="html" (
			GOTO Usage
		)
	)
)

IF %OUTDIR%=="" (GOTO Usage)

SET GENRPC_ARGS=%TARGET% %OUTDIR%
REM ECHO %GENRPC_ARGS%
IF "%TARGET%"=="java" (
	%BINDIR%\protoc.exe --java_out=%OUTDIR% --plugin=protoc-gen-rpc=%BINDIR%\protoc-gen-rpc.bat --rpc_out=%OUTDIR% %1 %2 %3 %4 %5 %6 %7 %8 %9
) ELSE IF "%TARGET%"=="objc" (
	ECHO "Don't support generate objective-c source on Windows."
) ELSE IF "%TARGET%"=="html" (
	%BINDIR%\protoc.exe --plugin=protoc-gen-rpc=%BINDIR%\protoc-gen-rpc.bat --rpc_out=%OUTDIR% %1 %2 %3 %4 %5 %6 %7 %8 %9
)

GOTO end

:Usage
ECHO Usage:%BATNAME% -g java -o out_dir -p proto_file

:end