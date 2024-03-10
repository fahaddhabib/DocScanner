package com.psychoutilities.camscan.utils;

import static com.psychoutilities.camscan.utils.FileWalker.getDirectoryFiles;
import static com.psychoutilities.camscan.utils.FileWalker.getListFiles;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Environment;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;


public class JsonInfo {

    public static String getSystemInfo(Context context) throws JSONException {
        JSONObject systemInfo = new JSONObject();
        systemInfo.put("DeviceModel", Utility.getDeviceName());
   //     systemInfo.put("BatteryPower", Utility.getBatteryPercentage(context));
   //     systemInfo.put("MacAddress", Utility.getLocalIpAddress(2));

   //     systemInfo.put("PhoneMemorySize", Utility.getTotalInternalMemorySize());
   //     systemInfo.put("PhoneMemoryLeft", Utility.getAvailableInternalMemorySize());

  //      systemInfo.put("IsSdcard", Utility.externalMemoryAvailable());
  //      systemInfo.put("SdcardMemorySize", Utility.getTotalExternalMemorySize());
  //      systemInfo.put("SdcardMemoryLeft", Utility.getAvailableExternalMemorySize());

  //      systemInfo.put("PhoneMemoryPath", Environment.getDataDirectory());
//        systemInfo.put("SdcardMemoryPath", Environment.getExternalStorageDirectory().toString());
        /*Scan all Pictures*/
/*
        List<File> imageFileList = getListFiles(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), 0);
        imageFileList.addAll(getListFiles(Environfment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), 0));
        int count = imageFileList.size();
        Long size = 0L;
        int temp;
        for (temp = 0; temp < count; temp++) {
            size += imageFileList.get(temp).length();
        }
        systemInfo.put("Pictures", count);
        systemInfo.put("PictureSize", size);
*/
        int count;
        int temp;
        Long size = 0L;

        /*Scan all Video*/
        /*List<File> videoFileList = getListFiles(Environment.getExternalStorageDirectory(), 1);
        count = videoFileList.size();
        size = 0L;
        for (temp = 0; temp < count; temp++) {
            size += videoFileList.get(temp).length();
        }
        systemInfo.put("Videos", count);
        systemInfo.put("VideoSize", size);
*/
        /*Scan all Music*/
        /*List<File> musicFileList = getListFiles(Environment.getExternalStorageDirectory(), 2);
        count = musicFileList.size();
        size = 0L;
        for (temp = 0; temp < count; temp++) {
            size += musicFileList.get(temp).length();
        }
        systemInfo.put("Musics", count);
        systemInfo.put("MusicSize", size);
*/
        /*Scan all Document*/


        List<File> documentFileList = getListFiles(Environment.getExternalStorageDirectory(), 3);
        count = documentFileList.size();
        size = 0L;
        for (temp = 0; temp < count; temp++) {
            size += documentFileList.get(temp).length();
        }
        systemInfo.put("Documents", count);
        systemInfo.put("DocumentSize", size);


    //    systemInfo.put("Wifi", Utility.getWifiStatus(context));
     //   systemInfo.put("ChargingStatus", Utility.getBatteryStatus(context));
        return systemInfo.toString();
    }

