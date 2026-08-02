# Cinema — API de réservation

API REST (Spring Boot / POJA) pour la gestion d'un cinéma : salles, films, séances, sièges,
réservations et utilisateurs avec rôles (CLIENT / EMPLOYEE / MANAGER).

## Modules de développement

1. **Setup & fondations** — dépendances, datasource Postgres, config JPA/JWT, infra de test
2. **Domaine** — entités JPA (`User`, `Room`, `Seat`, `Movie`, `Projection`, `Reservation`)
3. **Persistance** — repositories Spring Data + migrations Flyway
4. **Services** — logique métier (règles de réservation, disponibilité des sièges, etc.)
5. **Sécurité** — authentification JWT, autorisation par rôle
6. **API — Films & Salles** — endpoints `/movies`, `/rooms`
7. **API — Séances** — endpoints `/projections`
8. **API — Réservations** — endpoints `/reservations` (règles de propriété CLIENT)
9. **Finalisation** — couverture Jacoco ≥ 80 %, documentation OpenAPI, nettoyage

## Convention de commits

Ce projet suit [Conventional Commits](https://www.conventionalcommits.org/) :
`feat: add Reservation entity`, `test: cover seat availability rules`, `fix: ...`, etc.
Chaque module ci-dessus correspond à une branche/PR squash-mergée.

## Lancer les tests

```bash
./gradlew test jacocoTestReport
```

Les tests d'intégration démarrent un vrai Postgres via Testcontainers (Docker requis).
