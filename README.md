#  App Móvil - Sistema de Gestión de Inventario

##  Descripción del Proyecto

Esta aplicación móvil Android es un **sistema completo de gestión de inventario** desarrollado con kotlin.
 Permite gestionar productos desde el registro hasta la consulta y hasta compartir informacion a whatsapp

###  Características Principales

- **Sistema de autenticación** con credenciales 
- **Gestión completa de productos** (agregar, editar, eliminar)
- **Búsqueda inteligente** de productos por nombre
- **Integración con cámara** para fotos de productos
- **Compartir información** vía WhatsApp
- **Almacenamiento local** con base de datos SQLite
- **Interfaz moderna** con Material Design

---

##  Tecnologías Utilizadas

### **¿Por qué elegimos estas tecnologías?**

####  **Jetpack Compose**
```kotlin
@Composable
fun LoginScreen() {
    // UI declarativa y moderna
}
```
**¿Por qué Compose?**
- **Código más limpio**: 50% menos código que XML tradicional
- **Desarrollo más rápido**: UI declarativa, menos bugs
- **Mejor rendimiento**: Solo redibuja lo que cambia
- **Futuro de Android**: Tecnología oficial de Google

####  **Room Database**
```kotlin
@Entity(tableName = "productos")
data class Producto(
    @PrimaryKey val id: String,
    val nombre: String
)
```
**¿Por qué Room?**
- **Type-safe**: Sin errores de SQL en tiempo de compilación
- **Integración perfecta**: Funciona nativamente con LiveData
- **Fácil mantenimiento**: Migraciones automáticas
- **Mejor que SQLite directo**: Menos código, más funcionalidades

#### **Arquitectura MVVM**
```
UI (Compose) ↔ ViewModel ↔ Room Database
```
**¿Por qué MVVM?**
- **Separación clara**: UI, lógica y datos separados
- **Fácil testing**: Cada capa se prueba independientemente
- **Mantenimiento**: Código organizado y escalable
- **Patrón estándar**: Recomendado por Google

####  **LiveData**
```kotlin
val productos: LiveData<List<Producto>> = productoDao.getAllProductos()
```
**¿Por qué LiveData?**
- **Reactividad automática**: UI se actualiza sola
- **Manejo de ciclo de vida**: Previene crashes
- **Sin memory leaks**: Se limpia automáticamente
- **Integración perfecta**: Con Room y ViewModel

---

##  Funcionalidades de la Aplicación

###  **Sistema de Autenticación**
- Login con credenciales
- Navegación automática tras autenticación exitosa

###  **Pantalla Principal**
- Menú intuitivo con 3 opciones
- Navegación clara entre secciones


###  **Gestión de Productos**
- **Lista completa**: Visualización de todos los productos
- **Eliminación segura**: Confirmación antes de eliminar
- **Actualización automática**: Lista se actualiza

###  **Sistema de Búsqueda**
- **Búsqueda inteligente**: Filtrado por nombre de producto o el ID
- **Resultados en tiempo real**: Actualización 
- **Navegación a detalles**: ver información completa

###  **Registro de Productos**
- **Formulario completo**: ID, nombre, descripción, precio, cantidad
- **Integración con cámara**: Fotos de productos
- **Validación de datos**: Verificación antes de guardar
- **Modo edición**: Modificar productos 

###  **Detalles de Producto**
- **Información completa**: Todos los datos del producto
- **Visualización de fotos**: Imágenes guardadas
- **Compartir vía WhatsApp**: Envío de información 

---


### **Material Design 3**
- **Componentes modernos**: Botones, campos de texto, tarjetas
- **Animaciones fluidas**: Transiciones 
- **Responsive**: Se adapta a diferentes tamaños de pantalla

---
