package com.benpankow.pipeline.activity.component;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.benpankow.pipeline.R;
import com.benpankow.pipeline.data.User;

import java.util.Set;

/**
 * Created by Ben Pankow on 12/2/17.
 *
 * A RecyclerView ViewHolder corresponding to a User, w/ a checkbox to select the user
 */
public class UserHolderCheckbox extends RecyclerView.ViewHolder {

    private final View ivMain;
    private final TextView tvNickname;
    private final TextView tvUsername;
    private final CheckBox cbUser;
    private User targetUser;
    private Set<String> addedUsers;

    public UserHolderCheckbox(final View itemView) {
        super(itemView);
        this.ivMain = itemView;
        this.tvNickname = itemView.findViewById(R.id.tv_user_nickname);
        this.tvUsername = itemView.findViewById(R.id.tv_user_username);
        this.cbUser = itemView.findViewById(R.id.cb_user);

        this.ivMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // On click, add user + go back to conversation list
                if (targetUser != null) {
                    if (addedUsers.contains(targetUser.getUid())) {
                        addedUsers.remove(targetUser.getUid());
                        cbUser.setChecked(false);
                    } else {
                        addedUsers.add(targetUser.getUid());
                        cbUser.setChecked(true);
                    }
                }
            }
        });
    }

    public void bindTargetUser(User model) {
        targetUser = model;
        if (targetUser != null) {
            tvNickname.setText(targetUser.getNickname());
            tvUsername.setText(String.format("@%s", targetUser.getUsername()));
        }

    }

    public void bindSet(Set<String> addedUsers) {
        this.addedUsers = addedUsers;
        cbUser.setChecked(addedUsers.contains(targetUser.getUid()));
    }
}
