-- pgvector keeps semantic search in PostgreSQL for MVP simplicity.
create extension if not exists vector;

-- Auth Service owns user identity. Other services should reference users by id
-- instead of duplicating login credentials. The scaffold does not enforce
-- foreign keys yet because Auth Service still returns a fake token and does not
-- persist users.
create table if not exists users (
  id uuid primary key,
  email text not null unique,
  password_hash text not null,
  role text not null,
  status text not null default 'ACTIVE',
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

-- Candidate Service currently has the first real JPA persistence path. Keep this
-- table aligned with CandidateProfile.java while migration tooling is still
-- pending.
create table if not exists candidate_profiles (
  id uuid primary key,
  user_id uuid not null,
  full_name text not null,
  headline text,
  location text,
  total_experience_years integer,
  current_title text,
  desired_title text,
  preferred_location text,
  open_to_remote boolean not null default false,
  ai_summary text,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

create index if not exists idx_candidate_profiles_user_id
  on candidate_profiles (user_id);

create table if not exists candidate_skills (
  id uuid primary key,
  candidate_id uuid not null,
  skill_name text not null,
  skill_level text,
  years_experience numeric(4, 1)
);

create index if not exists idx_candidate_skills_candidate_id
  on candidate_skills (candidate_id);

-- ResumeDocument.java persists uploaded resume metadata and AI parse results.
-- File bytes belong in object storage; this table stores only URLs and metadata.
create table if not exists resumes (
  id uuid primary key,
  candidate_id uuid not null,
  file_url text not null,
  parsed_json text,
  ai_summary text,
  embedding_id uuid,
  created_at timestamptz not null default now()
);

create index if not exists idx_resumes_candidate_created
  on resumes (candidate_id, created_at desc);

-- Employer Service owns company metadata and employer account relationships.
create table if not exists companies (
  id uuid primary key,
  name text not null,
  website text,
  industry text,
  size text,
  created_at timestamptz not null default now()
);

create table if not exists employer_users (
  id uuid primary key,
  user_id uuid not null,
  company_id uuid not null,
  title text,
  role text not null
);

create index if not exists idx_employer_users_company_id
  on employer_users (company_id);

create table if not exists jobs (
  id uuid primary key,
  company_id uuid not null,
  created_by uuid not null,
  title text not null,
  description text not null,
  location text,
  employment_type text,
  experience_min integer,
  experience_max integer,
  salary_min numeric(12, 2),
  salary_max numeric(12, 2),
  status text not null default 'DRAFT',
  parsed_json text,
  embedding_id uuid,
  created_at timestamptz not null default now()
);

create index if not exists idx_jobs_company_status
  on jobs (company_id, status);

create table if not exists applications (
  id uuid primary key,
  job_id uuid not null,
  candidate_id uuid not null,
  status text not null default 'APPLIED',
  match_score numeric(5, 2),
  ai_summary text,
  applied_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);

create index if not exists idx_applications_job_id
  on applications (job_id);

create index if not exists idx_applications_candidate_id
  on applications (candidate_id);

create table if not exists match_scores (
  id uuid primary key,
  job_id uuid not null,
  candidate_id uuid not null,
  overall_score numeric(5, 2) not null,
  skills_score numeric(5, 2),
  experience_score numeric(5, 2),
  title_score numeric(5, 2),
  location_score numeric(5, 2),
  ai_reasoning_score numeric(5, 2),
  explanation text,
  missing_skills text,
  created_at timestamptz not null default now()
);

create index if not exists idx_match_scores_job_candidate
  on match_scores (job_id, candidate_id);

-- Shared embedding table for candidate profiles, resumes, and jobs. The model
-- column is required so vectors can be regenerated safely after model changes.
create table if not exists embeddings (
  id uuid primary key,
  owner_type text not null,
  owner_id uuid not null,
  model text not null,
  vector vector(1536),
  created_at timestamptz not null default now()
);

create index if not exists idx_embeddings_owner
  on embeddings (owner_type, owner_id);
