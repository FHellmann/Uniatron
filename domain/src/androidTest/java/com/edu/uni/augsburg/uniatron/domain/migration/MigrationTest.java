package com.edu.uni.augsburg.uniatron.domain.migration;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.db.framework.FrameworkSQLiteOpenHelperFactory;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.testing.MigrationTestHelper;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;
import com.edu.uni.augsburg.uniatron.domain.AppDatabase;
import com.edu.uni.augsburg.uniatron.domain.DatabaseSource;
import com.edu.uni.augsburg.uniatron.domain.model.AppUsageEntity;
import com.edu.uni.augsburg.uniatron.domain.util.DateConverterImpl;
import com.edu.uni.augsburg.uniatron.domain.util.TestUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class)
public class MigrationTest {
    @Rule
    public MigrationTestHelper mMigrationHelper = new MigrationTestHelper(
            InstrumentationRegistry.getInstrumentation(),
            AppDatabase.class.getCanonicalName(),
            new FrameworkSQLiteOpenHelperFactory()
    );
    private Context mContext;

    @Before
    public void setUp() {
        mContext = InstrumentationRegistry.getTargetContext();
    }

    @After
    public void tearDown() {
        mContext.deleteDatabase(mContext.getPackageName());
    }

    @Test
    public void migrationFrom1To2() throws IOException, InterruptedException {
        final SupportSQLiteDatabase db = mMigrationHelper.createDatabase(mContext.getPackageName(), 1);

        final long time = new Date().getTime();
        db.execSQL("INSERT INTO AppUsageEntity (`app_name`, `timestamp`, `usage_time_in_seconds`) VALUES ('com.test.app', " + time + ", 7)");
        db.execSQL("INSERT INTO AppUsageEntity (`app_name`, `timestamp`, `usage_time_in_seconds`) VALUES ('com.test.app1', " + time + ", 91)");
        db.execSQL("INSERT INTO AppUsageEntity (`app_name`, `timestamp`, `usage_time_in_seconds`) VALUES ('com.test.app2', " + time + ", 435)");
        db.execSQL("INSERT INTO AppUsageEntity (`app_name`, `timestamp`, `usage_time_in_seconds`) VALUES ('com.test.app3', " + time + ", 12)");
        db.execSQL("INSERT INTO AppUsageEntity (`app_name`, `timestamp`, `usage_time_in_seconds`) VALUES ('com.test.app4', " + time + ", 45)");

        db.execSQL("INSERT INTO TimeCreditEntity (`timestamp`, `time_in_minutes`, `steps`, `type`) VALUES (" + time + ", 2, 3987, 'MINUTES_FOR_STEPS')");
        db.execSQL("INSERT INTO TimeCreditEntity (`timestamp`, `time_in_minutes`, `steps`, `type`) VALUES (" + time + ", 3, 76, 'MINUTES_FOR_STEPS')");
        db.execSQL("INSERT INTO TimeCreditEntity (`timestamp`, `time_in_minutes`, `steps`, `type`) VALUES (" + time + ", 10, 567, 'MINUTES_FOR_STEPS')");
        db.execSQL("INSERT INTO TimeCreditEntity (`timestamp`, `time_in_minutes`, `steps`, `type`) VALUES (" + time + ", 20, 3465, 'MINUTES_FOR_STEPS')");
        db.execSQL("INSERT INTO TimeCreditEntity (`timestamp`, `time_in_minutes`, `steps`, `type`) VALUES (" + time + ", 45, 456, 'MINUTES_FOR_STEPS')");

        db.close();

        mMigrationHelper.runMigrationsAndValidate(mContext.getPackageName(), 2, true, Migrations.V1_TO_V2.getMigration());

        final DatabaseSource migratedDb = getMigratedRoomDatabase();

        final LiveData<List<AppUsageEntity>> liveDataAppUsage = migratedDb.appUsageDao()
                .loadAppUsageTime(DateConverterImpl.DATE_MIN_TIME.convert(new Date(time)), DateConverterImpl.DATE_MAX_TIME.convert(new Date(time)));
        final List<AppUsageEntity> liveDataValueAppUsage = TestUtils.getLiveDataValue(liveDataAppUsage);
        assertThat(liveDataValueAppUsage, is(notNullValue()));
        final List<Long> usageTimeValues = Stream.of(liveDataValueAppUsage).map(AppUsageEntity::getUsageTime).collect(Collectors.toList());
        assertThat(usageTimeValues, hasItems(7 * 1000L, 91 * 1000L, 435 * 1000L, 12 * 1000L, 45 * 1000L));
    }

    private DatabaseSource getMigratedRoomDatabase() {
        final DatabaseSource database = DatabaseSource.create(mContext);
        mMigrationHelper.closeWhenFinished((RoomDatabase) database);
        return database;
    }
}