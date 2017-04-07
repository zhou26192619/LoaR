package com.loar.storage;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.ArrayList;

/**
 * 暂时未用到，数据库操作
 */
public class CommonProvider extends ContentProvider {

    //主机名，authority
    public static final String AUTHORITY = "com.loar.CommonProvider";

    public static final String TABLE_OPTIONS = "options"; //背景图片表
    public static final String TABLE_IMAGES = "images";

    public static final Uri URI_OPTIONS = Uri.parse("content://" + AUTHORITY + "/" + TABLE_OPTIONS);//配置表的uri
    public static final Uri URI_IMAGES = Uri.parse("content://" + AUTHORITY + "/" + TABLE_IMAGES);//图片表的uri

    private SQLiteOpenHelper mOpenHelper;

    @Override
    public boolean onCreate() {
        mOpenHelper = initHelper(getContext());
        return true;
    }

    /**
     * 需要自定义时，只要覆盖该方法，返回自己的helper即可
     *
     * @param context
     * @return
     */
    public SQLiteOpenHelper initHelper(Context context) {
        return new DatabaseHelper(context, DatabaseHelper.DATABASE_NAME, null, DatabaseHelper.DATABASE_VERSION);
    }


    @Override
    public String getType(Uri uri) {
        SqlArguments args = new SqlArguments(uri, null, null);
        if (TextUtils.isEmpty(args.where)) {
            return "vnd.android.cursor.dir/" + args.table;
        } else {
            return "vnd.android.cursor.item/" + args.table;
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor result = null;
        try {
            SqlArguments args = new SqlArguments(uri, selection, selectionArgs);
            SQLiteDatabase db = mOpenHelper.getReadableDatabase();
            SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
            qb.setTables(args.table);
            result = qb.query(db, projection, args.where, args.args, null, null, sortOrder);
            ContentResolver cr = getContext().getContentResolver();
            if (cr != null) {
                result.setNotificationUri(cr, uri);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        try {
            SqlArguments args = new SqlArguments(uri);
            SQLiteDatabase db = mOpenHelper.getWritableDatabase();
            long id = db.insert(args.table, null, values);
            if (id <= 0) {
                return null;
            }
            uri = ContentUris.withAppendedId(uri, id);
            ContentResolver cr = getContext().getContentResolver();
            if (cr != null) {
                cr.notifyChange(uri, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return uri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count = 0;
        try {
            SqlArguments args = new SqlArguments(uri, selection, selectionArgs);
            SQLiteDatabase db = mOpenHelper.getWritableDatabase();
            count = db.delete(args.table, args.where, args.args);
            if (count > 0) {
                ContentResolver cr = getContext().getContentResolver();
                if (cr != null) {
                    cr.notifyChange(uri, null);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int count = 0;
        try {
            SqlArguments args = new SqlArguments(uri, selection, selectionArgs);
            SQLiteDatabase db = mOpenHelper.getWritableDatabase();
            count = db.update(args.table, values, args.where, args.args);
            if (count > 0) {
                ContentResolver cr = getContext().getContentResolver();
                if (cr != null) {
                    cr.notifyChange(uri, null);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    /**
     * 复写 使其支持事务
     *
     * @param operations
     * @return
     * @throws OperationApplicationException
     */
    @NonNull
    @Override
    public ContentProviderResult[] applyBatch(@NonNull ArrayList<ContentProviderOperation> operations)
            throws OperationApplicationException {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        db.beginTransaction();//开始事务
        try {
            ContentProviderResult[] results = super.applyBatch(operations);
            db.setTransactionSuccessful();//设置事务标记为successful
            return results;
        } finally {
            db.endTransaction();//结束事务
        }
    }

    public interface OptionsColumns extends BaseColumns {
        String VALUE = "value";
        String KEY = "key";
        String TAG = "tag";
    }

    public interface ImagesColumns extends BaseColumns {
        String IMAGE = "image";
        String URL = "url";
        String TAG = "tag";
    }

    public class SqlArguments {
        public String table;
        public String where;
        public String[] args;

        SqlArguments(Uri uri, String where, String[] args) {
            if (uri.getPathSegments().size() == 1) {
                this.table = uri.getPathSegments().get(0);
                this.where = where;
                this.args = args;
            } else if (uri.getPathSegments().size() != 2) {
                throw new IllegalArgumentException("Invalid URI: " + uri);
            } else {
                this.table = uri.getPathSegments().get(0);
                this.where = "_id=" + ContentUris.parseId(uri);
                this.where += where == null ? "" : " and " + where;
                this.args = args;
            }
        }

        SqlArguments(Uri uri) {
            this(uri, null, null);
        }
    }
}
