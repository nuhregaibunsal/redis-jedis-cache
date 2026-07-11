package com.i2i.academy.redis.service;

import com.i2i.academy.redis.model.Person;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Generates dummy {@link Person} objects for the caching demo.
 */
public final class PersonFactory {

    private static final String[] FIRST_NAMES = {
            "Ada", "Bora", "Ceren", "Deniz", "Ege", "Fatma", "Gokhan", "Hazal",
            "Irmak", "Jale", "Kerem", "Leyla", "Mert", "Nazli", "Onur", "Pelin"
    };

    private static final String[] LAST_NAMES = {
            "Yilmaz", "Kaya", "Demir", "Sahin", "Celik", "Yildiz", "Aydin", "Ozturk"
    };

    private PersonFactory() {
    }

    /**
     * Builds {@code count} dummy people with ids from 1 to {@code count}.
     */
    public static List<Person> createDummies(int count) {
        List<Person> people = new ArrayList<>(count);
        for (int id = 1; id <= count; id++) {
            people.add(new Person(id, randomName(), randomAge()));
        }
        return people;
    }

    private static String randomName() {
        String first = FIRST_NAMES[ThreadLocalRandom.current().nextInt(FIRST_NAMES.length)];
        String last = LAST_NAMES[ThreadLocalRandom.current().nextInt(LAST_NAMES.length)];
        return first + " " + last;
    }

    private static int randomAge() {
        return ThreadLocalRandom.current().nextInt(18, 80);
    }
}
