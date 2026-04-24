const { execSync } = require('child_process');
const path = require('path');
const fs = require('fs');

// Configuración
const AGENTS_DIR = __dirname;
const AGENTS = [
  { name: 'Lint & Format', file: 'agent-lint.js', required: true, skipOnError: false },
  { name: 'Quality (DRY/KISS/SOLID)', file: 'agent-quality.js', required: true, skipOnError: false },
  { name: 'Type Check',   file: 'agent-typecheck.js', required: false, skipOnError: true },
  { name: 'Tests',        file: 'agent-test.js', required: true, skipOnError: false },
  { name: 'AI Review',    file: 'agent-ai-review.js', required: false, skipOnError: true },
  { name: 'AI Docs',      file: 'agent-ai-docs.js', required: false, skipOnError: true },
  { name: 'Audit',        file: 'agent-audit.js', required: false, skipOnError: true }
];

// Parsear argumentos (ej: --skip=test,ai)
const args = process.argv.slice(2);
const skipList = args
  .find(a => a.startsWith('--skip='))
  ?.split('=')[1]
  ?.split(',') || [];

async function runAgent(agent) {
  if (skipList.includes(agent.name.toLowerCase().replace(/ /g, ''))) {
    console.log(`⏭️  Skipping ${agent.name}`);
    return { success: true, skipped: true };
  }

  const agentPath = path.join(AGENTS_DIR, agent.file);
  if (!fs.existsSync(agentPath)) {
    console.warn(`⚠️  Agent file missing: ${agent.file}`);
    return { success: !agent.required, missing: true };
  }

  console.log(`\n🚀 Running: ${agent.name}`);
  try {
    execSync(`node "${agentPath}"`, { stdio: 'inherit', cwd: process.cwd() });
    console.log(`✅ ${agent.name} passed`);
    return { success: true };
  } catch (error) {
    console.error(`❌ ${agent.name} failed`);
    if (agent.required && !agent.skipOnError) {
      console.error(`⛔ Stopping because ${agent.name} is required.`);
      process.exit(1);
    }
    return { success: false, failed: true };
  }
}

(async () => {
  console.log('🧠 Agent Orchestrator started\n');
  for (const agent of AGENTS) {
    await runAgent(agent);
  }
  console.log('\n🏁 Orchestrator finished.');
})();