package com.edu.uni.augsburg.uniatron.domain.migration;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.migration.Migration;
import android.database.SQLException;
import android.support.annotation.NonNull;

import com.orhanobut.logger.Logger;

/**
 * Migration from version 1 to version 2.
 *
 * @author Fabio Hellmann
 */
class Migration1To2 extends Migration {
    Migration1To2() {
        super(1, 2);
    }

    @Override
    public void migrate(@NonNull final SupportSQLiteDatabase database) {
        try {
            database.beginTransaction();

            // Table AppUsageTable:
            // Rename column usage_time_in_seconds to usage_time
            // Update usage_time_in_seconds values to milliseconds in usage_time
            database.execSQL("DROP INDEX `index_AppUsageEntity_app_name`");
            database.execSQL("ALTER TABLE AppUsageEntity RENAME TO AppUsageEntityOld");
            database.execSQL("CREATE TABLE IF NOT EXISTS `AppUsageEntity` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `app_name` TEXT, "
                    + "`timestamp` INTEGER, `usage_time` INTEGER NOT NULL)");
            database.execSQL("CREATE INDEX `index_AppUsageEntity_app_name` ON `AppUsageEntity` (`app_name`)");
            database.execSQL("INSERT INTO AppUsageEntity SELECT id, app_name, timestamp, usage_time_in_seconds * 1000 FROM AppUsageEntityOld");
            database.execSQL("DROP TABLE AppUsageEntityOld");

            // Table TimeCreditTable:
            // Rename column time_in_minutes to time_bonus
            // Update time_in_minutes values to milliseconds in time_bonus
            database.execSQL("ALTER TABLE TimeCreditEntity RENAME TO TimeCreditEntityOld");
            database.execSQL("CREATE TABLE IF NOT EXISTS `TimeCreditEntity` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                    + "`timestamp` INTEGER, `time_bonus` INTEGER NOT NULL, `steps` INTEGER NOT NULL, `type` TEXT)");
            database.execSQL("INSERT INTO TimeCreditEntity SELECT id, timestamp, time_in_minutes * 60 * 1000, steps, type "
                    + "FROM TimeCreditEntityOld");
            database.execSQL("DROP TABLE TimeCreditEntityOld");

            database.setTransactionSuccessful();
        } catch (SQLException e) {
            Logger.e(e, "Unable to migrate database from " + startVersion + " to " + endVersion);
        } finally {
            database.endTransaction();
        }
    }
}
