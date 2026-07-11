package com.i2i.academy.redis;

import com.i2i.academy.redis.config.RedisConfig;
import com.i2i.academy.redis.model.Person;
import com.i2i.academy.redis.service.PersonCacheService;
import com.i2i.academy.redis.service.PersonFactory;
import redis.clients.jedis.JedisPooled;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Entry point: inserts 10.000 dummy Person objects into Redis and reads them
 * back to verify the round trip.
 */
public class Application {

    private static final int PERSON_COUNT = 10_000;
    private static final int SAMPLE_SIZE = 5;

    public static void main(String[] args) {
        RedisConfig config = RedisConfig.fromEnvironment();
        System.out.printf("Connecting to Redis at %s:%d%n", config.getHost(), config.getPort());

        try (JedisPooled jedis = new JedisPooled(config.getHost(), config.getPort())) {
            System.out.println("PING -> " + jedis.ping());

            PersonCacheService cacheService = new PersonCacheService(jedis);

            insertPeople(cacheService);
            verifyPeople(cacheService);

            System.out.printf("Total person entries in Redis: %d%n", cacheService.count());
        }
    }

    private static void insertPeople(PersonCacheService cacheService) {
        System.out.printf("Generating and inserting %,d dummy people...%n", PERSON_COUNT);
        List<Person> people = PersonFactory.createDummies(PERSON_COUNT);

        long start = System.currentTimeMillis();
        cacheService.saveAll(people);
        long elapsed = System.currentTimeMillis() - start;

        System.out.printf("Inserted %,d people in %d ms.%n", PERSON_COUNT, elapsed);
    }

    private static void verifyPeople(PersonCacheService cacheService) {
        List<Integer> ids = IntStream.rangeClosed(1, PERSON_COUNT)
                .boxed()
                .collect(Collectors.toList());

        long start = System.currentTimeMillis();
        List<Person> retrieved = cacheService.findByIds(ids);
        long elapsed = System.currentTimeMillis() - start;

        System.out.printf("Retrieved %,d people in %d ms.%n", retrieved.size(), elapsed);
        System.out.printf("Showing first %d as a sanity check:%n", SAMPLE_SIZE);
        retrieved.stream()
                .limit(SAMPLE_SIZE)
                .forEach(person -> System.out.println("  " + person));
    }
}
