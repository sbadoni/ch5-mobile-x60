package com.crestron.itemattribute.db.tables;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * This is a row for the item table.
 * Only consists of an {@link ItemEntity#id} and an {@link ItemEntity#item_key} columns
 * You can extend this class if you need to organize your items
 */
@Entity(tableName = "item")
public class ItemEntity {

    /**
     * the id
     */
    @PrimaryKey(autoGenerate = true)
    private Long id;

    /**
     * the name
     */
    @NonNull
    @ColumnInfo(name = "item_key")
    private String item_key;


    public ItemEntity(Long id, @NonNull String item_key) {
        if (id != null)
            this.id = id;
        this.item_key = item_key;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @NonNull
    public String getItem_key() {
        return item_key;
    }

    public void setItem_key(@NonNull String item_key) {
        this.item_key = item_key;
    }

}
