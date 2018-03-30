package main.services;

import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;


public class FileStorageService {
    String path;

    public FileStorageService() throws IOException {
        this.path = "static/images";
        createStorageDirectory();
    }

    private void createStorageDirectory() throws IOException {
        if (!Files.exists(Paths.get(this.path))) {
            Files.createDirectories(Paths.get(this.path));
        } else {
            System.out.println("static find");
        }
    }

//    public void someMethod(MultipartFile file) {
//        try {
//            final byte []  byteArr = file.getBytes();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }

}
