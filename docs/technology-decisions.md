# Technology Decisions, Tutorials, And Interview Guide

## Project Summary

This project is an AI-powered job portal MVP that helps candidates find better
job matches and helps employers review applicants faster. Candidates can upload a
resume, receive an AI-generated profile summary, see recommended jobs, and get
resume improvement suggestions. Employers can post jobs, review applicants, and
use AI-assisted ranking and explanations to understand why a candidate may be a
good fit.

The MVP is designed as a practical, production-shaped system:

- Two focused web apps: one for candidates and one for employers.
- Domain-based backend services for auth, candidates, employers, jobs,
  applications, matching, and notifications.
- PostgreSQL as the transactional source of truth.
- pgvector for early semantic resume/job matching without a separate vector
  database.
- Redis for cache, rate limits, and short-lived workflow state.
- MinIO for local S3-compatible resume file storage.
- AI provider abstraction so Vertex AI Gemini, OpenAI, or another model provider
  can be swapped without rewriting product workflows.
- Docker Compose for local development and Cloud Run/GKE/Terraform paths for
  later deployment.

The main architecture idea is simple: keep core hiring data, permissions, and
workflow decisions in reliable backend services, while using AI as an assistive
layer for parsing, recommendations, explanations, and candidate guidance.

## How To Use This Document

This document explains the MVP technology stack in a way that is useful for both
implementation and interviews. Each technology section answers four questions:

- What does it do?
- Why did we pick it for this AI-powered job portal?
- Why did we pick it instead of common alternatives?
- How do we use it in this project?

The stack follows the architecture document, but the choices are also practical:
move fast for MVP, keep production paths open, and avoid adding expensive
specialized infrastructure before product-market fit is proven.

## Quick Technology Map

| Area                   | Technology                                                      | Where It Appears                               |
| ---------------------- | --------------------------------------------------------------- | ---------------------------------------------- |
| Candidate web          | Next.js, React, TypeScript, Tailwind CSS, TanStack Query, Axios | `frontend/candidate-web`                       |
| Employer web           | Next.js, React, TypeScript, Tailwind CSS, TanStack Query, Axios | `frontend/employer-web`                        |
| Backend services       | Java 25, Spring Boot 4.0.6, Spring Security, Spring Data JPA    | `backend/*-service`                            |
| API contracts          | OpenAPI / Swagger later, markdown contracts now                 | `docs/api-contracts.md`                        |
| Primary database       | PostgreSQL                                                      | `docker-compose.yml`, `scripts/seed-data.sql`  |
| Vector search          | pgvector                                                        | `scripts/seed-data.sql`, matching design       |
| Cache and rate limit   | Redis                                                           | `docker-compose.yml`                           |
| Resume object storage  | MinIO locally, S3-compatible storage later                      | `docker-compose.yml`                           |
| AI providers           | Vertex AI Gemini and OpenAI-compatible interfaces               | `ai/prompts`, backend AI clients               |
| Local development      | Docker Compose                                                  | `docker-compose.yml`, `scripts/start-local.sh` |
| Cloud deployment       | Cloud Run first, GKE/Kubernetes when needed                     | `infra/k8s`                                    |
| Infrastructure as code | Terraform placeholders                                          | `infra/terraform`                              |

## Interview Story For The Whole Architecture

If someone asks why this architecture is designed this way, use this answer:

The product has two main user groups, candidates and employers, with different
workflows. I separated the frontend into candidate and employer apps so each
workflow can move independently. The backend is split by domain because auth,
candidate profiles, jobs, applications, matching, and notifications have
different scaling, security, and ownership needs. PostgreSQL is the source of
truth because hiring workflows are transactional. pgvector is used for MVP vector
matching so embeddings stay close to product data. Redis, MinIO, and Docker
Compose provide local infrastructure without requiring cloud accounts. AI
provider calls are hidden behind interfaces so prompts, models, and vendors can
change without rewriting the product.

## Frontend

### Next.js 15

#### What It Does

Next.js is the React application framework used by both web apps. It provides
file-based routing, build tooling, production bundling, layouts, metadata, and a
clear project structure.

In this project:

- Candidate routes live in `frontend/candidate-web/src/app`.
- Employer routes live in `frontend/employer-web/src/app`.
- Each app has its own port and deployment boundary.

#### Why We Picked It

Next.js is a strong fit because this product is not just a static website. It is
a workflow application with authenticated pages, dashboards, forms, and
eventually server-rendered or server-assisted views. Next.js gives us a mature
React setup without hand-assembling routing, bundling, TypeScript, and production
configuration from scratch.

#### Why Not The Alternatives?

- Vite + React SPA: excellent for a pure client-side app, but it leaves routing,
  metadata, deployment conventions, and server rendering choices more manual.
- Angular: powerful, but heavier for an MVP and less aligned with the React
  ecosystem requested in the architecture.
- Vue/Nuxt: productive, but the project stack already standardizes on React.
- Plain HTML/JS: too limited for authenticated dashboards and reusable product
  workflows.

#### Mini Tutorial

Run the candidate app:

```bash
cd frontend/candidate-web
npm install
npm run dev
```

Open:

```text
http://localhost:3000
```

Create a new route by adding a folder and page:

```text
src/app/applications/page.tsx
```

Use a client component when you need browser-only interactivity:

```tsx
"use client";

export default function UploadButton() {
  return <button onClick={() => alert("Upload clicked")}>Upload</button>;
}
```

Keep server-only secrets out of client components. Variables exposed to the
browser must start with `NEXT_PUBLIC_`.

#### Common Mistakes

- Using `use client` everywhere instead of only where browser state is needed.
- Fetching server state manually in many components instead of using React Query
  hooks.
- Mixing candidate and employer workflow code in one app too early.

#### Interview Questions And Answers

**Q: Why did you choose Next.js instead of a plain React SPA?**

A: Next.js gives React a production application structure: routing, layouts,
metadata, optimized builds, and a path to server rendering. For a job portal,
that matters because we have dashboard workflows now and may need SEO-friendly
public job pages later.

**Q: What is the App Router?**

A: It is Next.js routing based on the `src/app` directory. Folders define routes,
`page.tsx` defines route content, and `layout.tsx` defines shared layout.

**Q: When would you avoid Next.js?**

A: If the app is a small embedded widget, a simple admin page, or a pure SPA with
no need for framework-level routing and deployment features, Vite may be simpler.

### React 19

#### What It Does

React is the UI library used to build components, compose screens, render lists,
handle user actions, and keep UI state predictable.

In this project:

- Pages are React components.
- `StatCard` is a reusable UI component.
- Hooks such as `useRecommendedJobs` expose data to components.

#### Why We Picked It

React has a large ecosystem, strong hiring market familiarity, and excellent
composition for workflow UIs. Candidate and employer screens share repeated
patterns like cards, lists, buttons, and status indicators.

#### Why Not The Alternatives?

