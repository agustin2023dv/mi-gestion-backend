import js from '@eslint/js';
import globals from 'globals';
import reactHooks from 'eslint-plugin-react-hooks';
import reactRefresh from 'eslint-plugin-react-refresh';
import tseslint from 'typescript-eslint';
import sonarjs from 'eslint-plugin-sonarjs';
import unicorn from 'eslint-plugin-unicorn';
// Opcional: descomenta si lograste instalar eslint-plugin-import con --legacy-peer-deps
// import importPlugin from 'eslint-plugin-import';
import { defineConfig, globalIgnores } from 'eslint/config';

export default defineConfig([
  globalIgnores(['dist', 'node_modules', '.agents']),
  {
    files: ['**/*.{ts,tsx}'],
    plugins: {
      sonarjs,
      unicorn,
      // import: importPlugin, // opcional
    },
    extends: [
      js.configs.recommended,
      tseslint.configs.recommended,
      reactHooks.configs.flat.recommended,
      reactRefresh.configs.vite,
    ],
    languageOptions: {
      globals: globals.browser,
    },
    rules: {
      // === Reglas de calidad que tu agente necesita ===
      'max-lines': ['error', { max: 250, skipBlankLines: true, skipComments: true }],
      'max-lines-per-function': ['error', { max: 200, skipBlankLines: true, skipComments: true }],
      'max-params': ['error', 3],
      'max-depth': ['error', 3],
      'complexity': ['error', 10],
      
      // === DRY (código duplicado) - lo maneja jscpd, pero SonarJS ayuda ===
      'sonarjs/no-duplicate-string': ['error', { threshold: 3 }],
      
      // === Reglas adicionales de SonarJS para SOLID y buenas prácticas ===
      'sonarjs/cognitive-complexity': ['error', 15],
      'sonarjs/no-identical-functions': 'error',
      'sonarjs/no-collapsible-if': 'warn',
      
      // === Reglas de Unicorn (código moderno y limpio) ===
      'unicorn/filename-case': 'warn',
      'unicorn/no-array-for-each': 'warn',
      'unicorn/prefer-ternary': 'warn',
     
    },
    settings: {
      react: {
        version: 'detect',
      },
    },
  },
  {
    files: ['vite.config.*', '*.config.*', '.eslintrc.*'],
    rules: {
      'unicorn/filename-case': 'off',
    },
  },
]);