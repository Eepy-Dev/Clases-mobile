# Configuración de MockAPI.io para el Backend Simulado

Este documento explica cómo configurar MockAPI.io para que la aplicación pueda consumir productos de pastelería desde un backend simulado.

## Pasos para Configurar MockAPI.io

### 1. Crear una cuenta en MockAPI.io
1. Ve a https://mockapi.io/
2. Regístrate o inicia sesión (puedes usar GitHub, Google, etc.)

### 2. Crear un Proyecto
1. Haz clic en "New Project"
2. Dale un nombre al proyecto (ej: "Pasteleria-App")
3. Haz clic en "Create"

### 3. Crear un Recurso llamado "productos"
1. En tu proyecto, haz clic en "New Resource"
2. Nombre del recurso: `productos`
3. Agrega los siguientes campos:

```
Field Name      | Type    | Example Value
----------------|---------|------------------
id              | String  | (automático)
title           | String  | Torta de Chocolate
price           | Number  | 25.50
description     | String  | Deliciosa torta de chocolate...
image           | String  | https://ejemplo.com/imagen.jpg
```

**Nota**: El campo `category` es opcional. No se usa en el inventario local, pero puedes agregarlo si lo necesitas para filtrado en el catálogo online.

4. Haz clic en "Create Resource"

### 4. Obtener la URL Base
1. Una vez creado el recurso, verás una URL similar a:
   ```
   https://TU_PROJECT_ID.mockapi.io/api/v1/productos
   ```
2. La URL base que necesitas es:
   ```
   https://TU_PROJECT_ID.mockapi.io/api/v1/
   ```

### 5. Configurar la URL en el Código
1. Abre el archivo: `app/src/main/java/com/example/appmovil/network/RetrofitClient.kt`
2. Busca la línea:
   ```kotlin
   private const val BASE_URL_MOCK_API = "https://TU_PROJECT_ID.mockapi.io/api/v1/"
   ```
3. Reemplaza `TU_PROJECT_ID` con tu ID de proyecto real

### 6. Agregar Productos de Prueba (Opcional)
Puedes agregar productos de ejemplo directamente desde MockAPI.io:

1. En el recurso "productos", haz clic en "Add Record"
2. Agrega productos de pastelería de ejemplo:

```json
{
  "title": "Torta de Chocolate",
  "price": 25.50,
  "description": "Deliciosa torta de chocolate con crema",
  "image": "https://images.unsplash.com/photo-1578985545062-69928b1d9587?w=400"
}
```

### 7. Probar la API
Puedes probar los endpoints directamente en MockAPI.io o usando Postman:

- GET: `https://TU_PROJECT_ID.mockapi.io/api/v1/productos` - Lista todos los productos
- GET: `https://TU_PROJECT_ID.mockapi.io/api/v1/productos/1` - Obtiene un producto por ID
- POST: `https://TU_PROJECT_ID.mockapi.io/api/v1/productos` - Crea un nuevo producto
- PUT: `https://TU_PROJECT_ID.mockapi.io/api/v1/productos/1` - Actualiza un producto
- DELETE: `https://TU_PROJECT_ID.mockapi.io/api/v1/productos/1` - Elimina un producto

## Estructura JSON Esperada

La API debe devolver productos en este formato:

```json
[
  {
    "id": "1",
    "title": "Torta de Chocolate",
    "price": 25.50,
    "description": "Deliciosa torta de chocolate con crema",
    "image": "https://ejemplo.com/imagen.jpg"
  },
  {
    "id": "2",
    "title": "Cupcakes de Vainilla",
    "price": 12.00,
    "description": "6 cupcakes de vainilla con frosting",
    "image": "https://ejemplo.com/imagen2.jpg"
  }
]
```

## Notas Importantes

- La versión gratuita de MockAPI.io tiene límites (1000 requests/mes)
- Los datos se mantienen mientras tu proyecto esté activo
- Puedes editar/eliminar productos directamente desde MockAPI.io para probar

## Solución de Problemas

Si la app muestra un error al cargar productos:
1. Verifica que la URL en `RetrofitClient.kt` sea correcta
2. Verifica que el recurso se llame exactamente "productos"
3. Verifica que tengas al menos un producto agregado
4. Revisa los logs de Android Studio para ver el error específico

