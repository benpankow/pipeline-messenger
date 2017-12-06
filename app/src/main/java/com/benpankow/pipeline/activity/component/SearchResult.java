package com.benpankow.pipeline.activity.component;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.benpankow.pipeline.R;
import com.benpankow.pipeline.data.User;
import com.benpankow.pipeline.helper.AuthenticationHelper;
import com.benpankow.pipeline.helper.DatabaseHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Ben Pankow on 12/2/17.
 */

public class SearchResult extends RecyclerView.ViewHolder {

    private final View ivMain;
    private final TextView tvName;
    private User targetUser;

    public SearchResult(View itemView) {
        super(itemView);
        this.ivMain = itemView;
        this.tvName = itemView.findViewById(R.id.search_result_name);
        this.ivMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (targetUser != null) {
                    AuthenticationHelper.getLoggedInUserInfo(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User loggedInUser = dataSnapshot.getValue(User.class);

                            DatabaseHelper.createConversationBetween(loggedInUser.uid, targetUser.uid);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        });
    }

    public void bindUser(User model) {
        targetUser = model;
        tvName.setText(targetUser.nickname);
    }
}
