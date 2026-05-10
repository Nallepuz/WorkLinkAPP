# WorkLink 📱

Aplicación Android de gestión de turnos y solicitudes laborales desarrollada como Trabajo de Fin de Ciclo (TFC) del Ciclo Superior de Desarrollo de Aplicaciones Multiplataforma (DAM).

---

## 📋 Descripción

WorkLink es una solución móvil que permite a empleados y administradores gestionar el calendario de turnos, solicitudes de ausencia, balance laboral y comunicaciones internas de forma centralizada y en tiempo real.

---

## 🛠️ Stack Tecnológico

### Backend
- **Java 21** + **Spring Boot**
- **MariaDB** — base de datos relacional
- **AWS EC2** — servidor en la nube
- **JWT** — autenticación mediante tokens

### App Android
- **Kotlin**
- **Jetpack Compose** + **Material Design 3**
- **Retrofit** — cliente HTTP
- **DataStore Preferences** — persistencia de sesión
- **Kotlin Coroutines** + **StateFlow** — asincronía y estado reactivo
- **Arquitectura MVVM**

---

## 🚀 Funcionalidades

### Empleado
- 📅 Calendario mensual con turnos por colores
- 📝 Crear y consultar solicitudes (Vacaciones, Días Exceso, No Retribuido, Bolsa de horas, Cambio de turno)
- ⚖️ Consultar balance laboral (días de vacaciones, exceso, no retribuidos y bolsa de horas)
- 🔔 Recibir notificaciones de cambios en solicitudes y anuncios del admin
- 👤 Editar perfil personal

### Administrador
- 📅 Calendario de turnos
- ✅ Aprobar o rechazar solicitudes de empleados
- 🗓️ Asignar, modificar y eliminar turnos de empleados
- 📢 Enviar anuncios a todos los empleados
- 🔔 Recibir notificaciones

---

## 🗄️ Base de Datos

### Tablas principales
| Tabla | Descripción |
|-------|-------------|
| `users` | Usuarios de la aplicación |
| `rol` | Roles (Admin / Empleado) |
| `turn` | Tipos de turno con colores |
| `turn_assigned` | Turnos asignados por día y usuario |
| `application` | Solicitudes de los empleados |
| `application_type` | Tipos de solicitud |
| `user_balance` | Balance laboral anual por usuario |
| `notification` | Notificaciones personales y anuncios |

### Tipos de turno
| ID | Nombre | Color |
|----|--------|-------|
| 1 | Mañana | #F5CC27 |
| 2 | Tarde | #F562A4 |
| 3 | Noche | #0E8262 |
| 4 | Vacaciones | #FF8000 |
| 5 | Día Exceso | #1976D2 |
| 6 | No Retribuido | #9C27B0 |

---

## 🌐 API REST — Endpoints

### Auth
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/login` | Login y obtención de token JWT |

### Usuarios
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/users` | Listar usuarios |
| GET | `/users?email=` | Buscar usuario por email |
| PUT | `/users/{id}` | Actualizar usuario |

### Turnos
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/turns` | Listar tipos de turno |
| GET | `/assigned?user_id=` | Turnos asignados por usuario |
| POST | `/assigned` | Asignar turno |
| PUT | `/assigned/{id}` | Modificar turno asignado |
| DELETE | `/assigned/{id}` | Eliminar turno asignado |

### Solicitudes
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/application` | Listar solicitudes |
| GET | `/application?status=` | Filtrar por estado |
| GET | `/application/user/{userId}` | Solicitudes por usuario |
| GET | `/application/days/{id}` | Detalle solicitud de días |
| GET | `/application/hours/{id}` | Detalle solicitud de horas |
| GET | `/application/change/{id}` | Detalle cambio de turno |
| POST | `/application` | Crear solicitud |
| PUT | `/application/{id}` | Aprobar/rechazar solicitud |

### Balance
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/userBalances?user_id=` | Balance laboral por usuario |

### Notificaciones
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/notifications/{userId}` | Listar notificaciones |
| GET | `/notifications/{userId}/unread` | Notificaciones no leídas |
| GET | `/notifications/{userId}/count` | Contador de no leídas |
| PUT | `/notifications/{userId}/read` | Marcar todas como leídas |
| POST | `/notifications/announcement` | Enviar anuncio a todos |
| DELETE | `/notifications/single/{id}` | Eliminar notificación |
| DELETE | `/notifications/{userId}/read` | Eliminar todas las leídas |

---

## ⚙️ Despliegue del Backend en AWS EC2

### Requisitos
- Java 21 instalado en la instancia EC2
- MariaDB en ejecución
- JAR del proyecto subido a la instancia

### Comandos

**Arrancar MariaDB:**
# WorkLink 📱

Aplicación Android de gestión de turnos y solicitudes laborales desarrollada como Trabajo de Fin de Ciclo (TFC) del Ciclo Superior de Desarrollo de Aplicaciones Multiplataforma (DAM).

---

## 📋 Descripción

WorkLink es una solución móvil que permite a empleados y administradores gestionar el calendario de turnos, solicitudes de ausencia, balance laboral y comunicaciones internas de forma centralizada y en tiempo real.

---

## 🛠️ Stack Tecnológico

### Backend
- **Java 21** + **Spring Boot**
- **MariaDB** — base de datos relacional
- **AWS EC2** — servidor en la nube
- **JWT** — autenticación mediante tokens

### App Android
- **Kotlin**
- **Jetpack Compose** + **Material Design 3**
- **Retrofit** — cliente HTTP
- **DataStore Preferences** — persistencia de sesión
- **Kotlin Coroutines** + **StateFlow** — asincronía y estado reactivo
- **Arquitectura MVVM**

---

## 🚀 Funcionalidades

