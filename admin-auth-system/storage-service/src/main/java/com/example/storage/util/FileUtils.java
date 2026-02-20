package com.example.storage.util;

public class FileUtils{

    private FileUtils(){}

    public static String getFileExtension(String fileName){
        if(fileName == null || fileName.isBlank()){
            return "";
        }
        int dot = fileName.lastIndexOf('.');
        if(dot<0 || dot == fileName.length()-1){
            return "";
        }
        return  fileName.substring(dot);
    }

}
