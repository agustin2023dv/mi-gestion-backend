const { execSync } = require('child_process');

try {
  console.log('🔍 Running ESLint...');
  execSync('npx eslint . --ext .js,.jsx,.ts,.tsx --fix', { stdio: 'inherit' });

  console.log('✨ Running Prettier...');
  execSync('npx prettier --write .', { stdio: 'inherit' });

  console.log('✅ Lint & Format completed');
} catch (error) {
  console.error('Linting or formatting failed');
  process.exit(1);
}