### Empleado
- 📅 Calendario mensual con turnos por colores
- 📝 Crear y consultar solicitudes (Vacaciones, Días Exceso, No Retribuido, Bolsa de horas, Cambio de turno)
- ⚖️ Consultar balance laboral (días de vacaciones, exceso, no retribuidos y bolsa de horas)
- 🔔 Recibir notificaciones de cambios en solicitudes y anuncios del admin
- 👤 Editar perfil personal

### Administrador
- 📅 Calendario de turnos
- ✅ Aprobar o rechazar solicitudes de empleados
- 🗓️ Asignar, modificar y eliminar turnos de empleados
- 📢 Enviar anuncios a todos los empleados
- 🔔 Recibir notificaciones

---

## 🗄️ Base de Datos

### Tablas principales
| Tabla | Descripción |
|-------|-------------|
| `users` | Usuarios de la aplicación |
| `rol` | Roles (Admin / Empleado) |
| `turn` | Tipos de turno con colores |
| `turn_assigned` | Turnos asignados por día y usuario |
| `application` | Solicitudes de los empleados |
| `application_type` | Tipos de solicitud |
| `user_balance` | Balance laboral anual por usuario |
| `notification` | Notificaciones personales y anuncios |

### Tipos de turno
| ID | Nombre | Color |
|----|--------|-------|
| 1 | Mañana | #F5CC27 |
| 2 | Tarde | #F562A4 |
| 3 | Noche | #0E8262 |
| 4 | Vacaciones | #FF8000 |
| 5 | Día Exceso | #1976D2 |
| 6 | No Retribuido | #9C27B0 |

---

## 🌐 API REST — Endpoints

### Auth
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/login` | Login y obtención de token JWT |

### Usuarios
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/users` | Listar usuarios |
| GET | `/users?email=` | Buscar usuario por email |
| PUT | `/users/{id}` | Actualizar usuario |

### Turnos
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/turns` | Listar tipos de turno |
| GET | `/assigned?user_id=` | Turnos asignados por usuario |
| POST | `/assigned` | Asignar turno |
| PUT | `/assigned/{id}` | Modificar turno asignado |
| DELETE | `/assigned/{id}` | Eliminar turno asignado |

### Solicitudes
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/application` | Listar solicitudes |
| GET | `/application?status=` | Filtrar por estado |
| GET | `/application/user/{userId}` | Solicitudes por usuario |
| GET | `/application/days/{id}` | Detalle solicitud de días |
| GET | `/application/hours/{id}` | Detalle solicitud de horas |
| GET | `/application/change/{id}` | Detalle cambio de turno |
| POST | `/application` | Crear solicitud |
| PUT | `/application/{id}` | Aprobar/rechazar solicitud |

### Balance
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/userBalances?user_id=` | Balance laboral por usuario |

### Notificaciones
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/notifications/{userId}` | Listar notificaciones |
| GET | `/notifications/{userId}/unread` | Notificaciones no leídas |
| GET | `/notifications/{userId}/count` | Contador de no leídas |
| PUT | `/notifications/{userId}/read` | Marcar todas como leídas |
| POST | `/notifications/announcement` | Enviar anuncio a todos |
| DELETE | `/notifications/single/{id}` | Eliminar notificación |
| DELETE | `/notifications/{userId}/read` | Eliminar todas las leídas |

---

## ⚙️ Despliegue del Backend en AWS EC2

### Requisitos
- Java 21 instalado en la instancia EC2
- MariaDB en ejecución
- JAR del proyecto subido a la instancia

### Comandos

**Arrancar MariaDB:**
sudo systemctl start mariadb

**Arrancar la API:**
java -jar worklink-0.0.1-SNAPSHOT.jar 
--spring.datasource.url=jdbc:mariadb://127.0.0.1:3306/APIWorkLink_prod?serverTimezone=UTC 
--spring.datasource.username=root 
--spring.datasource.password= 
--spring.jpa.hibernate.ddl-auto=validate &

**Parar la API:**
pkill -9 -f java

**Verificar que está corriendo:**
ps aux | grep java

**Conectarse a MariaDB:**
sudo mysql -u root -proot

---

## 📱 Configuración de la App Android

En `Constants.kt` actualiza la IP de la EC2 si cambia al reiniciar la instancia:
const val BASE_URL = "http://TU_IP_EC2:8081/"

---

## 👥 Usuarios de Prueba

| Nombre | Email | Contraseña | Rol |
|--------|-------|------------|-----|
| Admin | admin@worklink.com | 1234 | Administrador |
| David | dasimix@gmail.com | 1234 | Empleado |
| Jesús | jesus@worklink.com | 1234 | Empleado |
| Alberto | alberto@worklink.com | 1234 | Empleado |

---

## 🧪 Pruebas

- **299 tests de integración** con Postman cubriendo todos los endpoints principales
- **Pruebas manuales** en emulador y dispositivo físico
- Tests verifican: códigos HTTP, estructura JSON, tipos de datos y validaciones de negocio

---

## 📁 Estructura del Proyecto Android
app/
├── data/
│   ├── api/           ApiService, RetrofitClient
│   ├── model/         Modelos de datos
│   └── repository/    WorkLinkRepository
├── ui/
│   ├── admin/         AdminScreen, TurnManagementScreen
│   ├── balance/       BalanceScreen
│   ├── calendar/      CalendarScreen
│   ├── login/         LoginScreen
│   ├── notifications/ NotificationsScreen
│   ├── requests/      RequestsScreen
│   ├── account/       AccountScreen
│   └── navigation/    NavGraph, Destinations, BottomNavBar
└── utils/
└── SessionManager.kt

---

## 👨‍💻 Autor

Néstor — TFC DAM 2025/2026
