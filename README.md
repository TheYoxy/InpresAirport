# InpresAirport
#### Délais de présentation
| Application                              | Semaine                               | Pondération |
| ---------------------------------------- | ------------------------------------- | ----------- |
| 1: [Client/Serveur multithread TCP en C/C++](#1. Serveur_CheckIN et Application_CheckIn) | 2/10/2017                             | 20          |
| 2: [JDBC]()                                  | 9/10/2017                             | 10          |
| 3: Client serveur multithread TCP en Java | 23/10/2017                            | 20          |
| 4: Programmation web java classique      | 20/11/2017                            | 20          |
| 5: Client-serveur UDP en C/C++ et Java   | 4/12/2017                             | 10          |
| 6: Client-serveur sécurisé en Java et complément caddie virtuel en Java | Examen de laboratoire de Janvier 2018 | 80          |
| 7: Communications réseaux C/C++ - Java   | Examen de laboratoire de janvier 2018 | 20          |
#### Fonctions utiles:
> ##### TCP
- ###### Client:
    - socket
    - connect
    - send
    - recv
    - close
- ###### Serveur:
    - socket
    - bind
    - listen
    - accept
    - send
    - recv
    - close

*Pour les send et recv l'ordre n'a pas d'importance, on peut appeler l'un avant l'autre ou vice-versa. Ils sont par ailleurs facultatifs.*
> ##### UDP
- ###### Client
    - socket
    - sendto
    - recvfrom
    - close
- ###### Serveur
    - socket
    - bind
    - recvfrom
    - sendto
    - close

- Il faut écrire les bagages dans des fichiers csv mais, dans la programmation, de manière telle à ce que l'on puisse facilement remplacer cette écriture dans un fichier par un envoi sur le réseau vers Serveur_bagages. Les fonctions correspondantes sont donc, +/-, *"
- Pour le diagramme, il suffit de montrer une séquence d'échanges pour le traitement complet d'un client passager par un employé qui s'est fait reconnaître. Donc, oui, cela commence comme ça, mais il reste à faire ...
- Tableau de commandes : une colonne reprend les états possible du thread serveur (attente, login fait, passager reconnu) - une colonne reprend en regard les commandes que l'on peut recevoir dans cet état en regard (ex: dans l'état *"login_fait"*, seule la commande **CHECK_TICKET** est recevable - donc, les autres sont refusées) - une colonne avec la sémantique de ces commandes


----------


