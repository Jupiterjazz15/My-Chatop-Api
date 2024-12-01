package com.chatop.My_Chatop_Api.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service // SAUVEGARDER LES PHOTOS ENVOYÉES PAR LE USER
public class FileStorageService {

    // Définission du dossier de stockage des photos
    private final String uploadDir = "/Users/coraliehaller/Code/Jupiterjazz15/My-Chatop-Api/src/main/images";

    public String saveFile(MultipartFile file) {

        if (file.isEmpty()) {
            throw new IllegalArgumentException("Le fichier est vide.");
        }

        // Vérifier le type de fichier (par exemple, accepter uniquement les fichiers image)
        String fileType = file.getContentType(); // Récupérer le type MIME du fichier
        if (fileType == null || !fileType.startsWith("image")) { // Vérifier si c'est une image
            throw new IllegalArgumentException("Seuls les fichiers images sont autorisés.");
        }

        try {
            // Créer le répertoire d'upload si nécessaire
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath); // Créer le répertoire s'il n'existe pas
            }

            // Générer un nom unique pour le fichier pour éviter les conflits
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename(); // Utilise le timestamp pour garantir l'unicité

            // Construire le chemin complet du fichier
            Path filePath = uploadPath.resolve(fileName); // Combiner le répertoire avec le nom du fichier

            // Sauvegarder le fichier en écrivant son contenu dans le répertoire spécifié
            Files.write(filePath, file.getBytes()); // Sauvegarder le fichier sous forme de bytes dans le répertoire

            // Retourner le chemin absolu du fichier sauvegardé
            return filePath.toString(); // Retourner le chemin complet du fichier stocké

        } catch (IOException e) {
            // Gérer les erreurs d'entrée/sortie (par exemple, permission de répertoire, problème d'écriture)
            throw new RuntimeException("Erreur lors de l'enregistrement du fichier.", e); // Lance une exception RuntimeException avec le message d'erreur
        }
    }
}
