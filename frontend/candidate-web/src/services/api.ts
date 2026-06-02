import axios from "axios";

// All browser API calls should use this client so auth headers, request IDs, and
// retries can be added once without touching every feature hook.
export const api = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_BASE_URL ?? "http://localhost:8080",
  timeout: 10000,
});
