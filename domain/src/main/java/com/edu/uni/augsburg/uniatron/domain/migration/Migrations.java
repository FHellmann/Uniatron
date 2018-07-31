package com.edu.uni.augsburg.uniatron.domain.migration;

import android.arch.persistence.room.migration.Migration;

import com.annimon.stream.Stream;

/**
 * Migrates the database versions.
 *
 * @author Fabio Hellmann
 */
public enum Migrations {
    /**
     * The migration from version 1 to version 2.
     */
    V1_TO_V2(new Migration1To2());

    private final Migration mMigration;

    Migrations(final Migration migration) {
        mMigration = migration;
    }

    /**
     * Get the migration.
     *
     * @return The migration.
     */
    public Migration getMigration() {
        return mMigration;
    }

    /**
     * Get all migrations.
     *
     * @return All migrations.
     */
    public static Migration[] getAll() {
        return Stream.of(values()).map(Migrations::getMigration).toArray(Migration[]::new);
    }
}
