## Typography Resources

### Cómo usar `typography-pairs.js`
1. Elige un par según el tono del proyecto (`editorial`, `retroTech`, `softOrganic`, etc.).
2. Importa `getFontPreloadUrl` y agrega el `<link>` en tu `_document.tsx` o `index.html`:
   ```tsx
   <link rel="preload" href={getFontPreloadUrl(typographyPairs.editorial.display)} as="style" />
   <link rel="stylesheet" href={getFontPreloadUrl(typographyPairs.editorial.display)} />