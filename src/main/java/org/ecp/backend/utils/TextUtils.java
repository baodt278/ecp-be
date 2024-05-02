package org.ecp.backend.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
@Slf4j
public class TextUtils {
    public static void createTextFile(String directoryPath, String fileName) {
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        File file = new File(directory, fileName + ".txt");
        try {
            if (file.createNewFile()) {
                log.info("Tập tin đã được tạo thành công.");
            } else {
                log.info("Tập tin đã tồn tại.");
            }
        } catch (IOException e) {
            log.info("Đã xảy ra lỗi khi tạo tập tin: " + e.getMessage());
        }
    }

    public static List<String> getTxtFileNames(String directoryPath) {
        List<String> txtFiles = new ArrayList<>();
        File directory = new File(directoryPath);
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && file.getName().toLowerCase().endsWith(".txt")) {
                        txtFiles.add(file.getName());
                    }
                }
            }
        } else {
            log.info("Directory does not exist or is not a directory.");
        }
        return txtFiles;
    }
}
