package com.offbit.offbit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.offbit.offbit.manager.CallHistoryManager;
import com.offbit.offbit.model.Contact;

import java.util.ArrayList;
import java.util.List;

public class FavoritesActivity extends AppCompatActivity {
    private RecyclerView favoritesRecyclerView;
    private FavoritesAdapter favoritesAdapter;
    private List<Contact> favoriteList;
    private CallHistoryManager callHistoryManager;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        initViews();
        setupToolbar();
        setupRecyclerView();
        loadFavorites();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        favoritesRecyclerView = findViewById(R.id.favoritesRecyclerView);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Favorites");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupRecyclerView() {
        favoriteList = new ArrayList<>();
        favoritesAdapter = new FavoritesAdapter(favoriteList);
        favoritesRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        favoritesRecyclerView.setAdapter(favoritesAdapter);
    }

    private void loadFavorites() {
        callHistoryManager = CallHistoryManager.getInstance(this);
        List<Contact> favorites = callHistoryManager.getFavoriteContacts();
        
        if (favorites != null) {
            favoriteList.clear();
            favoriteList.addAll(favorites);
            favoritesAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Adapter for favorites recycler view
     */
    private class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.ViewHolder> {
        private List<Contact> favoriteList;

        public FavoritesAdapter(List<Contact> favoriteList) {
            this.favoriteList = favoriteList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_favorite, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Contact contact = favoriteList.get(position);
            holder.bind(contact);
        }

        @Override
        public int getItemCount() {
            return favoriteList.size();
        }

        /**
         * View holder for favorite items
         */
        class ViewHolder extends RecyclerView.ViewHolder {
            private ImageView contactAvatarImage;
            private TextView contactNameText;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                contactAvatarImage = itemView.findViewById(R.id.contactAvatarImage);
                contactNameText = itemView.findViewById(R.id.contactNameText);
                
                // Set click listener for the entire item
                itemView.setOnClickListener(v -> {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Contact contact = favoriteList.get(position);
                        // TODO: Implement contact click functionality (start call)
                    }
                });
            }

            public void bind(Contact contact) {
                contactNameText.setText(contact.getDisplayName());
            }
        }
    }
}
