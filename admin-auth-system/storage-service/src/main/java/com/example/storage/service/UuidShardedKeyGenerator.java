package com.example.storage.service;

import com.example.storage.util.FileUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;


@Component
public class UuidShardedKeyGenerator  {

    public String generateKey(MultipartFile file){
        String extension = FileUtils.getFileExtension(file.getOriginalFilename());
        String id = UUID.randomUUID()
                .toString()
                .replace("-","");
        //Shard prefix
        String p1 = id.substring(0,2);
        String p2 = id.substring(2,4);
        return "uploads/"+p1+"/"+p2+"/"+id+extension;
    }

}