- Angular: more batteries included, but heavier for this MVP.
- Vue: very productive, but the project uses React and shadcn-style primitives.
- Svelte: elegant, but a smaller enterprise hiring and library ecosystem.

#### Mini Tutorial

Create a presentational component:

```tsx
type MatchBadgeProps = {
  score: number;
};

export function MatchBadge({ score }: MatchBadgeProps) {
  return <span>{score}% match</span>;
}
```

Render a list:

```tsx
{
  jobs.map((job) => <article key={job.id}>{job.title}</article>);
}
```

Use props for data going down. Use hooks for behavior and state.

#### Common Mistakes

- Storing server data in local component state when React Query should own it.
- Using array indexes as keys for dynamic lists.
- Creating components that know too much about API details.

#### Interview Questions And Answers

**Q: What is the benefit of component-based UI?**

A: It lets us build complex screens from smaller, reusable pieces. For example,
candidate and employer dashboards can share card, table, button, and status
patterns without duplicating markup.

**Q: What is the difference between props and state?**

A: Props are inputs passed from a parent component. State is data owned by a
component or hook that can change over time.

**Q: How do you keep React apps maintainable?**

A: Keep components focused, move server state to React Query, keep API calls in
service modules, and use TypeScript types for API data.

### TypeScript

#### What It Does

TypeScript adds static typing to JavaScript. It catches many data shape mistakes
before runtime and makes refactoring safer.

In this project:

- Candidate job types live in `frontend/candidate-web/src/types/job.ts`.
- Employer candidate types live in `frontend/employer-web/src/types/candidate.ts`.
- `tsc --noEmit` checks frontend type correctness.

#### Why We Picked It

AI job matching depends on structured data: candidates, jobs, scores, statuses,
and explanations. TypeScript helps the frontend stay aligned with backend API
contracts and reduces mistakes when fields change.

#### Why Not Plain JavaScript?

Plain JavaScript is faster to start, but type mistakes appear at runtime. For an
MVP with multiple services and two frontends, TypeScript is worth the small
setup cost because it documents data shapes directly in code.

#### Mini Tutorial

Define an API response type:

```ts
export type RecommendedJob = {
  id: string;
  title: string;
  company: string;
  location: string;
  matchScore: number;
  explanation: string;
};
```

Use it in a hook:

```ts
const jobs: RecommendedJob[] = [];
```

Run:

```bash
npm run typecheck:web
```

#### Common Mistakes

- Using `any` to silence real API mismatches.
- Letting frontend types drift from backend DTOs.
- Forgetting that TypeScript checks compile-time types, not runtime validation.

#### Interview Questions And Answers

**Q: Why use TypeScript in an MVP?**

A: It reduces refactor risk and documents API data shapes. That matters in this
project because AI and matching flows pass structured scores and explanations
across several layers.

**Q: Does TypeScript replace backend validation?**

A: No. TypeScript helps during development, but backend services still need
runtime validation for all external requests.

### Tailwind CSS

#### What It Does

Tailwind CSS provides utility classes for spacing, color, layout, typography,
borders, and responsive behavior.

In this project:

- Global styles live in `src/app/globals.css`.
- Theme extension lives in `tailwind.config.ts`.
- Components use utility classes directly in JSX.

#### Why We Picked It

Tailwind is fast for MVP UI development and keeps styling close to the component.
It avoids creating a large CSS architecture before the product language is known.

#### Why Not The Alternatives?

- CSS Modules: good isolation, but more files and slower iteration for dashboard
  surfaces.
- Bootstrap: quick, but the product can start looking generic quickly.
- Material UI: powerful, but a heavier design system and harder to customize
  deeply.
- Plain CSS: flexible, but consistency becomes harder as the app grows.

#### Mini Tutorial

Responsive card layout:

```tsx
<section className="grid gap-4 md:grid-cols-3">
  <StatCard label="Strong matches" value="12" />
</section>
```

Common utility patterns:

- `flex items-center gap-2` for inline layout.
- `grid gap-4 md:grid-cols-3` for responsive grids.
- `rounded-md border border-border bg-white` for simple panels.
- `text-sm text-slate-600` for secondary text.

#### Common Mistakes

- Copying very long class lists everywhere instead of extracting a component.
- Using arbitrary colors everywhere instead of theme tokens.
- Styling UI before deciding the workflow hierarchy.

#### Interview Questions And Answers

**Q: Why Tailwind instead of Material UI?**

A: Tailwind keeps the MVP lightweight and highly customizable. Material UI is
great for enterprise apps, but it can bring stronger visual opinions and more
abstraction than this early product needs.

**Q: How do you avoid messy Tailwind code?**

A: Use reusable components for repeated patterns, keep theme tokens in config,
and avoid one-off arbitrary values unless there is a real design reason.

### Shadcn-Style UI Primitives

#### What It Does

Shadcn-style UI is a component ownership pattern. Instead of importing a black-box
component library, we keep reusable UI primitives inside the app.

In this project:

- Reusable UI belongs under `src/components/ui`.
- `StatCard` is an example of a local primitive.
- Future primitives could include `Button`, `Dialog`, `Input`, `Table`, and
  `Badge`.

#### Why We Picked It

This approach gives us accessible, consistent UI while keeping the code editable.
For a recruiting product, we will need custom states like match score badges,
application status chips, resume parsing feedback, and recruiter decision
controls.

#### Why Not The Alternatives?

- Full component library: faster initially, but harder to deeply customize.
- Building everything from scratch: maximum control, but slower and easier to
  get accessibility wrong.
- Design-system package too early: creates overhead before the design language is
  stable.

#### Mini Tutorial

Create a shared button:

```tsx
type ButtonProps = React.ButtonHTMLAttributes<HTMLButtonElement>;

export function Button({ className, ...props }: ButtonProps) {
  return (
    <button
      className={`rounded-md px-4 py-2 text-sm font-medium ${className ?? ""}`}
      {...props}
    />
  );
}
```

Use primitives for visual consistency, not domain logic. Domain-specific
components should live closer to their feature.

#### Common Mistakes

- Putting business logic into UI primitives.
- Creating too many components before repetition is clear.
- Forgetting accessibility states like focus, disabled, and keyboard behavior.

#### Interview Questions And Answers

**Q: What is the benefit of owning UI primitives?**

A: We get consistency and accessibility patterns without being locked into a
third-party component API. We can adapt components as the product design matures.

**Q: When would you choose Material UI instead?**

A: If the team needs a complete enterprise UI suite immediately, with complex
tables, menus, date pickers, and theming out of the box, Material UI may be more
efficient.

### Axios

#### What It Does

Axios is the HTTP client used by frontend services to call backend APIs.

In this project:

- Candidate API client: `frontend/candidate-web/src/services/api.ts`.
- Employer API client: `frontend/employer-web/src/services/api.ts`.
- Each client centralizes `baseURL`, timeout, and future auth headers.

#### Why We Picked It

Axios provides a simple, familiar wrapper around HTTP calls with useful defaults:
base URLs, timeouts, request/response interceptors, and JSON handling.

