---
name: api-connection-review
description: Validates ONLY API connection setup, request configuration, and response handling against `docs/api/*.md` contracts. Flags mismatches, ignores business logic/UI/DB, and asks for clarification when contracts and code diverge.
version: "1.0"
complexity: low
category: integration-validation
tags: ["api", "network", "error-handling", "response-parsing", "connection", "http", "contract-validation", "docs-sync"]
---

# API Connection & Response Validation Skill

Use this skill **exclusively** to verify how external/internal API connections are established and how responses are processed. Treat `docs/api/*.md` as the authoritative contract. Do not review business rules, UI, state management, caching, or database operations.

## 🔍 Validation Checklist

### 1. 📜 Contract Alignment (`docs/api/*.md`)
- [ ] Endpoint path, HTTP method, and query/path params match the documentation.
- [ ] Required/optional headers, auth scheme, and `Content-Type`/`Accept` match the spec.
- [ ] Request payload structure (fields, types, nesting) aligns with documented schema.
- [ ] Documented status codes (`2xx`, `4xx`, `5xx`) are explicitly handled in code.
- [ ] Response shape (data wrapper, pagination, error format) matches the documented contract.
- [ ] If doc is marked `@deprecated`/`@draft`, note it but still validate current implementation.

### 2. 🔌 Connection & Request Setup
- [ ] Base URL is externalized (env/config), not hardcoded.
- [ ] Authentication tokens/API keys are securely injected (never logged or hardcoded).
- [ ] Network timeouts, connection limits, and retry/backoff policies are configured.
- [ ] Request payloads are validated/sanitized before sending.

### 3. 📥 Response Routing & Parsing
- [ ] All relevant status codes are routed correctly; no silent `200` assumptions.
- [ ] JSON/text parsing uses safe methods (`try/catch`, schema validation, optional chaining).
- [ ] Error payloads are extracted and mapped consistently (`message`, `code`, `details`).
- [ ] Failures are logged with context (endpoint, status, request ID, timestamp).
- [ ] Critical endpoints have fallbacks or graceful degradation paths.

### 4. 🛡️ Security & Resilience
- [ ] No secrets, PII, or internal IDs leaked in logs or error messages.
- [ ] `429 Too Many Requests` triggers exponential backoff, not tight retries.
- [ ] Circuit breaker or timeout isolation applied for unstable dependencies.
- [ ] Request size limits and rate-limit headers respected when documented.

## ⚖️ Discrepancy Protocol (Code vs `docs/api/`)
When implementation diverges from documentation:
1. **Identify exact mismatch**: e.g., `Code uses POST /users, docs specify PATCH /users/{id}`
2. **Classify impact**:
   - 🔴 `Breaking`: Auth change, required field removed, status routing missing
   - 🟠 `Structural`: Payload shape differs, extra undocumented params, missing error codes
   - 🟡 `Minor`: Header casing, optional field added, doc out of sync but behavior safe
3. **Action**:
   - ✅ If doc is clearly outdated: Flag as `🟡 Doc-sync needed` and suggest doc update.
   - 🔍 If intent is ambiguous: Add `💬 Clarification request` with exact question.
   - ❌ If code violates security/contract: Flag as `🔴 Contract violation` and request fix.
4. **Never assume**: Always surface the mismatch; do not silently "fix" to match docs or code.

## 📝 How to Provide Feedback
- **Strict scope**: Only comment on connection, request, response-handling, or doc mismatch. Ignore downstream logic.
- **Comment structure**: `File:Line → Issue → Risk → Suggested fix`
- **Always include a snippet** showing the corrected pattern or the exact doc excerpt.
- **Reference docs explicitly**: e.g., `📖 See docs/api/users.md#POST /users`

## 📤 Output Template
```markdown
## 📋 API Connection Review
- **Endpoints reviewed**: {count}
- **Contract alignment**: ✅ Match / ⚠️ Partial / ❌ Divergent
- **Recommendation**: ✅ Approve / 🔁 Request changes

### 🔍 Findings
| File | Line | Severity | Issue | Doc Ref | Suggestion |
|------|------|----------|-------|---------|------------|
| `src/api/users.ts` | 22 | 🟠 Major | Missing `409 Conflict` handling | `docs/api/users.md#responses` | Add case for `409` per contract |
| `src/api/payments.ts` | 45 | 🔴 Critical | Logs full response (PII leak) | `docs/api/security.md#logging` | Sanitize log to `status` + `requestId` |
| ... | ... | ... | ... | ... | ... |

### 📜 Contract Discrepancies
| Endpoint | Code Behavior | Documented Behavior | Action |
|----------|---------------|---------------------|--------|
| `POST /webhooks` | Returns `201` with `{ id }` | Docs specify `202 Accepted` + `{ webhook_url }` | 💬 Ask: Is doc outdated or code pending fix? |

### ✅ Strengths
- Base URL correctly loaded from `API_BASE_URL` env
- Safe JSON parsing with fallback for malformed responses

### 🚀 Next Steps
- [ ] Resolve 🔴/🟠 findings
- [ ] Clarify open discrepancies
- [ ] Sync `docs/api/` if code is authoritative