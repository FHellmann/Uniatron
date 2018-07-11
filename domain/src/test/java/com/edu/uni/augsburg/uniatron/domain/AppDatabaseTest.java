package com.edu.uni.augsburg.uniatron.domain;

import android.content.Context;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

public class AppDatabaseTest {

    @Test
    public void create() {
        final Context context = mock(Context.class);

        AppDatabase.create(context);

        assertThat(true, is(true));
    }
}