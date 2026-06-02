"use client";

import { Briefcase, ListChecks, UsersRound } from "lucide-react";
import { StatCard } from "@/components/ui/stat-card";
import { useRankedCandidates } from "@/hooks/useRankedCandidates";

export default function EmployerHome() {
  const { data: candidates = [] } = useRankedCandidates();

  return (
    <main className="min-h-screen">
      {/* Recruiters need dense, decision-oriented information. The first screen
          therefore starts with job context, applicant metrics, and ranked
          candidates instead of a marketing hero. */}
      <section className="border-b border-border bg-white">
        <div className="mx-auto flex max-w-6xl flex-col gap-6 px-6 py-8">
          <div className="flex flex-wrap items-center justify-between gap-4">
            <div>
              <h1 className="text-3xl font-semibold tracking-normal">
                Recruiter Review Workspace
              </h1>
              <p className="mt-2 max-w-2xl text-sm text-slate-600">
                Manage roles, review applicants, and act on AI-ranked candidate
                shortlists.
              </p>
            </div>
            <button className="inline-flex items-center gap-2 rounded-md bg-primary px-4 py-2 text-sm font-medium text-white">
              <Briefcase size={18} />
              Post job
            </button>
          </div>
          <div className="grid gap-3 md:grid-cols-3">
            <StatCard icon={Briefcase} label="Open jobs" value="8" />
            <StatCard icon={UsersRound} label="Applicants" value="126" />
            <StatCard icon={ListChecks} label="Shortlisted" value="19" />
          </div>
        </div>
      </section>

      <section className="mx-auto grid max-w-6xl gap-4 px-6 py-6 lg:grid-cols-[380px_1fr]">
        <aside className="rounded-md border border-border bg-white p-4">
          <h2 className="text-base font-semibold">Active role</h2>
          <p className="mt-2 text-sm font-medium">
            Senior AI Platform Engineer
          </p>
          <p className="mt-2 text-sm leading-6 text-slate-700">
            Required: Java, React, distributed systems, embeddings, and cloud
            deployment experience.
          </p>
          <div className="mt-5 rounded-md bg-muted p-3 text-sm">
            AI parser found 12 required skills and 5 preferred skills.
          </div>
        </aside>

        <div className="overflow-hidden rounded-md border border-border bg-white">
          <div className="border-b border-border px-4 py-3">
            <h2 className="text-base font-semibold">Ranked candidates</h2>
          </div>
          <div className="divide-y divide-border">
            {candidates.map((candidate) => (
              <article key={candidate.id} className="px-4 py-4">
                <div className="flex items-start justify-between gap-4">
                  <div>
                    <h3 className="font-medium">{candidate.name}</h3>
                    <p className="text-sm text-slate-600">
                      {candidate.headline}
                    </p>
                  </div>
                  <span className="rounded-md bg-muted px-2 py-1 text-sm font-medium">
                    {candidate.matchScore}%
                  </span>
                </div>
                <p className="mt-3 text-sm text-slate-700">
                  {candidate.recruiterSummary}
                </p>
              </article>
            ))}
          </div>
        </div>
      </section>
    </main>
  );
}
