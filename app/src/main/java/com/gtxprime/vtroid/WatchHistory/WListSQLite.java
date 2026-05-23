package com.gtxprime.vtroid.WatchHistory;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.gtxprime.vtroid.Search.VisitedPages;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WListSQLite extends SQLiteOpenHelper {
    private final String watched_movies = "watched_movies";
    private final SQLiteDatabase dB;

    public WListSQLite(Context context) {
        super(context, "WHistory.db", null, 1);
        dB = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE watched_movies (title TEXT, link TEXT, time TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addPageToHistory(com.gtxprime.vtroid.WatchHistory.Watched page) {
        ContentValues v = new ContentValues();
        v.put("title", page.title);
        v.put("link", page.link);
        Date time = Calendar.getInstance().getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy MM dd HH mm ss SSS",
                Locale.getDefault());
        v.put("time", simpleDateFormat.format(time));
        if (dB.update(watched_movies, v, "link = '" + page.link + "'", null) <= 0) {
            dB.insert(watched_movies, null, v);
        }
    }

    public void deleteFromHistory(String link) {
        dB.delete(watched_movies, "link = '" + link + "'", null);
    }

    public void clearHistory() {
        dB.execSQL("DELETE FROM watched_movies");
    }

    public List<VisitedPages> getAllVisitedPages() {
        Cursor c = dB.query(watched_movies, new String[]{"title", "link"}, null, null, null,
                null, "time DESC");
        List<VisitedPages> pages = new ArrayList<>();
        while (c.moveToNext()) {
            VisitedPages page = new VisitedPages();

            page.title = c.getString(c.getColumnIndex("title"));
            page.link = c.getString(c.getColumnIndex("link"));

            pages.add(page);
        }
        c.close();
        return pages;
    }

    public List<VisitedPages> getVisitedPagesByKeyword(String keyword) {
        Cursor c = dB.query(watched_movies, new String[]{"title", "link"}, "title LIKE '%" +
                keyword + "%'", null, null, null, "time DESC");
        List<VisitedPages> pages = new ArrayList<>();
        while (c.moveToNext()) {
            VisitedPages page = new VisitedPages();
            page.title = c.getString(c.getColumnIndex("title"));
            page.link = c.getString(c.getColumnIndex("link"));
            pages.add(page);
        }
        c.close();
        return pages;
    }
}
