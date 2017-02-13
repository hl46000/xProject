// Change below line to =====> #include "YOUR_GAME_PROJECT_NAME.h"
//#include "project_name.h"

//#if PLATFORM_ANDROID
#if 1
#include "AppSealingSecurity_Unreal.h"
#include <dlfcn.h>
#ifndef MAX_PATH
#define MAX_PATH 260
#endif
#ifndef APPSEALING_DYNAMIC_NAME
#define APPSEALING_DYNAMIC_NAME "libcovault-appsec.so"
#endif
unsigned long ___cec90c3d=0,___dec90c3e=0,___eec90c3d=0,___aec90c3d=0;
long *___0xDCD,*___0xECD,*___0xFCD,___0xAAA=2,___0xBBB=1,___0xCCC;
void ___eec90c3f(const char* tag,unsigned char* addr,int len,bool) {};
const char *  gsft="____appsec_tag_ASD8SD2SAD1ASDNJK";
const char * __ass_s_4___(const char *p){const char *ch="____appsec_tag_ASD8SD2SAD1ASDNJK";return ch;}
int appsealing_init(const char*, unsigned char* ptr, int length, bool)
{
	___aec90c3d=___cec90c3d=___dec90c3e=(unsigned long)___eec90c3f;___eec90c3d=(unsigned long)___eec90c3f;
	__ass_s_4___(NULL);
	void* handle=dlopen(APPSEALING_DYNAMIC_NAME,RTLD_LAZY);
	const char* errmsg=dlerror();
	if(handle==NULL&&errmsg!=NULL){/*UE_LOG(LogTemp,Warning,TEXT("<AppSealing> dlopen fail. ( %s )"),ANSI_TO_TCHAR(errmsg));*/return -1;}
	___0xDCDFCD gptr = (___0xDCDFCD)dlsym(handle, "get_ptr");errmsg = dlerror();
	if(gptr==NULL||errmsg!= NULL){/*UE_LOG(LogTemp,Warning,TEXT("<AppSealing> dlsym fail. ( %s )"),ANSI_TO_TCHAR(errmsg));*/return -1;}
	___0xDCDDCD __=(___0xDCDDCD)gptr(handle);___cec90c3d=__(NULL,NULL,0x99BEEDDA,true);___dec90c3e=__(NULL,NULL,0x34698AFD,true);___eec90c3d=__(NULL,NULL,0x9347500E,true);
	return  0;
}
#endif