#### Why Not The Alternatives?

- Native `fetch`: built in and capable, but timeouts and interceptors require
  more custom code.
- `ky`: nice fetch wrapper, but less familiar to many teams.
- Generated client only: useful later, but early API contracts are still moving.

#### Mini Tutorial

Configured client:

```ts
export const api = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_BASE_URL ?? "http://localhost:8080",
  timeout: 8000,
});
```

Future auth interceptor:

```ts
api.interceptors.request.use((config) => {
  const token = localStorage.getItem("accessToken");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});
```

#### Common Mistakes

- Creating ad hoc Axios clients in every component.
- Forgetting request timeouts.
- Logging full request bodies that may contain resumes or personal data.

#### Interview Questions And Answers

**Q: Why use Axios if browsers already have fetch?**

A: Axios gives a consistent client with interceptors, timeouts, and centralized
configuration. That is helpful once every request needs auth and error handling.

**Q: Where should API calls live?**

A: In service modules and hooks, not directly inside UI markup. Components should
consume data, not know transport details.

### TanStack React Query

#### What It Does

TanStack Query manages server state: fetching, caching, loading states, retries,
refetching, and invalidation.

In this project:

- Candidate recommendations are loaded with `useRecommendedJobs`.
- Employer ranked candidates are loaded with `useRankedCandidates`.
- Providers are mounted in `src/app/providers.tsx`.

#### Why We Picked It

Dashboards are full of server data. Without React Query, teams often rewrite the
same loading, error, cache, and refetch logic in every component. React Query
keeps that behavior consistent and easier to test.

#### Why Not The Alternatives?

- `useEffect` plus local state: fine for one call, messy across a product.
- Redux Toolkit: excellent for client state, but server state needs caching and
  invalidation semantics.
- SWR: simpler and good, but TanStack Query has stronger mutation and cache tools
  for dashboard workflows.

#### Mini Tutorial

Create a query hook:

```ts
export function useRecommendedJobs() {
  return useQuery({
    queryKey: ["recommended-jobs"],
    queryFn: async () => {
      const response = await api.get("/api/jobs/recommended");
      return response.data;
    },
  });
}
```

Invalidate after a mutation:

```ts
queryClient.invalidateQueries({ queryKey: ["recommended-jobs"] });
```

#### Common Mistakes

- Using unstable query keys like objects created inline without care.
- Forgetting to invalidate data after mutations.
- Treating React Query as a replacement for all local UI state.

#### Interview Questions And Answers

**Q: What problem does React Query solve?**

A: It manages server state. It fetches, caches, refreshes, deduplicates, and
invalidates API data so components do not hand-roll that behavior.

**Q: How is server state different from client state?**

A: Server state lives remotely, can become stale, is shared by other users, and
requires async fetch/update operations. Client state is local UI state like a
selected tab or open dialog.

## Backend

### Java 25

#### What It Does

Java is the backend language for all services. Java 25 is the target release in
the project POM and the architecture document.

In this project:

- `backend/pom.xml` sets `<java.version>25</java.version>`.
- Spring Boot 4.0.6 supports Java 17 through Java 26, so Java 25 is compatible.
- Backend modules compile as Java services.
- DTOs can use modern Java patterns such as records when appropriate.

#### Why We Picked It

Java is a strong fit for a hiring platform because it is mature, strongly typed,
well understood in enterprise teams, and has excellent support for APIs,
security, observability, and relational databases. Java 25 also gives the stack a
current long-term platform target.

#### Why Not The Alternatives?

- Node.js: fast for API development, but Java gives stronger compile-time domain
  modeling and mature Spring enterprise patterns.
- Python: excellent for AI experiments, but less ideal as the main transactional
  backend for this architecture.
- Go: simple and efficient, but Spring Boot provides more built-in product
  infrastructure for security, validation, data access, and configuration.
- Kotlin: great on the JVM, but Java has the widest team familiarity.

#### Mini Tutorial

Check the configured version:

```bash
cd backend
mvn -v
```

Compile all services:

```bash
mvn clean verify
```

Use records for simple immutable DTOs when they fit:

```java
public record MatchScoreResponse(String candidateId, String jobId, int score) {}
```

#### Common Mistakes

- Using Java 25 language features before the build and deployment JDKs are
  aligned.
- Putting domain logic in controllers instead of services.
- Ignoring nullability and validation at API boundaries.

#### Interview Questions And Answers

**Q: Why Java for an AI job portal?**

A: The AI part is only one part of the system. The core product also needs secure
accounts, profiles, jobs, applications, auditability, and reliable transactions.
Java and Spring are strong for those backend requirements.

**Q: Why Java 25 instead of Java 21?**

A: The architecture requests Java 25, and it is a current long-term Java line.
For a real company, I would confirm the chosen JDK vendor support window and
runtime availability before production rollout.

### Spring Boot 4.0.6

#### What It Does

Spring Boot is the framework used to build backend services. It provides
auto-configuration, dependency injection, embedded web servers, application
configuration, health endpoints, validation, and integration with data and
security libraries.

In this project:

- Each service has its own module under `backend`.
- Each service has a `src/main/resources/application.yml`.
- The root POM imports Spring Boot dependency management.
- Web modules use `spring-boot-starter-webmvc`, the Spring MVC starter used by
  this Spring Boot 4 scaffold.

#### Why We Picked It

Spring Boot 4.0.6 is the current stable 4.0 line used by this scaffold. It is
one of the fastest ways to build production-grade Java services.
It gives the project a conventional structure, strong ecosystem, and a clear path
to observability, security, database access, and deployment.

#### Why Not The Alternatives?

- Express/NestJS: productive, but this backend is Java-centered.
- Quarkus/Micronaut: lighter and strong for cloud-native Java, but Spring Boot
  has broader team familiarity and ecosystem depth.
- Raw servlets: too much plumbing for an MVP.

#### Mini Tutorial

Controller example:

```java
@RestController
@RequestMapping("/api/candidates")
class CandidateController {
  @GetMapping("/{id}")
  CandidateProfileResponse getCandidate(@PathVariable UUID id) {
    return service.getCandidate(id);
  }
}
```

Run one service:

```bash
cd backend
mvn -pl candidate-service spring-boot:run
```

#### Common Mistakes

- Creating too many microservices before the domain is understood.
- Letting services share database tables without clear ownership.
- Hiding business rules inside framework annotations.

#### Interview Questions And Answers

**Q: Why use Spring Boot for microservices?**

A: It gives each service a repeatable structure with web, configuration,
security, validation, and data integration. That reduces custom infrastructure
code and helps teams operate services consistently.

**Q: What is dependency injection?**

A: It is a pattern where Spring creates and provides dependencies to classes,
instead of classes constructing everything themselves. That improves testability
and separation of concerns.

**Q: Does Spring Boot mean the system must be microservices?**

