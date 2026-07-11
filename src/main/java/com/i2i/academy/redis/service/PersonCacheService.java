package com.i2i.academy.redis.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.i2i.academy.redis.model.Person;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.Pipeline;

import java.util.ArrayList;
import java.util.List;

/**
 * Stores and reads {@link Person} objects in Redis.
 * Each person lives under the key {@code person:<id>} as a JSON string.
 * Writes use a pipeline so 10.000 objects are sent in a single round trip
 * instead of 10.000 blocking calls.
 */
public class PersonCacheService {

    private static final String KEY_PREFIX = "person:";

    private final JedisPooled jedis;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public PersonCacheService(JedisPooled jedis) {
        this.jedis = jedis;
    }

    /**
     * Serializes every person to JSON and writes them in one pipelined batch.
     */
    public void saveAll(List<Person> people) {
        try (Pipeline pipeline = jedis.pipelined()) {
            for (Person person : people) {
                pipeline.set(keyFor(person.getId()), toJson(person));
            }
            pipeline.sync();
        }
    }

    /**
     * Reads the persons with the given ids back from Redis.
     */
    public List<Person> findByIds(List<Integer> ids) {
        List<Person> result = new ArrayList<>(ids.size());
        for (int id : ids) {
            String json = jedis.get(keyFor(id));
            if (json != null) {
                result.add(fromJson(json));
            }
        }
        return result;
    }

    /**
     * Number of person entries currently stored in Redis.
     */
    public long count() {
        return jedis.keys(KEY_PREFIX + "*").size();
    }

    private String keyFor(int id) {
        return KEY_PREFIX + id;
    }

    private String toJson(Person person) {
        try {
            return objectMapper.writeValueAsString(person);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to serialize person " + person.getId(), e);
        }
    }

    private Person fromJson(String json) {
        try {
            return objectMapper.readValue(json, Person.class);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to deserialize person from: " + json, e);
        }
    }
}
