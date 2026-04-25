---
name: code-review
description: Comprehensive code review assistant for evaluating pull requests and code changes. Focuses on correctness, security, performance, maintainability, and adherence to team conventions. Use when reviewing PRs, conducting peer reviews, or auditing code quality.
tags: ["review", "quality-assurance", "best-practices", "pr-review", "security", "maintainability"]
---

# Code Review Skill

When reviewing code, follow this structured approach to ensure thorough, actionable, and consistent feedback.

## 🔍 Review Checklist

1. **Correctness & Logic**
   - Does the implementation match the stated requirements or ticket description?
   - Are control flows, state transitions, and data transformations accurate?
   - Watch for race conditions, off-by-one errors, or unintended side effects.

2. **Edge Cases & Error Handling**
   - Are null/undefined/empty inputs handled gracefully?
   - Are boundary values, timeouts, retries, and fallbacks considered?
   - Are errors typed, logged appropriately, and surfaced to the right layer?

3. **Security & Compliance**
   - Check for injection risks (SQL, XSS, command, path traversal).
   - Verify authentication/authorization checks aren't bypassed.
   - Ensure no secrets, tokens, or sensitive data are hardcoded or logged.
   - Flag outdated or vulnerable dependencies when visible.

4. **Performance & Scalability**
   - Identify N+1 queries, unbounded loops, or synchronous blocking in async paths.
   - Note memory leaks, unnecessary re-renders, or excessive allocations.
   - Suggest caching, pagination, indexing, or lazy loading where appropriate.

5. **Style & Maintainability**
   - Follow project conventions (naming, formatting, folder structure, imports).
   - Enforce DRY, KISS, and SOLID principles without over-engineering.
   - Prefer explicit over implicit, and readable over clever.
   - Flag magic numbers/strings; suggest constants or config.

6. **Testing & Coverage**
   - Are new/modified paths covered by unit, integration, or e2e tests?
   - Do tests assert behavior, not implementation details?
   - Check for flaky setups, missing mocks, or unrealistic fixtures.
   - Suggest missing edge-case tests when obvious.

7. **Documentation & Developer Experience**
   - Update README, API docs, or changelog if public behavior changes.
   - Comments should explain *why*, not repeat *what* the code does.
   - Ensure commit messages and PR descriptions are clear and actionable.

## 📝 How to Provide Feedback

- **Prioritize by severity**:
  - 🔴 `Critical`: Blocks merge (bug, security flaw, broken build)
  - 🟠 `Major`: Should be fixed before merge (logic gap, poor error handling)
  - 🟡 `Minor`: Nitpick or improvement (style, naming, micro-optimization)
  - 🔵 `Info`: Context, question, or future consideration
- **Structure each comment**: `Location → Observation → Impact → Suggestion → (Optional) Code snippet`
- **Be constructive**: Use collaborative language (`Consider...`, `Could be improved by...`, `What if we...?`) instead of absolute judgments.
- **Reference standards**: Link to style guides, architecture decisions, or existing codebase patterns.
- **Ask when unsure**: If intent is unclear, request clarification rather than guessing.

## 📤 Suggested Output Format

```markdown
## 📋 Code Review Summary
- **Files changed**: {count}
- **Risk level**: Low / Medium / High
- **Recommendation**: ✅ Approve / 🔁 Request changes / 💬 Comment

### 🔍 Detailed Feedback
| File | Line(s) | Severity | Category | Comment | Suggestion |
|------|---------|----------|----------|---------|------------|
| `src/auth.ts` | 42-48 | 🟠 Major | Security | Missing rate limit on login endpoint | Add express-rate-limit or similar |
| ... | ... | ... | ... | ... | ... |

### ✅ Positives & Strengths
- Clear separation of concerns in `{module}`
- Excellent test coverage for edge cases in `{feature}`

### 🚀 Next Steps
- [ ] Address 🔴/🟠 comments before merge
- [ ] Update docs if public API changed
- [ ] Run `{lint/test/build}` command locally