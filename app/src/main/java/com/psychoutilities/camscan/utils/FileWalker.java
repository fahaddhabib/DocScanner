package com.psychoutilities.camscan.utils;


import static com.psychoutilities.camscan.utils.Utility.getExtension;

import android.os.Environment;
import android.util.Log;


import com.psychoutilities.camscan.main_utils.Constant;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileWalker {

    public static List<File> getListFiles(File parentDir, int type) {
        ArrayList<File> inFiles = new ArrayList<File>();
        File[] files = parentDir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    if (!file.isHidden()) {
                        if (!file.getPath().equals(Environment.getExternalStorageDirectory().toString() + "/Android")) {
                            File f = new File(file.getAbsolutePath() + "/.nomedia");
                            if (!f.exists()) {
                                inFiles.addAll(getListFiles(file, type));
                            }
                        }
                    }
                } else {
                    String ext = getExtension(file.getName());
                    if (ext != "" && Arrays.asList(Constant.EXTENSIONS[type]).contains(ext)) {
                        inFiles.add(file);
                        Log.i("file:", file.getName());
                    }
                }
            }
        }
        return inFiles;
    }


    public static List<File> getDirectoryFiles(File parentDir) {
        ArrayList<File> inFiles = new ArrayList<File>();
        File[] files = parentDir.listFiles();
        if (files != null) {
            for (File file : files) {
                inFiles.add(file);
                Log.i("file:", file.getName());
            }
        }
        return inFiles;
    }
}