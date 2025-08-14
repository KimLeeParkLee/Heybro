/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./App.{js,jsx,ts,tsx}",
    "./src/**/*.{js,jsx,ts,tsx}"
  ],
  presets: [require("nativewind/preset")],
  darkMode: 'class', // Enable dark mode based on class
  theme: {
    extend: {
      colors: {
        primary: '#3490dc', // Example primary color
        secondary: '#ffed4a', // Example secondary color
        danger: '#e3342f', // Example danger color
        dark: {
          DEFAULT: '#1a202c', // Default dark background
          text: '#e2e8f0', // Default dark text color
        },
        light: {
          DEFAULT: '#f7fafc', // Default light background
          text: '#2d3748', // Default light text color
        },
      },
      spacing: {
        'sm': '8px',
        'md': '16px',
        'lg': '24px',
        'xl': '32px',
      },
      fontFamily: {
        sans: ['System', 'sans-serif'], // Example font family
        serif: ['Georgia', 'serif'],
      },
    },
  },
  plugins: [],
};
