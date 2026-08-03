import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import tailwindcss from '@tailwindcss/vite'

// https://vite.dev
export default defineConfig({
  plugins: [
    react(),
    tailwindcss(),
  ],
  build: {
    rolldownOptions: {
      output: {
        codeSplitting: {
          minSize: 20000, // Tamaño mínimo de 20KB para crear un fragmento independiente
          groups: [
            {
              name: 'vendor',
              test: /node_modules/, // Mueve todas las librerías externas a un archivo separado
              priority: 10,
            },
          ],
        },
      },
    },
  },
})
