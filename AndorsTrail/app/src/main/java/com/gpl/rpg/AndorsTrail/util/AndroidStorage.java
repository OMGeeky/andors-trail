package com.gpl.rpg.AndorsTrail.util;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;

import com.gpl.rpg.AndorsTrail.controller.Constants;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class AndroidStorage {
    public static File getStorageDirectory(Context context, String name) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return context.getExternalFilesDir(name);
        }
        else {
            File root = Environment.getExternalStorageDirectory();
            return new File(root, name);
        }
    }
    public static boolean shouldMigrateToInternalStorage(Context context) {
        boolean ret = false;
        File externalSaveGameDirectory = new File(Environment.getExternalStorageDirectory(), Constants.FILENAME_SAVEGAME_DIRECTORY);
        File internalSaveGameDirectory = getStorageDirectory(context, Constants.FILENAME_SAVEGAME_DIRECTORY);

        if (externalSaveGameDirectory.exists()
                && externalSaveGameDirectory.isDirectory()
                && externalSaveGameDirectory.listFiles().length > 0
                && (
                !internalSaveGameDirectory.exists()
                        || internalSaveGameDirectory.isDirectory() && internalSaveGameDirectory.listFiles().length < 2)
                ) {
            ret = true;
        }
        return ret;
    }

    public static boolean migrateToInternalStorage(Context context) {
        try {
            copy(new File(Environment.getExternalStorageDirectory(), Constants.CHEAT_DETECTION_FOLDER),
                    getStorageDirectory(context, Constants.CHEAT_DETECTION_FOLDER));
            copy(new File(Environment.getExternalStorageDirectory(), Constants.FILENAME_SAVEGAME_DIRECTORY),
                    getStorageDirectory(context, Constants.FILENAME_SAVEGAME_DIRECTORY));
        } catch (IOException e) {
            L.log("Error migrating data: " + e.toString());
            return false;
        }
        return true;
    }

    private static void copy(File sourceLocation, File targetLocation) throws IOException {
        if (!sourceLocation.exists()) {
            return;
        }
        if (sourceLocation.isDirectory()) {
            copyDirectory(sourceLocation, targetLocation);
        } else {
            copyFile(sourceLocation, targetLocation);
        }
    }

    private static void copyDirectory(File source, File target) throws IOException {
        if (!target.exists()) {
            target.mkdir();
        }

        for (String f : source.list()) {
            copy(new File(source, f), new File(target, f));
        }
    }

    private static void copyFile(File source, File target) throws IOException {
        InputStream in = null;
        OutputStream out = null;
        try {
            in = new FileInputStream(source);
            out = new FileOutputStream(target);
            byte[] buf = new byte[1024];
            int length;
            while ((length = in.read(buf)) > 0) {
                out.write(buf, 0, length);
            }
        } finally {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
        }
    }

    public static String getUrlForFile(Context context, File worldmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Uri uri = FileProvider.getUriForFile(context, "com.gpl.rpg.AndorsTrail.fileprovider", worldmap);
            return uri.toString();
        } else {
            return "file://" + worldmap.getAbsolutePath();
        }
    }
}
