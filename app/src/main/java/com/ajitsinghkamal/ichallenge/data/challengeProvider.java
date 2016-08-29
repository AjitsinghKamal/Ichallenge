package com.ajitsinghkamal.ichallenge.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * Created by deku on 14/8/16.
 */
public class challengeProvider extends ContentProvider {

    //uri matcher
    public static final UriMatcher uriWizard=buildUriMatcher();
    private challengeDbHelper cHelper;

    static final int CHALLENGE=1;
    static final int PROGRESS=2;
    static final int PROGRESS_BY_DAY=3;
    static final int CHALLENGE_BY_NAME=4;
   // static final int PROGRESS_DAY_BY_STATUS=5;
    private static final SQLiteQueryBuilder qProgressByChallengeQueryBuilder;
    //private static final SQLiteQueryBuilder qProgress;

    static {
        qProgressByChallengeQueryBuilder=new SQLiteQueryBuilder();

        qProgressByChallengeQueryBuilder.setTables(
                challengeContract.Challenge.TABLE_NAME+" INNER JOIN "+
                        challengeContract.Progress.TABLE_NAME+
                        " ON "+challengeContract.Challenge.TABLE_NAME+"."+
                        challengeContract.Challenge._ID+" = "+
                        challengeContract.Progress.TABLE_NAME+"."+
                        challengeContract.Progress.COLUMN_C_ID
        );



        
    }
    /*------building selection clause here---------------------*/

    //challenge.name=?
    private static final String qChallengeNameSelection=
            challengeContract.Challenge.TABLE_NAME+"."+challengeContract.Challenge.COLUMN_C_NAME+" = ? ";

    //challenge.name=? AND day=?
    private static final String qChallengewithDay=
            challengeContract.Challenge.TABLE_NAME+"."+challengeContract.Challenge.COLUMN_C_NAME+" = ? AND "+
                    challengeContract.Progress.TABLE_NAME+"."+challengeContract.Progress.COLUMN_C_DAY+" = ? ";

    private static final String qStatusSelection=
            challengeContract.Progress.TABLE_NAME+"."+challengeContract.Progress.COLUMN_STATUS+" = ? ";

    private Cursor getDayofProgress(Uri uri,String[] projection,String sortOrder){
        String ChallengeName=challengeContract.Progress.getNamefromUri(uri);
        long day=challengeContract.Progress.getDaybyNamefromUri(uri);

        return  qProgressByChallengeQueryBuilder.query(cHelper.getReadableDatabase(),
                projection,
                qChallengewithDay,
                new String[]{ChallengeName,Long.toString(day)},
                null,
                null,
                sortOrder
                );
    }

    private Cursor getByName(Uri uri,String[] projection,String sortOrder){
        String ChallengeName=challengeContract.Progress.getNamefromUri(uri);

        return qProgressByChallengeQueryBuilder.query(cHelper.getReadableDatabase(),
                projection,
                qChallengeNameSelection,
                new String[]{ChallengeName},
                null,
                null,
                sortOrder
        );
    }

    private Cursor getCode(Uri uri,String[] projection,String sortOrder){
        int code=challengeContract.Progress.statusCode(uri);
        return qProgressByChallengeQueryBuilder.query(cHelper.getReadableDatabase(),
                projection,
                qStatusSelection,
                new String[]{Integer.toString(code)},
                null,
                null,
                sortOrder
        );
    }
    static UriMatcher buildUriMatcher(){
        final UriMatcher matcher=new UriMatcher(UriMatcher.NO_MATCH);
        final String authority=challengeContract.CONTENT_AUTHORITY;

        matcher.addURI(authority,challengeContract.PATH_CHALLENGE,CHALLENGE);
        matcher.addURI(authority,challengeContract.PATH_PROGRESS,PROGRESS );
        matcher.addURI(authority,challengeContract.PATH_CHALLENGE+"/*",CHALLENGE_BY_NAME);
        matcher.addURI(authority,challengeContract.PATH_PROGRESS+"/*/*",PROGRESS_BY_DAY );

        return matcher;
    }
    @Override
    public boolean onCreate(){
        cHelper=new challengeDbHelper(getContext());
        return true;
    }


    @Override
    public String getType(Uri uri){

        final int match= uriWizard.match(uri);

        switch(match){
            case CHALLENGE:
                return challengeContract.Challenge.CONTENT_TYPE;
            case CHALLENGE_BY_NAME:
                return challengeContract.Challenge.CONTENT_ITEM_TYPE;
            case PROGRESS:
                return  challengeContract.Progress.CONTENT_TYPE;
            case PROGRESS_BY_DAY:
                return challengeContract.Progress.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("2:Unknown Uri:"+uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor retCursor;
        switch (uriWizard.match(uri)) {
            // "progress/*/#"
            case PROGRESS_BY_DAY:
            {
                retCursor = getCode(uri, projection, sortOrder);
                break;
            }
            // "challenge/*"
            case CHALLENGE_BY_NAME: {
                retCursor = getByName(uri, projection, sortOrder);
                break;
            }
            // "challenge"
            case CHALLENGE: {
                retCursor = cHelper.getReadableDatabase().query(
                        challengeContract.Challenge.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            // "progress"
            case PROGRESS: {
                retCursor = cHelper.getReadableDatabase().query(
                        challengeContract.Progress.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }

            default:
                throw new UnsupportedOperationException("1:Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public Uri insert(Uri uri , ContentValues values){
        final SQLiteDatabase db=cHelper.getWritableDatabase();
        final int match=uriWizard.match(uri);
        Uri returnUri;

        switch(match){
            case CHALLENGE:{
                long _id=db.insert(challengeContract.Challenge.TABLE_NAME,null,values);
                if(_id>0)
                    returnUri=challengeContract.Challenge.buildChallengeUri(_id);
                else
                    throw new android.database.SQLException("failed to insert row in "+uri);
                break;
                }

            case PROGRESS:{
                long _id=db.insert(challengeContract.Progress.TABLE_NAME,null,values);
                if(_id>0)
                    returnUri=challengeContract.Progress.buildProgressUri(_id);
                else
                    throw new android.database.SQLException("failed to insert row in "+uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: "+uri);
            }

        getContext().getContentResolver().notifyChange(uri,null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri,String selection,String[] selectionArgs){
        final SQLiteDatabase db=cHelper.getWritableDatabase();
        final int match=uriWizard.match(uri);
        int rowsDeleted;

        if(null==selection) selection="1";
        switch(match) {
            case CHALLENGE:
                rowsDeleted = db.delete(challengeContract.Challenge.TABLE_NAME, selection, selectionArgs);
                break;
            case PROGRESS:
                rowsDeleted = db.delete(challengeContract.Progress.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri " + uri);

        }
        if(rowsDeleted!=0){
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = cHelper.getWritableDatabase();
        final int match = uriWizard.match(uri);
        int rowsUpdated;

        switch (match) {
            case CHALLENGE:
                rowsUpdated = db.update(challengeContract.Challenge.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            case PROGRESS:
                rowsUpdated = db.update(challengeContract.Progress.TABLE_NAME, values, selection,
                        selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

}
