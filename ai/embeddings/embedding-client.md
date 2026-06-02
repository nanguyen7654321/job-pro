# Embedding Client Design

## MVP

- Generate candidate and job embeddings from normalized profile/job JSON.
- Store vectors in PostgreSQL using pgvector.
- Keep embedding metadata: provider, model, source type, source id, created at.

## Interface

```text
EmbeddingClient.embed(text, metadata) -> EmbeddingResult
```

## Provider Strategy

- Start with one provider configured by `AI_PROVIDER`.
- Keep provider-specific code behind an interface.
- Store model name with each vector so future re-embedding is auditable.
