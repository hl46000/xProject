package com.purehero.module.filelist.common;

/**
 * Created by purehero on 2017-04-03.
 */

public interface iFile {
    public String getAbsolutePath();
    public String getName();
    public String getParent();

    public boolean isDirectory();
    public boolean isFile();
    public boolean exists();
    public long length();
    public long lastModified();

    /**
     * isDirectory() 함수가 true 인 경우 해당 directory 내부의 item 개수를 반환해 준다.
     * @return 해당 directory 내부의 item( file/directory ) 개수
     */
    public int getSubItems();
}
