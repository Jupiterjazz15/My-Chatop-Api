# Chatop API : Facilitez la communication entre locataires et propriétaires

Chatop API est une application conçue pour simplifier la communication entre les locataires potentiels et les propriétaires d'appartements. Elle offre une plateforme intuitive pour :

- Créer un compte utilisateur,

- Gérer ses informations,

- Interagir avec les annonces de location.

## Fonctionnalités principales

- Création d'un compte utilisateur : Toute personne peut s'inscrire.

- Connexion : Se connecter à son compte utilisateur.

- Gestion de compte : Consulter les informations relatives à son compte.

- Gestion des locations :

  - Créer une location.

  - Modifier les informations d'une location (uniquement pour les propriétaires).

  - Consulter une ou plusieurs locations disponibles.

## Technologies utilisées

### Back-end

- Java 17
- Spring Boot 3.3.5
- Spring Data JPA
- Spring Security
- OAuth 2 Resource Server
- Spring Validation
- Spring Web
- SpringDoc OpenAPI
- ModelMapper
- MySQL Connector
- JWT (Java Web Tokens)
- Maven
- Apache Maven Resources Plugin

### Front-end

- Angular 
- TypeScript
- SCSS
- HTML
- Node

## Prérequis

Assurez-vous d'avoir les éléments suivants installés sur votre machine :

Back-end : 
- Java 17 ou version compatible.
- Apache Maven 
- MySQL 

Front-end 
- Node.js
- npm 

### Installation du Back-end

Étape 1 : Cloner le dépôt

> git clone https://github.com/Jupiterjazz15/My-Chatop-Api.git
> 
> cd Chatop-Api

Étape 2 : Configurer la base de données MySQL

Connectez-vous à MySQL en utilisant votre terminal ou un outil comme MySQL Workbench.

Créez une base de données nommée new_location_db :

>CREATE DATABASE new_location_db;

Vérifiez les identifiants de connexion dans le fichier application.properties :

>spring.datasource.url=jdbc:mysql://localhost:3306/new_location_db
spring.datasource.username=your_username
spring.datasource.password=your_password

Étape 3 : Installer les dépendances

>mvn clean install

Étape 4 : Démarrer le serveur

>mvn spring-boot:run

Étape 5 : Vérifier l'installation en lançant le serveur à cette adresse http://localhost:8080


### Installation du Front-end

Étape 1 : Cloner le dépôt

>git clone https://github.com/OpenClassrooms-Student-Center/Developpez-le-back-end-en-utilisant-Java-et-Spring.git

>cd Developpez-le-back-end-en-utilisant-Java-et-Spring

Étape 2 : Installer les dépendances

> npm install

Étape 3 : Modifier le port pour correspondre au Back-end

Ouvrez les fichiers suivants :

- src/environments/environment.ts
- src/environments/environment.prod.ts
- src/proxy.config.json

Remplacez le port dans chaque fichier en mettant 9090.


Étape 4 : Lancer l'application

>ng serve

Étape 5 : Accéder à l'application

Accédez à l'application à cette adresse :http://localhost:4200

## Auteur

Coralie Haller