# Documentación del Proyecto Choco App

## 1. Arquitectura
El proyecto sigue una arquitectura **MVVM (Model-View-ViewModel)** con principios de **Clean Architecture** para garantizar la separación de responsabilidades y la escalabilidad.

- **Model (Dominio/Data):** Contiene las entidades (`Product`, `User`, `InventoryMovement`) y la lógica de acceso a datos (`ProductRepository`, `RetrofitClient`).
- **View (UI):** Implementada con **Jetpack Compose**, incluye las pantallas (`LoginScreen`, `MenuScreen`, `IngresoScreen`, `SalidaScreen`, `ConsultaScreen`) y componentes reutilizables (`ChocoButton`).
- **ViewModel (Presentación):** Gestiona el estado de la UI y la comunicación con el repositorio (`LoginViewModel`, `ProductViewModel`).

## 2. Microservicios (Spring Boot)
El backend se compone de tres microservicios independientes desarrollados en **Spring Boot**:

1.  **Products Service (Puerto 8081):** Gestiona el catálogo de productos.
    -   Endpoints: GET `/api/productos`, POST `/api/productos`, PUT `/api/productos/{id}`, DELETE `/api/productos/{id}`, POST `/api/productos/salida`.
2.  **Inventory Service (Puerto 8082):** Registra los movimientos de inventario (entradas y salidas).
    -   Endpoints: GET `/api/movimientos`, POST `/api/movimientos`.
3.  **User Service (Puerto 8083):** Gestiona la autenticación y usuarios.
    -   Endpoints: POST `/api/usuarios/login`, POST `/api/usuarios`.

## 3. Integración Backend (Retrofit)
La aplicación Android utiliza **Retrofit** para consumir los microservicios.
-   `RetrofitClient`: Configura las instancias de Retrofit para cada microservicio.
-   `ProductApiService`: Define las llamadas al servicio de productos.
-   `InventoryApiService`: Define las llamadas al servicio de inventario.
-   `UserApiService`: Define las llamadas al servicio de usuarios.

## 4. Sincronización
-   **Registro Automático de Movimientos:** Al registrar una salida de producto (`ProductViewModel.registerOutput`), el backend (`Products Service`) actualiza el stock y comunica automáticamente el movimiento al `Inventory Service` (o lo registra en su propia base de datos si es monolítico por ahora, preparado para microservicios).
-   **Validación:** Se utiliza `ProductoValidator` para asegurar la integridad de los datos antes de enviarlos al backend.

## 5. Esquema de Base de Datos (H2)
Cada microservicio utiliza una base de datos en memoria **H2** para desarrollo.
-   **Producto:** `id`, `nombre`, `precio`, `stock`, `imagenUrl`.
-   **MovimientoInventario:** `id`, `productoId`, `tipo` (ENTRADA/SALIDA), `cantidad`, `fecha`.
-   **Usuario:** `id`, `username`, `password`, `email`.

## 6. Dependencias Principales
-   **Android:**
    -   `androidx.core:core-ktx`
    -   `androidx.lifecycle:lifecycle-runtime-ktx`
    -   `androidx.activity:activity-compose`
    -   `androidx.compose.ui:ui`
    -   `androidx.compose.material3:material3`
    -   `com.squareup.retrofit2:retrofit`
    -   `com.squareup.retrofit2:converter-gson`
    -   `io.coil-kt:coil-compose`
    -   `io.mockk:mockk` (Testing)
    -   `org.jetbrains.kotlinx:kotlinx-coroutines-test` (Testing)

## 7. Instrucciones de Ejecución

### Backend
1.  Navegar a cada carpeta de servicio (`backend/products-service`, `backend/inventory-service`, `backend/user-service`).
2.  Ejecutar `./mvnw spring-boot:run` en cada una.
3.  Asegurarse de que los puertos 8081, 8082 y 8083 estén libres.

### Android
1.  Abrir el proyecto en Android Studio.
2.  Sincronizar Gradle.
3.  Ejecutar la aplicación en un emulador o dispositivo físico.
    -   **Nota:** Para emulador, la IP del backend es `10.0.2.2`. Para dispositivo físico, cambiar `BASE_URL` en `RetrofitClient` a la IP de la máquina de desarrollo.

## 8. Generación de APK
Para generar el APK firmado:
1.  Ejecutar `./gradlew assembleDebug` en la terminal.
2.  El APK se generará en `app/build/outputs/apk/debug/app-debug.apk`.
