package combilbobx182.github.micmusic2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.sql.SQLException;

public class DBManager
{
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Music.db";
    private static final String TABLE_TASKS = "Sensitivity";

    private static final String KEY_ID = "_id";
    private static final String KEY_SENSITIVITY = "status";

    private static final String CREATE_TASKS_TABLE = "CREATE TABLE Tasks (_id INTEGER PRIMARY KEY autoincrement, sensitivity TEXT);";

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
            db.execSQL(CREATE_TASKS_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
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

    public long insertSensitivity(String sensitivity)
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_SENSITIVITY, sensitivity);
        return db.insert(TABLE_TASKS, null, initialValues);
    }

    public Cursor getAll()
    {
        Cursor mCursor = db.rawQuery("SELECT * FROM Sensitivity", null);

        if (mCursor != null)
        {
            mCursor.moveToFirst();
        }
        return mCursor;
    }
}