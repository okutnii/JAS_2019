package com.example.multitool.data;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

public class AlarmReminderContract {
    private AlarmReminderContract(){}

    public static final String CONTENT_AUTHORITY = "com.example.multitool";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_VEHICLE = "reminder-path";

    public static final class AlarmReminderEntry implements BaseColumns {

        public static final  Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_VEHICLE);

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE+ "/" + CONTENT_AUTHORITY + "/" + PATH_VEHICLE;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE+ "/" + CONTENT_AUTHORITY + "/" + PATH_VEHICLE;

        public final static String TABLE_NAME = "schedule";

        public final static String _ID = BaseColumns._ID;

        public static final String KEY_DESCRIPTION = "description";
        public static final String KEY_DAY = "day";
        public static final String KEY_HOURS = "hours";
        public static final String KEY_MINUTES = "minutes";
        public static final String KEY_ACTIVE_VOICE = "voice";
    }

    public static String getColumnString(Cursor cursor, String columnName){
        return  cursor.getString(cursor.getColumnIndex(columnName));
    }
}
