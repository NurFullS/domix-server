package com.example.demo;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryService() {
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dop4mtq1t",
                "api_key", "293689374536852",
                "api_secret", "dhAhHNVpXSpMicpMIFLd8a4F3FM"));
    }

    public Map<String, Object> uploadFile(byte[] fileBytes, String fileName) throws IOException {
        @SuppressWarnings("unchecked")
        Map<String, Object> result = cloudinary.uploader().upload(fileBytes, ObjectUtils.asMap(
                "resource_type", "auto",
                "public_id", fileName != null ? fileName : null));
        return result;
    }

}
