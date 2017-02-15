#include <stdio.h>
#include <stdlib.h>
#include <jni.h>

// JNI_OnLoad
jint JNI_OnLoad( JavaVM* vm, void* )
{
	asm (
			"mov X0, #0;"
			"str X0,[sp, #-16]!;"
			"mov X0, #1;"
			"ldr X0,[sp],#16;"
			"mov X0, #2;"
			"mov X0, #3;"
			"mov X0, #4;"
			"mov X0, #5;"
			"mov X0, #6;"
			"mov X0, #7;"
			"mov X0, #8;"
			"mov X0, #9;"
			"mov X0, #11;"
	);

	return JNI_VERSION_1_4;
}
