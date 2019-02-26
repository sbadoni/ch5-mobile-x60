package com.crestron.itemattribute.db.tables;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.support.annotation.NonNull;

import static android.arch.persistence.room.ForeignKey.CASCADE;

/**
 * This is a row for the attributes table.
 * Consists of an {@link AttributeEntity#attrID}, an {@link AttributeEntity#attrKey}, and a {@link AttributeEntity#value} columns
 * You can extend this class if you need to organize your attributes
 */
@Entity(tableName = "attributes",
        primaryKeys = {"attrId", "attrKey"},
        foreignKeys = {
                @ForeignKey(entity = ItemEntity.class,
                        parentColumns = "id",
                        childColumns = "attrId",
                        onDelete = CASCADE,
                        onUpdate = CASCADE
                ),
        },
        indices = @Index("attrId")
)
public class AttributeEntity {

    /**
     * the key
     */
    @NonNull
    public String attrKey;

    /**
     * the value
     */
    public String value;

    /**
     * the foreign key
     */
    @ColumnInfo(name = "attrId")
    @NonNull
    public Long attrID;

}
