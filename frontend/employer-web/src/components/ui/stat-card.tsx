import { LucideIcon } from "lucide-react";

type StatCardProps = {
  icon: LucideIcon;
  label: string;
  value: string;
};

export function StatCard({ icon: Icon, label, value }: StatCardProps) {
  return (
    <div className="rounded-md border border-border bg-white p-4">
      <div className="flex items-center gap-3">
        <span className="rounded-md bg-muted p-2">
          <Icon size={18} />
        </span>
        <div>
          <p className="text-xs uppercase text-slate-500">{label}</p>
          <p className="text-xl font-semibold">{value}</p>
        </div>
      </div>
    </div>
  );
}
