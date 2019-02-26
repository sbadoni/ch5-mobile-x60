package com.crestron.itemattribute.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.crestron.itemattribute.db.tables.ItemEntity;

import java.util.List;

/**
 * The {@link Dao} (Data Access Object) that handles the CRUD operations for {@link ItemEntity}
 */
@Dao
public interface ItemDao {

    @Insert
    void insert(ItemEntity item);

    @Update
    void update(ItemEntity item);

    @Delete
    void delete(ItemEntity item);

    @Query("select * from item")
    List<ItemEntity> getAllItems();

    @Query("select * from item where id=:id")
    List<ItemEntity> getItemFromID(Long id);

    @Query("select * from item where item_key=:key")
    List<ItemEntity> getItemFromKey(String key);

}
