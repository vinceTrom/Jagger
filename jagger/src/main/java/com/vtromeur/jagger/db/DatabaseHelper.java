package com.vtromeur.jagger.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.vtromeur.jagger.xmpp.XMPPMessage;

import java.sql.SQLException;

/**
 * Created by Vince on 25/03/16.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME = "messagedir.db";
    private static final int DATABASE_VERSION = 1;

    private static DatabaseHelper sDataBaseHelper;

    private Dao<XMPPMessage, Integer> mMessageDAO;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static DatabaseHelper getHelper() {
        return sDataBaseHelper;
    }

    public static DatabaseHelper init(Context pCtx) {
        if (sDataBaseHelper == null) {
            sDataBaseHelper = OpenHelperManager.getHelper(pCtx, DatabaseHelper.class);
        }
        return sDataBaseHelper;
    }

    public static void destroy() {
        if (sDataBaseHelper != null) {
            OpenHelperManager.releaseHelper();
            sDataBaseHelper = null;
        }
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, XMPPMessage.class);
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Unable to create datbass", e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, XMPPMessage.class, true);
            onCreate(database, connectionSource);
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Unable to upgrade database from version " + oldVersion + " to new " + newVersion, e);
        }
    }

    public Dao<XMPPMessage, Integer> getMessageDao() throws SQLException {
        if (mMessageDAO == null) {
            mMessageDAO = getDao(XMPPMessage.class);
        }
        return mMessageDAO;
    }
}
