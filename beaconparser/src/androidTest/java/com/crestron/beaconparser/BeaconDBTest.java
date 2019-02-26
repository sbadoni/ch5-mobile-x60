package com.crestron.beaconparser;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.crestron.beaconparser.jsonparsing.BeaconInfo;
import com.crestron.beaconparser.jsonparsing.EntryInfo;
import com.crestron.beaconparser.jsonparsing.JSONParser;
import com.crestron.beaconparser.jsonparsing.RoomInfo;
import com.crestron.itemattribute.db.ItemAttrDB;
import com.crestron.itemattribute.db.dao.AttributeDao;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class BeaconDBTest {

    private ItemAttrDB itemAttrDB;
    private AttributeDao attrDao;

    @Before
    public void setUp() {
        itemAttrDB = ItemAttrDB.createInstance(InstrumentationRegistry.getTargetContext(), true, "testing");
        attrDao = itemAttrDB.getAttrDao();
    }

    @After
    public void tearDown() {
        itemAttrDB.close();
        ItemAttrDB.clearInstance();
    }

    @Test
    public void readAndStoreAndRead() {
        List<EntryInfo> entry = JSONParser.jsonParser(new BeaconConfigProviderFromStringTestCase(InstrumentationRegistry.getTargetContext()));

        assert entry != null;

        for (EntryInfo e : entry) {
            Assert.assertEquals("resName", "TestOne", e.getResName());
            Assert.assertEquals("csFriendlyName", "Jacobs MC3", e.getCsFriendlyName());

            RoomInfo r1 = e.getRooms().get(0);
            Assert.assertEquals("joinID", 13, r1.getJoinID());
            Assert.assertEquals("roomName", "Cubicle", r1.getRoomName());

            BeaconInfo b1 = r1.getBeacons().get(0);
            Assert.assertEquals("beaconName", "TC3EC7F3", b1.getBeaconName());
            Assert.assertEquals("imajor", 67, b1.getImajor());
            Assert.assertEquals("iminor", 1, b1.getIminor());

            Assert.assertEquals("index", 6, r1.getIndex());

            RoomInfo r2 = e.getRooms().get(1);
            Assert.assertEquals("joinID", 31, r2.getJoinID());
            Assert.assertEquals("roomName", "Mac", r2.getRoomName());

            BeaconInfo b2 = r2.getBeacons().get(0);
            Assert.assertEquals("beaconName", "TC9EC99F", b2.getBeaconName());
            Assert.assertEquals("imajor", 67, b2.getImajor());
            Assert.assertEquals("iminor", 2, b2.getIminor());

            Assert.assertEquals("index", 9, r2.getIndex());

            Assert.assertEquals("uuid", "D1D94D19-439E-43A2-84AE-0B8E7BC3EF87", e.getUuid());
            Assert.assertEquals("CrestronBeaconConfig", "1.0.0", e.getCrestronBeaconConfig());
        }

        JSONParser.storeBeaconInfo(entry, attrDao);

        List<EntryInfo> lei = JSONParser.getBeaconInfo(attrDao);
        for (EntryInfo e : lei) {
            Assert.assertEquals("resName", "", e.getResName());
            Assert.assertEquals("csFriendlyName", "Jacobs MC3", e.getCsFriendlyName());

            RoomInfo r1 = e.getRooms().get(0);
            Assert.assertEquals("joinID", 13, r1.getJoinID());
            Assert.assertEquals("roomName", " Cubicle", r1.getRoomName());

            BeaconInfo b1 = r1.getBeacons().get(0);
            Assert.assertEquals("beaconName", "BCN", b1.getBeaconName());
            Assert.assertEquals("imajor", 67, b1.getImajor());
            Assert.assertEquals("iminor", 1, b1.getIminor());

            Assert.assertEquals("index", 0, r1.getIndex());

            RoomInfo r2 = e.getRooms().get(1);
            Assert.assertEquals("joinID", 31, r2.getJoinID());
            Assert.assertEquals("roomName", " Mac", r2.getRoomName());

            BeaconInfo b2 = r2.getBeacons().get(0);
            Assert.assertEquals("beaconName", "BCN", b2.getBeaconName());
            Assert.assertEquals("imajor", 67, b2.getImajor());
            Assert.assertEquals("iminor", 2, b2.getIminor());

            Assert.assertEquals("index", 1, r2.getIndex());

            Assert.assertEquals("uuid", "D1D94D19-439E-43A2-84AE-0B8E7BC3EF87", e.getUuid());
            Assert.assertEquals("CrestronBeaconConfig", "", e.getCrestronBeaconConfig());
        }

        attrDao.deleteItemDB(attrDao.getAllItems().get(0).getId());

        Assert.assertEquals("Should be no items in db", 0, attrDao.getAllItems().size());
        Assert.assertEquals("Should be no attributes in db", 0, attrDao.getAllItems().size());

    }
}