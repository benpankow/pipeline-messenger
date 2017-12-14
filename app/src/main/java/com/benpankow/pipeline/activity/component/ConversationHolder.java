package com.benpankow.pipeline.activity.component;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.benpankow.pipeline.R;
import com.benpankow.pipeline.activity.ConversationActivity;
import com.benpankow.pipeline.data.Conversation;
import com.benpankow.pipeline.helper.DatabaseHelper;
import com.daimajia.swipe.SwipeLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

import java8.util.function.Consumer;

/**
 * Created by Ben Pankow on 12/2/17.
 *
 * A RecyclerView ViewHolder corresponding to a Conversation
 */
public class ConversationHolder extends RecyclerView.ViewHolder {

    private final View ivMain;
    private final TextView tvTitle;
    private final TextView tvPreview;
    private final TextView tvTimestamp;
    private final SwipeLayout slBase;
    private Conversation conversation;
    private boolean isSwiped;
    private boolean wasRemoved;

    public ConversationHolder(View itemView) {
        super(itemView);
        isSwiped = false;
        wasRemoved = false;

        this.ivMain = itemView;
        this.tvTitle = itemView.findViewById(R.id.tv_user_nickname);
        this.tvPreview = itemView.findViewById(R.id.tv_conversation_preview);
        this.tvTimestamp = itemView.findViewById(R.id.tv_conversation_timestamp);
        this.slBase = itemView.findViewById(R.id.sl_base);
        slBase.setShowMode(SwipeLayout.ShowMode.PullOut);
        slBase.setRightSwipeEnabled(false);

        slBase.addDrag(SwipeLayout.DragEdge.Left, itemView.findViewById(R.id.cl_trash));

        // On click open messages
        this.ivMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View itemView) {
                if (!isSwiped && !wasRemoved) {
                    Intent conversationActivity =
                            new Intent(itemView.getContext(), ConversationActivity.class);
                    conversationActivity.putExtra("convoid", conversation.getConvoid());
                    itemView.getContext().startActivity(conversationActivity);
                }
            }
        });

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
                if (isSwiped) {
                    String uid = FirebaseAuth.getInstance().getUid();
                    if (uid != null && conversation != null) {
                        DatabaseHelper.removeConversationFromUser(uid, conversation.getConvoid());
                        wasRemoved = true;
                    }
                }
            }
        });
    }

    public void bindConversation(Conversation model) {
        conversation = model;
        conversation.getTitle(new Consumer<String>() {
               @Override
               public void accept(String s) {
                   tvTitle.setText(s);
               }
        });

        String uid = FirebaseAuth.getInstance().getUid();

        String preview = conversation.getPreviewMessage(uid, itemView.getContext());
        if (preview != null) {
            if (preview.length() > 150) {
                tvPreview.setText(preview.substring(0, 50) + "...");
            } else {
                tvPreview.setText(preview);
            }
        }

        tvTimestamp.setText(conversation.getTimestamp(uid));

    }
}
