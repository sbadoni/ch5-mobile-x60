package com.crestron.itemattribute.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;
import android.arch.persistence.room.Update;

import com.crestron.itemattribute.db.tables.AttributeEntity;
import com.crestron.itemattribute.db.tables.ItemEntity;

import java.util.List;

/**
 * The {@link Dao} (Data Access Object) that handles the CRUD operations for both {@link AttributeEntity} and {@link ItemEntity}
 */
@Dao
public abstract class AttributeDao {

    /**
     * Inserts a new attribute, will throw an error if there is already an attribute with the same key value.
     * In the past we have combined the insertAttr and updateAttr methods which makes some things easier and others
     * harder. SO instead we will split the two methods and do a try catch. Worse comes to worse,
     * we just call updateAttr, but that will be done out of this class
     * (or in the future we make a method that does those two things).
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertAttr(AttributeEntity item);

    /**
     * updates an attribute
     */
    @Update
    public abstract void updateAttr(AttributeEntity item);

    /**
     * deletes an attribute
     *
     * @param item an {@link AttributeEntity}
     */
    @Delete
    public abstract void deleteAttr(AttributeEntity... item);

    /**
     * deletes a certain attribute based on id
     *
     * @param id a list of ids to deleteAttr
     * @return the id of the deleted {@link AttributeEntity}
     */
    @Query("delete from attributes where attrId in (:id)")
    public abstract int deleteAttributeDB(Long... id);

    /**
     * deletes a certain attribute based on key
     *
     * @param key a list of keys to deleteAttr from the attributes table
     * @return the id of the deleted {@link AttributeEntity}
     */
    @Query("delete from attributes where attrKey in (:key)")
    public abstract int deleteAttributeDBByKey(String... key);

    /**
     * deletes a certain attribute based on id
     *
     * @param id a list of ids
     * @return the id of the deleted {@link ItemEntity}
     */
    @Query("delete from item where id in (:id)")
    public abstract int deleteItemDB(Long... id);

    /**
     * gets all of the attributes
     *
     * @return a list of attributes
     */
    @Query("select * from attributes order by attrId, attrKey")
    public abstract List<AttributeEntity> getAllAttributesInTable();

    /**
     * gets all of the items
     *
     * @return a list of items
     */
    @Query("select * from item order by id")
    public abstract List<ItemEntity> getAllItems();

    /**
     * gets all of the attributes based on id and a key
     *
     * @param id  the id
     * @param key the key
     * @return {@link List<AttributeEntity>}
     */
    @Transaction
    @Query("select * from attributes " +
            "where attrId=:id and attrKey=:key " +
            "order by attrId, attrKey")
    public abstract List<AttributeEntity> getAttrFromID(Long id, String key);

    /**
     * gets a list of attributes based on id
     *
     * @param id the id
     * @return {@link List<AttributeEntity>}
     */
    @Query("select * from attributes where attrId=:id")
    public abstract List<AttributeEntity> getItemListFromID(Long id);

    /**
     * gets a single item based on id
     *
     * @param id the id
     * @return an item
     */
    @Query("select * from item where id=:id")
    public abstract ItemEntity getItemFromID(Long id);

    /**
     * gets an item from a key
     *
     * @param key the key
     * @return a list of items
     */
    @Query("select * from item where item_key=:key")
    public abstract List<ItemEntity> getItemFromKey(String key);

    /**
     * gets attributes based on {@link android.arch.persistence.room.ForeignKey} from {@link ItemEntity#id}
     *
     * @param id the id
     * @return {@link List<AttributeEntity>}
     */
    @Transaction
    @Query("select attrKey, [value], attrId " +
            "from item inner join attributes on " +
            "item.id = attributes.attrId" +
            " where id=:id order by attrId, attrKey")
    public abstract List<AttributeEntity> getAttributesFromID(Long id);

    /**
     * Inserts a new item, will throw an error if there is already an item with the same key value.
     * In the past we have combined the insertAttr and updateAttr methods which makes some things easier and others
     * harder. SO instead we will split the two methods and do a try catch. Worse comes to worse,
     * we just call updateAttr, but that will be done out of this class
     * (or in the future we make a method that does those two things).
     *
     * @param item the item
     * @return the id of the item
     */
    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract Long insertItem(ItemEntity item);

    /**
     * gets the amount of rows in item
     *
     * @return the number of items (rows)
     */
    @Query("select count(*) from item")
    public abstract int itemCount();

    /**
     * gets the amount of rows in attributes
     *
     * @return the number of attributes (rows)
     */
    @Query("select count(*) from attributes")
    public abstract int attributeCount();

    /**
     * updates an item
     *
     * @param item the item
     */
    @Update
    public abstract void updateItemDB(ItemEntity item);

    /**
     * updates an item, if there are no rows then it creates an item with the same information
     *
     * @param item the item
     * @return the id of the item
     */
    public Long updateItem(ItemEntity item) {
        long id;

        if (itemCount() == 0 || item.getId() == null) {
            return insertItem(item);
        } else {
            updateItemDB(item);
            id = item.getId();
        }

        return id;
    }


}
