package com.edu.uni.augsburg.uniatron.domain.migration;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.migration.Migration;
import android.support.annotation.NonNull;

/**
 * Migrates from version 1 to version 2.
 *
 * @author Fabio Hellmann
 */
public class MigrationV1 extends Migration {
    /**
     * Creates a new migration between 1 and 2.
     */
    public MigrationV1() {
        super(1, 2);
    }

    @Override
    public void migrate(@NonNull SupportSQLiteDatabase database) {

    }
}
