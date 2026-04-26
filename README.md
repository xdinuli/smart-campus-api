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

---

## Report Answers (as required by specification)

## Part 1: Setup and Discovery

### Q1.1 - Resource lifecycle and thread safety
By default, JAX-RS resource classes are request-scoped (new resource instance per request).  
Because resources are stateless, shared data is kept in a singleton `DataStore`.  
To avoid race conditions under concurrent requests, thread-safe structures (`ConcurrentHashMap`) are used.

### Q1.2 - Why hypermedia links are useful
Hypermedia links let clients discover API paths from responses (for example rooms and sensors links in discovery).  
This reduces reliance on hardcoded URLs and static documentation.

## Part 2: Room Management

### Q2.1 - IDs only vs full room objects in list responses
- IDs only: smaller payload but requires extra client requests for details.
- Full objects: larger payload but easier and faster for clients to use directly.

### Q2.2 - Is DELETE idempotent?
Yes. Repeating the same DELETE request leaves the same final state (resource removed).  
In this API, deleting a non-existing room returns `204 No Content`, keeping the operation idempotent.

## Part 3: Sensors and Filtering

### Q3.1 - Effect of `@Consumes(MediaType.APPLICATION_JSON)`
If a client sends a different media type (for example `text/plain`), JAX-RS can reject it with `415 Unsupported Media Type`.  
So `@Consumes` enforces the request contract.

### Q3.2 - Why query param is better for filtering
`?type=...` is optional filtering of a collection, which is what query parameters are designed for.  
Path parameters are better for identifying specific resources.

## Part 4: Sub-resources

### Q4.1 - Benefit of Sub-Resource Locator pattern
It separates nested logic (`/sensors/{id}/readings`) into its own class, reducing controller complexity and improving maintainability.

### Q4.2 - Historical readings and consistency
`SensorReadingResource` supports reading history (`GET`) and appending readings (`POST`).  
On successful POST, the parent sensor `currentValue` is updated so data stays consistent.

## Part 5: Error handling and logging

### Q5.1 - Why 422 is more accurate than 404 for missing linked roomId
`404` usually means requested URL resource not found.  
Here, endpoint exists, but payload contains invalid linked data (`roomId`), so `422 Unprocessable Entity` is more precise.

### Q5.2 - Risk of exposing stack traces
Stack traces may reveal internal class names, package structure, and implementation details, which can help attackers.

### Q5.3 - Why filters for logging
Filters centralize cross-cutting concerns (request/response logging) in one place instead of duplicating logging code in every endpoint.

---

## Video Demonstration Checklist (Postman)

1. `GET /api/v1` (show metadata)
2. `POST /rooms` -> show `201 Created` and `Location` header
3. `GET /rooms/{id}` (confirm created room)
4. `DELETE /rooms/{emptyRoom}` -> `204`
5. `DELETE /rooms/{roomWithSensors}` -> `409`
6. `POST /sensors` with invalid `roomId` -> `422`
7. `POST /sensors` with valid `roomId` -> `201`
8. `GET /sensors?type=...` (filtered output)
9. `POST /sensors/{id}/readings` -> `201`
10. `GET /sensors/{id}` (show `currentValue` updated)
11. `POST /sensors/{maintenanceId}/readings` -> `403`
12. Trigger unexpected error path -> show clean `500` JSON (no stack trace)

---

## Compliance Notes

- Uses JAX-RS only (no Spring Boot)
- Uses in-memory storage only (no SQL/NoSQL database)
- Suitable for Tomcat 9 deployment used in lectures
