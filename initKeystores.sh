#!/bin/bash
# Initialisation du dossier de travail
clear
rm -r SSLCert
rm ../Librairie/src/Tools/Crypto/Keystores/* -rf

mkdir -p SSLCert
cd SSLCert
mkdir -p demoCA/certs
mkdir -p demoCA/crl
mkdir -p demoCA/newcerts
mkdir -p demoCA/private

touch demoCA/index.txt

ks="azerty"

mastercard="Serveur_Mastercard.pkcs12"
payement="Serveur_Payement.pkcs12"
appBillets="Application_Billets.pkcs12"
servBillets="Serveur_Billets.pkcs12"
web="Application_Web.pkcs12"
employe="Employe.pkcs12"

if [ ! -f demoCA/serial ]; then
    echo 01 > demoCA/serial
fi

if [ ! -f ca.key ]; then
    openssl genrsa -out ca.key 4096
fi

if [ ! -f demoCA/cacert.pem ]; then
    printf "\n\nGénération du certificat du CA\n\n"
    openssl req -new -x509 -key ca.key -out demoCA/cacert.pem -subj "/CN=Simar Floryan - Bonemme Nicolas | CA/OU=2326/O=HEPL/L=Liege/ST=Liege/C=BE"
    keytool -delete -alias sslauthority -storepass ${ks} -keystore ${payement} -noprompt
    keytool -delete -alias sslauthority -storepass ${ks} -keystore ${mastercard} -noprompt
fi

printf "\nGénération des keystores pour le serveur_payement\n"

printf "Génération du certificat du serveur_payement\n"
keytool -genkey -alias payement -storepass ${ks} -keyalg RSA -keysize 4096 -keypass ${ks} -dname "CN=Simar Floryan - Bonemme Nicolas | ServeurPayement,OU=2326,O=HEPL,L=Liège,ST=Liège,C=BE" -keystore ${payement} -storetype PKCS12

printf "Génération du certificat du serveur_mastercard\n"
keytool -genkey -alias payement -storepass ${ks} -keyalg RSA -keysize 4096 -keypass ${ks} -dname "CN=Simar Floryan - Bonemme Nicolas | ServeurMasterCard,OU=2326,O=HEPL,L=Liège,ST=Liège,C=BE" -keystore ${mastercard} -storetype PKCS12

# Création du certif validé
# Client
printf "Exportation du certificat du serveur_payement\n"
keytool -certreq -alias payement -file payement.csr -storepass ${ks} -keystore ${payement} -storetype PKCS12

printf "Signature du certificat du serveur_payement\n"
openssl ca -in payement.csr -out payement.pem -keyfile ca.key -batch
openssl x509 -in payement.pem -out payement.der -outform DER

# Server
printf "Exportation du certificat du serveur_mastercard\n"
keytool -certreq -alias payement -file mastercard.csr -storepass ${ks} -keystore ${mastercard} -storetype PKCS12

printf "Signature du certificat du serveur_mastercard\n"
openssl ca -in mastercard.csr -out mastercard.pem -keyfile ca.key -batch
openssl x509 -in mastercard.pem -out mastercard.der -outform DER

printf "Importation de l'authority\n"
# Importation du certificat dans le client et le serveur
keytool -J-Duser.language=en -import -v -alias sslauthority -file demoCA/cacert.pem -storepass ${ks} -keystore ${payement} -noprompt -storetype PKCS12
keytool -J-Duser.language=en -import -v -alias sslauthority -file demoCA/cacert.pem -storepass ${ks} -keystore ${mastercard} -noprompt -storetype PKCS12

printf "Importation du certificat signé du client\n"
# Importation du certificat du client signé par le CA
keytool -J-Duser.language=en -import -alias payement -file payement.der -storepass ${ks} -keystore ${payement} -noprompt -storetype PKCS12
keytool -J-Duser.language=en -import -alias mastercard -file mastercard.der -storepass ${ks} -keystore ${payement} -noprompt -storetype PKCS12

printf "Importation du certificat signé du serveur\n"
# Importation du certificat du serveur signé par le CA
keytool -J-Duser.language=en -import -alias payement -file mastercard.der -storepass ${ks} -keystore ${mastercard} -noprompt -storetype PKCS12

printf "Création d'une entrée par défaut pour le tour opérateur\n"
keytool -genkey -alias TUI -storepass ${ks} -keyalg RSA -keysize 4096 -keypass ${ks} -dname "CN=Simar Floryan - Bonemme Nicolas | EmployeTUI,OU=2326,O=HEPL,L=Liège,ST=Liège,C=BE" -keystore ${employe} -storetype PKCS12

printf "Création de la clef privée du serveur_billets\n"
keytool -genkey -alias appbillets -storepass ${ks} -keyalg RSA -keysize 4096 -keypass ${ks} -dname "CN=Simar Floryan - Bonemme Nicolas | ApplicationBillets,OU=2326,O=HEPL,L=Liège,ST=Liège,C=BE" -keystore ${servBillets} -storetype PKCS12

printf "Exportation du certificat du serveur_billets vers application_billets et web\n"
keytool -exportcert -alias appbillets -storepass ${ks} -keypass ${ks} -file ServeurBillets.pem -keystore ${servBillets} -storetype PKCS12
keytool -J-Duser.language=en -importcert -alias appbillets -storepass ${ks} -keypass ${ks} -file ServeurBillets.pem -keystore ${appBillets} -noprompt -storetype PKCS12
keytool -J-Duser.language=en -importcert -alias appbillets -file ServeurBillets.pem -storepass ${ks} -keypass ${ks} -keystore ${web} -noprompt -storetype PKCS12

printf "Exportation du certificat du serveur_payement vers application_billets et web\n"
keytool -J-Duser.language=en -importcert -alias serveurpayement -file payement.der -storepass ${ks} -keypass ${ks} -keystore ${appBillets} -noprompt -storetype PKCS12
keytool -J-Duser.language=en -importcert -alias serveurpayement -file payement.der -storepass ${ks} -keypass ${ks} -keystore ${web} -noprompt -storetype PKCS12

printf "Exportation du certificat de l'employé dans serveur_payement\n"
keytool -exportcert -alias TUI -storepass ${ks} -keypass ${ks} -file TUI.pem -keystore ${employe} -storetype PKCS12
keytool -J-Duser.language=en -importcert -alias TUI -file TUI.pem -storepass ${ks} -keypass ${ks} -keystore ${payement} -noprompt -storetype PKCS12

printf "=========================\n"

# printf "Keystore: ${mastercard}\n"
# keytool -J-Duser.language=en -list -storepass ${ks} -keystore ${mastercard} -v
#
# printf "\n\nKeystore: ${payement}\n\n"
# keytool -J-Duser.language=en -list -storepass ${ks} -keystore ${payement} -v
#
# printf "\n\nKeystore: ${appBillets}\n\n"
# keytool -J-Duser.language=en -list -storepass ${ks} -keystore ${appBillets} -v
#
# printf "\n\nKeystore: ${servBillets}\n\n"
# keytool -J-Duser.language=en -list -storepass ${ks} -keystore ${servBillets} -v
#
# printf "\n\nKeystore: ${web}\n\n"
# keytool -J-Duser.language=en -list -storepass ${ks} -keystore ${web} -v
#
# printf "\n\nKeystore: ${employe}\n\n"
# keytool -J-Duser.language=en -list -storepass ${ks} -keystore ${employe} -v

printf "\n\nDéplacement des keystores dans le dossier correspondant à l'application\n"
mv *.pkcs12 ../Librairie/src/Tools/Crypto/Keystores/
