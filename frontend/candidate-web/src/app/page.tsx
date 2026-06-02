"use client";

import { BriefcaseBusiness, FileUp, Sparkles } from "lucide-react";
import { StatCard } from "@/components/ui/stat-card";
import { useRecommendedJobs } from "@/hooks/useRecommendedJobs";

export default function CandidateHome() {
  const { data: jobs = [] } = useRecommendedJobs();

  return (
    <main className="min-h-screen">
      {/* This first screen is the candidate's actual work surface: resume,
          profile readiness, and matches. It is intentionally not a landing page
          because MVP users need to validate workflow speed more than marketing
          copy. */}
      <section className="border-b border-border bg-white">
        <div className="mx-auto flex max-w-6xl flex-col gap-6 px-6 py-8">
          <div className="flex flex-wrap items-center justify-between gap-4">
            <div>
              <h1 className="text-3xl font-semibold tracking-normal">
                Candidate Match Workspace
              </h1>
              <p className="mt-2 max-w-2xl text-sm text-slate-600">
                Resume profile, job recommendations, and match explanations in
                one focused workflow.
              </p>
            </div>
            <button className="inline-flex items-center gap-2 rounded-md bg-primary px-4 py-2 text-sm font-medium text-white">
              <FileUp size={18} />
              Upload resume
            </button>
          </div>
          <div className="grid gap-3 md:grid-cols-3">
            <StatCard icon={Sparkles} label="Profile readiness" value="82%" />
            <StatCard
              icon={BriefcaseBusiness}
              label="Strong matches"
              value="12"
            />
            <StatCard icon={FileUp} label="Resume version" value="Latest" />
          </div>
        </div>
      </section>

      <section className="mx-auto grid max-w-6xl gap-4 px-6 py-6 lg:grid-cols-[1fr_360px]">
        <div className="overflow-hidden rounded-md border border-border bg-white">
          <div className="border-b border-border px-4 py-3">
            <h2 className="text-base font-semibold">Recommended jobs</h2>
          </div>
          <div className="divide-y divide-border">
            {jobs.map((job) => (
              <article key={job.id} className="px-4 py-4">
                <div className="flex items-start justify-between gap-4">
                  <div>
                    <h3 className="font-medium">{job.title}</h3>
                    <p className="text-sm text-slate-600">
                      {job.company} - {job.location}
                    </p>
                  </div>
                  <span className="rounded-md bg-muted px-2 py-1 text-sm font-medium">
                    {job.matchScore}%
                  </span>
                </div>
                <p className="mt-3 text-sm text-slate-700">{job.explanation}</p>
              </article>
            ))}
          </div>
        </div>

        <aside className="rounded-md border border-border bg-white p-4">
          <h2 className="text-base font-semibold">AI profile summary</h2>
          <p className="mt-3 text-sm leading-6 text-slate-700">
            Full-stack engineer with strong React, Java, and cloud experience.
            Best fit is platform product teams that need hands-on delivery plus
            pragmatic architecture judgment.
          </p>
          <div className="mt-5">
            <h3 className="text-sm font-medium">Resume improvements</h3>
            <ul className="mt-2 space-y-2 text-sm text-slate-700">
              <li>Add measurable impact for backend performance work.</li>
              <li>Group AI and search experience into a dedicated section.</li>
              <li>Include cloud deployment keywords for senior roles.</li>
            </ul>
          </div>
        </aside>
      </section>
    </main>
  );
}
