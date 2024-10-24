package store.roombook;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FileStorageProperties {
    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${file.thumbnail.path}")
    private String thumbnailUploadPath;

    @Value("${file.original.path}")
    private String originalUploadPath;

    @Value("${file.noimg.path}")
    private String noImgPath;

    public String getUploadDir() {
        return uploadDir;
    }

    public String getThumbnailUploadPath() { return thumbnailUploadPath; }

    public String getOriginalUploadPath() { return originalUploadPath; }

    public String getNoImgPath() { return noImgPath; }
}
