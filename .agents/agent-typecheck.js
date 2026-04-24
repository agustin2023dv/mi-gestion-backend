const { execSync } = require('child_process');
try {
  execSync('npx tsc --noEmit', { stdio: 'inherit' });
} catch {
  process.exit(1);
}