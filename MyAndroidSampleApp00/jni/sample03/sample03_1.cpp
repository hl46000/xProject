#include <stdio.h>
#include <stdlib.h>
#include <jni.h>

// JNI_OnLoad
jint JNI_OnLoad( JavaVM* vm, void* )
{
	asm (
			"mov X0, #0;"
			"mov X1, #128;"
			"mov X2, #0;"
			"mov X3, #0;"
			"mov X4, #0;"
			"mov X5, #0;"
			"mov X6, #0;"
			"mov X7, #0;"
			"mov X8, #0;"
			"mov X9, #0;"
			"mov X10, #0;"
	);

	return JNI_VERSION_1_4;
}
