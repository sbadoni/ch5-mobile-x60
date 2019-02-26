package com.crestron.itemattribute;

import android.database.sqlite.SQLiteConstraintException;
import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.crestron.itemattribute.db.ItemAttrDB;
import com.crestron.itemattribute.db.dao.AttributeDao;
import com.crestron.itemattribute.db.tables.AttributeEntity;
import com.crestron.itemattribute.db.tables.ItemEntity;
import com.crestron.itemattribute.db.util.RxUtil;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.reactivex.Maybe;

/**
 * DB integration tests which will execute on an Android device.
 * They verify the behavior of our inner join table via a Dao.
 */
@RunWith(AndroidJUnit4.class)
public class DatabaseTest {

    @NonNull
    private ItemAttrDB itemAttrDB;
    private AttributeDao attributeDao;

    @Before
    public void setUp() {
        // Generate a random string as db name
        byte[] array = new byte[7]; // length is bounded by 7
        new Random().nextBytes(array);
        String generatedString = new String(array, Charset.forName("UTF-8"));

        itemAttrDB = ItemAttrDB.createInstance(InstrumentationRegistry.getTargetContext(), true, generatedString);
        attributeDao = itemAttrDB.getAttrDao();
    }

    @After
    public void tearDown() {
        itemAttrDB.close();
        ItemAttrDB.clearInstance();
    }

    @Test
    public void testInsertSingle() {

        ItemEntity firstItem = new ItemEntity(null, "First Single");

        Long id = attributeDao.insertItem(firstItem);

        AttributeEntity adb = new AttributeEntity();
        adb.attrID = id;
        adb.value = "This is a value";
        adb.attrKey = "This is a key";

        attributeDao.insertAttr(adb);

        List<AttributeEntity> ladb = attributeDao.getAttributesFromID(id);

        Assert.assertEquals("Error: not the right inserted value for key!", "This is a value", ladb.get(0).value);
        Assert.assertEquals("Error: not the right inserted value for item_key!", "First Single", attributeDao.getAllItems().get(0).getItem_key());

    }

    @Test
    public void testInsertMultiple() {

        ItemEntity firstItem = new ItemEntity(null, "First Item");
        ItemEntity secondItem = new ItemEntity(null, "Second Item");
        ItemEntity thirdItem = new ItemEntity(null, "Third Item");

        Long id = attributeDao.insertItem(firstItem);
        Long id1 = attributeDao.insertItem(secondItem);
        Long id2 = attributeDao.insertItem(thirdItem);

        AttributeEntity adb = new AttributeEntity();
        adb.attrID = id;
        adb.value = "This is a first value";
        adb.attrKey = "First Key";

        AttributeEntity adbOne = new AttributeEntity();
        adbOne.attrID = id;
        adbOne.value = "This is a second value in first item";
        adbOne.attrKey = "Second Key";

        AttributeEntity adb1 = new AttributeEntity();
        adb1.attrID = id1;
        adb1.value = "This is a second value";
        adb1.attrKey = "Second Key";

        attributeDao.insertAttr(adb);
        attributeDao.insertAttr(adbOne);
        attributeDao.insertAttr(adb1);

        Assert.assertEquals(1L, id.longValue());
        Assert.assertEquals(2L, id1.longValue());
        Assert.assertEquals(3L, id2.longValue());

        List<AttributeEntity> ladb = attributeDao.getAllAttributesInTable();
        List<ItemEntity> lit = attributeDao.getAllItems();

        Assert.assertEquals("Error: only three items should be in DB!", 3, ladb.size());
        Assert.assertEquals("Error: only three items should be in DB!", 3, lit.size());

    }

    @Test
    public void testInsertMultipleSameKeySameID() {

        ItemEntity firstItem = new ItemEntity(null, "First Item");

        Long id = attributeDao.insertItem(firstItem);

        AttributeEntity adb = new AttributeEntity();
        adb.attrID = id;
        adb.value = "This is a first value";
        adb.attrKey = "First Key";

        AttributeEntity adbOne = new AttributeEntity();
        adbOne.attrID = id;
        adbOne.value = "This is a second value in first item";
        adbOne.attrKey = "First Key";

        attributeDao.insertAttr(adb);
        attributeDao.insertAttr(adbOne);

        Assert.assertEquals(1L, id.longValue());

        List<AttributeEntity> ladb = attributeDao.getAllAttributesInTable();
        List<ItemEntity> lit = attributeDao.getAllItems();

        Assert.assertEquals("Error: only three items should be in DB!", 1, ladb.size());
        Assert.assertEquals("Error: only three items should be in DB!", 1, lit.size());

    }

