rm -rf %~dp0assets\probe

if not exist %~dp0assets\probe mkdir %~dp0assets\probe
if not exist %~dp0assets\probe\x86 mkdir %~dp0assets\probe\x86
if not exist %~dp0assets\probe\armeabi mkdir %~dp0assets\probe\armeabi

copy /y %~dp0\libs\armeabi\test_tool %~dp0assets\probe\armeabi\test_tool

copy /y %~dp0\libs\armeabi\probe %~dp0assets\probe\armeabi\probe
copy /y %~dp0\libs\x86\probe %~dp0assets\probe\x86\probe