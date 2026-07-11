# i2i Academy — Redis Caching with Java

A small Java application that demonstrates the fundamentals of an **in-memory data store**.
It spins up **Redis** and **RedisInsight** with Docker, then uses a Java client
([Jedis](https://github.com/redis/jedis)) to insert **10,000** dummy `Person` objects into
Redis and read them back to verify the round trip.

## Theoretical Background

**What is Redis, and why is it so fast compared to relational databases?**
Redis is an in-memory key-value data store that keeps its entire dataset in RAM and serves
requests with simple, single-threaded, non-blocking operations. Because it avoids the disk
I/O, query planning, and locking overhead of a traditional relational database, most
operations complete in microseconds.

**Redis cache vs. a plain Java `HashMap`?**
A `HashMap` lives inside a single JVM process, so its data is lost when the app stops and
cannot be shared with other services. Redis runs as a separate server that many applications
can share, survives restarts through persistence, and adds features like key expiration
(TTL), atomic operations, and clustering.

## Project Structure

```
.
├── docker-compose.yml                # Redis + RedisInsight containers
├── pom.xml                           # Maven build with Jedis + Jackson
└── src/main/java/com/i2i/academy/redis/
    ├── Application.java               # Entry point: insert + verify
    ├── config/RedisConfig.java        # Host/port (env-overridable)
    ├── model/Person.java              # Domain object (id, name, age)
    └── service/
        ├── PersonFactory.java         # Generates dummy people
        └── PersonCacheService.java    # Pipelined save + read from Redis
```

## Requirements

- Docker & Docker Compose
- JDK 17+
- Maven 3.8+

## 1. Start Redis and RedisInsight

```bash
docker compose up -d
```

This starts two containers:

| Service       | Image                          | Port   | URL                       |
|---------------|--------------------------------|--------|---------------------------|
| Redis         | `redis:latest`                 | `6379` | —                         |
| RedisInsight  | `redis/redisinsight:latest`    | `5540` | http://localhost:5540     |

Open http://localhost:5540 and add a database with host `redis` (or `host.docker.internal`)
and port `6379` to browse the keys through the GUI.

## 2. Run the Java Client

```bash
mvn clean compile exec:java
```

Expected output:

```
Connecting to Redis at localhost:6379
PING -> PONG
Generating and inserting 10,000 dummy people...
Inserted 10,000 people in 120 ms.
Retrieved 10,000 people in 340 ms.
Showing first 5 as a sanity check:
  Person{id=1, name='Ada Yilmaz', age=34}
  ...
Total person entries in Redis: 10000
```

The connection can be pointed elsewhere with the `REDIS_HOST` and `REDIS_PORT`
environment variables.

## Implementation Notes

- People are stored as JSON strings under the key pattern `person:<id>` (serialized with Jackson).
- Inserts use a **Jedis pipeline**, so all 10,000 writes are flushed in a single network
  round trip instead of 10,000 blocking calls — the reason the insert stays in the
  low hundreds of milliseconds.
