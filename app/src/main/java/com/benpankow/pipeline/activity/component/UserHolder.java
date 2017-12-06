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

import java8.util.function.Consumer;

/**
 * Created by Ben Pankow on 12/2/17.
 *
 * A RecyclerView ViewHolder corresponding to a User
 */

public class UserHolder extends RecyclerView.ViewHolder {

    private final View ivMain;
    private final TextView tvName;
    private User targetUser;

    public UserHolder(View itemView) {
        super(itemView);
        this.ivMain = itemView;
        this.tvName = itemView.findViewById(R.id.search_result_name);
        this.ivMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (targetUser != null) {
                    AuthenticationHelper.getLoggedInUserInfo(new Consumer<User>() {
                        @Override
                        public void accept(User loggedInUser) {
                            DatabaseHelper.createConversationBetween(
                                    loggedInUser.uid,
                                    targetUser.uid
                            );
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
