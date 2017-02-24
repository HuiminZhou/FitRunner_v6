package com.example.huimin_zhou.fitrunner.backend.Datastore;

import java.util.ArrayList;

/**
 * Created by Lucidity on 17/2/23.
 */

public class ExerciseEntry {
    public static final String ENTRY_PARENT_ENTITY_NAME = "ExerciseParent";
    public static final String ENTRY_PARENT_KEY = "ExerciseParentKey";
    public static final String ENTRY_ENTITY_ID = "ExerciseID";

    public static final String ID = "_id";
    public static final String INPUT_TYPE = "inputType";
    public static final String ACTIVITY_TYPE = "activityType";
    public static final String DATE = "date";
    public static final String DURATION = "duration";
    public static final String DISTANCE = "distance";
    public static final String CLIMB = "climb";
    public static final String AVG_SPEED = "avgSpeed";
    public static final String CALORIES = "calories";
    public static final String HEART_RATE = "heartRate";
    public static final String COMMENT = "comment";

    public String mId;
    public String mInputType = "";
    public String mActivityType = "";
    public String mDate;
    public String mDuration; // min and seconds
    public String mCalories;
    public String mHeart;
    public String mDistance; // miles
    public String mClimb;
    public String mAvgSpeed;     // Average speed
    public String mComment;

    public ExerciseEntry(String mId, String mInputType, String mActivityType, String mDate,
                         String mDuration, String mDistance, String mClimb, String mAvgSpeed,
                         String mCalories, String mHeart, String mComment) {
        this.mId = mId;
        this.mInputType = mInputType;
        this.mActivityType = mActivityType;
        this.mDate = mDate;
        this.mDuration = mDuration;
        this.mCalories = mCalories;
        this.mHeart = mHeart;
        this.mDistance = mDistance;
        this.mClimb = mClimb;
        this.mAvgSpeed = mAvgSpeed;
        this.mComment = mComment;
    }
}