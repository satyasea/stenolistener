package com.blake.db;

/**
 * Created by blake on 6/23/14.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.blake.voice.tasks.ActionCommand;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class MySQLiteHelper extends SQLiteOpenHelper {

    // Books table name
    private static final String TABLE_COMMANDS = "commands";

    // Books Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_CMD = "command";
    private static final String KEY_RECIP = "recipient";
    private static final String KEY_TIME = "actiontime";

    private static final String[] COLUMNS = {KEY_ID,KEY_CMD,KEY_RECIP, KEY_TIME};




    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "cmd_db";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL statement to create book table
        String CREATE_COMMAND_TABLE = "CREATE TABLE commands ( " +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "command TEXT, "+
                "recipient TEXT, "+
                "actiontime DATETIME)";

        // create books table
        db.execSQL(CREATE_COMMAND_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older books table if existed
        db.execSQL("DROP TABLE IF EXISTS commands");

        // create fresh books table
        this.onCreate(db);
    }


    public void addCommand(ActionCommand act){
        //for logging
      //  Log.d("add cmd", act.toString());

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(KEY_CMD, act.getAction()); // get title
        values.put(KEY_RECIP, act.getRecipient()); // get author
        values.put(KEY_TIME, act.getActionTime().toString()); // get author
        // 3. insert
        db.insert(TABLE_COMMANDS, // table
                null, //nullColumnHack
                values); // key/value -> keys = column names/ values = column values
        db.close();
    }



    public List<ActionCommand> getAllCommands() {
        List<ActionCommand> cmdsList = new LinkedList<ActionCommand>();
        String query = "SELECT  * FROM " + TABLE_COMMANDS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        ActionCommand cmd = null;
        if (cursor.moveToFirst()) {
            do {
                cmd = new ActionCommand();
                cmd.setAction(cursor.getString(1));
                cmd.setRecipient(cursor.getString(2));



             //   sb.append("On " + ft.format(act.getActionTime()) + ", you " + act.getAction() + " " + act.getRecipient()+". ");

                Calendar t = new GregorianCalendar();
                SimpleDateFormat ft =new SimpleDateFormat("EEE MMM d HH:mm:ss z yyyy", Locale.US);
                Date dt = null; //replace 4 with the column index
                try {
                    dt = ft.parse(cursor.getString(3));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                t.setTime(dt);

                cmd.setActionTime(t.getTime());





                cmdsList.add(cmd);
            } while (cursor.moveToNext());
        }
        Log.d("getAllCmds()", cmdsList.toString());
        return cmdsList;
    }





}
