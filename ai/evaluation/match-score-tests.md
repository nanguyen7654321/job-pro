# Match Score Evaluation

Use this folder to keep sample resumes, sample jobs, and expected matching
behavior.

## Test Cases

1. Strong skill overlap and same domain should score above 80.
2. Strong skill overlap but wrong seniority should score between 60 and 80.
3. Weak skill overlap should score below 50.
4. Missing required certification should appear in `missingSkills`.
5. Remote preference mismatch should reduce location score only.