A: No. Spring Boot can build monoliths or services. This project uses service
modules because the architecture separates candidate, job, matching, auth, and
notification domains.

### Maven Multi-Module Build

#### What It Does

Maven manages backend dependencies, builds, and module relationships.

In this project:

- `backend/pom.xml` is the aggregator.
- Service modules inherit shared version management.
- `common-lib` holds shared DTOs and error models.

#### Why We Picked It

Maven is predictable, widely used in Java organizations, and works well with
Spring Boot. A multi-module layout keeps services version-aligned while still
allowing service boundaries.

#### Why Not The Alternatives?

- Gradle: more flexible, but more build logic choices and less uniform for some
  teams.
- Separate repos immediately: more operational overhead for an MVP.
- One giant module: simpler at first, but service boundaries become unclear.

#### Mini Tutorial

Build everything:

```bash
cd backend
mvn clean verify
```

Build one service and required dependencies:

```bash
mvn -pl candidate-service -am verify
```

#### Interview Questions And Answers

**Q: Why use a multi-module repo?**

A: It lets the team share dependency management and common DTOs while keeping
domain services separated. It is a practical middle ground before splitting into
many repositories.

**Q: What does `-pl` do in Maven?**

A: It selects a project/module to build. `-am` also builds modules that selected
module depends on.

### Spring Security And JWT

#### What It Does

Spring Security handles authentication and authorization. JWTs let the backend
verify signed user identity and roles without storing server-side sessions for
every request.

In this project:

- `auth-service` is responsible for signup, login, and token issuing.
- Role concepts include `CANDIDATE`, `EMPLOYER_ADMIN`, `RECRUITER`, and `ADMIN`.
- Current Auth Service token issuing is an MVP placeholder.
- API Gateway and Auth Service include minimal SecurityFilterChain configs so
  local demo endpoints work.
- Full gateway/service JWT validation is still a production-readiness task.

#### Why We Picked It

The platform handles resumes, employer data, applications, and AI-generated
profile summaries. Security cannot be custom afterthought code. Spring Security
is the standard security layer for Spring applications.

#### Why Not The Alternatives?

- Custom auth: high risk and easy to get wrong.
- Cookie sessions only: workable for one web app, but JWTs fit service-to-service
  and API boundary validation better.
- Third-party auth only: useful later, but the MVP still needs internal role and
  account modeling.

#### Mini Tutorial

Typical protected endpoint idea:

```java
@PreAuthorize("hasRole('CANDIDATE')")
@GetMapping("/me")
CandidateProfileResponse getMyProfile() {
  return service.getCurrentProfile();
}
```

JWT flow:

1. User logs in through Auth Service.
2. Auth Service returns signed access token.
3. Frontend sends `Authorization: Bearer <token>`.
4. Gateway or service validates token and role.

#### Common Mistakes

- Trusting user IDs from request bodies instead of token claims.
- Logging tokens or resume data.
- Confusing authentication with authorization.

#### Interview Questions And Answers

**Q: What is the difference between authentication and authorization?**

A: Authentication proves who the user is. Authorization decides what that user is
allowed to do.

**Q: Why JWT?**

A: JWTs work well for APIs because each request can carry signed identity and
role claims. Services can validate the token without a central session lookup on
every request.

**Q: What should not go in a JWT?**

A: Sensitive personal data, resumes, long profile summaries, or anything that
should change immediately without issuing a new token.

### Spring Data JPA

#### What It Does

Spring Data JPA provides repository interfaces and object-relational mapping for
PostgreSQL-backed entities.

In this project:

- Entities should live under service-specific `entity` packages.
- Repositories should live under `repository`.
- Business rules should live under `service`.

#### Why We Picked It

The platform has many relational workflows: users, candidates, companies, jobs,
applications, match scores, and statuses. JPA speeds up standard CRUD and query
work without hand-writing every SQL statement.

#### Why Not The Alternatives?

- Raw JDBC: maximum control, but more boilerplate.
- jOOQ: excellent SQL-first approach, but more setup for an MVP.
- MyBatis: useful for SQL mapping, but less integrated with Spring domain
  entities.
- MongoDB repositories: not ideal for transactional hiring workflows.

#### Mini Tutorial

Repository example:

```java
interface CandidateProfileRepository
    extends JpaRepository<CandidateProfileEntity, UUID> {
  Optional<CandidateProfileEntity> findByUserId(UUID userId);
}
```

Service transaction:

```java
@Transactional
public CandidateProfile updateProfile(UpdateProfileRequest request) {
  // Load entity, apply business rules, save changes.
}
```

#### Common Mistakes

- Returning entities directly from controllers.
- Triggering N+1 queries with careless lazy loading.
- Using `ddl-auto: update` in production instead of migrations.

#### Interview Questions And Answers

**Q: Why use JPA instead of SQL everywhere?**

A: JPA reduces boilerplate for common entity persistence and repository patterns.
For complex matching or reporting queries, I would still use explicit SQL where
it is clearer or faster.

**Q: What is an N+1 query problem?**

A: It happens when loading one list causes an extra query per row for related
data. It can be fixed with fetch joins, projections, batching, or query design.

### OpenAPI / Swagger

#### What It Does

OpenAPI describes REST endpoints, request bodies, response bodies, status codes,
and error formats. Swagger UI can display the API interactively.

In this project:

- Contracts are summarized in `docs/api-contracts.md`.
- Springdoc OpenAPI is added to each Spring Boot service so `/v3/api-docs` and
  `/swagger-ui.html` are available during local development.

#### Why We Picked It

The system has two frontends and many backend services. API contracts prevent
guesswork and make it easier to generate clients, write contract tests, and
onboard developers.

#### Why Not The Alternatives?

- Postman collection only: useful for testing, weaker as a source of truth.
- Wiki-only docs: easy to drift from code.
- GraphQL schema: useful for flexible clients, but REST is simpler for this MVP's
  service boundaries.

#### Mini Tutorial

Later, add OpenAPI annotations:

```java
@Operation(summary = "Get recommended jobs for the current candidate")
@GetMapping("/recommended")
List<RecommendedJobResponse> getRecommendedJobs() {
  return service.getRecommendedJobs();
}
```

#### Interview Questions And Answers

**Q: Why is OpenAPI valuable in a microservice architecture?**

A: It gives teams a machine-readable contract. Frontend developers, backend
developers, QA, and API clients can all use the same endpoint definitions.

**Q: Should documentation be generated or handwritten?**

A: Both can help. During early MVP design, markdown is easier to change. Once
endpoints stabilize, generated OpenAPI reduces drift.

## Data And AI

### PostgreSQL

#### What It Does

PostgreSQL is the primary relational database. It stores durable product data:
users, profiles, resumes, companies, jobs, applications, match scores, and audit
metadata.

In this project:

- Local Postgres is defined in `docker-compose.yml`.
- The MVP schema starts in `scripts/seed-data.sql`.
- Candidate Service currently has JPA repositories wired to Postgres.
- The other services still use placeholder flows and need repository/migration
  work before they persist their domain data.

