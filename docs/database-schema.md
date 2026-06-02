# Core Database Schema

PostgreSQL is the system of record. Add pgvector extension before creating
embedding columns.

```sql
create extension if not exists vector;
```

The current seed script creates these MVP tables without strict foreign-key
constraints so services with placeholder auth and in-memory flows can still be
tested independently. Add migrations and referential constraints before
production.

## Tables

- `users`: id, email, password_hash, role, status, created_at, updated_at
- `candidate_profiles`: id, user_id, full_name, headline, location,
  total_experience_years, current_title, desired_title, preferred_location,
  open_to_remote, ai_summary, created_at, updated_at
- `candidate_skills`: id, candidate_id, skill_name, skill_level,
  years_experience
- `resumes`: id, candidate_id, file_url, parsed_json, ai_summary,
  embedding_id, created_at
- `companies`: id, name, website, industry, size, created_at
- `employer_users`: id, user_id, company_id, title, role
- `jobs`: id, company_id, created_by, title, description, location,
  employment_type, experience_min, experience_max, salary_min, salary_max,
  status, parsed_json, embedding_id, created_at
- `applications`: id, job_id, candidate_id, status, match_score, ai_summary,
  applied_at, updated_at
- `match_scores`: id, job_id, candidate_id, overall_score, skills_score,
  experience_score, title_score, location_score, ai_reasoning_score,
  explanation, missing_skills, created_at
- `embeddings`: id, owner_type, owner_id, model, vector, created_at
