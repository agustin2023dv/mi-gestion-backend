const { execSync } = require('child_process');
console.log('🔒 Running npm audit...');
try {
  execSync('npm audit --production --audit-level=moderate', { stdio: 'inherit' });
} catch {
  console.warn('Audit found issues (non-blocking)');
  process.exit(0); // No bloquea
}