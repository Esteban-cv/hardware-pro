package com.mine.hardware_pro.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path fileStorageLocation;

    public FileStorageService(@Value("${app.upload.dir}") String uploadDir) {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("No se pudo crear el directorio de carga.", ex);
        }
    }

    public String storeFile(MultipartFile file, String subdirectory) throws IOException {
        String originalFileName = file.getOriginalFilename();
        String fileExtension = "";
        int dotIndex = originalFileName.lastIndexOf('.');
        if (dotIndex > 0) {
            fileExtension = originalFileName.substring(dotIndex);
        }

        String fileName = UUID.randomUUID().toString() + fileExtension;

        Path targetLocation = this.fileStorageLocation.resolve(subdirectory).resolve(fileName);

        Path subDirLocation = this.fileStorageLocation.resolve(subdirectory);
        if (!Files.exists(subDirLocation)) {
            Files.createDirectories(subDirLocation);
        }

        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        return subdirectory + "/" + fileName; //
    }

    public Path loadFileAsResource(String fileName) {
        return this.fileStorageLocation.resolve(fileName).normalize();
    }

    public void deleteFile(String fileName) throws IOException {
        Path filePath = loadFileAsResource(fileName);
        Files.deleteIfExists(filePath);
    }
}