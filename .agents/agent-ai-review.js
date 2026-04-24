const { execSync } = require('child_process');
const fs = require('fs');
const path = require('path');

// Obtener archivos staged (git) o modificados
function getStagedFiles() {
  try {
    const output = execSync('git diff --cached --name-only --diff-filter=ACM', { encoding: 'utf8' });
    return output.split('\n').filter(f => f.match(/\.(js|jsx|ts|tsx)$/));
  } catch {
    return [];
  }
}

async function aiReview(filePath) {
  const code = fs.readFileSync(filePath, 'utf8');
  const prompt = `Eres un revisor de código React. Analiza este archivo y responde solo con un JSON con las claves: "issues" (array de strings) y "suggestions" (array de strings). No agregues texto fuera del JSON. Archivo: ${path.basename(filePath)}\n\n\`\`\`jsx\n${code}\n\`\`\``;
  
  try {
    const result = execSync(`ollama run llama3 "${prompt}"`, { encoding: 'utf8', maxBuffer: 10 * 1024 * 1024 });
    // Intentar parsear JSON (Ollama puede devolver texto extra)
    const jsonMatch = result.match(/\{[\s\S]*\}/);
    if (jsonMatch) {
      const review = JSON.parse(jsonMatch[0]);
      if (review.issues?.length || review.suggestions?.length) {
        console.log(`\n📝 Review for ${filePath}:`);
        if (review.issues.length) console.log('  ❌ Issues:', review.issues);
        if (review.suggestions.length) console.log('  💡 Suggestions:', review.suggestions);
      } else {
        console.log(`✅ ${filePath} looks good.`);
      }
    } else {
      console.log(`⚠️ Could not parse AI response for ${filePath}`);
    }
  } catch (err) {
    console.error(`AI review failed for ${filePath}:`, err.message);
  }
}

(async () => {
  const files = getStagedFiles();
  if (files.length === 0) {
    console.log('No staged JS/TS files to review.');
    process.exit(0);
  }
  console.log(`🤖 AI reviewing ${files.length} files...`);
  for (const file of files) {
    await aiReview(file);
  }
})();