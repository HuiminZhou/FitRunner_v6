package com.example.huimin_zhou.fitrunner.backend.Datastore;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Lucidity on 17/2/23.
 */

public class MyDatastore {
    private static final Logger mLogger = Logger
            .getLogger(MyDatastore.class.getName());
    private static final DatastoreService mDatastore = DatastoreServiceFactory
            .getDatastoreService();

    private static Key getKey() {
        return KeyFactory.createKey(ExerciseEntry.ENTRY_PARENT_ENTITY_NAME,
                ExerciseEntry.ENTRY_PARENT_KEY);
    }


    public static boolean add(ExerciseEntry entry) {
        if (getEntryById(entry.mId) != null) {
            mLogger.log(Level.INFO, "entry exists");
            return false;
        }

        Key parentKey = getKey();

        Entity entity = new Entity(ExerciseEntry.ENTRY_ENTITY_ID, entry.mId,
                parentKey);
        entity.setProperty(ExerciseEntry.ID,            entry.mId);
        entity.setProperty(ExerciseEntry.INPUT_TYPE,    entry.mInputType);
        entity.setProperty(ExerciseEntry.ACTIVITY_TYPE, entry.mActivityType);
        entity.setProperty(ExerciseEntry.DATE,          entry.mDate);
        entity.setProperty(ExerciseEntry.DURATION,      entry.mDuration);
        entity.setProperty(ExerciseEntry.DISTANCE,      entry.mDistance);
        entity.setProperty(ExerciseEntry.CLIMB,         entry.mClimb);
        entity.setProperty(ExerciseEntry.AVG_SPEED,     entry.mAvgSpeed);
        entity.setProperty(ExerciseEntry.CALORIES,      entry.mCalories);
        entity.setProperty(ExerciseEntry.HEART_RATE,    entry.mHeart);
        entity.setProperty(ExerciseEntry.COMMENT,       entry.mComment);

        mDatastore.put(entity);

        return true;
    }

    public static boolean delete(String id) {
        // use id to get key, then use the key to delete the
        // entity from datastore directly
        // because id is also the entity's key
        if (id == null || id.length() == 0) return false;

        // query
        Query.Filter filter = new Query.FilterPredicate(ExerciseEntry.ID,
                Query.FilterOperator.EQUAL, id);

        Query query = new Query(ExerciseEntry.ENTRY_ENTITY_ID);
        query.setFilter(filter);

        // Use PreparedQuery interface to retrieve results
        PreparedQuery pq = mDatastore.prepare(query);

        Entity result = pq.asSingleEntity();
        boolean ret = false;
        if (result != null) {
            // delete
            mDatastore.delete(result.getKey());
            ret = true;
        }

        return ret;
    }

    public static ArrayList<ExerciseEntry> query(String id) {
        ArrayList<ExerciseEntry> resultList = new ArrayList<>();
        if (id != null && id.length() != 0) { // query one entry
            ExerciseEntry entry = getEntryById(id);
            if (entry != null) {
                resultList.add(entry);
            }
        } else { // query all entries
            Query query = new Query(ExerciseEntry.ENTRY_ENTITY_ID);
            // get every record from datastore, no filter
            query.setFilter(null);
            // set query's ancestor to get strong consistency
            query.setAncestor(getKey());

            PreparedQuery pq = mDatastore.prepare(query);

            for (Entity entity : pq.asIterable()) {
                ExerciseEntry entry = getEntryFromEntity(entity);
                if (entry != null) {
                    resultList.add(entry);
                }
            }
        }
        return resultList;
    }

    public static ExerciseEntry getEntryById(String id) {
        Entity result = null;
        try {
            result = mDatastore.get(KeyFactory.createKey(getKey(),
                    ExerciseEntry.ENTRY_ENTITY_ID, id));
        } catch (Exception ex) {

        }

        return getEntryFromEntity(result);
    }

    private static ExerciseEntry getEntryFromEntity(Entity entity) {
        if (entity == null) {
            return null;
        }

        return new ExerciseEntry(
                (String) entity.getProperty(ExerciseEntry.ID),
                (String) entity.getProperty(ExerciseEntry.INPUT_TYPE),
                (String) entity.getProperty(ExerciseEntry.ACTIVITY_TYPE),
                (String) entity.getProperty(ExerciseEntry.DATE),
                (String) entity.getProperty(ExerciseEntry.DURATION),
                (String) entity.getProperty(ExerciseEntry.DISTANCE),
                (String) entity.getProperty(ExerciseEntry.CLIMB),
                (String) entity.getProperty(ExerciseEntry.AVG_SPEED),
                (String) entity.getProperty(ExerciseEntry.CALORIES),
                (String) entity.getProperty(ExerciseEntry.HEART_RATE),
                (String) entity.getProperty(ExerciseEntry.COMMENT));
    }
}
