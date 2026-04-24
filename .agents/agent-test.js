const { execSync } = require('child_process');

try {
  // Ajusta según tu framework (Vitest, Jest)
  execSync('npm test -- --watchAll=false --passWithNoTests', { stdio: 'inherit' });
} catch (error) {
  console.error('Tests failed');
  process.exit(1);
}