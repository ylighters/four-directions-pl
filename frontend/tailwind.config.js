/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{ts,tsx}'],
  theme: {
    extend: {
      colors: {
        brand: {
          50: '#eef8ff',
          100: '#d9edff',
          500: '#0b70f3',
          700: '#0054c4',
          900: '#083175'
        }
      }
    }
  },
  plugins: []
};