#### Why We Picked It

Hiring workflows are transactional. Applying to a job, changing application
status, updating a profile, and saving match decisions all need consistency.
PostgreSQL is reliable, mature, supports relational modeling, supports JSON for
evolving AI parse output, and can support vector search through pgvector.

#### Why Not The Alternatives?

- MySQL: solid relational database, but PostgreSQL has stronger extension support
  and pgvector.
- MongoDB: flexible documents, but weaker fit for transactional relationships and
  reporting across candidates, jobs, and applications.
- DynamoDB: scalable, but access patterns must be designed carefully up front and
  local development is less straightforward.
- Separate vector database immediately: unnecessary operational complexity for
  MVP scale.

#### Mini Tutorial

Start Postgres:

```bash
docker compose up -d postgres
```

Connect:

```bash
psql postgresql://aijobs:aijobs@localhost:5432/aijobs
```

Example table idea:

```sql
create table candidate_profiles (
  id uuid primary key,
  user_id uuid not null,
  headline text,
  skills jsonb not null default '[]'::jsonb,
  created_at timestamptz not null default now()
);
```

#### Common Mistakes

- Storing everything as JSON and losing relational integrity.
- Forgetting indexes on foreign keys and common filters.
- Running schema changes manually instead of using migrations later.

#### Interview Questions And Answers

**Q: Why PostgreSQL for this product?**

A: It handles transactional hiring data well and also supports JSON and vector
extensions. That lets the MVP keep product data, AI parse output, and embeddings
close together.

**Q: When would you move beyond PostgreSQL?**

A: If vector volume, search latency, or analytics scale outgrow Postgres, I would
consider a specialized vector database, search engine, or warehouse. I would wait
for measured pressure first.

### pgvector

#### What It Does

pgvector is a PostgreSQL extension for storing and querying vector embeddings.
Embeddings represent resumes and jobs as numeric vectors so similar items can be
found by distance.

In this project:

- `scripts/seed-data.sql` enables the vector extension.
- Matching design expects candidate and job embeddings.
- `CandidateEmbeddingService` is the placeholder for real embedding storage.

#### Why We Picked It

pgvector keeps vector search inside PostgreSQL, which is ideal for MVP. We avoid
paying for and operating a separate vector database before we know matching
volume and latency requirements.

#### Why Not The Alternatives?

- Pinecone/Weaviate/Milvus: strong vector systems, but more infrastructure and
  vendor decisions.
- Elasticsearch/OpenSearch kNN: useful if full-text search is already central,
  but heavier for this MVP.
- Manual keyword matching only: explainable, but misses semantic similarity
  between resumes and jobs.

#### Mini Tutorial

Enable extension:

```sql
create extension if not exists vector;
```

Example embedding table:

```sql
create table embeddings (
  id uuid primary key,
  owner_type text not null,
  owner_id uuid not null,
  embedding vector(1536) not null
);
```

Similarity query idea:

```sql
select owner_id, embedding <=> :query_embedding as distance
from embeddings
where owner_type = 'JOB'
order by embedding <=> :query_embedding
limit 20;
```

#### Common Mistakes

- Mixing embeddings from different models in one comparable column.
- Forgetting to store model name and embedding dimensions.
- Adding approximate indexes before measuring real query volume.

#### Interview Questions And Answers

**Q: Why pgvector instead of Pinecone?**

A: For MVP, pgvector is simpler because it keeps embeddings with product data in
Postgres. Pinecone can be better at large vector scale, but I would introduce it
after measuring a real need.

**Q: What is cosine similarity?**

A: It measures the angle between vectors, which helps compare semantic similarity
independent of magnitude. It is common for text embeddings.

### Redis

#### What It Does

Redis is an in-memory data store used for short-lived data such as cache entries,
rate limit counters, locks, and eventually lightweight queues.

In this project:

- Local Redis is defined in `docker-compose.yml`.
- It is reserved for temporary operational data, not durable hiring records.

#### Why We Picked It

Job matching and AI calls can be expensive. Redis can cache repeated match
results, store rate limit counters, and coordinate background work without
putting extra pressure on PostgreSQL.

#### Why Not The Alternatives?

- PostgreSQL-only cache: possible, but it increases load on the durable database.
- Memcached: simpler cache, but Redis supports more data structures and locks.
- Kafka/RabbitMQ: better for durable event streaming or queues, but heavier than
  needed at MVP stage.

#### Mini Tutorial

Start Redis:

```bash
docker compose up -d redis
```

Example use cases:

- `rate-limit:login:<ip>` with a short TTL.
- `match-score:<candidateId>:<jobId>` cache for recent scoring.
- `lock:resume-parse:<resumeId>` to avoid duplicate parsing.

#### Common Mistakes

- Treating Redis as the source of truth.
- Forgetting TTLs on cache keys.
- Caching private data without considering access scope.

#### Interview Questions And Answers

**Q: What should Redis store here?**

A: Temporary, reproducible data: rate limits, cache entries, locks, and short
workflow state. Durable candidate, job, and application data belongs in
PostgreSQL.

**Q: How do you avoid stale cache bugs?**

A: Use clear key design, short TTLs, and invalidate or refresh keys after writes.

### MinIO

#### What It Does

MinIO is local S3-compatible object storage. It stores uploaded resume files in
development while preserving the same object-storage pattern used in cloud.

In this project:

- MinIO runs from `docker-compose.yml`.
- Resume storage is abstracted by `ObjectStorageClient`.
- The local console runs on `http://localhost:9001`.

#### Why We Picked It

Resumes are files, not relational rows. Object storage is the right place for
PDFs and DOCX files. MinIO lets developers test the storage flow locally without
AWS or GCP credentials.

#### Why Not The Alternatives?

- Local filesystem: easy, but does not match cloud deployment behavior.
- Storing files in PostgreSQL: bloats the database and complicates backups.
- Cloud bucket only: requires credentials and network access for local
  development.

#### Mini Tutorial

Start MinIO:

```bash
docker compose up -d minio
```

Open:

```text
http://localhost:9001
```

Object key pattern:

```text
resumes/{candidateId}/{resumeId}/original.pdf
```

Store metadata in PostgreSQL, but store file bytes in object storage.

#### Common Mistakes

- Making uploaded files public by default.
- Using original filenames as object keys without sanitization.
- Losing the link between object storage key and database metadata.

#### Interview Questions And Answers

**Q: Why not store resumes directly in the database?**

A: Resumes are binary objects. Object storage is cheaper, easier to scale, and
better suited for file lifecycle policies. The database should store metadata and
references.

**Q: Why use MinIO locally?**

A: It gives the same S3-style development pattern without requiring a real cloud
bucket.

### Vertex AI Gemini And OpenAI-Compatible AI Providers

#### What They Do

AI providers parse resumes, parse job descriptions, generate match explanations,
suggest resume improvements, and produce embeddings.

In this project:

