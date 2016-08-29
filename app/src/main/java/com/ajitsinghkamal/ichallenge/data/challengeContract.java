package com.ajitsinghkamal.ichallenge.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import java.net.URI;

/**
 * Database contract for tables to be used by sqlite and contentprovider api
 */
public class challengeContract {


    /**
     * --------------Content Provider contract part----------------------------------------
     */

    //name for entire Content Provider
            public static final String CONTENT_AUTHORITY="com.ajitsinghkamal.ichallenge.provider";

    //using CONTENT_AUTHORITY to create base URI
        public static final Uri CONTENT_BASE_URI= Uri.parse("content://"+ CONTENT_AUTHORITY);

    //api paths
        public static final String PATH_CHALLENGE="challenge";
        public static final String PATH_PROGRESS="progress";


    /**
     * ------ Inner class defining table contents of the challenge entered by user---------
     * */
    public static final class Challenge implements BaseColumns{

    /* .....Uri descriptions for Content Provider of this table...... */

        //Base Uri for this table
        public static final Uri CONTENT_URI=
                CONTENT_BASE_URI.buildUpon().appendPath(PATH_CHALLENGE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CHALLENGE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CHALLENGE;

    /*..........data agreements for this table................*/
        //table name
        public static final String TABLE_NAME="challenge";
        //user defined title for challenge
        public static final String COLUMN_C_NAME="challenge_name";
        //user defined time frame for completion of challenge
        public static final String COLUMN_DUR="challenge_dur";
        //user defined one of the two difficulty settings
        //defined as either 0 or 1
        public static final String COLUMN_MODE="challenge_mode";
        //day of the start
        public static final String COLUMN_STATUS="challenge_status";

        //this helper function allows to fetch using id
        public static Uri buildChallengeUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI,id);
        }
    }

    /**
     * ------------- Inner class defining table for challenge progress ---------------
     * */
    public static final class Progress implements BaseColumns {

    /* .....Uri descriptions for Content Provider of this table...... */

        //Base Uri for this table
        public static final Uri CONTENT_URI =
                CONTENT_BASE_URI.buildUpon().appendPath(PATH_PROGRESS).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PROGRESS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PROGRESS;

    /*..........data agreements for this table................*/
        //table name
        public static final String TABLE_NAME = "progress";
        //foreign key
        public static final String COLUMN_C_ID = "challenge_id";

        public static final String COLUMN_C_DAY = "day_number";

        public static final String COLUMN_STATUS = "day_status";

        public static final String COLUMN_TODAY = "today";

        //helper function to fetch data using id
        public static Uri buildProgressUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI,id);
        }

        //uri builder
        //for getting progress by challenge
        public static Uri buildProgressChallengeUri(String challengeName){
            return CONTENT_URI.buildUpon().appendPath(challengeName).build();

        }
        //uri builder
        //from progress table get progress for given day
        public static Uri ProgressChallengeUriByDay(String challengeName,int dayNum){
            return CONTENT_URI.buildUpon().appendPath(challengeName)
                    .appendQueryParameter(COLUMN_C_DAY,Integer.toString(dayNum)).build();
        }
        public static Uri DayByStatus(int code){
            return CONTENT_URI.buildUpon().appendQueryParameter(COLUMN_STATUS,Integer.toString(code)).build();
        }

        public static int statusCode(Uri uri){
            return Integer.parseInt(uri.getPathSegments().get(1));
        }
        public static String getNamefromUri(Uri uri){
            return uri.getPathSegments().get(1);
        }

        public static int getDaybyNamefromUri(Uri uri){
            return Integer.parseInt(uri.getPathSegments().get(2));
        }
    }



}
