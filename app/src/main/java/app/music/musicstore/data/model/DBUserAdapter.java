package app.music.musicstore.data.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static app.music.musicstore.GlobalDefinitions.g_externalStorageDownloadPath;


public class DBUserAdapter {
    public static final String KEY_ROWID = "_id";
    public static final String KEY_USERNAME= "username";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_DISPLAYNAME = "displayname";
    private static final String TAG = "DBAdapter";

    private static final String DATABASE_NAME = "usersdb";
    private static final String DATABASE_TABLE = "users";
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_CREATE =
            "create table users (_id integer primary key autoincrement, "
                    + "username text not null, "
                    + "displayname text not null,"
                    + "password text not null);";

    private Context context = null;
    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;

    public DBUserAdapter(Context ctx)
    {
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }



    private static class DatabaseHelper extends SQLiteOpenHelper
    {
        DatabaseHelper(Context context)
        {
            //shantanu change this to private cache
            super(context, g_externalStorageDownloadPath+DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
            Log.w(TAG, "Upgrading database from version " + oldVersion
                    + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS users");
            onCreate(db);
        }
    }

    public void open() throws SQLException
    {
        db = DBHelper.getWritableDatabase();
    }

    public void close()
    {
        DBHelper.close();
    }

    public long AddUser(String username, String password, String displayname)
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_USERNAME, username);
        initialValues.put(KEY_PASSWORD, password);
        initialValues.put(KEY_DISPLAYNAME, displayname);
        return db.insert(DATABASE_TABLE, null, initialValues);

    }

    public boolean Logout(String username) throws SQLException
    {
        boolean status = true;
        status = db.delete(DATABASE_TABLE, KEY_USERNAME + "=?", new String[]{username}) > 0;

        //shantanu
        System.out.println("!!!!!!!! Shantanu delete successful status = "+ status);
        return status;
    }

    public LoggedInUser Login() throws SQLException
    {
        LoggedInUser user;
        //Cursor mCursor = db.rawQuery("SELECT * FROM " + DATABASE_TABLE + " WHERE username=? AND password=?", new String[]{username,password});
        Cursor mCursor = db.rawQuery("SELECT * FROM " + DATABASE_TABLE, null);

        if (mCursor != null) {
            if(mCursor.getCount() > 0)
            {
                String username = "";
                String password = "";
                String displayname = "";

                if (mCursor.moveToFirst())
                {
                    username = mCursor.getString(mCursor.getColumnIndex(KEY_USERNAME));
                    password = mCursor.getString(mCursor.getColumnIndex(KEY_PASSWORD));
                    displayname = mCursor.getString(mCursor.getColumnIndex(KEY_DISPLAYNAME));
                }

                user = new LoggedInUser(username, displayname);
                user.setPassword(password);

                System.out.println("@@@@@@@@ user exists in DB");
                System.out.println("\nuserid = " + username +
                "\n password = " + password +
                "\n displayname = " + displayname);

                return user;
            }
        }
        System.out.println("@@@@@@@@ user does not exist in db");
        return null;
    }



}