- Prompts live in `ai/prompts`.
- Backend AI calls should go behind interfaces such as `AiClient`.
- Provider keys belong in `.env`, never in git.

#### Why We Picked Them

The architecture mentions Vertex AI Gemini and OpenAI. Supporting more than one
provider reduces vendor lock-in and lets the team compare quality, latency, and
cost for each AI task.

#### Why Not The Alternatives?

- One provider hardcoded everywhere: fastest at first, but painful to switch.
- Local-only LLMs: useful for privacy experiments, but more operational work and
  often weaker quality per dollar for an MVP.
- No AI provider abstraction: creates tight coupling between product logic and
  vendor SDK details.

#### Mini Tutorial

Prompt files should request structured output:

```text
Return strict JSON with:
- skills: string[]
- yearsOfExperience: number
- suggestedTitles: string[]
- summary: string
```

Backend shape:

```java
interface AiClient {
  ResumeParseResult parseResume(String resumeText);
  MatchExplanation explainMatch(MatchContext context);
}
```

Provider selection belongs in configuration:

```text
AI_PROVIDER=openai
OPENAI_API_KEY=...
VERTEX_PROJECT_ID=...
```

#### Common Mistakes

- Sending unnecessary personal data to prompts.
- Trusting AI JSON without validation.
- Changing prompts without regression examples.
- Hiding model version and prompt version from logs/metadata.

#### Interview Questions And Answers

**Q: How do you reduce AI vendor lock-in?**

A: Keep provider SDK code behind interfaces, version prompts in the repo, and
store model/prompt metadata with outputs. The application should depend on AI
capabilities, not vendor classes.

**Q: How do you make AI output safer?**

A: Minimize personal data, request structured JSON, validate responses, set
timeouts, handle refusals/errors, and keep humans in control of hiring decisions.

**Q: Should AI make hiring decisions automatically?**

A: No. AI should assist with ranking, explanations, and suggestions. Final
decisions should stay with authorized recruiters or hiring teams.

### Prompt Files And AI Evaluation

#### What They Do

Prompt files define the instructions sent to AI models. Evaluation files define
sample inputs and expected behavior so prompt changes can be tested.

In this project:

- Resume prompt: `ai/prompts/resume-parser.prompt.md`.
- Job prompt: `ai/prompts/job-parser.prompt.md`.
- Match explanation prompt: `ai/prompts/match-explanation.prompt.md`.
- Evaluation notes: `ai/evaluation`.

#### Why We Picked This Pattern

Prompts are part of the product. They affect candidate summaries, match quality,
and recruiter trust. Versioning prompts in git makes AI behavior reviewable.

#### Why Not Keep Prompts Only In Code?

Prompts hidden in code are harder for product, QA, and AI reviewers to inspect.
Separate prompt files make prompt review and prompt regression easier.

#### Mini Tutorial

When changing a prompt:

1. Add or update sample resume/job pairs.
2. Run the parser or scorer against samples.
3. Compare JSON shape and explanation quality.
4. Record expected behavior in `ai/evaluation`.
5. Only then wire the prompt into a provider client.

#### Interview Questions And Answers

**Q: How would you test AI matching quality?**

A: I would create a fixed evaluation set of resumes and jobs, define expected
match behavior, run prompts/models against it, and track regressions in score,
explanation quality, and structured output validity.

**Q: Why version prompts?**

A: Prompt changes are behavior changes. Versioning makes them reviewable and
traceable when match quality changes.

## Infrastructure

### Docker Compose

#### What It Does

Docker Compose defines and runs local multi-container infrastructure from a YAML
file.

In this project:

- `docker-compose.yml` starts PostgreSQL, Redis, and MinIO.
- `scripts/start-local.sh` wraps `docker compose up`.
- `scripts/stop-local.sh` wraps `docker compose down`.

#### Why We Picked It

Developers should be able to start the MVP dependencies with one command. Docker
Compose avoids manual installation differences across machines.

#### Why Not The Alternatives?

- Manual installs: fragile and inconsistent across developers.
- Full Kubernetes locally: too much overhead for day-to-day MVP work.
- Cloud-only dependencies: slower feedback and requires credentials.

#### Mini Tutorial

Start services:

```bash
./scripts/start-local.sh
```

Check status:

```bash
docker compose ps
```

Read logs:

```bash
docker compose logs -f postgres
```

Stop services:

```bash
./scripts/stop-local.sh
```

#### Common Mistakes

- Committing real secrets into Compose files.
- Forgetting persistent volumes when data should survive restarts.
- Assuming local Compose is identical to production.

#### Interview Questions And Answers

**Q: Why use Docker Compose?**

A: It makes local infrastructure reproducible. Any developer can start Postgres,
Redis, and MinIO with the same configuration.

**Q: Is Docker Compose production orchestration?**

A: Usually no for this kind of system. It is great for local development and
simple environments, but Cloud Run or Kubernetes is better for managed
production deployment.

### GitHub Actions And Jenkins

#### What They Do

GitHub Actions and Jenkins run automated CI pipelines. The pipeline checks that
the frontend typechecks and builds, the backend passes Maven verification, and
Docker Compose stays valid.

In this project:

- GitHub Actions workflow: `.github/workflows/ci.yml`.
- Jenkins pipeline: `Jenkinsfile`.
- Shared commands: `npm run ci:web`, `npm run ci:backend`, and
  `npm run compose:config`.

#### Why We Picked Them

GitHub Actions is the easiest hosted free path when the project is on GitHub,
especially for public repositories. Jenkins is included as a free self-hosted
alternative for teams that want full control over agents, private networking, or
CI runtime costs.

#### Why Not GitLab CI Here?

GitLab CI is a strong option when the repository is hosted in GitLab. This
project does not add `.gitlab-ci.yml` because the earlier instruction was to
avoid changing GitLab CI configuration. If the project moves to GitLab, the same
stages can be translated into GitLab jobs.

#### Mini Tutorial

Run the same checks locally:

```bash
npm run format:check
npm run ci:web
npm run ci:backend
npm run compose:config
```

GitHub Actions runs automatically on push, pull request, and manual dispatch.
Jenkins runs the `Jenkinsfile` when a Pipeline or Multibranch Pipeline job points
at this repository.

#### Interview Questions And Answers

**Q: Why add CI before production deployment exists?**

A: CI protects the MVP from basic regressions. It confirms the app can typecheck,
build, verify backend modules, and load Docker Compose before code is merged.

**Q: Why include both GitHub Actions and Jenkins?**

A: GitHub Actions is a low-friction hosted path. Jenkins is a free self-hosted
path. Keeping both gives the project options without changing product code.

### Prometheus

#### What It Does

Prometheus collects and stores time-series metrics. It scrapes Spring Boot
Actuator's `/actuator/prometheus` endpoint for each backend service.

In this project:

- Config file: `infra/observability/prometheus/prometheus.yml`.
- Local URL: `http://localhost:9090`.
- Backend services expose Prometheus metrics through Actuator and Micrometer.

