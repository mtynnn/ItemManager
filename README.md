# ItemManager

Un potente gestor de items personalizados para servidores de Minecraft.

## Características

- **Items Personalizados**
  - Soporte para colores hexadecimales (#RRGGBB)
  - Sistema de menú interactivo
  - Acciones de click personalizables
  - Slots bloqueados
  - Items automáticos al unirse
  - Sistema de permisos integrado
  - Soporte para NBT personalizado
  - Glow y encantamientos
  - Fácil configuración mediante YAML

## Comandos

- `/itemmanager` o `/im` - Comando principal

  - `/itemmanager reload` - Recarga la configuración
  - `/itemmanager giveitem <id>` - Da un item personalizado
  - `/itemmanager debug` - Activa/desactiva el modo debug

- `/giveitem <id>` - Da un item personalizado a un jugador

## Permisos

- `itemmanager.admin` - Acceso a todos los comandos (default: op)
- `itemmanager.reload` - Permite recargar la configuración (default: op)
- `itemmanager.giveitem` - Permite dar items personalizados (default: op)
- `itemmanager.menu` - Permite usar el menú principal (default: true)
- `itemmanager.kit` - Permite reclamar el kit de bienvenida (default: true)
- `itemmanager.debug` - Permite activar/desactivar el modo debug (default: op)

## Instalación

1. Descarga la última versión del plugin
2. Coloca el archivo .jar en la carpeta `plugins` de tu servidor
3. Reinicia el servidor
4. Configura los items en `plugins/ItemManager/items.yml`

## Configuración

### items.yml

```yaml
items:
  menu_principal:
    material: COMPASS
    name: "#00FF00&lMenú Principal"
    lore:
      - "#FFFFFFHaz click derecho para abrir"
      - "#FFFFFFel menú principal"
    glow: true
    giveOnJoin: true
    slot: 0
    click_actions:
      right_click:
        commands:
          - "[MESSAGE] &aAbriendo menú..."
          - "[CONSOLE] menu %player%"
        permission: "itemmanager.menu"
```

## Soporte

- Versión de Minecraft: 1.21+
- Versión de Java: 17+
- Dependencias: Paper/Spigot

## Créditos

Desarrollado por Mtyn

## Licencia

Este proyecto está bajo la licencia MIT. Ver el archivo LICENSE para más detalles.
