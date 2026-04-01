
# Cacophony


![Statut](https://img.shields.io/badge/Statut-En%20d%C3%A9veloppement-F0AD4E)   ![Java](https://img.shields.io/badge/Java-OpenJDK-007396?logo=openjdk&logoColor=white) ![Python](https://img.shields.io/badge/Python-3.x-3776AB?logo=python&logoColor=white) ![Gradle](https://img.shields.io/badge/Gradle-Wrapper-02303A?logo=gradle&logoColor=white) ![OS](https://img.shields.io/badge/OS-Windows-0078D4?logo=windows&logoColor=white)

## Badges

Badges de statut et de stack affichés ci-dessus (Shields.io).

> Messagerie distribuée academique composee d'un Node Java, d'un Speaker Java et d'un client desktop Python.


## Points forts

- Architecture hybride Java + Python, separee en composants clairs.
- Demarrage local rapide avec Gradle Wrapper (pas de Gradle global requis).
- Client GUI Python compatible TLS.
- Configuration simple via fichiers JSON.


## Présentation

Cacophony est un projet de messagerie distribuee organise autour de trois composants :

- `Node` : coeur serveur en Java (multi-modules Gradle).
- `Speaker` : composant Java de diffusion/traitement.
- `cacophony-client` : client GUI Python pour se connecter au Node.

Le depot a ete pense pour un usage pedagogique, avec une structure lisible et des points d'entree explicites pour lancer chaque composant.


### Statut du projet

Projet academique en developpement actif.


### Auteurs

Contributeurs principaux (historique Git) :

- Thomas Derdeyn (GitLab : `t.derdeyn`)
- Clement Franchimont (GitLab : `cl.franchimont`)
- Tibo Maes (GitLab : `ti.maes`)
- Antoine Toussaint (GitLab : `an.toussaint`)


## Installation


### Prérequis

- OpenJDK installe (pour les modules Java).
- Python 3.x et `pip` (pour le client).
- Windows PowerShell (commandes ci-dessous donnees pour Windows).


### Installation du client Python

Depuis la racine du depot :

```powershell
cd cacophony-client
pip install bcrypt
```

Option recommandee (environnement virtuel) :

```powershell
cd cacophony-client
python -m venv .venv
.venv\Scripts\activate.bat
pip install bcrypt
```


## Exécution locale

Ouvrez trois terminaux depuis la racine du projet.


### 1) Lancer le Node

```powershell
cd Cacophony-Node
.\gradlew :Node:App:run
```

Configuration utilisee : `Cacophony-Node/Node/config-node.json`


### 2) Lancer le Speaker

```powershell
cd Cacophony-Node
.\gradlew :Speaker:App:run
```

Configuration utilisee : `Cacophony-Node/Speaker/config-speaker.json`


### 3) Lancer le client

```powershell
cd cacophony-client
python main.py
```

Arguments optionnels pour pre-remplir l'interface :

```powershell
python main.py <host> <port> <login> <password> [tls]
```

Exemple :

```powershell
python main.py node1.swilabus.xyz 5002 louisswinnen rootroot tls
```


## Exécuter les tests

Pour executer les tests Java :

```powershell
cd Cacophony-Node
.\gradlew test
```

Commande utile supplémentaire (build) :

```powershell
cd Cacophony-Node
.\gradlew build
```


## Captures d'écran

Capture d'ecran a ajouter.


## Structure du dépôt

- `Cacophony-Node/` : code Java (Node, Speaker, Grammar)
- `cacophony-client/` : client desktop Python
- `logs/` : journaux generes a la racine du projet


## Configuration

Node :

- `Cacophony-Node/Node/config-node.json`
- `Cacophony-Node/Node/users.json`
- `Cacophony-Node/Node/tags.json`

Speaker :

- `Cacophony-Node/Speaker/config-speaker.json`
- `Cacophony-Node/Speaker/domains.json`


## Journaux

Les journaux sont produits notamment dans :

- `Cacophony-Node/Node/logs/`
- `Cacophony-Node/Speaker/logs/`
- `logs/`


## Dépannage

- Si les modules Java ne demarrent pas, verifiez que les commandes sont lancees depuis `Cacophony-Node/`.
- Si Python ne trouve pas `bcrypt`, relancez `pip install bcrypt`.
- En cas d'erreur reseau, verifiez host/port/interfaces dans les JSON de configuration.
- En cas d'echec TLS cote client, verifiez certificat et configuration TLS du Node.



## Documentation

- Client Python : `cacophony-client/README.md`


## Ressources de cours

Les ressources suivantes sont fournies via la plateforme du cours ("disponible ici") :

- `InterfaceIdentifier.jar` : affiche le nom des interfaces reseau (Java 21 a modifie certains noms d'interfaces).
- `MulticastSpy21.jar` : affiche les echanges multicast.
- `cacophony-node.json` : exemple de configuration JSON pour le Node.
- `cacophony-speaker.json` : exemple de configuration JSON pour le Speaker.
- `star.swilabus.xyz.p12` : certificat TLS pour le client (mot de passe : `labo2026`).
- Certificats CA : `Swilabus Root CA RSA 2025` et `Swilabus Education CA RSA TLS 2025`.
- `MulticastForwarder.py` (projet fourni) : simulation des envois multicast via VPN.
- Fichier Excel d'inscription des membres du groupe.
- Video d'exemple de gestion de projet dans GitLab.

Important pour le multicast :

- Selectionner l'interface reseau avant tout echange multicast (Node/Speaker/outils multicast).
- Verifier que la valeur `networkInterface` dans les fichiers JSON correspond bien a l'interface active sur votre machine.


## Variables d'environnement

Ce projet n'utilise pas de fichier `.env` obligatoire dans sa forme actuelle.
La configuration est geree via des fichiers JSON :

- `Cacophony-Node/Node/config-node.json`
- `Cacophony-Node/Speaker/config-speaker.json`
