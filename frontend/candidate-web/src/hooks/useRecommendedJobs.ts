import { useQuery } from "@tanstack/react-query";
import { RecommendedJob } from "@/types/job";

// Fallback data keeps the MVP UI usable before backend endpoints are connected.
// Replace this with api.get('/api/candidates/jobs/recommended') once auth and
// candidate identity are wired through the gateway.
const fallbackJobs: RecommendedJob[] = [
  {
    id: "job-1",
    title: "Senior Full Stack Engineer",
    company: "Northstar Health",
    location: "Remote",
    matchScore: 91,
    explanation: "Strong React, Java, API, and cloud alignment.",
  },
  {
    id: "job-2",
    title: "AI Platform Engineer",
    company: "SignalWorks",
    location: "San Francisco, CA",
    matchScore: 84,
    explanation: "Excellent backend fit with a small MLOps gap.",
  },
];

export function useRecommendedJobs() {
  return useQuery({
    queryKey: ["recommended-jobs"],
    queryFn: async () => fallbackJobs,
    staleTime: 60_000,
  });
}
