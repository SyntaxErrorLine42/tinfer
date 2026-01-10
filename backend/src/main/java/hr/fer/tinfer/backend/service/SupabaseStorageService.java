package hr.fer.tinfer.backend.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.UUID;

/**
 * Service for uploading and managing files in Supabase Storage.
 * Handles base64 to binary conversion and upload to the "photos" bucket.
 */
@Service
@Slf4j
public class SupabaseStorageService {

    private static final String BUCKET_NAME = "photos";

    @Value("${supabase.url:}")
    private String supabaseUrl;

    @Value("${supabase.anon-key:}")
    private String supabaseAnonKey;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Check if Supabase Storage is configured.
     */
    public boolean isConfigured() {
        return supabaseUrl != null && !supabaseUrl.isBlank()
                && supabaseAnonKey != null && !supabaseAnonKey.isBlank();
    }

    /**
     * Upload a base64 encoded image to Supabase Storage.
     * Returns the original base64 data if upload fails (fallback).
     *
     * @param base64Data The base64 encoded image (with or without data URI prefix)
     * @param userId     The user ID (used for folder organization)
     * @return The public URL of the uploaded image, or original base64 on failure
     */
    public String uploadImage(String base64Data, UUID userId) {
        // If not configured, return base64 as-is (backwards compatibility)
        if (!isConfigured()) {
            log.warn("Supabase Storage not configured, storing base64 directly");
            return base64Data;
        }

        try {
            // Parse base64 data
            String contentType = "image/jpeg"; // default
            String base64Content = base64Data;

            if (base64Data.startsWith("data:")) {
                // Extract content type from data URI
                int commaIndex = base64Data.indexOf(",");
                if (commaIndex > 0) {
                    String header = base64Data.substring(0, commaIndex);
                    base64Content = base64Data.substring(commaIndex + 1);

                    // Extract mime type
                    if (header.contains("image/png")) {
                        contentType = "image/png";
                    } else if (header.contains("image/gif")) {
                        contentType = "image/gif";
                    } else if (header.contains("image/webp")) {
                        contentType = "image/webp";
                    }
                }
            }

            // Decode base64 to binary
            byte[] imageBytes = Base64.getDecoder().decode(base64Content);

            // Generate unique filename
            String extension = contentType.split("/")[1];
            String fileName = String.format("%s/%s.%s", userId, UUID.randomUUID(), extension);

            // Build upload URL
            String uploadUrl = String.format("%s/storage/v1/object/%s/%s",
                    supabaseUrl, BUCKET_NAME, fileName);

            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + supabaseAnonKey);
            headers.set("apikey", supabaseAnonKey);
            headers.setContentType(MediaType.parseMediaType(contentType));

            HttpEntity<byte[]> requestEntity = new HttpEntity<>(imageBytes, headers);

            // Upload file
            ResponseEntity<String> response = restTemplate.exchange(
                    uploadUrl,
                    HttpMethod.POST,
                    requestEntity,
                    String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                // Return public URL
                String publicUrl = String.format("%s/storage/v1/object/public/%s/%s",
                        supabaseUrl, BUCKET_NAME, fileName);
                log.info("Uploaded image to Supabase Storage: {}", publicUrl);
                return publicUrl;
            } else {
                log.error("Failed to upload image: {}", response.getStatusCode());
                // Fallback to base64
                return base64Data;
            }
        } catch (HttpClientErrorException e) {
            log.error("Supabase Storage error: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            // Fallback to base64
            return base64Data;
        } catch (Exception e) {
            log.error("Error uploading image to Supabase Storage: {}", e.getMessage());
            // Fallback to base64
            return base64Data;
        }
    }

    /**
     * Delete an image from Supabase Storage.
     *
     * @param imageUrl The public URL of the image to delete
     */
    public void deleteImage(String imageUrl) {
        if (imageUrl == null || !imageUrl.contains(supabaseUrl)) {
            return; // Not a Supabase Storage URL
        }

        try {
            // Extract file path from URL
            String marker = "/object/public/" + BUCKET_NAME + "/";
            int pathStart = imageUrl.indexOf(marker);
            if (pathStart < 0) {
                return;
            }
            String filePath = imageUrl.substring(pathStart + marker.length());

            // Build delete URL
            String deleteUrl = String.format("%s/storage/v1/object/%s/%s",
                    supabaseUrl, BUCKET_NAME, filePath);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + supabaseAnonKey);
            headers.set("apikey", supabaseAnonKey);

            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

            restTemplate.exchange(deleteUrl, HttpMethod.DELETE, requestEntity, String.class);
            log.info("Deleted image from Supabase Storage: {}", filePath);
        } catch (Exception e) {
            log.warn("Failed to delete image from storage: {}", e.getMessage());
            // Don't throw - deletion failure shouldn't break the flow
        }
    }

    /**
     * Get a thumbnail URL for the image using Supabase image transformation.
     * Returns a resized version of the image for faster loading.
     *
     * @param imageUrl The original image URL
     * @param width    Desired width
     * @param height   Desired height
     * @return Transformed image URL
     */
    public String getThumbnailUrl(String imageUrl, int width, int height) {
        if (imageUrl == null || !imageUrl.contains("/storage/v1/object/public/")) {
            return imageUrl;
        }

        // Supabase image transformation URL format:
        // /storage/v1/render/image/public/bucket/path?width=100&height=100
        return imageUrl.replace(
                "/storage/v1/object/public/",
                String.format("/storage/v1/render/image/public/"))
                + String.format("?width=%d&height=%d&resize=cover", width, height);
    }
}
