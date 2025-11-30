# Guía de Ejecución del Proyecto Choco App

Esta guía te ayudará a iniciar tanto el backend como la aplicación Android, y a solucionar problemas comunes de visualización del dispositivo en Android Studio.

## 1. Iniciando el Backend (Microservicios)

Para que la aplicación funcione, necesitas ejecutar los 3 microservicios simultáneamente. Puedes hacerlo abriendo 3 terminales diferentes (en Android Studio o en tu sistema operativo):

### Terminal 1: Products Service
```bash
./gradlew :backend:products-service:bootRun
```
*Esperar a que diga: `Started ProductsApplication in ... seconds` (Puerto 8081)*

### Terminal 2: Inventory Service
```bash
./gradlew :backend:inventory-service:bootRun
```
*Esperar a que diga: `Started InventoryApplication in ... seconds` (Puerto 8082)*

### Terminal 3: User Service
```bash
./gradlew :backend:user-service:bootRun
```
*Esperar a que diga: `Started UserApplication in ... seconds` (Puerto 8083)*

> **Nota:** Si ves un error de "Address already in use", asegúrate de detener cualquier proceso anterior en esos puertos.

### Verificación
Ahora puedes abrir tu navegador y verificar que los servicios responden (ya no debería salir error 404):
- http://localhost:8081/actuator/health
- http://localhost:8082/actuator/health
- http://localhost:8083/actuator/health

Deberías ver: `{"status":"UP"}`.

---

## 2. Iniciando la Aplicación Android

El problema de "no ver el running device" suele ser de configuración en Android Studio. Sigue estos pasos:

### Paso A: Verificar el Dispositivo Virtual (AVD)
1.  En Android Studio, ve al menú **Tools** > **Device Manager**.
2.  Deberías ver una lista de dispositivos. Si está vacía, haz clic en **Create Device**, elige un "Phone" (ej. Pixel 6) y sigue los pasos para descargar una imagen de sistema (recomendado API 34 o 35).
3.  Haz clic en el botón de **Play** (triángulo verde) al lado de tu dispositivo en el Device Manager.
4.  Espera a que el emulador se abra y cargue completamente la pantalla de inicio de Android.

### Paso B: Configurar la Ejecución
1.  Mira la barra de herramientas superior de Android Studio.
2.  Asegúrate de que el módulo seleccionado sea **app**.
3.  Asegúrate de que el dispositivo seleccionado sea el emulador que acabas de abrir (ej. "Pixel 6 API 34").
    *   Si dice "No devices", espera un momento o reinicia el ADB (`Tools` > `Troubleshoot Device Connections`).

### Paso C: Ejecutar la App
1.  Haz clic en el botón **Run 'app'** (triángulo verde en la barra superior) o presiona `Shift + F10`.
2.  Abre la pestaña **Run** en la parte inferior para ver los logs de instalación.
3.  La app debería abrirse en el emulador.

---

## 3. Solución de Problemas Comunes

### "Connection Refused" (Error de Red)
Si la app no conecta con el backend:
-   **Emulador:** Usa `http://10.0.2.2:8081` (ya configurado en el código).
-   **Dispositivo Físico:** Necesitas cambiar la IP en `RetrofitClient.kt` por la IP local de tu PC (ej. `192.168.1.X`) y asegurarte de que ambos estén en la misma red Wi-Fi.

### "No puedo ver el Running Device"
Si el emulador está abierto pero Android Studio no lo detecta:
1.  Ve a **File** > **Settings** > **Languages & Frameworks** > **Android SDK** > **SDK Tools**.
2.  Asegúrate de que "Android Emulator" y "Android SDK Platform-Tools" estén marcados y actualizados.
3.  Intenta reiniciar el servidor ADB:
    -   Abre la terminal en Android Studio.
    -   Ejecuta: `adb kill-server`
    -   Ejecuta: `adb start-server`
