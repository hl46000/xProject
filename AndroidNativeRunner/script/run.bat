@echo off

call E:\SDK\batch\adb\ADB_common.bat

adb -s %DEVICE_ID% push %1 /mnt/sdcard/temp/z100
adb -s %DEVICE_ID% shell su -c cp /mnt/sdcard/temp/z100 /data/z100
adb -s %DEVICE_ID% shell su -c chmod 777 /data/z100
adb -s %DEVICE_ID% shell su -c /data/z100

adb -s %DEVICE_ID% shell su -c rm /data/z100
adb -s %DEVICE_ID% shell su -c rm /mnt/sdcard/temp/z100