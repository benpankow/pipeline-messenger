package com.benpankow.pipeline.activity.component;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.benpankow.pipeline.R;
import com.benpankow.pipeline.activity.ConversationActivity;
import com.benpankow.pipeline.data.Conversation;
import com.benpankow.pipeline.data.User;
import com.benpankow.pipeline.helper.AuthenticationHelper;
import com.benpankow.pipeline.helper.ConversationHelper;
import com.benpankow.pipeline.helper.DatabaseHelper;
import com.daimajia.swipe.SwipeLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

import java8.util.function.Consumer;

/**
 * Created by Ben Pankow on 12/2/17.
 *
 * A RecyclerView ViewHolder corresponding to a User that can be swiped to be removed
 */

public class UserHolderRemovable extends RecyclerView.ViewHolder {

    private final View ivMain;
    private final TextView tvNickname;
    private final TextView tvUsername;
    private final SwipeLayout slBase;
    private User targetUser;
    private boolean isSwiped;
    private String targetConvoid;

    public UserHolderRemovable(final View itemView) {
        super(itemView);
        isSwiped = false;

        this.ivMain = itemView;
        this.tvNickname = itemView.findViewById(R.id.tv_user_nickname);
        this.tvUsername = itemView.findViewById(R.id.tv_user_username);
        this.slBase = itemView.findViewById(R.id.sl_base);

        // Enable swiping left to remove user
        slBase.setShowMode(SwipeLayout.ShowMode.PullOut);
        slBase.setRightSwipeEnabled(false);
        slBase.addDrag(SwipeLayout.DragEdge.Left, itemView.findViewById(R.id.cl_trash));

        // On swipe, remove conversation
        slBase.addSwipeListener(new SwipeLayout.SwipeListener() {
            @Override
            public void onStartOpen(SwipeLayout layout) {
            }

            @Override
            public void onOpen(SwipeLayout layout) {
                isSwiped = true;
            }

            @Override
            public void onStartClose(SwipeLayout layout) {
                isSwiped = false;
            }

            @Override
            public void onClose(SwipeLayout layout) {
            }

            @Override
            public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {
            }

            @Override
            public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {
                // Remove user from conversation if swipe right
                if (isSwiped && targetUser != null) {
                    String uid = targetUser.getUid();
                    if (uid != null && targetConvoid != null) {
                        DatabaseHelper.removeConversationFromUser(uid, targetConvoid);
                    }
                }
            }
        });
    }

    public void bindUser(User model) {
        targetUser = model;
        if (targetUser != null) {
            tvNickname.setText(targetUser.getNickname());
            tvUsername.setText(String.format("@%s", targetUser.getUsername()));
            String currentUid = FirebaseAuth.getInstance().getUid();
            slBase.setLeftSwipeEnabled(!targetUser.getUid().equals(currentUid));
        }
    }

    public void bindConversationId(String convoid) {
        targetConvoid = convoid;
    }

}
