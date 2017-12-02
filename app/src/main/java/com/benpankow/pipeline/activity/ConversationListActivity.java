package com.benpankow.pipeline.activity;

import android.os.Bundle;

import com.benpankow.pipeline.R;
import com.benpankow.pipeline.activity.base.AuthenticatedActivity;

/**
 * Created by Ben Pankow on 12/2/17.
 */
public class ConversationListActivity extends AuthenticatedActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation_list);
    }
}
