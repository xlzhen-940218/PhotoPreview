package com.imobie.photopreview;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IModel<T> {
    private Context context;
    private String uri;
    private Class<T> cls;

    public IModel(Context context,String uri, Class<T> cls) {
        this.context=context;
        this.uri = uri;
        this.cls = cls;
    }

    public List<T> queryAll() {
        List<Map<String, Object>> maps = new ArrayList<>();
        ContentResolver contentResolver = context.getContentResolver();
        Cursor cursor = null;
        try {
            cursor = contentResolver.query(Uri.parse(uri), null,
                    null, null, null);
            if (cursor == null) {
                return null;
            }
            if (cursor.moveToFirst()) {

                do {
                    Map<String, Object> map = new HashMap<>();
                    for (int i = 0; i < cursor.getColumnCount(); i++) {
                        try {
                            map.put(cursor.getColumnName(i), cursor.getString(i));
                        } catch (Exception ex) {
                            map.put(cursor.getColumnName(i), cursor.getBlob(i));
                        }

                    }
                    maps.add(map);
                }
                while (cursor.moveToNext());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        String map = FastTransJson.toJson(maps);
        return FastTransJson.fromToListJson(map, cls);
    }
}