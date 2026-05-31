package com.carebuddy;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "carebuddy.db";
    private static final int DATABASE_VERSION = 1;

    // ─── Medicine Table ───────────────────────────────────────────────────────
    public static final String TABLE_MEDICINES       = "medicines";
    public static final String COL_MED_ID            = "id";
    public static final String COL_MED_NAME          = "name";
    public static final String COL_MED_DOSAGE        = "dosage";
    public static final String COL_MED_TIME          = "time";       // e.g. "Morning"
    public static final String COL_MED_REPEAT        = "repeat";     // e.g. "Every Day"
    public static final String COL_MED_START_DATE    = "start_date";
    public static final String COL_MED_END_DATE      = "end_date";
    public static final String COL_MED_REMINDER_ON   = "reminder_on";// 1 or 0
    public static final String COL_MED_USER_EMAIL    = "user_email";

    // ─── Health Log Table ─────────────────────────────────────────────────────
    public static final String TABLE_HEALTH_LOGS     = "health_logs";
    public static final String COL_LOG_ID            = "id";
    public static final String COL_LOG_METRIC        = "metric";     // e.g. "Blood Pressure"
    public static final String COL_LOG_VALUE         = "value";
    public static final String COL_LOG_DATE          = "date";
    public static final String COL_LOG_USER_EMAIL    = "user_email";

    // ─── User Table ───────────────────────────────────────────────────────────
    public static final String TABLE_USERS           = "users";
    public static final String COL_USER_ID           = "id";
    public static final String COL_USER_NAME         = "name";
    public static final String COL_USER_EMAIL        = "email";
    public static final String COL_USER_PASSWORD     = "password";
    public static final String COL_USER_ROLE         = "role";       // "senior","family","doctor"
    public static final String COL_USER_AGE          = "age";
    public static final String COL_USER_BLOOD_GROUP  = "blood_group";
    public static final String COL_USER_EMERGENCY_NAME  = "emergency_name";
    public static final String COL_USER_EMERGENCY_PHONE = "emergency_phone";
    public static final String COL_USER_CONDITIONS   = "conditions"; // comma-separated
    public static final String COL_USER_ALLERGIES    = "allergies";  // comma-separated

    // ─── Mood Table ───────────────────────────────────────────────────────────
    public static final String TABLE_MOODS           = "moods";
    public static final String COL_MOOD_ID           = "id";
    public static final String COL_MOOD_VALUE        = "mood";       // Great/Good/Okay/Sad
    public static final String COL_MOOD_NOTE         = "note";
    public static final String COL_MOOD_DATE         = "date";
    public static final String COL_MOOD_USER_EMAIL   = "user_email";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_USERS + " (" +
                COL_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_USER_NAME + " TEXT, " +
                COL_USER_EMAIL + " TEXT UNIQUE, " +
                COL_USER_PASSWORD + " TEXT, " +
                COL_USER_ROLE + " TEXT, " +
                COL_USER_AGE + " TEXT, " +
                COL_USER_BLOOD_GROUP + " TEXT, " +
                COL_USER_EMERGENCY_NAME + " TEXT, " +
                COL_USER_EMERGENCY_PHONE + " TEXT, " +
                COL_USER_CONDITIONS + " TEXT, " +
                COL_USER_ALLERGIES + " TEXT)");

        db.execSQL("CREATE TABLE " + TABLE_MEDICINES + " (" +
                COL_MED_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_MED_NAME + " TEXT, " +
                COL_MED_DOSAGE + " TEXT, " +
                COL_MED_TIME + " TEXT, " +
                COL_MED_REPEAT + " TEXT, " +
                COL_MED_START_DATE + " TEXT, " +
                COL_MED_END_DATE + " TEXT, " +
                COL_MED_REMINDER_ON + " INTEGER DEFAULT 1, " +
                COL_MED_USER_EMAIL + " TEXT)");

        db.execSQL("CREATE TABLE " + TABLE_HEALTH_LOGS + " (" +
                COL_LOG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_LOG_METRIC + " TEXT, " +
                COL_LOG_VALUE + " TEXT, " +
                COL_LOG_DATE + " TEXT, " +
                COL_LOG_USER_EMAIL + " TEXT)");

        db.execSQL("CREATE TABLE " + TABLE_MOODS + " (" +
                COL_MOOD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_MOOD_VALUE + " TEXT, " +
                COL_MOOD_NOTE + " TEXT, " +
                COL_MOOD_DATE + " TEXT, " +
                COL_MOOD_USER_EMAIL + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEDICINES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HEALTH_LOGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MOODS);
        onCreate(db);
    }

    // ══════════════════════════════════════════════════════════════════════════
    // USER OPERATIONS
    // ══════════════════════════════════════════════════════════════════════════

    public boolean registerUser(String name, String email, String password, String role) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_USER_NAME, name);
        cv.put(COL_USER_EMAIL, email);
        cv.put(COL_USER_PASSWORD, password);
        cv.put(COL_USER_ROLE, role);
        long result = db.insert(TABLE_USERS, null, cv);
        return result != -1;
    }

    public boolean loginUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + TABLE_USERS +
                " WHERE " + COL_USER_EMAIL + "=? AND " + COL_USER_PASSWORD + "=?",
                new String[]{email, password});
        boolean found = cursor.getCount() > 0;
        cursor.close();
        return found;
    }

    public String getUserRole(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT " + COL_USER_ROLE + " FROM " + TABLE_USERS +
                " WHERE " + COL_USER_EMAIL + "=?", new String[]{email});
        if (cursor.moveToFirst()) {
            String role = cursor.getString(0);
            cursor.close();
            return role;
        }
        cursor.close();
        return "";
    }

    public User getUser(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_USERS +
                " WHERE " + COL_USER_EMAIL + "=?", new String[]{email});
        User user = null;
        if (c.moveToFirst()) {
            user = new User();
            user.setName(c.getString(c.getColumnIndexOrThrow(COL_USER_NAME)));
            user.setEmail(c.getString(c.getColumnIndexOrThrow(COL_USER_EMAIL)));
            user.setRole(c.getString(c.getColumnIndexOrThrow(COL_USER_ROLE)));
            user.setAge(c.getString(c.getColumnIndexOrThrow(COL_USER_AGE)));
            user.setBloodGroup(c.getString(c.getColumnIndexOrThrow(COL_USER_BLOOD_GROUP)));
            user.setEmergencyName(c.getString(c.getColumnIndexOrThrow(COL_USER_EMERGENCY_NAME)));
            user.setEmergencyPhone(c.getString(c.getColumnIndexOrThrow(COL_USER_EMERGENCY_PHONE)));
            user.setConditions(c.getString(c.getColumnIndexOrThrow(COL_USER_CONDITIONS)));
            user.setAllergies(c.getString(c.getColumnIndexOrThrow(COL_USER_ALLERGIES)));
        }
        c.close();
        return user;
    }

    public boolean updateUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_USER_NAME, user.getName());
        cv.put(COL_USER_AGE, user.getAge());
        cv.put(COL_USER_BLOOD_GROUP, user.getBloodGroup());
        cv.put(COL_USER_EMERGENCY_NAME, user.getEmergencyName());
        cv.put(COL_USER_EMERGENCY_PHONE, user.getEmergencyPhone());
        cv.put(COL_USER_CONDITIONS, user.getConditions());
        cv.put(COL_USER_ALLERGIES, user.getAllergies());
        int rows = db.update(TABLE_USERS, cv, COL_USER_EMAIL + "=?",
                new String[]{user.getEmail()});
        return rows > 0;
    }

    // ══════════════════════════════════════════════════════════════════════════
    // MEDICINE OPERATIONS (CRUD)
    // ══════════════════════════════════════════════════════════════════════════

    public long addMedicine(Medicine med) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_MED_NAME, med.getName());
        cv.put(COL_MED_DOSAGE, med.getDosage());
        cv.put(COL_MED_TIME, med.getTime());
        cv.put(COL_MED_REPEAT, med.getRepeat());
        cv.put(COL_MED_START_DATE, med.getStartDate());
        cv.put(COL_MED_END_DATE, med.getEndDate());
        cv.put(COL_MED_REMINDER_ON, med.isReminderOn() ? 1 : 0);
        cv.put(COL_MED_USER_EMAIL, med.getUserEmail());
        return db.insert(TABLE_MEDICINES, null, cv);
    }

    public List<Medicine> getMedicines(String userEmail) {
        List<Medicine> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_MEDICINES +
                " WHERE " + COL_MED_USER_EMAIL + "=?", new String[]{userEmail});
        if (c.moveToFirst()) {
            do {
                Medicine m = new Medicine();
                m.setId(c.getInt(c.getColumnIndexOrThrow(COL_MED_ID)));
                m.setName(c.getString(c.getColumnIndexOrThrow(COL_MED_NAME)));
                m.setDosage(c.getString(c.getColumnIndexOrThrow(COL_MED_DOSAGE)));
                m.setTime(c.getString(c.getColumnIndexOrThrow(COL_MED_TIME)));
                m.setRepeat(c.getString(c.getColumnIndexOrThrow(COL_MED_REPEAT)));
                m.setStartDate(c.getString(c.getColumnIndexOrThrow(COL_MED_START_DATE)));
                m.setEndDate(c.getString(c.getColumnIndexOrThrow(COL_MED_END_DATE)));
                m.setReminderOn(c.getInt(c.getColumnIndexOrThrow(COL_MED_REMINDER_ON)) == 1);
                m.setUserEmail(c.getString(c.getColumnIndexOrThrow(COL_MED_USER_EMAIL)));
                list.add(m);
            } while (c.moveToNext());
        }
        c.close();
        return list;
    }

    public boolean updateMedicine(Medicine med) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_MED_NAME, med.getName());
        cv.put(COL_MED_DOSAGE, med.getDosage());
        cv.put(COL_MED_TIME, med.getTime());
        cv.put(COL_MED_REPEAT, med.getRepeat());
        cv.put(COL_MED_START_DATE, med.getStartDate());
        cv.put(COL_MED_END_DATE, med.getEndDate());
        cv.put(COL_MED_REMINDER_ON, med.isReminderOn() ? 1 : 0);
        return db.update(TABLE_MEDICINES, cv, COL_MED_ID + "=?",
                new String[]{String.valueOf(med.getId())}) > 0;
    }

    public boolean deleteMedicine(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_MEDICINES, COL_MED_ID + "=?",
                new String[]{String.valueOf(id)}) > 0;
    }

    // ══════════════════════════════════════════════════════════════════════════
    // HEALTH LOG OPERATIONS (CRUD)
    // ══════════════════════════════════════════════════════════════════════════

    public long addHealthLog(HealthLog log) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_LOG_METRIC, log.getMetric());
        cv.put(COL_LOG_VALUE, log.getValue());
        cv.put(COL_LOG_DATE, log.getDate());
        cv.put(COL_LOG_USER_EMAIL, log.getUserEmail());
        return db.insert(TABLE_HEALTH_LOGS, null, cv);
    }

    public List<HealthLog> getHealthLogs(String userEmail) {
        List<HealthLog> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_HEALTH_LOGS +
                " WHERE " + COL_LOG_USER_EMAIL + "=? ORDER BY " + COL_LOG_DATE + " DESC",
                new String[]{userEmail});
        if (c.moveToFirst()) {
            do {
                HealthLog log = new HealthLog();
                log.setId(c.getInt(c.getColumnIndexOrThrow(COL_LOG_ID)));
                log.setMetric(c.getString(c.getColumnIndexOrThrow(COL_LOG_METRIC)));
                log.setValue(c.getString(c.getColumnIndexOrThrow(COL_LOG_VALUE)));
                log.setDate(c.getString(c.getColumnIndexOrThrow(COL_LOG_DATE)));
                log.setUserEmail(c.getString(c.getColumnIndexOrThrow(COL_LOG_USER_EMAIL)));
                list.add(log);
            } while (c.moveToNext());
        }
        c.close();
        return list;
    }

    public boolean deleteHealthLog(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_HEALTH_LOGS, COL_LOG_ID + "=?",
                new String[]{String.valueOf(id)}) > 0;
    }

    // ══════════════════════════════════════════════════════════════════════════
    // MOOD OPERATIONS
    // ══════════════════════════════════════════════════════════════════════════

    public long addMood(String mood, String note, String date, String userEmail) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_MOOD_VALUE, mood);
        cv.put(COL_MOOD_NOTE, note);
        cv.put(COL_MOOD_DATE, date);
        cv.put(COL_MOOD_USER_EMAIL, userEmail);
        return db.insert(TABLE_MOODS, null, cv);
    }

    public List<String[]> getMoods(String userEmail) {
        List<String[]> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT " + COL_MOOD_VALUE + ", " + COL_MOOD_NOTE +
                ", " + COL_MOOD_DATE + " FROM " + TABLE_MOODS +
                " WHERE " + COL_MOOD_USER_EMAIL + "=? ORDER BY " + COL_MOOD_DATE + " DESC",
                new String[]{userEmail});
        if (c.moveToFirst()) {
            do {
                list.add(new String[]{c.getString(0), c.getString(1), c.getString(2)});
            } while (c.moveToNext());
        }
        c.close();
        return list;
    }
}
