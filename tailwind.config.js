/** @type {import('tailwindcss').Config} */
export default {
  content: ["./src/main/frontend/**/*.{html,js,ts,jsx,tsx}", "./src/main/java/**/*.java"],
  darkMode: ['selector', '[theme="dark"]'],
  theme: {
    extend: {
      colors: {
        'bg-primary': 'var(--color-bg-primary)',
        'bg-secondary': 'var(--color-bg-secondary)',
        'bg-surface': 'var(--color-bg-surface)',
        'text-main': 'var(--color-text-main)',
        'text-secondary': 'var(--color-text-secondary)',
        'text-muted': 'var(--color-text-muted)',
        'text-inverted': 'var(--color-text-inverted)',
        'primary': 'var(--color-primary)',
        'primary-hover': 'var(--color-primary-hover)',
        'border': 'var(--color-border)',
        'success': '#16a34a', // green-600
        'error': '#dc2626',   // red-600
        'info': '#1f2937',    // gray-800
      }
    },
  },
  plugins: [],
}
