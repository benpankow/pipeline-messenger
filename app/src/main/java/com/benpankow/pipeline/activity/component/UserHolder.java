package com.benpankow.pipeline.activity.component;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.benpankow.pipeline.R;
import com.benpankow.pipeline.activity.ConversationListActivity;
import com.benpankow.pipeline.data.User;
import com.benpankow.pipeline.helper.AuthenticationHelper;
import com.benpankow.pipeline.helper.DatabaseHelper;

import java8.util.function.Consumer;

/**
 * Created by Ben Pankow on 12/2/17.
 *
 * A RecyclerView ViewHolder corresponding to a User
 */

public class UserHolder extends RecyclerView.ViewHolder {

    private final View ivMain;
    private final TextView tvNickname;
    private final TextView tvUsername;
    private User targetUser;

    public UserHolder(final View itemView) {
        super(itemView);
        this.ivMain = itemView;
        this.tvNickname = itemView.findViewById(R.id.tv_user_nickname);
        this.tvUsername = itemView.findViewById(R.id.tv_user_username);

        this.ivMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // On click, add user + go back to conversation list
                if (targetUser != null) {
                    AuthenticationHelper.bindLoggedInUserInfo(new Consumer<User>() {
                        @Override
                        public void accept(User loggedInUser) {
                            DatabaseHelper.createConversationBetween(
                                    loggedInUser.uid,
                                    targetUser.uid
                            );
                            Intent convoListActivity =
                                    new Intent(itemView.getContext(), ConversationListActivity.class);
                            itemView.getContext().startActivity(convoListActivity);
                        }
                    });
                }
            }
        });
    }

    public void bindUser(User model) {
        targetUser = model;
        tvNickname.setText(targetUser.nickname);
        tvUsername.setText(String.format("@%s", targetUser.username));
    }
}
