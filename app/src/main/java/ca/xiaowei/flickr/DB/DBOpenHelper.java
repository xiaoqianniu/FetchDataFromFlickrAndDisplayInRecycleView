package ca.xiaowei.flickr.DB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import ca.xiaowei.flickr.Model.Photo;

public class DBOpenHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "photo.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_PHOTOS = "photos";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_OWNER = "owner";
    private static final String COLUMN_SECRET = "secret";
    private static final String COLUMN_SERVER = "server";
    private static final String COLUMN_FARM = "farm";
    public DBOpenHelper(Context context){
        // Create the Database
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableQuery = "CREATE TABLE " + TABLE_PHOTOS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY,"
                + COLUMN_TITLE + " TEXT,"
                + COLUMN_OWNER + " TEXT,"
                + COLUMN_SECRET + " TEXT,"
                + COLUMN_SERVER + " TEXT,"
                + COLUMN_FARM + " TEXT"
                + ")";
        db.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PHOTOS);
        onCreate(db);
    }

    public void addPhoto(Photo photo) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, photo.getTitle());
        values.put(COLUMN_OWNER, photo.getOwner());
        values.put(COLUMN_SECRET, photo.getSecret());
        values.put(COLUMN_SERVER, photo.getServer());
        values.put(COLUMN_FARM, photo.getFarm());

        db.insert(TABLE_PHOTOS, null, values);
        db.close();
    }
    public List<Photo> getAllPhotos() {
        List<Photo> photos = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_PHOTOS, null, null, null, null, null, null);

        int idIndex = cursor.getColumnIndex(COLUMN_ID);
        int titleIndex = cursor.getColumnIndex(COLUMN_TITLE);
        int ownerIndex = cursor.getColumnIndex(COLUMN_OWNER);
        int secretIndex = cursor.getColumnIndex(COLUMN_SECRET);
        int serverIndex = cursor.getColumnIndex(COLUMN_SERVER);
        int farmIndex = cursor.getColumnIndex(COLUMN_FARM);

        while (cursor.moveToNext()) {
            String id = cursor.getString(idIndex);
            String title = cursor.getString(titleIndex);
            String owner = cursor.getString(ownerIndex);
            String secret = cursor.getString(secretIndex);
            String server = cursor.getString(serverIndex);
            String farm = cursor.getString(farmIndex);

            Photo photo = new Photo(id, title, owner, secret, server,farm);
            photos.add(photo);
        }

        cursor.close();
        db.close();

        return photos;
    }
}
