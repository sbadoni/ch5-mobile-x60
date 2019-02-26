package com.crestron.beaconparser;

import android.support.annotation.NonNull;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.crestron.itemattribute.db.ItemAttrDB;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class BeaconDatabaseTest {

    @NonNull
    private ItemAttrDB itemAttrDB;

    @Before
    public void setUp() {
        itemAttrDB = ItemAttrDB.createInstance(InstrumentationRegistry.getTargetContext(), true, "testing");
    }

    @After
    public void tearDown() {
        itemAttrDB.close();
        ItemAttrDB.clearInstance();
    }

    @Test
    public void readAndStoreAndRead() {

    }
}