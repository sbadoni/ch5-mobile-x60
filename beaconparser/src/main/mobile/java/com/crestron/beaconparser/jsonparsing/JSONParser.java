package com.crestron.beaconparser.jsonparsing;

import android.content.Context;
import android.util.Log;

import com.crestron.itemattribute.db.dao.AttributeDao;
import com.crestron.itemattribute.db.tables.AttributeEntity;
import com.crestron.itemattribute.db.tables.ItemEntity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JSONParser {

    private static final String TAG = JSONParser.class.getClass().getCanonicalName();

    /**
     * A parser for JSON. It will split the JSON file into its needed parts
     *
     * @return a list of entries
     */
    public static List<EntryInfo> jsonParser(BeaconConfigProviderInterface beaconConfigProviderInterface) {
        try {
            //the main jsonObject
            JSONObject jsonObj = new JSONObject(beaconConfigProviderInterface.getJSON());
            //a list of entries
            List<EntryInfo> entryList = new ArrayList<>();

            // Getting JSON Array node
            JSONArray entries = jsonObj.getJSONArray("entries");

            //looping through All Entries
            for (int i = 0; i < entries.length(); i++) {
                //the entry
                EntryInfo entry = new EntryInfo();
                //the json object
                JSONObject entriesJSONObject = entries.getJSONObject(i);
                //entry information
                String resName = entriesJSONObject.getString("resName");
                String csFriendlyName = entriesJSONObject.getString("csFriendlyName");
                String uuid = entriesJSONObject.getString("uuid");
                String config = jsonObj.getString("CrestronBeaconConfig");
                //setting the entry
                entry.setResName(resName);
                entry.setCsFriendlyName(csFriendlyName);
                entry.setUuid(uuid);
                entry.setCrestronBeaconConfig(config);

                JSONArray rooms = entriesJSONObject.getJSONArray("rooms");
                //looping through all rooms
                for (int j = 0; j < rooms.length(); j++) {
                    //rooms object
                    JSONObject roomsJSONObject = rooms.getJSONObject(j);
                    //room info
                    RoomInfo roomInfo = new RoomInfo();
                    //room info entries
                    int joinID = roomsJSONObject.getInt("joinID");
                    String roomName = roomsJSONObject.getString("roomName");
                    roomInfo.setJoinID(joinID);
                    roomInfo.setRoomName(roomName);

                    JSONArray beacons = roomsJSONObject.getJSONArray("beacons");
                    //looping through all beacons
                    for (int q = 0; q < beacons.length(); q++) {
                        //beacon object
                        JSONObject beaconsJSONObject = beacons.getJSONObject(0);
                        //beacon info
                        String beaconName = beaconsJSONObject.getString("beaconName");
                        int imajor = beaconsJSONObject.getInt("imajor");
                        int iminor = beaconsJSONObject.getInt("iminor");
                        //add beacon to the room
                        BeaconInfo beaconInfo = new BeaconInfo(beaconName, imajor, iminor);
                        roomInfo.addBeacon(beaconInfo);

                    }
                    //get and set the index
                    int index = roomsJSONObject.getInt("index");
                    roomInfo.setIndex(index);
                    //add the room to the list
                    entry.addRoom(roomInfo);
                }
                entryList.add(entry);
            }
            return entryList;
        } catch (JSONException e) {
            Log.e(TAG, "Json parsing error: " + e.getMessage());
            return null;
        }
    }

    /**
     * stores beacon info from a list of entries
     *
     * @param entryInfoList a list of entries to store. Usually obtained from {@link JSONParser#jsonParser(BeaconConfigProviderInterface)} )}
     * @param dao           the Data Access Object (DAO) that does the storing
     */
    public static void storeBeaconInfo(List<EntryInfo> entryInfoList, AttributeDao dao) {
        //the list of items
        List<ItemEntity> itemList = dao.getAllItems();
        //delete EVERYTHING!!!
        for (int i = 0; i < itemList.size(); i++) {
            dao.deleteItemDB(itemList.get(i).getId());
        }
        //go through the list of entries
        for (EntryInfo entryInfo : entryInfoList) {
            //get the item key which is made up of the uuid and the csFriendlyName
            String itemKey = entryInfo.getUuid() + " " + entryInfo.getCsFriendlyName();
            //the item
            ItemEntity item;
            //the id
            Long id;
            //if the item exists, use the item's id
            if (!dao.getItemFromKey(itemKey).isEmpty()) {
                item = dao.getItemFromKey(itemKey).get(0);
                id = item.getId();
                //otherwise, NEW ITEM!
            } else {
                item = new ItemEntity(null, itemKey);
                id = dao.insertItem(item);
            }
            //let's go through the rooms
            for (int i = 0; i < entryInfo.getRooms().size(); i++) {
                //getting the room
                RoomInfo r = entryInfo.getRooms().get(i);
                //looping through the beacon list
                for (BeaconInfo b : r.getBeacons()) {
                    //setting the beacon key
                    String beaconKey = "BCN_" + b.getImajor() + "_" + b.getIminor();
                    //setting the beacon value
                    String beaconValue = "ROOM_" + i;
                    //setting the room key which is the beaconValue but having two variables makes things
                    //more organized
                    String roomKey = beaconValue;
                    //setting the room value make up of the join id and the room name
                    String roomValue = r.getJoinID() + " " + r.getRoomName();
                    //make two attributes
                    AttributeEntity beacons = new AttributeEntity();
                    AttributeEntity rooms = new AttributeEntity();
                    //set their ids, keys, and values
                    beacons.attrID = id;
                    beacons.attrKey = beaconKey;
                    beacons.value = beaconValue;

                    rooms.attrID = id;
                    rooms.attrKey = roomKey;
                    rooms.value = roomValue;
                    //then insert them into the database
                    dao.insertAttr(beacons);
                    dao.insertAttr(rooms);

                }
            }
        }
    }

    /**
     * gets beacon info
     *
     * @param dao the Data Access Object (DAO) that gets the information
     * @return a list of entries
     */
    public static List<EntryInfo> getBeaconInfo(AttributeDao dao) {
        //the entry list
        List<EntryInfo> entryList = new ArrayList<>();
        //all of the items
        List<ItemEntity> itemList = dao.getAllItems();
        //go through the item list
        for (int q = 0; q < itemList.size(); q++) {
            //the id of the current item
            long id = itemList.get(q).getId();
            //the item
            ItemEntity item = itemList.get(q);
            //get all of the attributes of the item
            List<AttributeEntity> attrList = dao.getItemListFromID(id);
            //set up the entry
            EntryInfo entry = new EntryInfo();
            String key = item.getItem_key();
            entry.setUuid(key.substring(0, 36));
            entry.setCsFriendlyName(key.substring(37));
            //set up a room
            RoomInfo roomInfo = new RoomInfo();
            //go through the attributes
            for (int i = 0; i < attrList.size(); i++) {
                //only go through the list if we are dealing with a BCN (beacon key)
                if (attrList.get(i).attrKey.contains("ROOM_")) {
                    continue;
                }
                //get another attribute
                AttributeEntity beaconEntity = attrList.get(i);
                //this is a check if there is another beacon in the same room
                int index = Integer.parseInt(beaconEntity.value.substring("ROOM_".length()));
                boolean sameRoom = false;
                for (RoomInfo r : entry.getRooms()) {
                    if (r.getIndex() == index) {
                        sameRoom = true;
                        break;
                    }
                }
                //if we aren't in the same room, make a new room object
                if (!sameRoom) {
                    roomInfo = new RoomInfo();
                }
                //new beacon info and setting up its information
                BeaconInfo beaconInfo = new BeaconInfo();
                String[] beacon = beaconEntity.attrKey.split("_");
                beaconInfo.setBeaconName(beacon[0]);
                beaconInfo.setImajor(Integer.parseInt(beacon[1]));
                beaconInfo.setIminor(Integer.parseInt(beacon[2]));
                //make a room entity
                AttributeEntity roomEntity = null;
                //find the beacon's room
                for (int j = 0; j < attrList.size(); j++) {
                    if (beaconEntity.value.equals(attrList.get(j).attrKey)) {
                        roomEntity = attrList.get(j);
                        break;
                    }
                }
                //Warning: roomEntity should NEVER be null. If it is, something went wrong.
                assert roomEntity != null;
                //put the assert there so the lint doesn't yell at me
                String roomVal = roomEntity.value;
                //set up the id and the name
                String[] idName = new String[2];
                idName[0] = roomVal.substring(0, roomVal.indexOf(" "));
                idName[1] = roomVal.substring(roomVal.indexOf(" "));
                //if we aren't in the same room, set the room info up
                if (!sameRoom) {
                    roomInfo.setJoinID(Integer.parseInt(idName[0]));
                    roomInfo.setRoomName(idName[1]);
                    roomInfo.setIndex(index);
                }
                //add beacon to room and room to entry
                roomInfo.addBeacon(beaconInfo);
                entry.addRoom(roomInfo);
            }
            entryList.add(entry);
            //add entry to list
        }
        return entryList;
    }

}
