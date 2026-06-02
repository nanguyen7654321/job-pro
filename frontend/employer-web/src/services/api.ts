import axios from "axios";

// Employer features should share this API client so JWT handling, tenant/company
// headers, and request tracing are configured consistently.
export const api = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_BASE_URL ?? "http://localhost:8080",
  timeout: 10000,
});
