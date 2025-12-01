# 🍰 Sistema de Gestión de Inventario - Pastelería

Hola! 👋 Este es nuestro proyecto de la evaluación parcial 3. Es una aplicación móvil Android completa para gestionar el inventario de una pastelería, con integración a microservicios en Spring Boot.

## 👥 Integrantes

- **Paz Molina**
- **Jordan Villegas**

---

## 📱 Descripción del Proyecto

Esta app móvil fue desarrollada en **Kotlin con Jetpack Compose** y permite gestionar todo el inventario de una pastelería de forma completa. Puedes agregar productos, hacer ventas, consultar movimientos, sincronizar con el backend y mucho más.

La app funciona con un enfoque **offline-first**, es decir, guarda todo localmente primero (con Room/SQLite) y luego sincroniza en segundo plano con nuestros microservicios de Spring Boot. Así funciona aunque no tengas internet, y cuando vuelvas la conexión, se sincroniza automáticamente.

---

## 🚀 Funcionalidades Principales

### 🔐 Autenticación

- Login con credenciales que se validan contra nuestro User Service
- Si el backend no está disponible, hay un fallback con usuarios locales de prueba

### 📦 Gestión de Productos

- **Ver todos los productos** en una lista completa
- **Agregar productos** manualmente con foto desde cámara o galería
- **Editar productos** existentes
- **Eliminar productos** con confirmación
- **Búsqueda** por nombre o ID en tiempo real
- **Detalles completos** de cada producto

### 🌐 Catálogo Online

- Visualizar productos disponibles en el servidor Spring Boot
- Agregar productos del catálogo web al inventario local
- Las imágenes se descargan automáticamente
- Sincronización individual o masiva

### 💰 Ventas y Movimientos

- **Registrar ventas** que reducen el stock automáticamente
- **Registrar mermas** (productos dañados o perdidos)
- **Movimientos internos** de inventario
- **Historial completo** de todos los movimientos con filtros
- Todo se sincroniza automáticamente con el backend

### 📊 Historial

- Ver todos los movimientos de inventario
- Filtrar por tipo: Entradas, Salidas, o Todo
- Ver stock anterior y nuevo en cada movimiento
- Fechas y horas de cada operación

### 🔄 Sincronización

- Los productos se sincronizan automáticamente con el backend
- Los movimientos también se sincronizan
- Productos del catálogo web (EXT-*) se guardan solo localmente
- Productos creados manualmente se sincronizan completamente

---

## 🛠️ Tecnologías Utilizadas

### Frontend (App Móvil)

- **Kotlin** - Lenguaje principal
- **Jetpack Compose** - UI declarativa moderna
- **Room Database** - Base de datos local SQLite
- **MVVM Architecture** - Separación de responsabilidades
- **LiveData & Flow** - Reactividad y observables
- **Retrofit** - Cliente HTTP para APIs
- **Coil** - Carga de imágenes desde URLs
- **Material Design 3** - Componentes modernos

### Backend (Microservicios)

- **Spring Boot** - Framework Java
- **H2 Database** - Base de datos en memoria/persistente
- **JPA/Hibernate** - ORM para base de datos
- **REST API** - Endpoints HTTP

### Testing

- **JUnit 5** - Framework de pruebas
- **Kotest** - Testing más expresivo con Kotlin
- **MockK** - Mocking para pruebas unitarias
- **Coroutines Test** - Testing de código asíncrono

---

## 🔗 Endpoints Utilizados

### Microservicios Propios (Spring Boot)

#### User Service (Puerto 8083)

- `POST /usuarios/login` - Autenticación de usuarios
- `GET /usuarios` - Listar usuarios
- `POST /usuarios` - Crear usuario

**Base URL:** `http://localhost:8083/` (o `http://10.0.2.2:8083/` desde emulador)

#### Products Service (Puerto 8081)

- `GET /productos` - Obtener todos los productos
- `GET /productos/{id}` - Obtener producto por ID
- `POST /productos` - Crear producto
- `PUT /productos/{id}` - Actualizar producto
- `DELETE /productos/{id}` - Eliminar producto
- `GET /productos/buscar?nombre={nombre}` - Buscar productos por nombre
- `POST /productos/salida` - Registrar salida de producto

**Base URL:** `http://localhost:8081/` (o `http://10.0.2.2:8081/` desde emulador)

#### Inventory Service (Puerto 8082)

- `GET /movimientos` - Obtener todos los movimientos
- `GET /movimientos/producto/{id}` - Movimientos de un producto
- `POST /movimientos` - Crear movimiento

**Base URL:** `http://localhost:8082/` (o `http://10.0.2.2:8082/` desde emulador)

---

## 📋 Pasos para Ejecutar el Proyecto

### Prerrequisitos

- Android Studio (última versión recomendada)
- JDK 11 o superior
- Gradle 8.0+
- Dispositivo Android o Emulador (API 26+)

### 1. Clonar el Repositorio

```bash
git clone https://github.com/Eepy-Dev/Clases-mobile.git
cd Clases-mobile
```

### 2. Iniciar los Microservicios Backend

Abre **3 terminales** diferentes en la raíz del proyecto:

**Terminal 1 - User Service:**

```bash
.\gradlew.bat :backend:user-service:bootRun
```

**Terminal 2 - Products Service:**

```bash
.\gradlew.bat :backend:products-service:bootRun
```

