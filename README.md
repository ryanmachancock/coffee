# Coffee Recipe Organizer

A web app for creating, organizing, and sharing coffee brewing recipes. Built with Spring Boot and deployed on Railway.

**Live demo:** https://coffee-production-a4ad.up.railway.app

---

## Features

- **Recipe management** — combine a bean and a brewing method into a named recipe with notes
- **Bean library** — track beans by origin, roast level, and flavor profile
- **Brewing instructions** — store precise parameters: grind size, water temp, coffee/water ratios, and step-by-step instructions
- **Privacy controls** — recipes default to private; make them public or share selectively
- **Direct sharing** — share specific recipes with individual users by username
- **Community recipes** — browse all public recipes and clone any of them into your own collection

---

## Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.5 |
| Security | Spring Security — dual filter chains (session + JWT) |
| Database | PostgreSQL (production) · H2 file DB (local dev) |
| ORM | JPA / Hibernate |
| Frontend | Vanilla HTML/JS · Tailwind CSS |
| Build | Maven |
| Hosting | Railway |

The REST API (`/api/**`) uses stateless JWT Bearer tokens. The web UI uses session-based form login. Both are handled by separate `@Order`-prioritized `SecurityFilterChain` beans in a single Spring Boot application.

---

## Local development

**Prerequisites:** Java 21, Maven

```bash
git clone https://github.com/ryanmachancock/coffee.git
cd coffee
./mvnw spring-boot:run
```

App starts at `http://localhost:8080` using an H2 file database at `./data/coffeeDB`.

Set a custom JWT secret if needed:

```
JWT_SECRET=your-secret-here
```

---

## Production deployment (Railway)

1. Connect the GitHub repo to a Railway project
2. Add a PostgreSQL plugin
3. Set these environment variables on the app service:

```
SPRING_PROFILES_ACTIVE=prod
SPRING_DATASOURCE_URL=jdbc:postgresql://${{Postgres.PGHOST}}:${{Postgres.PGPORT}}/${{Postgres.PGDATABASE}}
SPRING_DATASOURCE_USERNAME=${{Postgres.PGUSER}}
SPRING_DATASOURCE_PASSWORD=${{Postgres.PGPASSWORD}}
JWT_SECRET=<random 32+ char string>
JAVA_TOOL_OPTIONS=-Xmx256m -Xms64m
```

Railway redeploys automatically on every push to `main`.