#### Why We Picked It

Prometheus is open source, widely used, and integrates cleanly with Spring Boot
through Micrometer. It is enough for MVP service health, JVM memory, HTTP request
rate, latency, and error-rate visibility.

#### Mini Tutorial

Start Prometheus:

```bash
docker compose up -d prometheus
```

Open targets:

```text
http://localhost:9090/targets
```

Useful starter queries:

```promql
up
sum by (job) (rate(http_server_requests_seconds_count[5m]))
sum by (job) (jvm_memory_used_bytes{area="heap"})
```

#### Interview Questions And Answers

**Q: What does the `up` metric mean?**

A: `up` is `1` when Prometheus can scrape a target and `0` when the target is
down or unreachable.

**Q: Why use Prometheus instead of only logs?**

A: Logs explain events. Metrics show trends, rates, saturation, and service
health over time. Production systems need both.

### Grafana

#### What It Does

Grafana visualizes metrics from Prometheus. It provides dashboards for service
availability, JVM memory, and request rate.

In this project:

- Local URL: `http://localhost:3002`.
- Datasource provisioning:
  `infra/observability/grafana/provisioning/datasources/prometheus.yml`.
- Dashboard provisioning:
  `infra/observability/grafana/provisioning/dashboards/dashboards.yml`.
- Dashboard JSON:
  `infra/observability/grafana/dashboards/ai-job-platform-overview.json`.

#### Why We Picked It

Grafana OSS is free, familiar, and works naturally with Prometheus. Provisioning
keeps dashboards in git so every developer starts with the same observability
view.

#### Mini Tutorial

Start Grafana:

```bash
docker compose up -d grafana
```

Open:

```text
http://localhost:3002
```

Default local login:

- User: `admin`
- Password: `admin`

#### Interview Questions And Answers

**Q: Why provision Grafana dashboards from files?**

A: It makes dashboards repeatable and reviewable. A new developer gets the same
Prometheus datasource and dashboard when Docker Compose starts.

**Q: What dashboard would you add next?**

A: I would add latency percentiles, error rate, AI provider call count, AI
provider latency, queue depth, database connection pool usage, and JVM GC panels.

### Cloud Run

#### What It Does

Cloud Run runs containerized HTTP services on managed infrastructure with
autoscaling.

In this project:

- Cloud Run is recommended as the early MVP deployment target.
- Each Spring Boot service can be packaged as a container and deployed
  independently.

#### Why We Picked It For Early MVP

Cloud Run is simpler than operating Kubernetes. It fits stateless HTTP services,
can scale down when idle, and reduces infrastructure work while the product is
still changing.

#### Why Not The Alternatives?

- GKE/Kubernetes immediately: more control, but more platform operations.
- VMs: familiar, but patching, scaling, and deployment automation are more manual.
- Serverless functions: good for small functions, but less natural for full
  Spring Boot services.

#### Mini Tutorial

High-level deployment flow:

```bash
gcloud builds submit --tag gcr.io/PROJECT_ID/candidate-service
gcloud run deploy candidate-service \
  --image gcr.io/PROJECT_ID/candidate-service \
  --region us-central1
```

Production checklist:

- Use Secret Manager for secrets.
- Configure service accounts with least privilege.
- Set CPU/memory intentionally.
- Add health checks and structured logs.

#### Interview Questions And Answers

**Q: Why Cloud Run before Kubernetes?**

A: It lets the team deploy containers without managing a cluster. For MVP HTTP
services, that is usually faster and cheaper operationally.

**Q: When would you move to GKE?**

A: When the system needs advanced networking, long-running workloads, custom
sidecars, complex service mesh needs, or platform control that Cloud Run does not
provide.

### Kubernetes / GKE

#### What It Does

Kubernetes orchestrates containers with deployments, services, config, secrets,
autoscaling, and rollout controls. GKE is Google Cloud's managed Kubernetes
service.

In this project:

- Starter manifests live in `infra/k8s`.
- They show how service deployments and service ports could be shaped later.

#### Why It Is Included

The architecture document names GKE as a deployment option. The manifests give a
future path for teams that need Kubernetes-level control.

#### Why Not Use It Immediately?

Kubernetes is powerful but operationally heavier. For an MVP, the team should
first validate product workflows and service boundaries. Cloud Run is usually
enough until scale or platform requirements justify GKE.

#### Mini Tutorial

Apply manifests after replacing placeholder images:

```bash
kubectl apply -f infra/k8s
kubectl get pods
kubectl get svc
```

Deployment concept:

- `Deployment` controls pod replicas and rollout.
- `Service` gives stable network access to pods.
- `ConfigMap` stores non-secret config.
- `Secret` stores sensitive values.

#### Common Mistakes

- Putting secrets directly in YAML.
- Using `latest` image tags in production.
- Deploying many services without resource limits.
- Treating Kubernetes as required before the product is proven.

#### Interview Questions And Answers

**Q: What is the difference between a pod and a deployment?**

A: A pod runs one or more containers. A deployment manages replicas, updates, and
rollbacks for pods.

**Q: Why include Kubernetes manifests if Cloud Run is recommended first?**

A: It documents a future deployment path and keeps service boundaries clear, but
we do not need to operate Kubernetes until the product requires it.

### Terraform

#### What It Does

Terraform defines cloud infrastructure as code. It can create and manage
databases, buckets, service accounts, networking, Cloud Run services, and GKE
clusters.

In this project:

- Terraform folders are placeholders for future dev/prod infrastructure.
- Real resources should be added after manual cloud architecture is understood.

#### Why We Picked It

Infrastructure should be repeatable. Terraform helps avoid one-off console setup
and makes infrastructure changes reviewable.

#### Why Not The Alternatives?

- Manual console setup: fast once, but hard to reproduce.
- Shell scripts only: useful for commands, weaker for tracking desired state.
- Pulumi: powerful and code-native, but Terraform is more widely recognized in
  infrastructure teams.
- Cloud-specific templates: good in one cloud, but Terraform keeps a broader
  skill path.

#### Mini Tutorial

Typical workflow:

```bash
cd infra/terraform/dev
terraform init
terraform plan
terraform apply
```

Recommended module order:

1. Networking and IAM.
2. Database and object storage.
3. Secrets.
4. Service deployment targets.
5. Monitoring and alerts.

#### Common Mistakes

- Adding Terraform before the infrastructure shape is known.
- Storing state locally for team environments.
- Committing secrets or generated credentials.

#### Interview Questions And Answers

**Q: Why use Terraform?**

A: It makes infrastructure reproducible and reviewable. Instead of manually
creating cloud resources, the team can plan and apply changes from versioned
code.

**Q: What is Terraform state?**

A: State is Terraform's record of managed infrastructure. It must be protected,
shared safely for teams, and kept out of normal source files when it contains
sensitive values.

## Cross-Cutting Design Decisions

### Why Separate Candidate And Employer Frontends?

