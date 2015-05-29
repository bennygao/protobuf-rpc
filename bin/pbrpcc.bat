@ECHO OFF

SETLOCAL EnableDelayedExpansion
SET BINDIR=%~dp0
SET BATNAME=%0
SET TARGET=""
SET FLAG=""
SET OUTDIR=""

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
IF NOT "%TARGET%"=="javanano" (
	IF NOT "%TARGET%"=="javasvc" (
		IF NOT "%TARGET%"=="objc" (
			IF NOT "%TARGET%"=="html" (
				GOTO Usage
			)
		)
	)
)

IF %OUTDIR%=="" (GOTO Usage)

SET GENRPC_ARGS=%TARGET% %OUTDIR%
REM ECHO %GENRPC_ARGS%
IF "%TARGET%"=="javanano" (
	%BINDIR%\protoc.exe --javanano_out=%OUTDIR% %1 %2 %3 %4 %5 %6 %7 %8 %9
) ELSE IF "%TARGET%"=="javasvc" (
	%BINDIR%\protoc.exe --plugin=protoc-gen-rpc=%BINDIR%\protoc-gen-rpc.bat --rpc_out=%OUTDIR% %1 %2 %3 %4 %5 %6 %7 %8 %9
) ELSE IF "%TARGET%"=="objc" (
	%BINDIR%\protoc.exe --plugin=protoc-gen-objc=%BINDIR%\protoc-gen-objc.exe --objc_out=%OUTDIR% --plugin=protoc-gen-rpc=%BINDIR%\protoc-gen-rpc.bat --rpc_out=%OUTDIR% %1 %2 %3 %4 %5 %6 %7 %8 %9
) ELSE IF "%TARGET%"=="html" (
	%BINDIR%\protoc.exe --plugin=protoc-gen-rpc=%BINDIR%\protoc-gen-rpc.bat --rpc_out=%OUTDIR% %1 %2 %3 %4 %5 %6 %7 %8 %9
)

GOTO end

:Usage
ECHO Usage:
ECHO     Generate Java nano classes for messages.
ECHO         %BATNAME% -g javanano -o out_dir -p proto_file ...
ECHO     Generate Java RPC services classes.
ECHO         %BATNAME% -g javasvc -o out_dir -p proto_file ...
ECHO     Generate Objective-C messages and services source code.
ECHO         %BATNAME% -g objc -o out_dir -p proto_file ...
ECHO     Generate HTML documents for messages and services.
ECHO         %BATNAME% -g html -o out_dir -p proto_file ...

:end