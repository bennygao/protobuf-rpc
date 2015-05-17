@ECHO OFF

SET BATNAME=%0
SET FLAG=""
SET TARGET=""
SET OUTDIR=""
SET PROTO=""

:Loop
IF "%1"=="" GOTO Continue
    IF "%1"=="-g" (
		SET FLAG="-g"
	) ELSE IF "%1"=="-o" (
		SET FLAG="-o"
	) ELSE IF "%1"=="-p" (
		SET FLAG="-p"
	) ELSE IF "%1"=="-h" (
		GOTO usage
	) ELSE (
		IF %FLAG%=="-g" (
			SET TARGET=%1
			SET FLAG=""
		) ELSE IF %FLAG%=="-o" (
			SET OUTDIR=%1
			SET FLAG=""
		) ELSE IF %FLAG%=="-p" (
			SET PROTO=%1
			SET FLAG=""
		) ELSE (
			GOTO Usage
		)
	)
SHIFT
GOTO Loop
:Continue

IF NOT "%TARGET%"=="java" (
	IF NOT "%TARGET%"=="objc" (
		IF NOT "%TARGET%"=="html" (
			GOTO Usage
		)
	)
)

IF %PROTO%=="" (GOTO Usage)
IF %OUTDIR%=="" (GOTO Usage)

SET GENRPC_ARGS=%TARGET% %OUTDIR%
ECHO %GENRPC_ARGS%

ECHO succ
GOTO end

:Usage
ECHO Usage:%BATNAME% -g java -o out_dir -p proto_file

:end