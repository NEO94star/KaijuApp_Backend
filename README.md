# Kaiju Backend

API REST de inventario en Spring Boot para PostgreSQL/Amazon Aurora RDS.

No contiene código ni dependencias del frontend Android. Las tablas se crean en el esquema
aislado `kaiju_app`, por lo que no interfieren con otras bases de datos que compartan la
misma instancia.

## Configuración

Elastic IP configurada: `184.192.5.101`

URL pública del backend:

```text
http://184.192.5.101:8080
```

En Android usa esa URL mientras se pruebe HTTP:

```text
BASE_URL="http://184.192.5.101:8080/"
```

La instancia incluye un `.env` local con los datos reales de Aurora. Este archivo está
incluido en `.gitignore`. Mantén permisos restrictivos:

```bash
cp .env.example .env
chmod 600 .env
```

Ejecutar:

```bash
./run.sh
```

También puedes ejecutar con Docker:

```bash
docker compose up --build
```

## Endpoints

| Método | Ruta | Descripción |
|---|---|---|
| GET | `/api/productos` | Lista ordenada por nombre |
| GET | `/api/productos/{id}` | Obtiene un producto |
| POST | `/api/productos` | Crea producto y registra entrada |
| PUT | `/api/productos/{id}` | Actualiza producto |
| DELETE | `/api/productos/{id}` | Elimina producto y sus movimientos |
| POST | `/api/productos/{id}/ventas` | Descuenta stock de forma transaccional y registra venta |
| GET | `/api/productos/{id}/movimientos` | Lista movimientos del producto |
| GET | `/api/sucursales` | Lista sucursales registradas |
| GET | `/api/productos?sucursal=Central` | Filtra productos por sucursal |
| GET | `/api/sucursales/{sucursal}/stock-bajo` | Revisa stock bajo por sucursal |
| GET | `/api/sincronizacion` | Estado de sincronización de sucursales |
| GET | `/api/sincronizacion/{sucursal}` | Estado de una sucursal |
| POST | `/api/sincronizacion/{sucursal}/revisar` | Registra revisión manual o automática |
| POST | `/api/sincronizacion/lote` | Recibe las operaciones pendientes guardadas en Room |

Ejemplo de creación por sucursal:

```bash
curl -X POST http://localhost:8080/api/productos \
  -H 'Content-Type: application/json' \
  -d '{"nombre":"Godzilla","categoria":"Figura","precio":19990,"cantidadDisponible":10,"stockMinimo":5,"sucursal":"Central","imagenUri":null}'
```

Consultar sucursales y stock:

```bash
curl http://localhost:8080/api/sucursales
curl 'http://localhost:8080/api/productos?sucursal=Central'
curl http://localhost:8080/api/sucursales/Central/stock-bajo
curl http://localhost:8080/api/sincronizacion
```

Estados de sincronización:

- `DISPONIBLE`: la sucursal tiene una actualización registrada dentro de los últimos 31 días.
- `NO_DISPONIBLE`: han pasado 31 días desde la última actualización registrada.

La aplicación Android puede almacenar sus cambios locales en Room y enviarlos una vez al día
a las 16:30 mediante el endpoint de lote. El backend aplicará todas las operaciones dentro
de una transacción y actualizará la fecha de última sincronización.

## Swagger

- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- Especificación OpenAPI JSON: `http://localhost:8080/api-docs`

Ejemplo de creación:

```bash
curl -X POST http://localhost:8080/api/productos \
  -H 'Content-Type: application/json' \
  -d '{"nombre":"Godzilla","categoria":"Figura","precio":19990,"cantidadDisponible":10,"stockMinimo":5,"imagenUri":null}'
```

Ejemplo de venta:

```bash
curl -X POST http://localhost:8080/api/productos/{id}/ventas \
  -H 'Content-Type: application/json' \
  -d '{"cantidad":2}'
```

Importa [la colección Postman](postman/KaijuBackend.postman_collection.json) para probar
todos los endpoints. El request `Crear producto` guarda automáticamente el ID generado
en la variable `productoId`.
