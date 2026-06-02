import { useQuery } from "@tanstack/react-query";
import { RankedCandidate } from "@/types/candidate";

// This hook models the eventual employer endpoint:
// GET /api/employers/jobs/{jobId}/ranked-candidates. Keeping the fallback shape
// close to the contract makes the UI easy to connect later.
const fallbackCandidates: RankedCandidate[] = [
  {
    id: "candidate-1",
    name: "Avery Chen",
    headline: "Senior full-stack engineer",
    matchScore: 93,
    recruiterSummary:
      "Excellent Java, React, and platform experience with recent AI search work.",
  },
  {
    id: "candidate-2",
    name: "Maya Patel",
    headline: "Backend engineer, AI infrastructure",
    matchScore: 88,
    recruiterSummary:
      "Strong backend and cloud profile; frontend depth should be checked in interview.",
  },
];

export function useRankedCandidates() {
  return useQuery({
    queryKey: ["ranked-candidates"],
    queryFn: async () => fallbackCandidates,
    staleTime: 60_000,
  });
}