    @Test
    public void testInsertMultipleSameKeyDiffID() {

        ItemEntity firstItem = new ItemEntity(null, "First Item");
        ItemEntity secondItem = new ItemEntity(null, "Second Item");

        Long id = attributeDao.insertItem(firstItem);
        Long id1 = attributeDao.insertItem(secondItem);

        AttributeEntity adb = new AttributeEntity();
        adb.attrID = id;
        adb.value = "This is a first value";
        adb.attrKey = "First Key";

        AttributeEntity adbOne = new AttributeEntity();
        adbOne.attrID = id1;
        adbOne.value = "This is a second value in first item";
        adbOne.attrKey = "First Key";

        attributeDao.insertAttr(adb);
        attributeDao.insertAttr(adbOne);

        Assert.assertEquals(1L, id.longValue());
        Assert.assertEquals(2L, id1.longValue());

        List<AttributeEntity> ladb = attributeDao.getAllAttributesInTable();
        List<ItemEntity> lit = attributeDao.getAllItems();

        Assert.assertEquals("Error: only three items should be in DB!", 2, ladb.size());
        Assert.assertEquals("Error: only three items should be in DB!", 2, lit.size());

    }

    @Test(expected = SQLiteConstraintException.class)
    public void testErrorInsert() {

        ItemEntity firstItem = new ItemEntity(null, "Error Item");

        Long id = attributeDao.insertItem(firstItem);

        AttributeEntity adb = new AttributeEntity();
        adb.value = "This is a value";
        adb.attrKey = "Q";
        adb.attrID = 3L;

        attributeDao.insertAttr(adb);

        List<AttributeEntity> ladb = attributeDao.getAttributesFromID(id);

    }


    @Test
    public void testUpdate() {

        ItemEntity firstItem = new ItemEntity(null, "6 Volvo Drive");

        Long id;

        id = attributeDao.insertItem(firstItem);

        AttributeEntity adb = new AttributeEntity();
        adb.attrID = id;
        adb.value = "This is a value";
        adb.attrKey = "USE_LOCAL_PROJECT";

        attributeDao.insertAttr(adb);

        ItemEntity item = new ItemEntity(id, "Home System");

        AttributeEntity attr = new AttributeEntity();
        attr.attrKey = "USE_LOCAL_PROJECT";
        attr.value = "true";
        attr.attrID = id;

        attributeDao.updateItem(item);
        attributeDao.updateAttr(attr);

        List<AttributeEntity> ladb = attributeDao.getAttributesFromID(id);

        Assert.assertEquals("Error: friendly name doesn't match!", "Home System", attributeDao.getItemFromID(id).getItem_key());
        Assert.assertEquals("Error: key doesn't match!", "USE_LOCAL_PROJECT", ladb.get(0).attrKey);
        Assert.assertEquals("Error: value doesn't match!", "true", ladb.get(0).value);
        Assert.assertEquals("Error: sysId doesn't match!", id, ladb.get(0).attrID);

        //verify no stale data
				Maybe<List<AttributeEntity>> listMaybe = RxUtil.convertToRxMaybe(() -> attributeDao.getAllItems());
        Assert.assertEquals("Error: stale data in db!", 1, listMaybe.blockingGet().size());

    }


