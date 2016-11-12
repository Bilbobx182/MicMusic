
package combilbobx182.github.micmusic2;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.sql.SQLException;

public class DBManager
{
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "SensitivityListValues.db";
    private static final String TABLE_SENSITIVITY = "Sensitivity";
    private static final String KEY_ID = "_id";
    private static final String KEY_SENSITIVITY = "sensitivity";
    private static final String CREATE_SENSITIVITY_TABLE = "CREATE TABLE " + TABLE_SENSITIVITY + "(_id INTEGER PRIMARY KEY autoincrement,sensitivity TEXT);";
    private final Context context;
    private MyDatabaseHelper DBHelper;
    private SQLiteDatabase db;

    // we must pass the context from our class that we called from
    public DBManager(Context ctx)
    {
        this.context = ctx;
        DBHelper = new MyDatabaseHelper(context);
    }

    //Used to test to see if a database exists for that dbname
    public static boolean dbtest(Context context, String dbName)
    {
        File dbFile = context.getDatabasePath(dbName);
        return dbFile.exists();
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
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_SENSITIVITY);

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

    public long insertSensitivity(String value)
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_SENSITIVITY,value);
        return db.insert(TABLE_SENSITIVITY, null, initialValues);
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

}