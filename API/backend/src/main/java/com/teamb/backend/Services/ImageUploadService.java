package com.teamb.backend.Services;

import com.cloudinary.Cloudinary;
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

    public String uploadImage(MultipartFile file) throws IOException {
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
        return (String) uploadResult.get("secure_url");
    }
}
