#!/bin/bash

mkdir -p SSLCert
cd SSLCert
mkdir -p demoCA/certs
mkdir -p demoCA/crl
mkdir -p demoCA/newcerts
mkdir -p demoCA/private

touch demoCA/index.txt
if [ ! -f demoCA/serial ]; then
    echo 01 > demoCA/serial
fi

read -p "Nom du certificat: " cert

read -sp "Mot de passe du keystore: " ks

# Gen du CA
printf "\n"
if [ ! -f ca.key ]; then
    openssl genrsa -out ca.key 4096
fi

if [ ! -f demoCA/cacert.pem ]; then
    printf "\n\nGénération du certificat du CA\n\n"
    openssl req -new -x509 -key ca.key -out demoCA/cacert.pem
    keytool -delete -alias sslauthority -storepass ${ks} -keystore client.jks
    keytool -delete -alias sslauthority -storepass ${ks} -keystore serveur.jks
fi

certc=${cert}
certc+="client"

certbc=${certc}
certbc+="base"

certs=${cert}
certs+="server"

certbs=${certs}
certbs+="base"

# Gen de la clef
printf "\nGénération du certificat du client\n"
keytool -genkey -alias ${cert} -storepass ${ks} -keyalg RSA -keysize 4096 -keypass ${ks} -dname "CN=SimarBonemmeClient$cert,O=FloryanNicolas,l=Liege,C=BE" -keystore client.jks

printf "Génération du certificat du serveur\n"
keytool -genkey -alias ${cert} -storepass ${ks} -keyalg RSA -keysize 4096 -keypass ${ks} -dname "CN=SimarBonemmeServer$cert,O=FloryanNicolas,l=Liege,C=BE" -keystore serveur.jks

# Création du certif validé
# Client
keytool -certreq -alias ${cert} -file ${certc}.csr -storepass ${ks} -keystore client.jks
openssl ca -in ${certc}.csr -out ${certc}.pem -keyfile ca.key
openssl x509 -in ${certc}.pem -out ${certc}.der -outform DER

# Server
keytool -certreq -alias ${cert} -file ${certs}.csr -storepass ${ks} -keystore serveur.jks
openssl ca -in ${certs}.csr -out ${certs}.pem -keyfile ca.key
openssl x509 -in ${certs}.pem -out ${certs}.der -outform DER

printf "Importation de l'authority\n"
# Importation du certificat dans le client et le serveur
keytool -J-Duser.language=en -import -v -alias sslauthority -file demoCA/cacert.pem -storepass ${ks} -keystore client.jks
keytool -J-Duser.language=en -import -v -alias sslauthority -file demoCA/cacert.pem -storepass ${ks} -keystore serveur.jks

printf "Importation du client\n"
# Importation du certificat du client signé par le CA
keytool -J-Duser.language=en -import -v -alias ${cert} -file ${certc}.der -storepass ${ks} -keystore client.jks

printf "Importation du serveur\n"
# Importation du certificat du serveur signé par le CA
keytool -J-Duser.language=en -import -v -alias ${cert} -file ${certs}.der -storepass ${ks} -keystore serveur.jks
