// Estilos globales (Tailwind procesado por PostCSS/Vite)
import './styles/tailwind.src.css';
import './styles/general.css';
import './styles/theme-light.css';
import './styles/theme-dark.css';

// Punto de entrada React para Vaadin Flow-React bridge.
// Vaadin incluirá este index desde generated/vaadin.ts como efecto secundario.
import React from 'react';
import { createRoot } from 'react-dom/client';
import { RouterProvider } from 'react-router-dom';
import { router } from './generated/routes';

const outlet = document.getElementById('outlet');
if (outlet) {
    const root = createRoot(outlet);
    root.render(<RouterProvider router={router} />);
}
