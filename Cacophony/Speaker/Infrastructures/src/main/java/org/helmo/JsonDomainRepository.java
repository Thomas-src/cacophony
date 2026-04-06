package org.helmo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.helmo.domain.Domain;
import org.helmo.dto.DomainDto;
import org.helmo.dto.DomainsDto;
import org.helmo.exceptions.DomainReaderException;
import org.helmo.exceptions.DomainSaveException;
import org.helmo.mapper.JsonDomainMapper;
import org.helmo.repository.IDomainRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Implémentation du dépôt de domaines basée sur un fichier JSON.
 * <p>
 * Cette classe gère la lecture et la persistance des domaines à partir d'un fichier
 * JSON local. Elle assure la création automatique du fichier s'il n'existe pas.
 * </p>
 */
public class JsonDomainRepository implements IDomainRepository {
    private static final Gson gson = new Gson();
    private static final Path configPath = findDomainFile();
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Object DOMAIN_FILE_LOCK = new Object();

    private final JsonDomainMapper mapper;
    public JsonDomainRepository(JsonDomainMapper mapper) {
        this.mapper = mapper;
    }

    private static Path findDomainFile() {
        String fileName = "domains.json";

        // Case 1: Running from 'Node' subdirectory (e.g., Gradle's 'run' task)
        Path pathFromNodeDir = Paths.get(fileName);
        if (Files.exists(pathFromNodeDir)) {
            return pathFromNodeDir;
        }

        // Case 2: Running from project root (e.g., IDE)
        Path pathFromRoot = Paths.get("Speaker", fileName);
        if (Files.exists(pathFromRoot)) {
            return pathFromRoot;
        }

        // Fallback to original path as a last resort
        return Paths.get("../Speaker/domains.json");
    }

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
        synchronized (DOMAIN_FILE_LOCK) {
            try {
                ensureStorage();
                try (BufferedReader reader = Files.newBufferedReader(configPath)) {
                    DomainsDto dto = gson.fromJson(reader, DomainsDto.class);
                    if (dto == null || dto.domains() == null || dto.domains().isEmpty()) {
                        throw new DomainReaderException("Invalid or empty domains configuration");
                    }

                    return mapper.mapToDomains(dto);
                } catch (IOException e) {
                    throw new DomainReaderException("Failed to read domains file: " + configPath);
                } catch (JsonSyntaxException e) {
                    throw new DomainReaderException("Invalid JSON format in domains file");
                }
            } catch (IOException e) {
                LOGGER.error("Failed to read domains file: {}", configPath, e);
                throw new DomainReaderException("Failed to initialize domains storage");
            }
        }

    }


    @Override
    public void clearDomainMessages(String domain) throws DomainSaveException {
        synchronized (DOMAIN_FILE_LOCK) {
            try {
                ensureStorage();

                DomainsDto dto;
                try (BufferedReader reader = Files.newBufferedReader(configPath)) {
                    dto = gson.fromJson(reader, DomainsDto.class);
                }

                if (dto == null || dto.domains() == null) {
                    throw new DomainSaveException("Fichier domains invalide");
                }

                Gson exporter = new GsonBuilder()
                        .setPrettyPrinting()
                        .create();

                DomainsDto updatedDto = new DomainsDto(
                        dto.domains().stream()
                                .map(d -> {
                                    if (!d.domain().equals(domain)) return d;

                                    return new DomainDto(
                                            d.domain(),
                                            d.base64AES(),
                                            new ArrayList<>()
                                    );
                                })
                                .toList()
                );

                try (var writer = Files.newBufferedWriter(configPath)) {
                    exporter.toJson(updatedDto, writer);
                }

            } catch (IOException e) {
                throw new DomainSaveException("Impossible de vider les messages du domaine : " + domain);
            }
        }
    }


    @Override
    public void saveMessage(String domain, String message) throws DomainSaveException {
        synchronized (DOMAIN_FILE_LOCK) {
            try {
                ensureStorage();

                DomainsDto dto;
                try (BufferedReader reader = Files.newBufferedReader(configPath)) {
                    dto = gson.fromJson(reader, DomainsDto.class);
                }

                if (dto == null || dto.domains() == null) {
                    throw new DomainSaveException("Fichier domains invalide");
                }
                Gson exporter = new GsonBuilder()
                        .setPrettyPrinting()
                        .create();

                DomainsDto updatedDto = mapper.updateMessage(dto, domain, message);

                try (var writer = Files.newBufferedWriter(configPath)) {
                    exporter.toJson(updatedDto, writer);
                }

            } catch (IOException e) {
                throw new DomainSaveException("Impossible de sauvegarder les messages du domaine : " + domain);
            }
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
            Files.writeString(configPath, gson.toJson(new DomainsDto(List.of())));

        }
    }

}
