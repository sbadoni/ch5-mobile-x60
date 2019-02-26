package com.crestron.itemattribute.db;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.crestron.itemattribute.db.dao.AttributeDao;
import com.crestron.itemattribute.db.dao.ItemDao;
import com.crestron.itemattribute.db.tables.AttributeEntity;
import com.crestron.itemattribute.db.tables.ItemEntity;

/**
 * The main database class that puts {@link ItemEntity} and {@link AttributeEntity} objects into a database via {@link AttributeDao}
 */
@Database(entities =
        {ItemEntity.class, AttributeEntity.class},
        version = 2,
        exportSchema = false)
public abstract class ItemAttrDB extends RoomDatabase {
    private static final String TAG = ItemAttrDB.class.getSimpleName();

    /**
     * @return the {@link ItemDao} (Data Access Object)
     */
    public abstract ItemDao getItemDao();

    /**
     * @return the {@link AttributeDao} (Data Access Object)
     */
    public abstract AttributeDao getAttrDao();

    /**
     * The instance of {@link ItemAttrDB}.
     * We declare it as volatile to store it in computer's main memory, and not in the CPU cache.
     */
    private static ItemAttrDB sInstance;

    private static String sDbName;

    /**
     * @param context    - Context
     * @param memoryOnly - not sure yet
     * @return the instance of {@link ItemAttrDB}
     */
    public static ItemAttrDB createInstance(final Context context, boolean memoryOnly, String dbName) {
        if (memoryOnly) {
            sInstance = Room.inMemoryDatabaseBuilder(context, ItemAttrDB.class)
                    .allowMainThreadQueries()
                    .addCallback(ROOM_DATABASE_CALLBACK)
                    .build();
        } else {
            sInstance = Room.databaseBuilder(context.getApplicationContext(),
                    ItemAttrDB.class, dbName)
                    .fallbackToDestructiveMigration()
                    .addCallback(ROOM_DATABASE_CALLBACK)
                    .build();
        }

        return sInstance;
    }

    /**
     * Clears the instance
     */
    public static void clearInstance() {
        if (sInstance != null && sInstance.isOpen()) {
            sInstance.close();
            sInstance = null;
        }
    }

    public static ItemAttrDB getInstance(Context ctxt, boolean memoryOnly, String dbName) {
        synchronized (ItemAttrDB.class) {
            sInstance = createInstance(ctxt, memoryOnly, dbName);
        }
        return (sInstance);
    }

    private static final RoomDatabase.Callback ROOM_DATABASE_CALLBACK =
            new RoomDatabase.Callback() {
                @Override
                public void onOpen(@NonNull SupportSQLiteDatabase db) {
                    Log.d(TAG, db.getPath() + " database opened");
                }

                @Override
                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                    Log.d(TAG, db.getPath() + " database created");
                }
            };
}
