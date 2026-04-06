package org.helmo.mapper;

import org.helmo.client.Client;
import org.helmo.client.HashPassword;
import org.helmo.tag.ITag;
import org.helmo.dto.UserDto;
import org.helmo.dto.UsersDto;
import org.helmo.subscriber.NativeUser;
import org.helmo.subscriber.IUserSubscriber;
import org.helmo.subscriber.RemoteUser;


import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import org.helmo.Grammar;
import org.helmo.tag.TagReference;

/**
 * Mapper responsable de la conversion entre les objets DTO JSON et les entités Client.
 */
public class JsonUserMapper {
    /**
     * Mappe une liste d'utilisateurs DTO vers une liste d'objets Client.
     *
     * @param dto           L'objet UsersDto contenant la liste des utilisateurs.
     * @param currentDomain Le domaine actuel du serveur.
     * @return Une liste d'objets Client.
     */
    public List<Client> mapUsers(UsersDto dto, String currentDomain) {
        List<UserDto> userDtos = dto.users() == null ? List.of() : dto.users();
        return userDtos.stream().map(u -> createUser(u, currentDomain)).toList();
    }

    /**
     * Crée une instance de Client à partir d'un UserDto.
     *
     * @param dto           Le DTO contenant les informations de l'utilisateur.
     * @param currentDomain Le domaine actuel du serveur.
     * @return Un objet Client initialisé, ou null si les données sont incomplètes.
     */
    private Client createUser(UserDto dto, String currentDomain) {
        List<IUserSubscriber> subscribers = new ArrayList<>();
        List<ITag> userInternalTags = new ArrayList<>();
        if (dto.subscribers() != null && dto.userTags() != null) {
            for (String subscriberName : dto.subscribers()) {
                if (subscriberName != null && !subscriberName.isBlank()) {
                    Matcher matcher = Grammar.P_USER_AT_DOMAIN.matcher(subscriberName);
                    if (matcher.matches()) {
                        String domain = matcher.group(2);
                        if (domain.equals(currentDomain)) {
                            subscribers.add(new NativeUser(subscriberName));
                        } else {
                            subscribers.add(new RemoteUser(subscriberName));
                        }
                    } else {
                        // si pas de domaine => utilisateur interne
                        subscribers.add(new NativeUser(subscriberName));
                    }
                }
            }

            for (String tag : dto.userTags()) {
                userInternalTags.add(new TagReference(tag));
            }
            return new Client(
                    dto.login(),
                    new HashPassword(dto.bcryptHash(), dto.bcryptRound(), dto.bcryptSalt()),
                    subscribers,
                    currentDomain,
                    userInternalTags,
                    dto.messages(),
                    dto.subscriptionUsers()
            );
        }
        return null;
    }


    /**
     * Convertit un objet Client en UserDto pour la sérialisation.
     *
     * @param client L'objet client à convertir.
     * @return Un UserDto contenant les informations du client.
     */
    public UserDto toUserDto(Client client) {
        HashPassword hashPassword = client.getHashPassword();
        return new UserDto(
                client.getName(),
                hashPassword.storedBcryptHash(),
                hashPassword.bcryptRound(),
                hashPassword.bcryptSalt(),
                client.getSubscribers().stream().map(IUserSubscriber::getFullName).toList(),
                client.getTags().stream().map(ITag::getFullName).toList(),
                client.getMessages(),
                client.getSubscribtions()
        );
    }

    /**
     * Crée une copie d'un UserDto avec une nouvelle liste d'abonnés.
     *
     * @param user           Le DTO utilisateur original.
     * @param newSubscribers La nouvelle liste des noms d'abonnés.
     * @return Un nouveau UserDto mis à jour.
     */
    public UserDto copyWithSubscribers(UserDto user, List<String> newSubscribers) {
        return new UserDto(
                user.login(),
                user.bcryptHash(),
                user.bcryptRound(),
                user.bcryptSalt(),
                newSubscribers,
                user.userTags(),
                user.messages(),
                user.subscriptionUsers()
        );
    }

    /**
     * Crée une copie d'un UserDto avec une nouvelle liste d'abonnements.
     *
     * @param user             Le DTO utilisateur original.
     * @param newSubscriptions La nouvelle liste des noms d'abonnements.
     * @return Un nouveau UserDto mis à jour.
     */
    public UserDto copyWithSubscriptions(UserDto user, List<String> newSubscriptions) {
        return new UserDto(
                user.login(),
                user.bcryptHash(),
                user.bcryptRound(),
                user.bcryptSalt(),
                user.subscribers(),
                user.userTags(),
                user.messages(),
                newSubscriptions
        );
    }

    /**
     * Crée une copie d'un UserDto avec une nouvelle liste de tags.
     *
     * @param user Le DTO utilisateur original.
     * @param tags La nouvelle liste de tags.
     * @return Un nouveau UserDto mis à jour.
     */
    public UserDto copyWithTags(UserDto user, List<String> tags) {
        return new UserDto(
                user.login(),
                user.bcryptHash(),
                user.bcryptRound(),
                user.bcryptSalt(),
                user.subscribers(),
                tags,
                user.messages(),
                user.subscriptionUsers()
        );
    }
}
