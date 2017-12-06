package com.benpankow.pipeline.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.benpankow.pipeline.activity.base.AuthenticatedActivity;

import com.benpankow.pipeline.R;
import com.benpankow.pipeline.activity.component.UserHolder;
import com.benpankow.pipeline.data.User;
import com.benpankow.pipeline.helper.DatabaseHelper;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.Query;

public class SearchActivity extends AuthenticatedActivity {

    private EditText etSearch;
    private RecyclerView rvSearchResults;
    FirebaseRecyclerAdapter<User, UserHolder> userAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        etSearch = (EditText) findViewById(R.id.et_search);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                search(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        rvSearchResults = (RecyclerView) findViewById(R.id.rv_search_results);
        rvSearchResults.setHasFixedSize(true);
        rvSearchResults.setLayoutManager(new LinearLayoutManager(this));
    }

    private void search(String queryText) {
        if (userAdapter != null) {
            userAdapter.stopListening();
        }

        Query query = DatabaseHelper.queryUserByUsername(queryText);
        FirebaseRecyclerOptions<User> userOptions =
                new FirebaseRecyclerOptions.Builder<User>()
                        .setQuery(query, User.class)
                        .build();

        userAdapter = new FirebaseRecyclerAdapter<User, UserHolder>(userOptions) {
            @Override
            public UserHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_user, parent, false);

                return new UserHolder(view);
            }

            @Override
            protected void onBindViewHolder(UserHolder holder, int position, User model) {
                holder.bindUser(model);
            }

        };
        userAdapter.startListening();
        rvSearchResults.setAdapter(userAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (userAdapter != null) {
            userAdapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (userAdapter != null) {
            userAdapter.stopListening();
        }
    }
}
