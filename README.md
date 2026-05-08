# Coffee Recipe Manager

  Spring Boot application for creating and sharing coffee brewing recipes.

  ## What it does
  Users register beans (origin, roast, flavor profile) and brewing methods (grind, temp, ratios) independently,
  then combine them into recipes that can be kept private, made public, or shared with specific users.

  ## Auth
  Dual security filter chains in a single Spring Boot app:
  - REST API layer: stateless JWT Bearer tokens
  - Web UI: session-based form login
  Both secured via `@Order`-separated `SecurityFilterChain` beans.

  ## Stack
  Java 21 · Spring Boot · Spring Security · JWT · JPA/Hibernate · H2 · REST API · Maven
