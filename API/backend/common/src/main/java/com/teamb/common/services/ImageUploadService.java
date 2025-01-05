package com.teamb.common.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class ImageUploadService {
    private Cloudinary cloudinary;

    public ImageUploadService() {
        cloudinary = new Cloudinary("cloudinary://445236893585978:NIBpyivrPUPW5fFQMOw5sx2Y-s8@dyb0upbnh");
    }


    public String uploadImage(MultipartFile file, int width, int height) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                "transformation", new Transformation().width(width).height(height).crop("fill").gravity("auto")
        ));

        return (String) uploadResult.get("secure_url");
    }
 
}
