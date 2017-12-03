package com.benpankow.pipeline.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.benpankow.pipeline.R;
import com.benpankow.pipeline.activity.base.AuthenticatedActivity;
import com.benpankow.pipeline.activity.base.UnauthenticatedActivity;

/**
 * Created by Ben Pankow on 12/2/17.
 */
public class ConversationListActivity extends AuthenticatedActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation_list);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Add settings button to menu bar
        getMenuInflater().inflate(R.menu.conversation_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.item_settings) {
            Intent settingsIntent =
                    new Intent(ConversationListActivity.this, SettingsActivity.class);
            ConversationListActivity.this.startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
