package com.nailiqi.travellingpaws.Utils;

import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FilePathMethods {

    // root - storage/emulated/0
    public String ROOT = Environment.getExternalStorageDirectory().getPath();

    public String PICTURES = ROOT + "/Pictures";
    public String CAMERA = ROOT + "/DCIM/camera";
    public String FIREBASE_STORAGE_PHOTOS = "photos/users";

    public static List<String> getDirectoryPaths(String dir){
        List<String> dirPaths = new ArrayList<>();
        File file = new File(dir);
        File[] listfiles = file.listFiles();

        if(listfiles != null){
            for(int i = 0; i < listfiles.length; i++){
                if(listfiles[i].isDirectory()){
                    dirPaths.add(listfiles[i].getAbsolutePath());
                }
            }
        }

        return dirPaths;
    }

    public static List<String> getFilePaths(String dir){
        List<String> filePaths = new ArrayList<>();
        File file = new File(dir);
        File[] listfiles = file.listFiles();

        if(listfiles != null) {
            for(int i = 0; i < listfiles.length; i++){
                if(listfiles[i].isFile()){
                    filePaths.add(listfiles[i].getAbsolutePath());
                }
            }
        }

        return filePaths;
    }
}