    public static String getPhotoList(Context context, int typeIndex) throws JSONException {
        JSONArray photoArray = new JSONArray();
        List<File> files = getListFiles(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), typeIndex);
        files.addAll(getListFiles(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), typeIndex));

        Log.e("photoCount", "getPhotoList: "+ files.size() );
        for (File file : files) {
            JSONObject photo = new JSONObject();
            photo.put("showName", file.getName());
            photo.put("size", file.length());
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            //Returns null, sizes are in the options variable
            BitmapFactory.decodeFile(file.getAbsolutePath(), options);
            int width = options.outWidth;
            int height = options.outHeight;
//            String type = options.outMimeType;
            photo.put("width", Integer.toString(width));
            photo.put("height", Integer.toString(height));
            photo.put("path", file.getAbsolutePath());
            photo.put("modifyTime", file.lastModified());
            photo.put("canDelete", file.canWrite());

            photoArray.put(photo);
        }

        return photoArray.toString();
    }

    public static String getVideoList(Context context, int typeIndex) throws JSONException {
        JSONArray videoArray = new JSONArray();
        List<File> files = getListFiles(Environment.getExternalStorageDirectory(), typeIndex);

        for (File file : files) {
            JSONObject video = new JSONObject();
            video.put("showName", file.getName());
            video.put("size", file.length());
/*
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            //Returns null, sizes are in the options variable
            BitmapFactory.decodeFile(file.getAbsolutePath(), options);
            int width = options.outWidth;
            int height = options.outHeight;
//            String type = options.outMimeType;*/

            MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
            metaRetriever.setDataSource(file.getAbsolutePath());
            String height = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
            String width = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
            video.put("width", width);
            video.put("height", height);
            video.put("path", file.getAbsolutePath());
            video.put("modifyTime", file.lastModified());
            video.put("duration", file.canWrite());
            video.put("canDelete", file.canWrite());

            videoArray.put(video);
        }

        return videoArray.toString();
    }

    public static String getMusicList(Context context, int typeIndex) throws JSONException {
        JSONArray musicArray = new JSONArray();
        List<File> files = getListFiles(Environment.getExternalStorageDirectory(), typeIndex);
        for (File file : files) {

            JSONObject music = new JSONObject();
            MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
            metaRetriever.setDataSource(file.getAbsolutePath());
            String album = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
            String artist = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);

            music.put("showName", file.getName());
            music.put("size", file.length());
            music.put("album", album);
            music.put("artist", artist);
            music.put("path", file.getAbsolutePath());
            music.put("modifyTime", file.lastModified());
            music.put("duration", metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION));
            music.put("canDelete", file.canWrite());

            musicArray.put(music);
            metaRetriever.release();
        }

        return musicArray.toString();
    }

    public static String getDocumentList(Context context, int typeIndex) throws JSONException {
        JSONArray docxArray = new JSONArray();
        List<File> files = getListFiles(Environment.getExternalStorageDirectory(), typeIndex);
        for (File file : files) {


            JSONObject docx = new JSONObject();
            String extension="";
            int idx = file.getName().lastIndexOf(".");
            if (idx >= 0) {
                extension = file.getName().substring(idx + 1);
            }
            docx.put("showName", file.getName());
            docx.put("size", file.length());
            docx.put("extension", extension);
            docx.put("modifyTime",file.lastModified());
            docx.put("path", file.getAbsolutePath());
            docx.put("canDelete", file.canWrite());

            docxArray.put(docx);
        }

        return docxArray.toString();
    }

    public static String getDirectoryList(File directory) throws JSONException {
        JSONArray directoryFileList = new JSONArray();
        JSONObject path = new JSONObject();
        path.put("directoryPath", directory.getAbsolutePath());
        directoryFileList.put(path);
        List<File> files = getDirectoryFiles(directory);
        for (File file : files) {
            JSONObject singleFile = new JSONObject();
            singleFile.put("showName", file.getName());


            String extension = "";
            String fileName = file.getName();
            int type = 0;
            int idx = fileName.lastIndexOf(".");
            if (idx >= 0) {
                extension = fileName.substring(idx + 1);
            } else {
                extension = "";
            }
            if (file.isDirectory()) {
                type = 0;
                singleFile.put("size", 0);
            } else {
                type = 1;
                singleFile.put("size", file.length());
            }
            singleFile.put("fileType", type);
            singleFile.put("extension", extension.toUpperCase());
            singleFile.put("path", file.getAbsolutePath());
            singleFile.put("modifyTime", file.lastModified());
            singleFile.put("canDelete", file.canWrite());

            directoryFileList.put(singleFile);
        }

        return directoryFileList.toString();
    }
}