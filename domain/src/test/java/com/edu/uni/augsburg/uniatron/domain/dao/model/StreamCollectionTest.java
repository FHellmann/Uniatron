package com.edu.uni.augsburg.uniatron.domain.dao.model;

import com.annimon.stream.Collectors;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class StreamCollectionTest {

    @Test
    public void isEmpty() {
        final DataCollection<Object> collection = new StreamCollection<>(Collections.emptyList());
        assertThat(collection.isEmpty(), is(true));
    }

    @Test
    public void getEntries() {
        final DataCollection<String> collection = new StreamCollection<>(Arrays.asList("Test", "bla", "da"));
        assertThat(collection.getEntries().collect(Collectors.toList()), hasItems("Test", "bla", "da"));
    }
}