#pragma once
//#if PLATFORM_ANDROID
#if 1
#include <string>
#ifdef __aarch64__
#define	POSTFIX		"\n\tldr X0,[sp],#16\n\t"
#define	OP_MOVE		"\n\tmov X0, "
#define	BASE_TAG	"str X0,[sp, #-16]!" OP_MOVE "#2" OP_MOVE "#0" OP_MOVE "#6" OP_MOVE "#8" OP_MOVE "#1" OP_MOVE "#5" OP_MOVE "#1" OP_MOVE "#5" OP_MOVE "#7" OP_MOVE
#else
#define	POSTFIX		"\n\tpop {R0}\n\t"
#define	OP_MOVE		"\n\tadd R0, "
#define	BASE_TAG	"push {R0}" OP_MOVE "#2" OP_MOVE "#0" OP_MOVE "#6" OP_MOVE "#8" OP_MOVE "#1" OP_MOVE "#5" OP_MOVE "#1" OP_MOVE "#5" OP_MOVE "#7" OP_MOVE
#endif
#define	DELIMETER	"#"
#define	LOAD_ADDRESS ___aosc=(unsigned long)&&___________b;___losc=(unsigned long)&&___________e-___aosc+4;
#define DECLARE		unsigned long ___aosc, ___losc;
#define ASM	__asm__
#define	AS_START_IDENTIFIER	DELIMETER"9"
#define	AS_END_IDENTIFIER	DELIMETER"11"
#define AS_DEC_MASK	0xDF087D
#define AS_ENC_MASK	0xED9A3F
typedef unsigned long(*___0xDCDDCD)(const char*,unsigned char*,int,bool);
typedef void(*___0xDCDECD)(const char*,unsigned char*,int,bool);
typedef unsigned long(*___0xDCDFCD)(void*);
extern long *___0xDCD,*___0xECD,*___0xFCD,___0xAAA,___0xBBB,___0xCCC;
extern unsigned long ___cec90c3d,___dec90c3e,___eec90c3d,___aec90c3d;
extern int appsealing_init(const char*, unsigned char*, int, bool);
static void __f0ba347d() {if(___aec90c3d==0)appsealing_init(0,0,0,false);___0xAAA=1;___0xBBB=2;___0xCCC=3; if(___0xDCD==0)___0xDCD=new long;*___0xDCD=(long)___cec90c3d^AS_DEC_MASK;if(___0xECD==0)___0xECD=new long;*___0xECD=(long)___dec90c3e^AS_ENC_MASK;if(___0xFCD==0)___0xFCD=new long;*___0xFCD=(long)___eec90c3d^AS_ENC_MASK;}
#define APPSEALING_ENCRYPTION_BEGIN			do{__f0ba347d();DECLARE LOAD_ADDRESS ((___0xDCDECD)(*___0xDCD^AS_DEC_MASK))(__PRETTY_FUNCTION__,(unsigned char*)___aosc,___losc,1);}while(0);ASM(BASE_TAG AS_START_IDENTIFIER POSTFIX);___________b:
#define APPSEALING_ENCRYPTION_END			___________e:ASM(BASE_TAG AS_END_IDENTIFIER POSTFIX); ((___0xDCDECD)(*___0xECD^AS_ENC_MASK))(__PRETTY_FUNCTION__,0,0,1);
#define APPSEALING_ENCRYPTION_END_RETURN(...)	___________e:ASM(BASE_TAG AS_END_IDENTIFIER POSTFIX); ((___0xDCDECD)(*___0xECD^AS_ENC_MASK))(__PRETTY_FUNCTION__,0,0,1);return(__VA_ARGS__);
#define APPSEALING_ENCRYPTION_RETURN(...)	{((___0xDCDECD)(*___0xECD^AS_ENC_MASK))(__PRETTY_FUNCTION__,0,0,0);if(___0xAAA<___0xBBB)return(__VA_ARGS__);else return(__VA_ARGS__);}
#define APPSEALING_ENCRYPTION_RETURN_VOID	{((___0xDCDECD)(*___0xECD^AS_ENC_MASK))(__PRETTY_FUNCTION__,0,0,0);if(___0xAAA<___0xBBB)return;else return;}
#else
#define APPSEALING_ENCRYPTION_BEGIN
#define APPSEALING_ENCRYPTION_END
#define APPSEALING_ENCRYPTION_END_RETURN(...)	return(__VA_ARGS__);
#define APPSEALING_ENCRYPTION_RETURN(...)	return(__VA_ARGS__);
#define APPSEALING_ENCRYPTION_RETURN_VOID	return;
#endif
#define __PRETTY_FUNCTION__	__FUNCTION__
#ifndef APPSEALING_LOGT
#define APPSEALING_LOGT	UE_LOG(LogTemp, Warning, TEXT("<AppSealing> ................ %s:%s %s"), *(FString(__PRETTY_FUNCTION__).Left(FString(__PRETTY_FUNCTION__).Find(TEXT(":")))), *(FString(__FUNCTION__)), *(FString::FromInt(__LINE__)));
#endif
