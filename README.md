# Smart Campus API (5COSC022W Coursework)

This repository contains a JAX-RS REST API for the Smart Campus scenario (Rooms, Sensors, and Sensor Readings), implemented as a Maven WAR and deployed to Apache Tomcat 9.

## Technology Stack

- Java 11
- Maven
- JAX-RS (Jersey 2.x, `javax.*`)
- Apache Tomcat 9.0.100
- In-memory storage only (`ConcurrentHashMap`, `ArrayList`) - no database

---

## How to Build and Run

## Option A: Apache NetBeans (recommended for this coursework)

1. Open the `smart-campus-api` folder in NetBeans.
2. Add/configure Apache Tomcat 9 server in NetBeans.
3. Set project Run server to that Tomcat instance.
4. Run **Clean and Build**.
5. Run project.

Base URL (typical):

`http://localhost:8080/smart-campus-api/api/v1`

## Option B: Maven + standalone Tomcat

```bash
mvn clean package
```

Deploy `target/smart-campus-api.war` into Tomcat `webapps/`, then start Tomcat.

---

## API Endpoints

- `GET /api/v1` - discovery metadata
- `GET /api/v1/rooms`
- `POST /api/v1/rooms`
- `GET /api/v1/rooms/{roomId}`
- `DELETE /api/v1/rooms/{roomId}`
- `GET /api/v1/sensors`
- `POST /api/v1/sensors`
- `GET /api/v1/sensors/{sensorId}`
- `GET /api/v1/sensors/{sensorId}/readings`
- `POST /api/v1/sensors/{sensorId}/readings`

---

## Sample cURL Commands (submission requirement)

1) Discovery
```bash
curl -X GET http://localhost:8080/smart-campus-api/api/v1
```

2) List rooms
```bash
curl -X GET http://localhost:8080/smart-campus-api/api/v1/rooms
```

3) Create room
```bash
curl -X POST http://localhost:8080/smart-campus-api/api/v1/rooms \
  -H "Content-Type: application/json" \
  -d "{\"id\":\"ENG-201\",\"name\":\"Engineering Workshop\",\"capacity\":40}"
```

4) Create sensor (valid room)
```bash
curl -X POST http://localhost:8080/smart-campus-api/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d "{\"id\":\"TEMP-999\",\"type\":\"Temperature\",\"status\":\"ACTIVE\",\"currentValue\":22.1,\"roomId\":\"ENG-201\"}"
```

5) Filter sensors
```bash
curl -X GET "http://localhost:8080/smart-campus-api/api/v1/sensors?type=Temperature"
```

6) Add reading
```bash
curl -X POST http://localhost:8080/smart-campus-api/api/v1/sensors/TEMP-999/readings \
  -H "Content-Type: application/json" \
  -d "{\"value\":23.4}"
```

7) Linked resource validation (422)
```bash
curl -X POST http://localhost:8080/smart-campus-api/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d "{\"id\":\"BAD-001\",\"type\":\"CO2\",\"status\":\"ACTIVE\",\"currentValue\":400,\"roomId\":\"NO-ROOM\"}"
```

## Compliance Notes

- Uses JAX-RS only (no Spring Boot)
- Uses in-memory storage only (no SQL/NoSQL database)
- Suitable for Tomcat 9 deployment used in lectures
