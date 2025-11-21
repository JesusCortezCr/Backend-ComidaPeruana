package com.app.backend_app.security.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Slf4j  // 👈 AGREGAR ESTO
@Service
public class FileStorageService {

    private final Path fileStorageLocation;

    public FileStorageService() {
        this.fileStorageLocation = Paths.get("uploads")
                .toAbsolutePath()
                .normalize();
        
        try {
            Files.createDirectories(this.fileStorageLocation);
            log.info("📁 Directorio de uploads creado en: {}", this.fileStorageLocation.toString()); // 👈 LOG
        } catch (Exception ex) {
            throw new RuntimeException("No se pudo crear el directorio para guardar archivos.", ex);
        }
    }

    public String storeFile(MultipartFile file) {
        try {
            log.info("📤 Intentando guardar archivo: {} ({} bytes)", file.getOriginalFilename(), file.getSize()); // 👈 LOG
            
            // Validar que el archivo no esté vacío
            if (file.isEmpty()) {
                throw new RuntimeException("El archivo está vacío");
            }

            // Validar que sea una imagen
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new RuntimeException("Solo se permiten archivos de imagen");
            }

            // Validar tamaño (máximo 5MB)
            if (file.getSize() > 5 * 1024 * 1024) {
                throw new RuntimeException("El archivo es demasiado grande. Tamaño máximo: 5MB");
            }

            // Generar nombre único para el archivo
            String originalFileName = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFileName != null && originalFileName.contains(".")) {
                fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            }
            
            String fileName = UUID.randomUUID().toString() + fileExtension;
            log.info("🆕 Nombre único generado: {}", fileName); // 👈 LOG

            // Copiar el archivo al directorio de destino
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            log.info("💾 Archivo guardado en: {}", targetLocation.toString()); // 👈 LOG

            // Verificar que el archivo existe
            boolean fileExists = Files.exists(targetLocation);
            log.info("🔍 Archivo existe después de guardar: {}", fileExists); // 👈 LOG

            // Generar URL para acceder al archivo
            String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/uploads/")
                    .path(fileName)
                    .toUriString();

            log.info("🌐 URL generada: {}", fileDownloadUri); // 👈 LOG
            return fileDownloadUri;

        } catch (IOException ex) {
            log.error("❌ Error al guardar el archivo: {}", ex.getMessage()); // 👈 LOG
            throw new RuntimeException("Error al guardar el archivo: " + ex.getMessage(), ex);
        }
    }
}