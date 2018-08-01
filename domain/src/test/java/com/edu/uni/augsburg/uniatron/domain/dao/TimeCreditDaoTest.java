package com.edu.uni.augsburg.uniatron.domain.dao;

import com.edu.uni.augsburg.uniatron.domain.QueryProvider;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

public class TimeCreditDaoTest {

    @Test
    public void create() {
        final QueryProvider queryProvider = mock(QueryProvider.class);
        final TimeCreditDao timeCreditDao = TimeCreditDao.create(queryProvider);
        assertThat(timeCreditDao, is(notNullValue()));
    }
}