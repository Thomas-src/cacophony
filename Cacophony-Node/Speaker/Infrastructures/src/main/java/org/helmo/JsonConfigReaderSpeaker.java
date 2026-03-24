package org.helmo;

import com.google.gson.Gson;
import org.helmo.dto.ConfigDto;
import org.helmo.exceptions.ConfigReaderException;
import org.helmo.mapper.JsonConfigMapper;
import org.helmo.repository.IConfigReaderRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Implémentation de la lecture de configuration pour le Speaker à partir d'un fichier JSON.
 * Cette classe lit le fichier config-speaker.json et le convertit en objet Config.
 */
public class JsonConfigReaderSpeaker  implements IConfigReaderRepository {
    private final Gson gson;

    /**
     * Constructeur par défaut qui initialise l'instance Gson pour la désérialisation JSON.
     */
    public JsonConfigReaderSpeaker()
    {
        this.gson = new Gson();
    }

    /**
     * Lit la configuration depuis le fichier config-speaker.json.
     *
     * @return Le DTO de configuration lu depuis le fichier JSON
     * @throws IOException Si le fichier n'existe pas, est invalide ou vide
     */
    private ConfigDto readConfig() throws IOException
    {
        String FILE_NAME = "config-speaker.json";
        Path path = Paths.get(FILE_NAME);
        try (BufferedReader reader = Files.newBufferedReader(path)){
         ConfigDto dto = gson.fromJson(reader, ConfigDto.class);

            if (dto == null) {
                throw new IOException("Configuration vide ou invalide");
            }

            return JsonConfigMapper.ConfigfromDto(dto);

    }catch (IOException ex) {
            throw new IOException("Impossible de charger la configuration. Tried path: " + path.toAbsolutePath(), ex);
        }

}


    /**
     * Récupère la configuration du Speaker.
     *
     * @return L'objet Config contenant l'adresse multicast, le port et l'interface réseau
     * @throws ConfigReaderException Si une erreur survient lors de la lecture de la configuration
     */
    @Override
    public Config getConfig() throws ConfigReaderException {
        try {
            ConfigDto dto = readConfig();
            return new Config(
                    dto.MulticastAdress(),
                    dto.MulticastPort(),
                    dto.NetworkInterface()
            );
            } catch (IOException e) {
            throw new ConfigReaderException();

        }
    }
}
