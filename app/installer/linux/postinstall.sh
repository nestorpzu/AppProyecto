#!/bin/bash
# Dar permisos de ejecución
chmod +x /opt/appproyecto/bin/AppProyecto
# Actualizar la base de datos del menú de aplicaciones
update-desktop-database /usr/share/applications/ 2>/dev/null || true