    @Test
    public void testUpdateMultiple() {

        Assert.assertEquals("There should be no data!", 0, attributeDao.itemCount());

        ItemEntity firstItem = new ItemEntity(null, "First Item");
        ItemEntity secondItem = new ItemEntity(null, "Second Item");
        ItemEntity thirdItem = new ItemEntity(null, "Third Item");

        Long id = attributeDao.insertItem(firstItem);
        Long id1 = attributeDao.insertItem(secondItem);
        Long id2 = attributeDao.insertItem(thirdItem);

        AttributeEntity adb = new AttributeEntity();
        adb.attrID = id;
        adb.value = "This is a first value";
        adb.attrKey = "First Key";

        AttributeEntity adbOne = new AttributeEntity();
        adbOne.attrID = id;
        adbOne.value = "This is a second value in first item";
        adbOne.attrKey = "Second Key";

        AttributeEntity adb1 = new AttributeEntity();
        adb1.attrID = id1;
        adb1.value = "This is a second value";
        adb1.attrKey = "Second Key";

        attributeDao.insertAttr(adb);
        attributeDao.insertAttr(adbOne);
        attributeDao.insertAttr(adb1);

        ItemEntity item = new ItemEntity(id, "Updated Item");

        AttributeEntity attr = new AttributeEntity();
        attr.attrKey = "First Key";
        attr.value = "This is an updated value";
        attr.attrID = id;

        AttributeEntity attr1 = new AttributeEntity();
        attr1.attrKey = "Second Key";
        attr1.value = "This is an updated value";
        attr1.attrID = id;

        attributeDao.updateItem(item);
        attributeDao.updateAttr(attr);
        attributeDao.updateAttr(attr1);

        List<AttributeEntity> ladb = attributeDao.getAttributesFromID(id);

        Assert.assertEquals("Error: item_key doesn't match!", "Updated Item", attributeDao.getItemFromID(id).getItem_key());
        Assert.assertEquals("Error: key doesn't match!", "First Key", ladb.get(0).attrKey);
        Assert.assertEquals("Error: value doesn't match!", "This is an updated value", ladb.get(0).value);
        Assert.assertEquals("Error: attrID doesn't match!", id, ladb.get(0).attrID);

        Assert.assertEquals("Error: stale data in db!", 3, attributeDao.attributeCount());

    }

    @Test
    public void testUpdateWithNoEntries() {
        Assert.assertEquals("There should be no data!", 0, attributeDao.itemCount());

        // Insert first item
        ItemEntity firstItem = new ItemEntity(null, "First Item");
        Long id = attributeDao.insertItem(firstItem);
        AttributeEntity adb = new AttributeEntity();
        adb.attrID = id;
        adb.value = "This is a value";
        adb.attrKey = "First Key";
        attributeDao.insertAttr(adb);

        // Update the inserted item
        ItemEntity updatedItem = new ItemEntity(id, "Updated Item");
        AttributeEntity attr = new AttributeEntity();
        attr.attrKey = "First Key";
        attr.value = "This is an updated value";
        attr.attrID = id;
        attributeDao.updateItem(updatedItem);
        attributeDao.updateAttr(attr);

        List<AttributeEntity> listOfAttributes = attributeDao.getAttributesFromID(id);

        Assert.assertEquals("Error: item_key doesn't match!", "Updated Item", attributeDao.getItemFromID(id).getItem_key());
        Assert.assertEquals("Error: key doesn't match!", "First Key", listOfAttributes.get(0).attrKey);
        Assert.assertEquals("Error: value doesn't match!", "This is an updated value", listOfAttributes.get(0).value);
        Assert.assertEquals("Error: attrID doesn't match!", id, listOfAttributes.get(0).attrID);
        Assert.assertEquals("Error: stale data in db!", 1, attributeDao.attributeCount());
    }

    @Test
    public void testRead() {
	
				Maybe<List<AttributeEntity>> listMaybe = RxUtil.convertToRxMaybe(() -> attributeDao.getAllItems());

        //Assert.assertEquals("Error: stale data in db!", 0, attributeDao.getAllAttributesInTable().size());
        Assert.assertEquals("Error: stale data in db!", 0, listMaybe.blockingGet() == null ? 0 : listMaybe.blockingGet().size());
        //Assert.assertNull("Error: data should not exist in db!", attributeDao.getAttributesFromID(1L));
        //Assert.assertArrayEquals("Error: data should not exist in db!", new ArrayList<>().toArray(), attributeDao.getAttributesFromID(1L).toArray());

        Maybe<List<AttributeEntity>> listMaybe1 = RxUtil.convertToRxMaybe(() -> attributeDao.getAttributesFromID(1L));

        Assert.assertArrayEquals("Error: data should not exist in db!", new ArrayList<>().toArray(), listMaybe1.blockingGet() == null ? new ArrayList<>().toArray() : listMaybe1.blockingGet().toArray());

        ItemEntity item = new ItemEntity(null, "Igor's system");
        AttributeEntity attr = new AttributeEntity();

        long id = attributeDao.insertItem(item);

        attr.attrID = id;

        attr.attrKey = "HOST";
        attr.value = "192.168.1.1";

        attributeDao.insertAttr(attr);

        List<AttributeEntity> read = attributeDao.getAttrFromID(id, "HOST");

        //List<ItemEntity> ladb = itemAttrDB.getItemDao().getItemListFromID(id);

			Maybe<List<ItemEntity>> listMaybe2 = RxUtil.convertToRxMaybe(new RxUtil.MaybeActions<List>() {
            @Override
            public List maybeConvert() {
                return attributeDao.getAllItems();
            }
        });

        Assert.assertEquals("Error: reading hostName1 for sysInfo doesn't match!",
                read.get(0).value, "192.168.1.1");

        /*Assert.assertEquals("Error: reading friendlyName for system doesn't match!",
                ladb.get(0).getItem_key(), "Igor's system");*/
        /*

        Assert.assertEquals("Error: reading friendlyName for system doesn't match!",
                listMaybe2.get(0).getItem_key(), "Igor's system");
        */
    }

