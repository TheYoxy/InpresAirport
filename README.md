# InpresAirport
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
Le serveur est un serveur multithread C/Unix en modèle pool de threads. Il est chargé
de répondre aux requêtes provenant de **Application_CheckIn (C/C++)** utilisée par les agents
des compagnies aériennes qui assurent l'accueil des passagers pour les différents vols
programmés. Le serveur attend ce type de requête sur le PORT_CHCK. Il utilise le protocole
applicatif (basé TCP) **CIMP** (CheckIn Management Protocol).
### 1.2 La gestion des bagages : accès à Serveur_Bagages
On utilise donc ici des fichiers de données de type csv. Il est bien clair que ce serveur
**Serveur_CheckIn** devra agir directement sur la base de données (exposée au point suivant)
ainsi qu'interagir avec le serveur **Serveur_Bagages** (voir plus loin) qui agit sur cette même
base de données BD_AIRPORT.
Des librairies d'accès aux bases de données relationnelles existent bien sûr en C/C++,
mais elles ne sont pas du tout portables et/ou posent des problèmes de déploiement d'un
système à un autre). Nous les éviterons donc.
Comme la base BD_AIRPORT et le Serveur_Bagages ne sont pas encore disponibles,
il convient de prévoir une conception logicielle qui isole les demandes à ce serveur dans une
librairie de fonctions dont l'implémentation sera modifiée ultérieurement (avec le minimum
de réécriture de code). On pense donc ici à des fonctions du type suivant (ce sont des
exemples - libre à vous d'en concevoir d'autres du même style) :

#### Exemple : Librairie AccessBilBag

Fonction | Sémantique | Valeur retournée
--- | --- | --- 
`int verifyTicket(char * number, int nbPassengers)` | Vérification de l'existence d'un billet d'avion avec un certain nombre d’accompagnants. | 0 ou 1
`float addLuggage(cahr * number, float weight, char suitcase)`|Enregistrement d'un bagage de poids donné, sous forme de valise ou pas, pour le billet d'avion précisé.| Poids total actuel pour le billet.

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
    
        Bien | Pas bien
        --- | ---
        `xxx receiveSize(void * struc, int size)` | `xxx receive(ListePassagers *lc,int size)` et pas `xxx getPassagers(...,...)`
        Couche basse : réutilisable dans une autre application |  Une seule couche: la fonction receive ne peut être utilisée dans une autre application
        `xxx receiveSep(char *chaine, char *sep)` | 
        Couche basse : réutilisable dans une autre application |
        avec `ListePassagers getPassengers (...,...)` |
        Couche haute : propre à cette application - utilise l'une des fonctions ci-dessus |
    - En tenant compte de l'administration du serveur, il serait avisé de faire intervenir dans le code du serveur la notion d'état de celui-ci (*Certaines commandes n'ont de sens que si elles sont précédées d'une autre*).
4. Il est impérieux de surveiller les écoutes, les connexions et les communisations réseaux
    - Au moyen des commandes **ping** et surtout **netstat** (Pour linux: `netstat -an | grep *NuméroDePort*`).
    - En utilisant un **sniffer* comme *Wireshark* ou autre encore analysant le trafic réseau (Attention au localhost qui ne permet pas de sniffer simplement). Cette pratique sera demandée lors des évaluations.
5. Il serait aussi intéréssant de prévoir un fichier de configuration lu par le serveur à son démarrage. A l'image des fichiers properties de *Java*, il s'agit d'un simple fichier texte dont chaque ligne comporte une propriété du serveur et la valeur de celle-ci.
    
    Fichier de configuration : | serveur_checkin.conf
    --- | ---
    Port_Service=70000 |
    Port_Admin=70009 |
    sep-trame=$ |
    fin-trame=# |
    sep-csv=; |
    pwd-master=tusaisquetuesbeautoi |
    pwd-admin=jeaclachralf.. |
    ---
## 2. Les accès aux bases de données
### 2.1 La base de données BD_AIRPORT
Base de données *MySql* BD_AIRPORT doit contenir toutes les informations utiles concernant le fonctionnement de l'aéroport (**Uniquement**). Ses tables, si on admet un certain nombre d'approximations, de simplifications et d'omission sans importance pour le projet tel que défini ici (Avec le minimum de champs, en définitive), seront en première analyse celle d'une base de données classique, que l'on peut définir dans un premier temps sommairement comme contenant les tables:

Table | Description
--- | --- 
Billets | Pierre d'angle de la base de données. Elle permet de repérér tout passager (*nom,prenom,numéro de carte d'identité ou de passeport,...*) par son billet. Ce billet permet d'accèder au vol correspondant ainsi qu'aux bagages qui y son associés.
Vols | Renseignements sur chaque vol (*destination, heure arrivée éventuelle, heure de départ, heure prévue d'arrivée à destination, avion utilisé, etc*).
Avions | Les appareils utilisés pour les vols, avec notamment une indication *check_OK* pour signifier qu'il est en état de vol ou non.
Bagages | Elle remplacera bien-sûr le fichier csv évoqué plus haut.
Agents | Elle contient tous les intervenants de l'aéroport (Agents de compagnies aériennes, bagagistes, employés agréés de tour-opérateur, aiguilleurs du ciel, etc).

On peut ajouter des tables ou des champs aux tables existantes, des vues ou des contraintes, mais uniquement si elles sont justifiées par le projet.
###2.2 Un outil d'accès aux bases de données