Candidates and employers have different workflows, vocabulary, metrics, and
permissions. Separate apps let each experience evolve without adding complex
role-based branching everywhere. Shared UI can still be extracted later if
duplication becomes real.

### Do We Use Backend Microservices?

Yes. The backend is designed as a microservice-style Spring Boot architecture.
Each major business domain has its own service module, Spring Boot application,
configuration, Dockerfile, and default port. They are kept in one monorepo for
MVP speed, but the boundaries are shaped so they can be deployed independently
later.

### Microservices Vs Docker Vs Kubernetes

These terms are often mixed together in interviews, but they are different
layers:

| Layer          | What It Is                                                                                        | Example In This Project                                                                                    |
| -------------- | ------------------------------------------------------------------------------------------------- | ---------------------------------------------------------------------------------------------------------- |
| Microservices  | Architecture style: split backend capabilities into small services with clear ownership and APIs. | `auth-service`, `candidate-service`, `job-service`, `matching-service`, and the other Spring Boot modules. |
| Docker         | Container packaging/runtime: build an image and run it consistently across machines.              | Each backend service has a `Dockerfile`; local dependencies run in containers.                             |
| Docker Compose | Local multi-container runner: define services, networks, and volumes in one YAML file.            | `docker-compose.yml` starts Postgres, Redis, MinIO, Prometheus, and Grafana.                               |
| Kubernetes     | Container orchestration platform: run and manage many containers across a cluster.                | Starter manifests live in `infra/k8s`, but Kubernetes is deferred for MVP runtime.                         |

#### Why Use Microservices But Not Kubernetes First?

Microservices are a code and ownership decision. Kubernetes is a runtime
operations decision. This MVP benefits from domain boundaries now, but it does
not need cluster operations on day one. Running Kubernetes too early would add
work around cluster setup, ingress, secrets, resource limits, service discovery,
deployment strategy, scaling rules, monitoring, and upgrades before the product
workflows are proven.

The project still keeps a Kubernetes path open:

- service Dockerfiles make container images possible;
- `infra/k8s` contains starter deployment manifests;
- Prometheus/Grafana prepare the project for operational visibility;
- Cloud Run can host early stateless services with less platform overhead;
- GKE/Kubernetes can be adopted when advanced orchestration is justified.

#### Interview Questions And Answers

**Q: Does using microservices mean we must use Kubernetes?**

A: No. Microservices describe how the backend is decomposed. Kubernetes describes
how containers are orchestrated. You can run microservices with Docker Compose,
Cloud Run, VMs, or Kubernetes.

**Q: Do we use Docker?**

A: Yes. Dockerfiles exist for backend services, and Docker Compose runs local
dependencies and observability services. Docker packages services into
repeatable containers.

**Q: Why not Kubernetes immediately?**

A: Kubernetes is powerful but operationally expensive for an MVP. The project
uses Docker Compose locally and recommends Cloud Run first for simple cloud
deployment. Kubernetes/GKE becomes a better fit when scaling, networking,
service mesh, or cluster-level control becomes necessary.

### Why Split Backend By Domain?

The domains have different responsibilities:

- Auth owns identity and tokens.
- Candidate Service owns profiles and resumes.
- Employer Service owns companies and recruiter access.
- Job Service owns job postings.
- Application Service owns application workflow.
- Matching Service owns scoring and explanations.
- Notification Service owns messages.

This keeps business logic clear. It also prevents the matching service from
owning candidate profile writes or the auth service from knowing resume details.

### Why Keep AI Behind Interfaces?

AI providers, models, prices, rate limits, and quality can change. An interface
lets the product call capabilities like `parseResume` or `explainMatch` without
knowing whether the implementation uses Vertex AI, OpenAI, or another provider.

### Why Start With Markdown API Contracts?

Early MVP APIs change quickly. Markdown is easy to update while product flows are
still being shaped. Once stable, OpenAPI should become the executable contract.

## Interview Cheat Sheet

Use these short answers when asked to explain the project:

**Q: What is the most important architecture choice?**

A: Keeping AI logic separate from core hiring workflow logic. AI can improve
matching and explanations, but transactional data and user permissions remain in
normal backend services.

**Q: How do you handle privacy?**

A: Minimize personal data sent to AI, avoid logging resumes or prompts with PII,
use role-based access, keep file storage private, and add retention/deletion
policies before production.

**Q: How would you scale matching?**

A: Start with Postgres and pgvector, cache repeated results in Redis, add
background embedding generation, index vectors when volume grows, and move to a
specialized vector store only when measured latency or volume requires it.

**Q: How would you make the system production-ready?**

A: Add migrations, real auth/JWT validation, OpenAPI docs, CI checks, tests,
observability, cloud secrets, deployment automation, data retention policies, and
AI evaluation gates.

**Q: What tradeoff did this MVP make?**

A: It chooses pragmatic simplicity first. pgvector instead of a separate vector
database, Cloud Run before Kubernetes, markdown contracts before generated
OpenAPI, and local UI primitives before a full design system.

## Official Reference Links

These are useful when preparing interview answers or updating implementation
details:

- Next.js App Router: https://nextjs.org/docs/app
- React: https://react.dev/learn
- TypeScript: https://www.typescriptlang.org/docs/
- Tailwind CSS: https://tailwindcss.com/docs
- Shadcn UI: https://ui.shadcn.com/docs
- Axios: https://axios-http.com/docs/intro
- TanStack Query: https://tanstack.com/query/latest/docs/framework/react/overview
- Java 25 release information: https://www.oracle.com/news/announcement/oracle-releases-java-25-2025-09-16/
- Spring Boot 4 reference: https://docs.spring.io/spring-boot/4.0/reference/index.html
- Spring Security: https://docs.spring.io/spring-security/reference/
- Spring Data JPA: https://docs.spring.io/spring-data/jpa/reference/
- PostgreSQL: https://www.postgresql.org/docs/current/
- pgvector: https://github.com/pgvector/pgvector
- Redis: https://redis.io/docs/latest/
- MinIO: https://min.io/docs/minio/linux/index.html
- Vertex AI Gemini: https://cloud.google.com/vertex-ai/generative-ai/docs
- OpenAI API: https://platform.openai.com/docs/overview
- Docker concepts: https://docs.docker.com/get-started/docker-concepts/the-basics/what-is-an-image/
- Docker Compose: https://docs.docker.com/compose/
- Kubernetes: https://kubernetes.io/docs/concepts/overview/
- Cloud Run: https://cloud.google.com/run/docs
- Terraform: https://developer.hashicorp.com/terraform/docs
- GitHub Actions billing:
  https://docs.github.com/en/billing/concepts/product-billing/github-actions
- Jenkins Pipeline: https://www.jenkins.io/doc/book/pipeline/
- Prometheus docs:
  https://prometheus.io/docs/prometheus/latest/getting_started/
- Grafana provisioning:
  https://grafana.com/docs/grafana/latest/administration/provisioning/
