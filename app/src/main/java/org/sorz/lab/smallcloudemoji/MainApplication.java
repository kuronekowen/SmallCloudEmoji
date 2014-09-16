package org.sorz.lab.smallcloudemoji;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.sony.smallapp.SmallAppWindow;
import com.sony.smallapp.SmallApplication;

import org.sorz.lab.smallcloudemoji.db.DaoSession;
import org.sorz.lab.smallcloudemoji.db.DatabaseOpenHelper;
import org.sorz.lab.smallcloudemoji.db.DatabaseUpgrader;
import org.sorz.lab.smallcloudemoji.db.Entry;
import org.sorz.lab.smallcloudemoji.db.Repository;

import java.util.Date;

/**
 * Small application for Sony.
 */
public class MainApplication extends SmallApplication {
    private SharedPreferences sharedPreferences;
    private MainExpandableAdapter adapter;
    private DaoSession daoSession;

    @Override
    public void onCreate() {
        super.onCreate();
        setContentView(R.layout.application_main);
        setTitle(R.string.app_name);

        SmallAppWindow.Attributes attr = getWindow().getAttributes();
        attr.minHeight = 350;
        attr.minWidth = 350;
        attr.width = 500;
        attr.height = 650;
        getWindow().setAttributes(attr);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // Open database.
        DatabaseOpenHelper databaseOpenHelper = new DatabaseOpenHelper(this);
        daoSession = databaseOpenHelper.getDaoSession();
        DatabaseUpgrader.checkAndDoUpgrade(this, daoSession);
        final Repository repository = databaseOpenHelper.getDefaultRepository();


        // Download if empty.
        if (repository.getCategories().isEmpty()) {
            // Minimize the windows rather than mask the process dialog.
            getWindow().setWindowState(SmallAppWindow.WindowState.MINIMIZED);
            new DownloadXmlAsyncTask(this, daoSession) {
                @Override
                protected void onPostExecute(Integer result) {
                    super.onPostExecute(result);
                    repository.resetCategories();
                    //if (adapter != null)
                        adapter.notifyDataSetChanged(true);
                    getWindow().setWindowState(SmallAppWindow.WindowState.NORMAL);
                }
            }.execute(repository);
        }

        final ExpandableListView listView =
                (ExpandableListView) findViewById(R.id.expandableListView);
        adapter = new MainExpandableAdapter(this, daoSession);
        listView.setAdapter(adapter);
        listView.expandGroup(0, false);
        listView.expandGroup(adapter.getGroupCount() - 1, false);

        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
                                        int childPosition, long id) {
                Object tag = v.getTag();
                if (tag instanceof Entry) {
                    Entry entry = (Entry) tag;
                    ClipboardManager clipboard =
                            (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                    ClipData clipData = ClipData.newPlainText("emoticon", entry.getEmoticon());
                    clipboard.setPrimaryClip(clipData);
                    Toast.makeText(MainApplication.this, R.string.toast_copied,
                            Toast.LENGTH_SHORT).show();
                    entry.setLastUsed(new Date());
                    entry.update();

                    String action = sharedPreferences.getString("action_after_copied", "MINIMIZE");
                    if (action.equals("MINIMIZE"))
                        getWindow().setWindowState(SmallAppWindow.WindowState.MINIMIZED);
                    else if (action.equals("CLOSE"))
                        finish();
                } else {  // is the settings item.
                    Intent intent = new Intent(MainApplication.this, SettingsActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
                return true;
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Object tag = view.getTag();
                if (! (tag instanceof Entry))
                    return false;
                Entry entry = (Entry) tag;
                entry.setStar(! entry.getStar());
                entry.update();
                adapter.notifyDataSetChanged();
                return true;
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
