# Hackhub

## 1) Descrizione
Hackhub è una piattaforma web Single-Page per la gestione di hackathon. Permette funzionalità
di autenticazione, visualizzare e creare hackathon, e iscrivere il proprio team a un
hackathon a scelta.
NOTA: questo è un progetto universitario di uno studente di UNICAM - Università degli
Studi di Camerino - Corso di Applicazioni Web, Mobile e Cloud.

## 2) Funzionalità
- Registrazione e Login
- Visualizzazione del profilo utente
- Creazione di hackathon
- Iscrizione del proprio team a un hackathon
- Visualizzazione degli hackathon

## 3) Tecnologie utilizzate

### Backend
- Java 21
- Spring Boot 4.0.2
- Spring Data JPA e Hibernate
- Spring Security
- JWT
- Gradle
- API REST

### Frontend
- React
- TypeScript
- Vite
- React Router

### Database
- MySQL

### DevOps
- Docker
- Docker Compose
- GitHub Actions

## 4) Architettura
- Frontend Vite + React che interagisce col Backend tramite le chiamate HTTP alle
API REST
- Backend Java + Spring Boot che risolve le chiamate HTTP e inoltra le risposte al
Frontend e utilizza il Database, se necessario
- Database MySQL per la persistenza dei dati

## 5) Struttura del repository GitHub
- Cartella root principale del progetto (Hackhub-clean)
- Cartella backend con il dominio, i servizi, gli handler, i DTO e le boundary che
espongono gli endpoint REST per le chiamate HTTP
- Cartella frontend con le viste e la logica TypeScript
La build è affidata a docker-compose tramite la definizione dei Dockerfile per Frontend
e Backend, e il Database vive esclusivamente dentro il container. Tuttavia, il volume
del container è persistente, e i dati rimangono persistiti ad ogni build del progetto.

## 6) Build + Configurazione
Requisiti:
- Docker
- Docker Compose
- Git
Sequenza di passi per la build:
- git clone <repository>
- cd Hackhub-clean
- cp .env.example .env
- docker compose up --build

### ESEMPIO DI TEMPLATE PER FILE .env
- MYSQL_DATABASE=hackhub [nome DB container MySQL]
- MYSQL_USER=hackhub [nome utente MySQL app]
- MYSQL_PASSWORD=change_me [password utente MySQL]
- MYSQL_ROOT_PASSWORD=change_me [password root a scelta, non necessariamente uguale a quella sopra]
- MYSQL_PORT=3306 [porta host MySQL, lasciare invariato]

- DB_HOST=mysql [host usato da Spring]
- DB_PORT=3306 [porta host MySQL, deve restare invariata]
- DB_NAME=hackhub [nome del database usato da Spring]
- DB_USERNAME=hackhub [nome utente usato da Spring]
- DB_PASSWORD=change_me [password a scelta usata da Spring, uguale a MYSQL_PASSWORD]

- SERVER_PORT=8081 [porta del backend]

- APP_JWT_SECRET=change_me_with_a_long_random_secret [JWT Secret a scelta, almeno 32 caratteri]
- APP_JWT_EXPIRATION_MS=3600000 [durata di validità del token, qui 1h]

- CORS_ALLOWED_ORIGIN=http://localhost [origine consentita dal CORS]

- Da aggiornare a fine progetto

## 7) Pipeline CI/CD
- GitHub Actions
- Da aggiornare a fine progetto

## 8) Deployment
- Da aggiornare a fine progetto

## 9) Autori
- Silenzi Jhonatan - Corso di laurea in informatica (L-31) - UNICAM
