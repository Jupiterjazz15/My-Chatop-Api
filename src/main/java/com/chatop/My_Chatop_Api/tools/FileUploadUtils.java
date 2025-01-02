package com.chatop.My_Chatop_Api.tools;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class FileUploadUtils {
    private static final String BASE_URL = "http://localhost:9090";
    private static final String UPLOAD_DIR = "src/main/resources/static/uploads";

    /**
     * Sauvegarde un fichier MultipartFile et retourne son chemin d'accès public.
     *
     * @param file MultipartFile à sauvegarder.
     * @return Le chemin public pour accéder au fichier.
     * @throws IOException En cas d'erreur lors de l'écriture du fichier.
     */
    public static String saveFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Le fichier est vide ou null");
        }

        // Générer un nom de fichier unique
        String originalFileName = file.getOriginalFilename();
        String sanitizedFileName = originalFileName.replaceAll(" ", "_").replaceAll("[^a-zA-Z0-9._-]", "");
        String fileName = UUID.randomUUID().toString() + "_" + sanitizedFileName;

        // Créer le chemin de destination
        Path filePath = Paths.get(UPLOAD_DIR, fileName);

        // Créer les répertoires si nécessaire
        Files.createDirectories(filePath.getParent());

        // Sauvegarder le fichier
        Files.write(filePath, file.getBytes());

        // Encodage du nom de fichier pour utilisation dans l'URL
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString());

        // Retourner le chemin d'accès public
        return BASE_URL + "/uploads/" + encodedFileName;
    }
}
