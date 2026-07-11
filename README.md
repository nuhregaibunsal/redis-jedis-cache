# i2i Academy — Redis Caching with Java

A Java application that demonstrates the fundamentals of an **in-memory data store**.
It runs **Redis** and **RedisInsight** with Docker, then uses a Java client to insert and
retrieve dummy `Person` objects.

> 🚧 Work in progress — build and usage instructions are added as the project grows.

## Start the infrastructure

```bash
docker compose up -d
```

| Service       | Image                          | Port   | URL                       |
|---------------|--------------------------------|--------|---------------------------|
| Redis         | `redis:latest`                 | `6379` | —                         |
| RedisInsight  | `redis/redisinsight:latest`    | `5540` | http://localhost:5540     |
