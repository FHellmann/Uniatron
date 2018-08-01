package com.edu.uni.augsburg.uniatron.domain.dao.model;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class TimeCreditTest {

    @Test
    public void getAll() {
        assertThat((int) TimeCredit.getAll().count(), is(TimeCredits.values().length));
    }
}