# ChocoApp - Mobile Inventory System

## Credentials / Credenciales

### Admin
- **Email/User**: `admin@pasteleria.cl`
- **Password**: `admin`
- **Role**: ADMIN (Full Access)

### Vendedor
- **Email/User**: `vendedor@pasteleria.cl`
- **Password**: `vendedor`
- **Role**: VENDEDOR (Restricted Access)

## Running the Project / Ejecución

### Backend (Microservices)
The backend consists of 3 microservices (Spring Boot).
Run them in separate terminals or using the provided Gradle wrapper:

```bash
./gradlew :backend:user-service:bootRun
./gradlew :backend:products-service:bootRun
./gradlew :backend:inventory-service:bootRun
```

**Note**: If you updated the credentials in `DataLoader.java`, please restart the `user-service`.

### Mobile App (Android)
1. Open the project in Android Studio.
2. Sync Gradle.
3. Run on an Emulator or Device (API 26+).
4. Ensure the emulator can access the backend (default `10.0.2.2` for localhost).

## Features / Funcionalidades
- **Login Strict**: Only specific emails allowed.
- **Role Based UI**: Admin sees all options; Vendedor sees only "Consulta".
- **Offline Support**: Room Database caches products.
- **Animations**: Smooth transitions and interactions.
