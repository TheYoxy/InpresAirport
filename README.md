# InpresAirport
## 1. Serveur_CheckIN et Application_CheckIn
### 1.1. L'enregistrement des passagers : client-serveur
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
		- `SocketServeur`
On évitera la construction de flux réseaux d'entrée et de sortie, `NetworkStreamBase` `ONetworkStream` `INetworkStream`, car cela devient trop ambitieux pour le temps dont on dispose.