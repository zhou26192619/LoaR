package com.loar.storage;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;

/**
 * 对数据操作
 * Created by Justsy on 2016/8/24.
 */
public class ProviderParser {

    /**
     * 删除数据库,清空所有数据
     *
     * @param context
     */
    public static void deleteDatebase(Context context) {
//        context.deleteDatabase(CommonProvider.DATABASE_NAME);//数据库都删除了，但app未重建，所以数据库未重建
//        new CommonProvider.DatabaseHelper(context);
        try {
            ContentResolver cr = context.getContentResolver();
            cr.delete(CommonProvider.URI_IMAGES, null, null);
            cr.delete(CommonProvider.URI_OPTIONS, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getOption(Context context, String key, String tag) {
        if (key == null) {
            return null;
        }
        ContentResolver cr = context.getContentResolver();
        if (cr != null) {
            Cursor c = cr.query(CommonProvider.URI_OPTIONS, null,
                    CommonProvider.OptionsColumns.KEY + "=? and " + CommonProvider.OptionsColumns.TAG + "=?",
                    new String[]{key, tag}, null);
            try {
                if (c != null) {
                    if (c.moveToNext()) {
                        return c.getString(c.getColumnIndex(CommonProvider.OptionsColumns.VALUE));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (c != null) {
                    c.close();
                }
            }
        }
        return null;
    }

    public static String[] getOption(Context context, String key) {
        if (key == null) {
            return null;
        }
        String[] result;
        ContentResolver cr = context.getContentResolver();
        if (cr != null) {
            Cursor c = cr.query(CommonProvider.URI_OPTIONS, null,
                    CommonProvider.OptionsColumns.KEY + "=?", new String[]{
                            key}, null);
            try {
                if (c != null) {
                    if (c.moveToNext()) {
                        result = new String[2];
                        result[0] = c.getString(c.getColumnIndex(CommonProvider.OptionsColumns.VALUE));
                        result[1] = c.getString(c.getColumnIndex(CommonProvider.OptionsColumns.TAG));
                        return result;
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (c != null) {
                    c.close();
                }
            }
        }
        return null;
    }

    public static String getOptionValue(Context context, String key) {
        String[] vt = getOption(context, key);
        if (vt != null) {
            return vt[0];
        }
        return null;
    }


    public static int deleteOptions(Context context, String key) {
        if (key == null) {
            return 0;
        }
        ContentResolver cr = context.getContentResolver();
        if (cr != null) {
            return cr.delete(CommonProvider.URI_OPTIONS,
                    CommonProvider.OptionsColumns.KEY + "=?", new String[]{
                            key});
        }
        return 0;
    }

    public static int deleteOptions(Context context, String key, String tag) {
        if (key == null) {
            return 0;
        }
        ContentResolver cr = context.getContentResolver();
        if (cr != null) {
            return cr.delete(CommonProvider.URI_OPTIONS,
                    CommonProvider.OptionsColumns.KEY + "=? and " + CommonProvider.OptionsColumns.TAG + "=?",
                    new String[]{key, tag});
        }
        return 0;
    }

    public static void saveOption(Context context, String key, String value, String tag) {
        if (key == null) {
            return;
        }
        ContentResolver cr = context.getContentResolver();
        if (cr != null) {
            Cursor c = cr.query(CommonProvider.URI_OPTIONS, null,
                    CommonProvider.OptionsColumns.KEY + "=?", new String[]{
                            key}, null);
            try {
                ContentValues values = new ContentValues();
                values.put(CommonProvider.OptionsColumns.KEY, key);
                values.put(CommonProvider.OptionsColumns.VALUE, value);
                values.put(CommonProvider.OptionsColumns.TAG, tag);
                if (c != null && c.moveToNext()) {
                    cr.update(CommonProvider.URI_OPTIONS, values,
                            CommonProvider.OptionsColumns.KEY + "=?", new String[]{
                                    key});
                } else {
                    cr.insert(CommonProvider.URI_OPTIONS, values);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (c != null) {
                    c.close();
                }
            }
        }
    }

    /**
     * 在已经存在的情况下，tag不会清
     *
     * @param context
     * @param key
     * @param value
     */
    public static void saveOption(Context context, String key, String value) {
        if (key == null) {
            return;
        }
        ContentResolver cr = context.getContentResolver();
        if (cr != null) {
            Cursor c = cr.query(CommonProvider.URI_OPTIONS, null,
                    CommonProvider.OptionsColumns.KEY + "=?", new String[]{
                            key}, null);
            try {
                ContentValues values = new ContentValues();
                values.put(CommonProvider.OptionsColumns.KEY, key);
                values.put(CommonProvider.OptionsColumns.VALUE, value);
                if (c != null && c.moveToNext()) {
                    cr.update(CommonProvider.URI_OPTIONS, values,
                            CommonProvider.OptionsColumns.KEY + "=?", new String[]{
                                    key});
                } else {
                    cr.insert(CommonProvider.URI_OPTIONS, values);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (c != null) {
                    c.close();
                }
            }
        }
    }

    /**
     * 更新时，只匹配url
     * 保存背景图片
     *
     * @param context
     */
    public static int saveImage(Context context, String url, Bitmap bm, String tag) {
        try {
            ContentResolver cr = context.getContentResolver();
            if (cr != null) {
                Cursor c = cr.query(CommonProvider.URI_IMAGES, null, CommonProvider.ImagesColumns.URL + "=?", new String[]{url}, null);
                ContentValues values = new ContentValues();
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.PNG, 100, out);
                values.put(CommonProvider.ImagesColumns.IMAGE, out.toByteArray());
                values.put(CommonProvider.ImagesColumns.TAG, tag);
                values.put(CommonProvider.ImagesColumns.URL, url);
                if (c != null && c.getCount() <= 0) {
                    cr.insert(CommonProvider.URI_IMAGES, values);
                    return 1;
                } else {
                    return cr.update(CommonProvider.URI_IMAGES, values, CommonProvider.ImagesColumns.URL + "=?", new String[]{url});
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
