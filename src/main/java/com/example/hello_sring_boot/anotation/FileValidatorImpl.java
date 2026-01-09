package com.example.hello_sring_boot.anotation;

import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.ConstraintValidator;
import org.springframework.util.StringUtils;
import org.apache.commons.io.FilenameUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class FileValidatorImpl implements ConstraintValidator<FileValidator, Object> {
    private long maxSize;
    private List<String> allowedTypes;
    private Map<String, String> mimeTypeMap;

    @Override
    public void initialize(FileValidator constraintAnnotation) {
        this.maxSize = constraintAnnotation.maxSize();
        this.allowedTypes = Arrays.asList(constraintAnnotation.allowedTypes());

        // Initialize MIME type mapping
        this.mimeTypeMap = createMimeTypeMap();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) return true;

        if (value instanceof MultipartFile) {
            return validateFile((MultipartFile) value, context);
        } else if (value instanceof List) {
            List<?> list = (List<?>) value;
            for (Object item : list) {
                if (item instanceof MultipartFile) {
                    if (!validateFile((MultipartFile) item, context)) {
                        return false;
                    }
                }
            }
            return true;
        }
        return true;
    }

    private boolean validateFile(MultipartFile file, ConstraintValidatorContext context) {
        if (file == null || file.isEmpty()) return true;

        // 1. Check file size
        if (file.getSize() > maxSize) {
            buildContext(context, "File '" + file.getOriginalFilename() +
                    "' quá lớn! Kích thước tối đa: " + formatFileSize(maxSize));
            return false;
        }

        // 2. Get filename and extension
        String filename = file.getOriginalFilename();
        if (filename == null || filename.trim().isEmpty()) {
            buildContext(context, "Filename không hợp lệ");
            return false;
        }

        // 3. Check extension
        String extension = getFileExtension(filename);
        if (extension == null || extension.isEmpty()) {
            buildContext(context, "File '" + filename + "' không có extension");
            return false;
        }

        // 4. Validate extension against allowed types
        if (!isValidExtension(extension)) {
            buildContext(context, "File '" + filename +
                    "'  có định dạng không được hỗ trợ. Định dạng cho phép: " +
                    String.join(", ", allowedTypes));
            return false;
        }

        // 5. Optional: Validate MIME type (lenient - chỉ warning)
        String contentType = file.getContentType();
        String expectedMimeType = getExpectedMimeType(extension);

        if (contentType != null &&
                !contentType.equals("application/octet-stream") &&
                !contentType.equalsIgnoreCase(expectedMimeType)) {
            // Log warning but don't fail validation
            System.out.println("Warning: MIME type mismatch for " + filename +
                    ". Expected: " + expectedMimeType + ", Got: " + contentType);
        }

        return true;
    }

    private String getFileExtension(String filename) {
        if (filename == null) return null;

        // Use FilenameUtils from commons-io for better extension extraction
        String extension = FilenameUtils.getExtension(filename);
        if (extension == null || extension.isEmpty()) {
            // Fallback to manual extraction
            int dotIndex = filename.lastIndexOf('.');
            if (dotIndex > 0 && dotIndex < filename.length() - 1) {
                return filename.substring(dotIndex + 1).toLowerCase();
            }
            return null;
        }
        return extension.toLowerCase();
    }

    private boolean isValidExtension(String extension) {
        if (extension == null) return false;

        // Check if extension is in allowed types
        return allowedTypes.stream()
                .anyMatch(allowed -> allowed.equalsIgnoreCase(extension));
    }

    private String getExpectedMimeType(String extension) {
        return mimeTypeMap.getOrDefault(extension.toLowerCase(), "application/octet-stream");
    }

    private Map<String, String> createMimeTypeMap() {
        Map<String, String> map = new HashMap<>();

        // Common file types
        map.put("pdf", "application/pdf");
        map.put("jpg", "image/jpeg");
        map.put("jpeg", "image/jpeg");
        map.put("png", "image/png");
        map.put("gif", "image/gif");
        map.put("bmp", "image/bmp");
        map.put("txt", "text/plain");
        map.put("doc", "application/msword");
        map.put("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        map.put("xls", "application/vnd.ms-excel");
        map.put("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        map.put("ppt", "application/vnd.ms-powerpoint");
        map.put("pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation");
        map.put("zip", "application/zip");
        map.put("rar", "application/x-rar-compressed");
        map.put("mp3", "audio/mpeg");
        map.put("mp4", "video/mp4");
        map.put("avi", "video/x-msvideo");
        map.put("mov", "video/quicktime");

        return map;
    }

    private String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " bytes";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp - 1) + "";
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
    }

    private void buildContext(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
    }
}