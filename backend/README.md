# HackHub

Backend Spring Boot per la gestione di hackathon, team, sottomissioni, valutazioni, richieste e notifiche.

## Indice
- [Funzionalita principali](#funzionalita-principali)
- [Stack tecnologico](#stack-tecnologico)
- [Architettura](#architettura)
- [Prerequisiti](#prerequisiti)
- [Configurazione ambiente](#configurazione-ambiente)
- [Avvio locale](#avvio-locale)
- [Autenticazione](#autenticazione)
- [Formato errori API](#formato-errori-api)
- [REST API Reference](#rest-api-reference)
- [Esempi cURL](#esempi-curl)
- [Struttura progetto](#struttura-progetto)
- [Test](#test)
- [Troubleshooting](#troubleshooting)
- [Sicurezza](#sicurezza)
- [Contributi](#contributi)
- [Roadmap](#roadmap)
- [Licenza](#licenza)
- [Autori](#autori)

## Funzionalita principali
- Registrazione e login con JWT.
- Creazione e gestione hackathon (staff, iscrizioni, espulsioni, vincitore, liquidazione premio).
- Gestione team (creazione, cambio nome, inviti, espulsioni, scioglimento, passaggio leadership).
- Sottomissioni progetto e valutazioni da parte del giudice.
- Richieste/notifiche e flussi di assistenza/call mentore-team.

## Stack tecnologico
- Java 21
- Spring Boot
- Spring Security + JWT
- Spring Data JPA (Hibernate)
- MySQL
- Docker Compose
- Gradle

## Architettura
- `boundary`: controller REST (ingresso HTTP).
- `handler`: logica applicativa/casi d'uso.
- `domain/implementazione`: modello di dominio.
- `repository`: accesso dati JPA.
- `servizi`: servizi infrastrutturali (JWT, notifiche, scheduler).

Pattern principali presenti:
- `State` per ciclo di vita hackathon.
- `Builder` per costruzione hackathon.

## Prerequisiti
- Java 21
- Docker + Docker Compose
- (opzionale) MySQL locale se non usi container

## Configurazione ambiente

### 1) Crea il file locale

```bash
cp .env.example .env
```

Su Windows PowerShell:

```powershell
Copy-Item .env.example .env
```

### 2) Variabili usate

| Variabile | Descrizione | Esempio |
|---|---|---|
| `MYSQL_DATABASE` | Nome DB container MySQL | `hackhub` |
| `MYSQL_USER` | Utente MySQL app | `hackhub` |
| `MYSQL_PASSWORD` | Password utente MySQL | `change_me` |
| `MYSQL_ROOT_PASSWORD` | Password root MySQL | `change_me` |
| `MYSQL_PORT` | Porta host MySQL | `3306` |
| `DB_HOST` | Host DB usato da Spring | `localhost` |
| `DB_PORT` | Porta DB usata da Spring | `3306` |
| `DB_NAME` | Nome DB usato da Spring | `hackhub` |
| `DB_USERNAME` | Utente DB usato da Spring | `hackhub` |
| `DB_PASSWORD` | Password DB usata da Spring | `change_me` |
| `APP_JWT_SECRET` | Secret JWT (min 32 char) | `replace_with_long_random_secret` |
| `APP_JWT_EXPIRATION_MS` | Scadenza token in ms | `3600000` |

## Avvio locale

### 1) Avvia MySQL con Docker Compose

```bash
docker compose up -d
```

### 2) Avvia backend

```bash
./gradlew bootRun
```

Su Windows PowerShell:

```powershell
.\gradlew.bat bootRun
```

Base URL API locale: `http://localhost:8080`

## Autenticazione
- Endpoint pubblici: `/api/autenticazione/**`
- Tutti gli altri endpoint richiedono JWT Bearer.
- Eccezione pubblica: `GET /api/hackathon` (lista info hackathon).

Header richiesto:

```http
Authorization: Bearer <token>
```

### Login
`POST /api/autenticazione/accesso`

Request JSON:

```json
{
  "nomeUtente": "mario",
  "password": "Password123!"
}
```

Response JSON:

```json
{
  "token": "<jwt>",
  "tipo": "Bearer"
}
```

## Formato errori API

Error response standard:

```json
{
  "message": "descrizione errore"
}
```

Mappatura principale:
- `400` `BadRequestException`, validazione DTO, `IllegalArgumentException`
- `403` `ForbiddenException`
- `404` `NotFoundException`
- `409` `ConflictException`, `TransizioneNonConsentitaException`
- `500` eccezioni non gestite

## REST API Reference

Nota: dove indicato `TEXT`, il body e plain text (`Content-Type: text/plain`).

### 1) Autenticazione
- `POST /api/autenticazione/registrazione` - registra utente
- `POST /api/autenticazione/accesso` - login e JWT

`RegisterRequest`:
- `nomeUtente` string
- `email` email
- `password` string (min 6)

### 2) Hackathon - creazione/gestione
- `POST /api/hackathon` - crea hackathon (organizzatore autenticato)
- `POST /api/hackathon/{nomeHackathon}/iscrizioni` - iscrive il team del leader
- `DELETE /api/hackathon/{nomeHackathon}/iscrizioni/mia` - annulla iscrizione del proprio team
- `POST /api/hackathon/{nomeHackathon}/violazione?nomeTeam=...` - mentore segnala violazione
- `POST /api/hackathon/{nomeHackathon}/nomine-mentori?nomeUtenteDaInvitare=...` - organizzatore invita mentore
- `DELETE /api/hackathon/{nomeHackathon}` - elimina hackathon
- `POST /api/hackathon/{nomeHackathon}/team/{nomeTeam}/espulsione` - espelle team
- `POST /api/hackathon/{nomeHackathon}/vincitore?nomeTeam=...` - proclama vincitore
- `POST /api/hackathon/{nomeHackathon}/liquidazione-premio?nomeTeam=...` - liquida premio

`HackathonRequest` (POST `/api/hackathon`):
- `nome` string
- `dataInizio` date (`yyyy-MM-dd`)
- `dataFine` date (`yyyy-MM-dd`)
- `luogo` string
- `premio` number
- `teamMin` int [3..6]
- `teamMax` int [3..6]
- `maxIscrizioni` int >= 1
- `regolamento` string
- `scadenzaIscrizioni` datetime (`yyyy-MM-dd'T'HH:mm:ss`)
- `nomeGiudice` string
- `nomeMentori` string[] (min 1)

### 3) Team
- `POST /api/team` - crea team (`TEXT`: nome team)
- `PATCH /api/team` - cambia nome team (`TEXT`: nuovo nome)
- `DELETE /api/team/membri/me` - esci dal team
- `DELETE /api/team/mio` - sciogli team
- `DELETE /api/team/membri/{nomeMembro}` - espelli membro
- `POST /api/team/leader?nomeMembro=...` - trasferimento ruolo leader immediato (senza richiesta)
- `POST /api/team/mio/invito?nomeUtenteDaInvitare=...` - invita utente nel team

### 4) Sottomissioni e valutazioni
- `POST /api/sottomissioni/{nomeHackathon}` - invia sottomissione (`TEXT`: link)
- `DELETE /api/sottomissioni/{nomeHackathon}` - rimuove sottomissione
- `POST /api/sottomissioni/{idSottomissione}/valutazione` - inserisce/aggiorna valutazione

`ValutazioneRequest`:
- `giudizio` string non vuota
- `punteggio` int [0..10]

### 5) Call e assistenza
- `POST /api/call/proposta` - mentore propone call
- `POST /api/assistenza/richiesta?nomeMentore=...&nomeHackathon=...` - leader richiede assistenza
- `POST /api/richieste-supporto/risposta?idNotifica=...` - mentore risponde a richiesta supporto

`PropostaCallRequest`:
- `idHackathon` string
- `idTeam` string
- `data` date (`yyyy-MM-dd`)
- `ora` time (`HH:mm:ss`)

### 6) Richieste
- `POST /api/richieste/{idRichiesta}/accetta` - accetta richiesta
- `POST /api/richieste/{idRichiesta}/rifiuta` - rifiuta richiesta

Le richieste gestite in questo blocco sono inviti team/staff e proposte call.
Il cambio leader del team avviene direttamente tramite `POST /api/team/leader`.

### 7) Visualizzazione
- `GET /api/hackathon/{nomeHackathon}/valutazioni`
- `GET /api/hackathon/{nomeHackathon}/sottomissioni`
- `GET /api/hackathon/{nomeHackathon}/iscrizioni`
- `GET /api/richieste`
- `GET /api/notifiche`
- `GET /api/hackathon` (pubblico, non richiede JWT)

## Esempi cURL

Registrazione:

```bash
curl -X POST http://localhost:8080/api/autenticazione/registrazione \
  -H "Content-Type: application/json" \
  -d '{"nomeUtente":"mario","email":"mario@example.com","password":"Password123!"}'
```

Login:

```bash
curl -X POST http://localhost:8080/api/autenticazione/accesso \
  -H "Content-Type: application/json" \
  -d '{"nomeUtente":"mario","password":"Password123!"}'
```

Creazione team (richiede token):

```bash
curl -X POST http://localhost:8080/api/team \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: text/plain" \
  -d 'TeamRocket'
```

Valutazione sottomissione (richiede token):

```bash
curl -X POST http://localhost:8080/api/sottomissioni/S-123/valutazione \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"giudizio":"Ottimo","punteggio":9}'
```

## Struttura progetto

```text
src/
  main/
    java/unicam/cs/hackhub/
      boundary/
      handler/
      domain/
      repository/
      servizi/
    resources/
      application.properties
      application.yml
  test/
    java/unicam/cs/hackhub/
      testHttp/
```

## Test

```bash
./gradlew test
```

Su Windows PowerShell:

```powershell
.\gradlew.bat test
```

## Troubleshooting
- Errore connessione DB: verifica che `docker compose ps` mostri MySQL `Up` e che `DB_*`/`MYSQL_*` siano coerenti.
- `401` sugli endpoint protetti: controlla header `Authorization` e scadenza token.
- Errori validazione (`400`): verifica payload e tipi campi secondo i DTO.

## Sicurezza
- Non committare `.env` (ignorato da `.gitignore`).
- Usa un `APP_JWT_SECRET` robusto e diverso per ambiente.
- Se credenziali sono state esposte, ruotale e valuta pulizia history Git.

## Contributi
1. Crea branch feature.
2. Mantieni test verdi (`./gradlew test`).
3. Apri PR con descrizione modifiche e impatto API.

## Roadmap
- Esposizione documentazione OpenAPI/Swagger.
- Maggiore copertura test su scenari edge e sicurezza.
- Hardening osservabilita (metriche/log strutturati).

## Licenza
Al momento non e specificata una licenza esplicita nel repository.

## Autori
- Letizia Pistola
- Giada Branchesi
- Jhonatan Silenzi
