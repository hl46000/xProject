package com.purehero.common;

import android.content.Context;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import share.file.purehero.com.fileshare.R;

/**
 * Created by MY on 2017-03-15.
 */

public class StorageUtils {
    public static class StorageInfo {
        public final String path;
        public final boolean readonly;
        public final boolean removable;
        public final int number;

        StorageInfo(String path, boolean readonly, boolean removable, int number) {
            this.path = path;
            this.readonly = readonly;
            this.removable = removable;
            this.number = number;
        }

        public String getDisplayName( Context context ) {
            StringBuilder res = new StringBuilder();
            if (!removable) {
                res.append( context.getString( R.string.internal_sdcard ));
            } else if (number > 1) {
                res.append(context.getString( R.string.sdcard ) + number);
            } else {
                res.append(context.getString( R.string.sdcard ));
            }
            if (readonly) {
                res.append(" (Read only)");
            }
            return res.toString();
        }
    }

    public static Map<String,StorageInfo> getStorageList() {

        Map<String,StorageInfo> list = new HashMap<String,StorageInfo>();
        String def_path = Environment.getExternalStorageDirectory().getPath();
        boolean def_path_removable = Environment.isExternalStorageRemovable();
        String def_path_state = Environment.getExternalStorageState();
        boolean def_path_available = def_path_state.equals(Environment.MEDIA_MOUNTED)
                || def_path_state.equals(Environment.MEDIA_MOUNTED_READ_ONLY);
        boolean def_path_readonly = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED_READ_ONLY);

        HashSet<String> paths = new HashSet<String>();
        int cur_removable_number = 1;

        if (def_path_available) {
            paths.add(def_path);
            //list.add(0, new StorageInfo(def_path, def_path_readonly, def_path_removable, def_path_removable ? cur_removable_number++ : -1));
            list.put( def_path, new StorageInfo(def_path, def_path_readonly, def_path_removable, def_path_removable ? cur_removable_number++ : -1));
        }

        BufferedReader buf_reader = null;
        try {
            buf_reader = new BufferedReader(new FileReader("/proc/mounts"));
            String line;
            while ((line = buf_reader.readLine()) != null) {
                line = line.trim();
                if (!line.startsWith("/mnt")) continue;

                StringTokenizer tokens = new StringTokenizer(line, " ");
                String unused = tokens.nextToken(); //device
                String mount_point = tokens.nextToken(); //mount point
                if (paths.contains(mount_point)) {
                    continue;
                }
                File mount_point_file = new File( mount_point );
                if( !mount_point_file.canRead()) continue;

                unused = tokens.nextToken(); //file system
                List<String> flags = Arrays.asList(tokens.nextToken().split(",")); //flags
                boolean readonly = flags.contains("ro");

                paths.add(mount_point);
                //list.add(new StorageInfo(mount_point, readonly, true, cur_removable_number++));
                list.put(mount_point,new StorageInfo(mount_point, readonly, true, cur_removable_number++));
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (buf_reader != null) {
                try {
                    buf_reader.close();
                } catch (IOException ex) {}
            }
        }
        return list;
    }
}
