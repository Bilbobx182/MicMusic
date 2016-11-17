
package combilbobx182.github.micmusic2;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.sql.SQLException;

public class DBManager
{
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "TEST3.db";

    private static final String KEY_ID = "_id";
    private static final String KEY_SENSITIVITY = "sensitivity";
    private static final String TABLE_SENSITIVITY = "Sensitivity";
    private static final String CREATE_SENSITIVITY_TABLE = "CREATE TABLE " + TABLE_SENSITIVITY + "(_id INTEGER PRIMARY KEY autoincrement,sensitivity TEXT);";

    private static final String KEY_STAT_ID = "_id";
    private static final String KEY_STAT= "stat";
    private static final String TABLE_STAT= "Stat";
    private static final String CREATE_STAT_TABLE = "CREATE TABLE " + TABLE_STAT + "(_id INTEGER PRIMARY KEY autoincrement,stat TEXT);";

    private final Context context;
    private MyDatabaseHelper DBHelper;
    private SQLiteDatabase db;

    // we must pass the context from our class that we called from
    public DBManager(Context ctx)
    {
        this.context = ctx;
        DBHelper = new MyDatabaseHelper(context);
    }

    private static class MyDatabaseHelper extends SQLiteOpenHelper
    {

        public MyDatabaseHelper(Context context)
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            db.execSQL(CREATE_SENSITIVITY_TABLE);
            db.execSQL(CREATE_STAT_TABLE);

            for(int count=10; count<=100;count+=10)
            {
                addSensitivity(db,String.valueOf(count));
            }
            addStat(db,"0");
        }

        public void addStat(SQLiteDatabase db,String value)
        {
            ContentValues values = new ContentValues();
            values.put(KEY_STAT, value);
            db.insert(TABLE_STAT, null, values);
        }

        public void addSensitivity(SQLiteDatabase db, String value)
        {
            ContentValues initialValues = new ContentValues();
            initialValues.put(KEY_SENSITIVITY,value);
            db.insert(TABLE_SENSITIVITY, null, initialValues);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_SENSITIVITY);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_STAT);

            onCreate(db);
        }
    }

    public DBManager open() throws SQLException
    {
        db = DBHelper.getWritableDatabase();
        return this;
    }

    public void close()
    {
        DBHelper.close();
    }

    public void insertStat(String value)
    {
        Log.d("DBM","UPDATE "+ TABLE_STAT +" SET " + KEY_STAT + " = "+value + ";");
        db.execSQL("UPDATE "+ TABLE_STAT +" SET " + KEY_STAT + " = "+value + ";");
    }

    public Cursor getAll()
    {
        Cursor mCursor = db.rawQuery("SELECT * FROM "+TABLE_SENSITIVITY, null);

        if (mCursor != null)
        {
            mCursor.moveToFirst();
        }

        return mCursor;
    }

    public Cursor getStat()
    {
        Cursor mCursor = db.rawQuery("SELECT * FROM "+TABLE_STAT, null);

        if (mCursor != null)
        {
            mCursor.moveToFirst();
        }

        return mCursor;
    }

}