## 1. Serveur_CheckIN et Application_CheckIn
### 1.1. L'enregistrement des passagers : client-serveur
Dossier attendu:
- Tableau des commandes et diagramme du protocole CIMP - du type ci-contre.
- Code *C*/*C++* du serveur Serveur_CheckIn.
- Vues des trames échangées par un sniffer lors du début des opérations.

Le **Serveur_CheckIn** a donc pour mission essentielle de gérer les arrivées des
passagers qui sont en possession d'un billet pour un vol donné : il s'agit essentiellement de la
vérification des billets, de la validation des billets présentés ainsi que de l'enregistrement des
bagages.
Le serveur est un serveur multithread C/Unix en modèle **pool de threads**. Il est chargé
de répondre aux requêtes provenant de **Application_CheckIn (C/C++)** utilisée par les agents
des compagnies aériennes qui assurent l'accueil des passagers pour les différents vols
programmés. Le serveur attend ce type de requête sur le *PORT_CHCK*. Il utilise le protocole
applicatif (basé TCP) **CIMP** (CheckIn Management Protocol).

### 1.2 La gestion des bagages : accès à Serveur_Bagages
On utilise donc ici des fichiers de données de type *csv*. Il est bien clair que ce serveur
**Serveur_CheckIn** devra agir directement sur la base de données (exposée au point suivant)
ainsi qu'interagir avec le serveur **Serveur_Bagages** (voir plus loin) qui agit sur cette même
base de données **BD_AIRPORT**.
Des librairies d'accès aux bases de données relationnelles existent bien sûr en C/C++,
mais elles ne sont pas du tout portables et/ou posent des problèmes de déploiement d'un
système à un autre). Nous les éviterons donc.
Comme la base **BD_AIRPORT** et le **Serveur_Bagages** ne sont pas encore disponibles,
il convient de prévoir une conception logicielle qui isole les demandes à ce serveur dans une
librairie de fonctions dont l'implémentation sera modifiée ultérieurement (avec le minimum
de réécriture de code). On pense donc ici à des fonctions du type suivant (ce sont des
exemples - libre à vous d'en concevoir d'autres du même style) :

#### Exemple : Librairie AccessBilBag

| Fonction                                 | Sémantique                               | Valeur retournée                   |
| ---------------------------------------- | ---------------------------------------- | ---------------------------------- |
| `int verifyTicket(char * number, int nbPassengers)` | Vérification de l'existence d'un billet d'avion avec un certain nombre d’accompagnants. | 0 ou 1                             |
| `float addLuggage(char * number, float weight, char suitcase)` | Enregistrement d'un bagage de poids donné, sous forme de valise ou pas, pour le billet d'avion précisé. | Poids total actuel pour le billet. |

### **1.3 Quelques conseils méthodologiques pour le développement de CIMP**

  1. Il faut tout d'abord choisir la manières d'implémenter les requêtes et les réponses des protocoles CIMP et plusieurs possibilités sont envisageables pour écrire les trames:
  Uniquement par chaîne de caractères contenant des séparateurs pour isoler les différents paramètres.
  - Sous forme de structures, avec des champs suffisamment précis pour permettre au serveur d'identifier la requête et de la satisfaire si possible.
  - Un mélange des deux: une chaîne pour déterminer la requête, une structure pour les données de la requête.
  - Fragmenter en plusieurs trames chaînées dans le cas d'une liste à transmettre.

*On veillera à utiliser des constantes `#define` et les fichiers \*.h et pas des nombres ou des caractères explicites.*

2. On peut ensuite construire un squelette de serveur multithread et y intégrer les appels de base des primitives des sockets. Mais il faudra très vite (sous peine de réécritures qui feraient perdre du temps) remplacer ces appels par les appels correspondants d'une libraire de fonctions **SocketUtilites** facilitant la manipulation des instructions réseaux. Selon ses goûts, il s'agira :
  - Soit une bibliothèque C de fonctions réseaux TCP/IP.
  - Soit, mais c'est peut-être un peu moins évident, d'une bibliothèque C++ implémentant une *hiérarchie de classes* utiles pour la programmation réseau TCP/IP.
    Par exemple:
    - `Socket`
    - `SocketClient`
    - `SocketServeur`\
      On évitera la construction de flux réseaux d'entrée et de sortie, `NetworkStreamBase` `ONetworkStream` `INetworkStream`, car cela devient trop ambitieux pour le temps dont on dispose.
3. Quelques remarques s'imposent:
    - Une fonction ou une méthode de bibliothèque ne présente d'intérpet que si elle est plus facile à utiliser que la (les) fonction(s) qu'elle remplace.
    - Une fonction ou une méthode de bibliothèque ne présente d'intérêt que si elle est indépendante du cas particulier du projet considéré ici.

        | Bien                                     | Pas bien                                 |
        | ---------------------------------------- | ---------------------------------------- |
        | `xxx receiveSize(void * struc, int size)` | `xxx receive(ListePassagers *lc,int size)` et pas `xxx getPassagers(...,...)` |
        | Couche basse : réutilisable dans une autre application | Une seule couche: la fonction receive ne peut être utilisée dans une autre application |
        | ```C xxx receiveSep(char *chaine, char *sep)``` |                                          |
        | Couche basse : réutilisable dans une autre application |                                          |
        | avec `ListePassagers getPassengers (...,...)` |                                          |
        | Couche haute : propre à cette application - utilise l'une des fonctions ci-dessus |                                          |
    - En tenant compte de l'administration du serveur, il serait avisé de faire intervenir dans le code du serveur la notion d'état de celui-ci (*Certaines commandes n'ont de sens que si elles sont précédées d'une autre*).
4. Il est impérieux de surveiller les écoutes, les connexions et les communisations réseaux
    - Au moyen des commandes **ping** et surtout **netstat** (Pour linux: `netstat -an | grep *NuméroDePort*`).
    - En utilisant un **sniffer** comme *Wireshark* ou autre encore analysant le trafic réseau (Attention au localhost qui ne permet pas de sniffer simplement). Cette pratique sera demandée lors des évaluations.
5. Il serait aussi intéréssant de prévoir un fichier de configuration lu par le serveur à son démarrage. A l'image des fichiers properties de *Java*, il s'agit d'un simple fichier texte dont chaque ligne comporte une propriété du serveur et la valeur de celle-ci.

    | Fichier de configuration : | serveur_checkin.conf |
    | -------------------------- | -------------------- |
    | Port_Service=              | 70000                |
    | Port_Admin=                | 70009                |
    | sep-trame=                 | $                    |
    | fin-trame=                 | #                    |
    | sep-csv=                   | ;                    |
    | pwd-master=                | tusaisquetuesbeautoi |
    | pwd-admin=                 | jeaclachralf..       |
    | ---                        | ---                  |

----------


## 2. Les accès aux bases de données

### 2.1 La base de données BD_AIRPORT
Dossier attendu:
-  Schéma relationnel de BD_AIRPORT.
-  Diagramme de classe *UML* des classes de *database.utilities*.

Base de données *MySql* BD_AIRPORT doit contenir toutes les informations utiles concernant le fonctionnement de l'aéroport (**Uniquement**). Ses tables, si on admet un certain nombre d'approximations, de simplifications et d'omission sans importance pour le projet tel que défini ici (Avec le minimum de champs, en définitive), seront en première analyse celle d'une base de données classique, que l'on peut définir dans un premier temps sommairement comme contenant les tables:

| Table   | Description                              |
| ------- | ---------------------------------------- |
| Billets | Pierre d'angle de la base de données. Elle permet de repérér tout passager (*nom,prenom,numéro de carte d'identité ou de passeport,...*) par son billet. Ce billet permet d'accèder au vol correspondant ainsi qu'aux bagages qui y son associés. |
| Vols    | Renseignements sur chaque vol (*destination, heure arrivée éventuelle, heure de départ, heure prévue d'arrivée à destination, avion utilisé, etc*). |
| Avions  | Les appareils utilisés pour les vols, avec notamment une indication *check_OK* pour signifier qu'il est en état de vol ou non. |
| Bagages | Elle remplacera bien-sûr le fichier csv évoqué plus haut. |
| Agents  | Elle contient tous les intervenants de l'aéroport (Agents de compagnies aériennes, bagagistes, employés agréés de tour-opérateur, aiguilleurs du ciel, etc). |

On peut ajouter des tables ou des champs aux tables existantes, des vues ou des contraintes, mais uniquement si elles sont justifiées par le projet.
### 2.2 Un outil d'accès aux bases de données
L'accès à la base de données ne devrait pas se faire avec les primitives JDBC utilisée telles quelles, mais plutôt au moyen d'objets métiers encapsulant le travail, idéalement des *Java Beans* mais **sans utilisation d'un mécanisme d'event**.
On demande donc de construire un groupe de telles classes (package *database.utilities*) permettant l'accès (c'est-à-dire à tout le moins la connextion et les sélections de base) le plus simple possible.
On souhaite pourvoir accéder, au minimum, à des bases relationnelles de type *MySql* ou *Oracle*.
Le programme de test **APPLICATION_TEST_JDBC** de la petite librairie ainsi construite proposera une interface graphique de base permettant:
- Soit de se connecter à la base *MySql* DB_AIRPORT pour y réaliser des requêtes élémentaires de type `select * from ... where...`,`select count(*) from` et `update ... set ... where ...` avec l'affichage des résultats (requêtes adaptées à la table visée - pas de tentatives de généricité à ce stade).
- Soit de se connecter à une base BD_JOURNALDEBORD, qui est une base Oracle à deux tables:

    | Table        | Description                              |
    | ------------ | ---------------------------------------- |
    | Activites    | Cours, cours-labo et travail labo en équipe, date, description, référence de l'intervenant principal `= prof si cours ou cours-labo, étudiant si activité en équipe de laboratoire` |
    | Intervenants | Profs,Etudiants                          |

    Rien n'interdit de lancer simultanément plusieurs instances de cette applications : attention donc aux accès concurrents.


----------


## 3. Le serveur Serveur_Bagages
Nous allons ici nous préoccuper de l'implémentation du modèle client-serveur pour le serveur **Serveur_Bagages** (Clients: *Application_Bagages* et *Serveur_CheckIn*)
### 3.1 Serveur_Bagages
Ce serveur est donc un serveur **multithread** *Java/Windows-Unix* (en modèle **pool de threads**) qui est chargé de gèrer tous les accès à la base de données **BD_AIRPORT** qui relèvent de la gestion des bagages (donc à l'exclusion des opérations portant sur les billets, qui relèvent de **Serveur_Billets**):
- Soit par les bagaistes de diverse compagnies aériennes
- Soit par les agents de la compagnie aérienne du checkIn, qui agissent par le serveur SerChk interposé.

Le serveur attends ses requêtes sur deux ports différents:
- **PORT_BAGAGES** pour les bagagistes
- **PORT_CHECKIN** pour les requêtes provenant de *Serveur_CheckIn*
### 3.2 Application_Bagages
Il s'agit donc ici de l'application destinée aux bagagistes. Pour interagir avec eux, le serveur utilise le *protocole applicatif* (basé TCP) **LUGAP** (**LUG**gage h**A**ndling **P**rotocol), dont les commnades sont à définir pour satisfaire au scénario exemple suivant:

*Les bagages de Mr et Mmes Charvilrom ont donc été enregistrés sous les identifiants 362-WACHARVILROM-22082017-0070-001 à 362-WACHARVILROM-22082017-0070-008.*

L'application présente donc un **GUI** qui permet tout d'abord à un bagagiste d'entrer dans l'application sur base d'un login-password (ce password ne passe pas en clair sur le réseau mais sous la forme d'un **digest salé**). Ce digest sera construit en utilisant la librairie *BouncyCastle*. En cas de succès, le bagagiste obtient alors une liste des vols prévus ce jour.\
Exemple:
---
VOL 714 WALABIES-AIRLINES - Sydney 5h30\
VOL 362 POWDER-AIRLINES - Peshawar 6h30\
VOL 152 AIR FRANCE CANAILLE - Paris 7h20
---

Un double-clic sur un item de la liste fait appraître dans une boîte de dialogue un tableau reprenant les bages enregistrés pour ce vol (Données provenant du fichier associé pour les 3 premières colonnes, initislisée avec les valeurs par défaut "N" ou "NEANT" selon le cas).
Exemple:

| Identifiant                        | Poids   | Type       | Réceptionné(O/N) | Chargé en soute (O/N) | Vérifié par la douane(O/N) | Remarques |
| ---------------------------------- | ------- | ---------- | ---------------- | --------------------- | -------------------------- | --------- |
| ...                                |         |            |                  |                       |                            |           |
| 362-WACHARVILROM-22082017-0070-001 | 27.3kg  | VALISE     | N                | N                     | N                          | NEANT     |
| ...                                |         |            |                  |                       |                            |           |
| 362-WACHARVILROM-22082017-0070-008 | 19.95kg | PAS VALISE | N                | N                     | N                          | NEANT     |
| ...                                |         |            |                  |                       |                            |           |

Le bagagiste, au fur et à mesure de ses activités, va interagir sur ce tableau. Chacune de ses
actions va générer une commande spécifique du protocole **LUGAP**, commande envoyée au
serveur *Serveur_Bagages* sur le port **PORT_BAGAGES**.

`**Sur base de l'exemple suivant, il vous appartient de définir les commandes du protocole LUGAP (et donc de leur donner un nom) et de choisir la manière de les implémenter (objets, chaînes de caractères, etc).**`

Exemple:
---
>1. Le bagagiste à réceptionné un bagage:

| Identifiant                        | Poids  | Type   | Réceptionné(O/N) | Chargé en soute (O/N) | Vérifié par la douane(O/N) | Remarques |
| ---------------------------------- | ------ | ------ | ---------------- | --------------------- | -------------------------- | --------- |
| 362-WACHARVILROM-22082017-0070-001 | 27.3kg | VALISE | **O**            | N                     | N                          | NEANT     |

>2. Le bagagiste à réceptionné un 2ème bagage:

| Identifiant                        | Poids   | Type       | Réceptionné(O/N) | Chargé en soute (O/N) | Vérifié par la douane(O/N) | Remarques |
| ---------------------------------- | ------- | ---------- | ---------------- | --------------------- | -------------------------- | --------- |
| 362-WACHARVILROM-22082017-0070-008 | 19.95kg | PAS VALISE | **O**            | N                     | N                          | NEANT     |

>3. Le bagagiste a alerté un agent des douanes pour un bagage:

| Identifiant                        | Poids   | Type       | Réceptionné(O/N) | Chargé en soute (O/N) | Vérifié par la douane(O/N) | Remarques      |
| :--------------------------------- | ------- | ---------- | ---------------- | --------------------- | -------------------------- | -------------- |
| 362-WACHARVILROM-22082017-0070-008 | 19.95kg | PAS VALISE | **O**            | N                     | **O**                      | Pas mauvais :) |

>4. Le bagagiste a réalisé le chargement en soute d'un bagage:

| Identifiant                        | Poids  | Type   | Réceptionné(O/N) | Chargé en soute (O/N) | Vérifié par la douane(O/N) | Remarques |
| ---------------------------------- | ------ | ------ | ---------------- | --------------------- | -------------------------- | --------- |
| 362-WACHARVILROM-22082017-0070-001 | 27.3kg | VALISE | **O**            | **O**                 | N                          | NEANT     |

>Le bagagiste ne peut refermer cette boîte de dialogue que si tous la bagages du vol on été chargés en soute **OU** refusés au chargement (*R* dans chargé en soute):

| Identifiant                        | Poids   | Type       | Réceptionné(O/N) | Chargé en soute (O/N) | Vérifié par la douane(O/N) | Remarques                                |
| ---------------------------------- | ------- | ---------- | ---------------- | --------------------- | -------------------------- | ---------------------------------------- |
| 362-WACHARVILROM-22082017-0070-007 | 17.95kg | PAS VALISE | **O**            | **R**                 | O                          | *poudre à lessiver de la marque "SchnoufSuper"???* |

>5. Quand cette boîte se referme, le bagagiste est automatiquement déconnecté du serveur.

##4 Programmation Web Java classique
Dossier attendu:
1. Diagramme de classes UML des classes de l'application Web.
2. ​
###4.1 L'application Web_Applic_Billets
