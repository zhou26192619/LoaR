package com.loar.storage;

import android.content.Context;
import android.graphics.Bitmap;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class FileOperator {
    /**
     * @param dir      目录
     * @param fileName 文件名
     * @return
     */
    public static File createFile(String dir, String fileName) {
        File fDir = new File(dir);
        if (!fDir.exists()) {
            fDir.mkdirs();
        }
        File file = new File(fDir, fileName);
        return file;
    }

    public static boolean isExists(String filePath) {
        File fDir = new File(filePath);
        return fDir.exists();
    }

    // public static void saveFile(OutputStream os, String dir, String fileName)
    // {
    // try {
    // File file = initFile(dir, fileName);
    // os.flush();
    // os.close();
    // } catch (FileNotFoundException e) {
    // e.printStackTrace();
    // } catch (IOException e) {
    // e.printStackTrace();
    // }
    // }

    public static File inputstreamToFile(InputStream ins, String dir, String fileName) {
        File file = createFile(dir, fileName);
        try {
            OutputStream os = new FileOutputStream(file);
            int bytesRead = 0;
            byte[] buffer = new byte[4096];
            while ((bytesRead = ins.read(buffer, 0, 4096)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            ins.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    public static File inputstreamToFile(String content, String dir, String fileName) {
        File file = createFile(dir, fileName);
        if (content == null) {
            return file;
        }
        try {
            FileOutputStream stream = new FileOutputStream(file, true);
            OutputStreamWriter output = new OutputStreamWriter(stream);
            BufferedWriter bw = new BufferedWriter(output);
            bw.newLine();
            //写入相关Log到文件
            bw.append(content);
            bw.newLine();
            bw.flush();
            bw.close();
            output.close();
            stream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    public static void saveBitmap(Bitmap bm, String dir, String fileName)
            throws IOException {
        File file = createFile(dir, fileName);
        FileOutputStream os = new FileOutputStream(file);
        bm.compress(Bitmap.CompressFormat.JPEG, 100, os);
        os.flush();
        os.close();
    }

    public static String inputstreamToString(InputStream in) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            BufferedReader bf = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }
}
