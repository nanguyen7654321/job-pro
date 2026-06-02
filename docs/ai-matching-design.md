# AI Matching Design

## Flow

1. Candidate uploads resume.
2. Resume text is extracted.
3. LLM parses resume into structured JSON.
4. Candidate profile is saved.
5. Resume/profile is converted into an embedding.
6. Job descriptions are parsed and converted into embeddings.
7. Similarity search finds top jobs or candidates.
8. Matching service calculates a weighted score.
9. LLM generates explanation, strengths, gaps, and suggestions.
10. Candidate or recruiter sees the result.

## Weighted Score

```text
final score =
  40% skills similarity
  25% experience relevance
  15% title/domain match
  10% location/preference match
  10% AI reasoning score plus open-to-work signal
```

## First Implementation

- Store embeddings in Postgres using pgvector.
- Compute cosine similarity in SQL for candidate/job shortlisting.
- Use deterministic weighted scoring before LLM explanation.
- Keep prompts versioned in `ai/prompts`.
- Add sample resumes and jobs in `ai/evaluation` before tuning prompts.
