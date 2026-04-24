const { execSync } = require('child_process');
const fs = require('fs');
const path = require('path');

function getStagedFiles() {
  try {
    const output = execSync('git diff --cached --name-only --diff-filter=ACM', { encoding: 'utf8' });
    return output.split('\n').filter(f => f.match(/\.(js|jsx|ts|tsx)$/));
  } catch {
    return [];
  }
}

async function addJsDoc(filePath) {
  let code = fs.readFileSync(filePath, 'utf8');
  // Detectar funciones/componentes sin JSDoc (muy básico)
  const funcRegex = /(function\s+\w+\s*\(|const\s+\w+\s*=\s*\([^)]*\)\s*=>|export\s+default\s+function\s+\w+)/g;
  let match;
  let modified = false;

  while ((match = funcRegex.exec(code)) !== null) {
    const funcName = match[0];
    // Verificar si ya tiene JSDoc arriba
    const lines = code.slice(0, match.index).split('\n');
    const lastNonEmpty = lines.slice().reverse().find(l => l.trim().length > 0);
    if (lastNonEmpty && lastNonEmpty.trim().startsWith('/**')) continue;

    // Pedir a Ollama que genere JSDoc
    const prompt = `Genera solo el comentario JSDoc (sin código) para la siguiente función React. Responde únicamente con el JSDoc, desde /** hasta */.\n\n${funcName}`;
    try {
      const jsdoc = execSync(`ollama run llama3 "${prompt}"`, { encoding: 'utf8' }).trim();
      if (jsdoc.startsWith('/**')) {
        // Insertar JSDoc antes de la función
        const insertPos = match.index;
        code = code.slice(0, insertPos) + jsdoc + '\n' + code.slice(insertPos);
        modified = true;
      }
    } catch (err) {
      console.error(`Failed to generate JSDoc for ${funcName}:`, err.message);
    }
  }

  if (modified) {
    fs.writeFileSync(filePath, code, 'utf8');
    console.log(`📄 JSDoc added to ${filePath}`);
  } else {
    console.log(`ℹ️ No missing JSDoc in ${filePath}`);
  }
}

(async () => {
  const files = getStagedFiles();
  if (files.length === 0) process.exit(0);
  for (const file of files) {
    await addJsDoc(file);
  }
})();