    @Test
    public void testDelete() {

        ItemEntity firstItem = new ItemEntity(null, "TE");
        long firstID = attributeDao.insertItem(firstItem);

        AttributeEntity firstAttr = new AttributeEntity();
        firstAttr.attrID = firstID;
        firstAttr.value = "Q";
        firstAttr.attrKey = "USE_LOCAL_PROJECT";
        attributeDao.insertAttr(firstAttr);

        ItemEntity secondItem = new ItemEntity(null, "TE");
        long secondID = attributeDao.insertItem(secondItem);

        AttributeEntity secondAttr = new AttributeEntity();
        secondAttr.attrID = secondID;
        secondAttr.value = "Q";
        secondAttr.attrKey = "CIP_PORT_1";
        attributeDao.insertAttr(secondAttr);

        attributeDao.deleteAttributeDB(firstID);
        attributeDao.deleteAttributeDB(secondID);

        Assert.assertEquals("Error: stale data in db!", 0, attributeDao.attributeCount());

    }

    @Test
    public void testDeleteItem() {

        Assert.assertEquals("There should be no data!", 0, attributeDao.itemCount());

        ItemEntity firstItem = new ItemEntity(null, "First Item");
        ItemEntity secondItem = new ItemEntity(null, "Second Item");
        ItemEntity thirdItem = new ItemEntity(null, "Third Item");

        Long id = attributeDao.insertItem(firstItem);
        Long id1 = attributeDao.insertItem(secondItem);
        Long id2 = attributeDao.insertItem(thirdItem);

        AttributeEntity adb = new AttributeEntity();
        adb.attrID = id;
        adb.value = "This is a first value";
        adb.attrKey = "First Key";

        AttributeEntity adbOne = new AttributeEntity();
        adbOne.attrID = id;
        adbOne.value = "This is a second value in first item";
        adbOne.attrKey = "Second Key";

        AttributeEntity adb1 = new AttributeEntity();
        adb1.attrID = id1;
        adb1.value = "This is a second value";
        adb1.attrKey = "Second Key";

        attributeDao.insertAttr(adb);
        attributeDao.insertAttr(adbOne);
        attributeDao.insertAttr(adb1);

        List<AttributeEntity> ladb = attributeDao.getAttributesFromID(id);

        Assert.assertEquals("Error: item_key doesn't match!", "First Item", attributeDao.getItemFromID(id).getItem_key());
        Assert.assertEquals("Error: key doesn't match!", "First Key", ladb.get(0).attrKey);
        Assert.assertEquals("Error: value doesn't match!", "This is a first value", ladb.get(0).value);
        Assert.assertEquals("Error: attrID doesn't match!", id, ladb.get(0).attrID);

        Assert.assertEquals("Error: stale data in db!", 3, attributeDao.attributeCount());

        attributeDao.deleteAttr(adb1);

        Assert.assertEquals("Error: stale data in db!", 2, attributeDao.attributeCount());

        attributeDao.deleteItemDB(id);

        Assert.assertEquals("Error: stale data in db!", 0, attributeDao.attributeCount());
        Assert.assertEquals("Error: stale data in db!", 2, attributeDao.itemCount());

    }


}