**Terminal 3 - Inventory Service:**

```bash
.\gradlew.bat :backend:inventory-service:bootRun
```

Espera a que los 3 servicios estén corriendo. Verás mensajes como "Started UserApplication" cuando estén listos.

### 3. Verificar que los Servicios Están Activos

Puedes verificar en tu navegador:

- **Usuarios:** http://localhost:8083/usuarios
- **Productos:** http://localhost:8081/productos
- **Movimientos:** http://localhost:8082/movimientos

### 4. Abrir el Proyecto en Android Studio

1. Abre Android Studio
2. Selecciona "Open an Existing Project"
3. Navega a la carpeta del proyecto
4. Espera a que Gradle sincronice (puede tomar unos minutos la primera vez)

### 5. Configurar el Emulador o Dispositivo

- **Emulador:** Crea un emulador Android (API 26 o superior) desde AVD Manager
- **Dispositivo físico:** Activa "Opciones de desarrollador" y "Depuración USB"

### 6. Ejecutar la App

1. Selecciona tu dispositivo/emulador en la barra superior
2. Haz clic en el botón "Run" (▶️) o presiona `Shift + F10`
3. La app se instalará y abrirá automáticamente

### 7. Iniciar Sesión

Usa alguna de estas credenciales (creadas automáticamente al iniciar el User Service):

- Usuario: `admin` / Contraseña: `admin`
- Usuario: `usuario1` / Contraseña: `usuario1`
- Usuario: `usuario2` / Contraseña: `usuario2`

---

## 🧪 Pruebas Unitarias

Hemos implementado pruebas unitarias que cubren más del 80% del código lógico:

- **LoginViewModelTest** - Validaciones de login
- **ProductRepositoryTest** - Operaciones CRUD y sincronización
- **ProductMapperTest** - Conversiones de datos
- **MovementMapperTest** - Mapeo de movimientos
- **ProductoViewModelTest** - Lógica del ViewModel

### Ejecutar las Pruebas

```bash
# Todas las pruebas
.\gradlew.bat test

# Pruebas específicas
.\gradlew.bat test --tests "LoginViewModelTest"
.\gradlew.bat test --tests "ProductRepositoryTest"
```

---

## 📁 Estructura del Proyecto

```
Clases-mobile/
├── app/                          # App móvil Android
│   ├── src/main/java/.../
│   │   ├── data/                 # Capa de datos
│   │   │   ├── local/           # Room Database
│   │   │   ├── remote/          # APIs Retrofit
│   │   │   ├── repository/      # Repositorios
│   │   │   └── mapper/          # Mappers de datos
│   │   ├── domain/              # Modelos de dominio
│   │   ├── ui/                  # UI con Compose
│   │   │   └── viewmodel/       # ViewModels MVVM
│   │   └── util/                # Utilidades
│   └── src/test/                # Pruebas unitarias
│
└── backend/                      # Microservicios Spring Boot
    ├── user-service/            # Servicio de usuarios
    ├── products-service/        # Servicio de productos
    └── inventory-service/       # Servicio de inventario
```

---

## 🎯 Características Técnicas

### Arquitectura

- **MVVM (Model-View-ViewModel)** - Separación clara de responsabilidades
- **Repository Pattern** - Abstracción de fuentes de datos
- **Offline-First** - Funciona sin conexión, sincroniza después

### Base de Datos

- **Room (SQLite)** - Base de datos local persistente
- **H2** - Base de datos para microservicios (puede ser persistente o en memoria)

### Sincronización

- Los productos se sincronizan automáticamente con el backend
- Los movimientos se registran tanto local como remoto
- Productos "EXT-*" (del catálogo web) se guardan solo localmente

---

## 🐛 Troubleshooting

### El backend no responde

- Verifica que los 3 servicios estén corriendo en sus puertos (8081, 8082, 8083)
- Revisa los logs en las terminales donde corriste los servicios
- Asegúrate de que no haya conflictos de puertos

### La app no se conecta al backend

- Si usas emulador: las URLs usan `10.0.2.2` que es el localhost del host
- Si usas dispositivo físico: cambia las URLs a la IP de tu PC en `RetrofitClient.kt`
- Verifica que el dispositivo y la PC estén en la misma red WiFi

### Las imágenes no cargan

- Verifica permisos de Internet en `AndroidManifest.xml`
- Revisa que las URLs de imágenes sean accesibles
- Los productos "EXT-*" descargan las imágenes automáticamente

---

## 📝 Notas Importantes

- Los productos con prefijo "EXT-" son importados del catálogo web y no se sincronizan con el backend al editar
- Los productos normales se sincronizan completamente
- Los movimientos siempre se sincronizan, incluso para productos "EXT-*"
- El backend crea automáticamente 5 productos de ejemplo al iniciar el Products Service

---

## 👨‍💻 Desarrollo

Este proyecto fue desarrollado como parte de la **Evaluación Parcial 3** del curso de Desarrollo de Aplicaciones Móviles (DSY1105).

### Cobertura de Pruebas

- Más del 80% del código lógico está cubierto por pruebas unitarias
- Pruebas implementadas con JUnit 5, Kotest y MockK

### Colaboración

- Usamos GitHub para control de versiones
- Commits progresivos para evidenciar participación de ambos integrantes
- Trello para planificación y distribución de tareas
