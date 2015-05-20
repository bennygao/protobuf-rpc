#include <sys/types.h>
#include <sys/uio.h>
#include <unistd.h>
#include <stdio.h>

int main(int argc, char* argv[])
{
	int cnt;
	char buffer[1024];
	FILE *binf = fopen("proto.bin", "w");

	while ((cnt = read(0, buffer, 1024)) > 0) {
		fwrite(buffer, cnt, 1, binf);
	}

	fclose(binf);
	return 0;
}
