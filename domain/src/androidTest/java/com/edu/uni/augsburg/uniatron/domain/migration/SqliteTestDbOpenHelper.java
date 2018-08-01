package com.edu.uni.augsburg.uniatron.domain.migration;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SqliteTestDbOpenHelper extends SQLiteOpenHelper {
    SqliteTestDbOpenHelper(final Context context, final String name) {
        super(context, name, null, 1);
    }

    @Override
    public void onCreate(final SQLiteDatabase sqLiteDatabase) {
        // ignore
    }

    @Override
    public void onUpgrade(final SQLiteDatabase sqLiteDatabase, final int i, final int i1) {
        // ignore
    }
}
