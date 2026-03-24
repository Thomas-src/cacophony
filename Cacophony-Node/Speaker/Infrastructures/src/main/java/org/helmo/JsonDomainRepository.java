package org.helmo;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.helmo.domain.Domain;
import org.helmo.dto.DomainDto;
import org.helmo.exceptions.DomainReaderException;
import org.helmo.mapper.JsonDomainMapper;
import org.helmo.repository.IDomainRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Implémentation du dépôt de domaines basée sur un fichier JSON.
 * <p>
 * Cette classe gère la lecture et la persistance des domaines à partir d'un fichier
 * JSON local. Elle assure la création automatique du fichier s'il n'existe pas.
 * </p>
 */
public class JsonDomainRepository implements IDomainRepository {
    private static final String FILE_NAME = "Speaker/domains.json";
    private static final Gson gson = new Gson();
    private static final Path configPath = Paths.get(FILE_NAME);
    private static final Logger LOGGER = LogManager.getLogger();

    /**
     * Récupère la liste de tous les domaines configurés depuis le fichier JSON.
     * <p>
     * Cette méthode lit le fichier de configuration des domaines, le désérialise
     * et retourne une liste d'objets Domain. Si le fichier n'existe pas, il est
     * créé automatiquement avant la lecture.
     * </p>
     *
     * @return la liste des domaines configurés
     * @throws DomainReaderException si le fichier ne peut pas être lu, si le format JSON est invalide,
     *                               ou si la configuration est vide ou invalide
     */
    @Override
    public List<Domain> getDomains() throws DomainReaderException {
        try {
            ensureStorage();
            try (BufferedReader reader = Files.newBufferedReader(configPath)) {
                DomainDto dto = gson.fromJson(reader, DomainDto.class);
                if (dto == null || dto.domain() == null) {
                    throw new DomainReaderException("Invalid or empty domains configuration");
                }
               
                return JsonDomainMapper.mapToDomains(dto);
            } catch (IOException e) {
                throw new DomainReaderException("Failed to read domains file: " + configPath);
            } catch (JsonSyntaxException e) {
                throw new DomainReaderException("Invalid JSON format in domains file");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Assure l'existence du fichier de stockage des domaines.
     * <p>
     * Si le fichier n'existe pas, cette méthode crée les répertoires parents nécessaires
     * et initialise le fichier avec une configuration par défaut (domaine vide).
     * </p>
     *
     * @throws IOException si la création du fichier ou des répertoires échoue
     */
    private void ensureStorage() throws IOException {
        if (Files.notExists(configPath)) {
            Path parent = configPath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.writeString(configPath, gson.toJson(new DomainDto("", "")));
            
        }
    }

}
