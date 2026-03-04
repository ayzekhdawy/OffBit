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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.offbit.offbit.manager.CallHistoryManager;
import com.offbit.offbit.model.Contact;

import java.util.ArrayList;
import java.util.List;

public class ContactsActivity extends AppCompatActivity {
    private RecyclerView contactsRecyclerView;
    private ContactsAdapter contactsAdapter;
    private List<Contact> contactList;
    private CallHistoryManager callHistoryManager;
    private Toolbar toolbar;
    private FloatingActionButton addContactFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        initViews();
        setupToolbar();
        setupRecyclerView();
        setupClickListeners();
        loadContacts();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        contactsRecyclerView = findViewById(R.id.contactsRecyclerView);
        addContactFab = findViewById(R.id.addContactFab);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Contacts");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupRecyclerView() {
        contactList = new ArrayList<>();
        contactsAdapter = new ContactsAdapter(contactList);
        contactsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        contactsRecyclerView.setAdapter(contactsAdapter);
    }

    private void setupClickListeners() {
        addContactFab.setOnClickListener(v -> {
            // TODO: Implement add contact functionality
        });
    }

    private void loadContacts() {
        callHistoryManager = CallHistoryManager.getInstance(this);
        List<Contact> contacts = callHistoryManager.getAllContacts();
        
        if (contacts != null) {
            contactList.clear();
            contactList.addAll(contacts);
            contactsAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Adapter for contacts recycler view
     */
    private class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {
        private List<Contact> contactList;

        public ContactsAdapter(List<Contact> contactList) {
            this.contactList = contactList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_contact, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Contact contact = contactList.get(position);
            holder.bind(contact);
        }

        @Override
        public int getItemCount() {
            return contactList.size();
        }

        /**
         * View holder for contact items
         */
        class ViewHolder extends RecyclerView.ViewHolder {
            private ImageView contactAvatarImage;
            private TextView contactNameText;
            private TextView lastSeenText;
            private ImageView favoriteIcon;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                contactAvatarImage = itemView.findViewById(R.id.contactAvatarImage);
                contactNameText = itemView.findViewById(R.id.contactNameText);
                lastSeenText = itemView.findViewById(R.id.lastSeenText);
                favoriteIcon = itemView.findViewById(R.id.favoriteIcon);
                
                // Set click listener for the entire item
                itemView.setOnClickListener(v -> {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Contact contact = contactList.get(position);
                        // TODO: Implement contact click functionality (start call)
                    }
                });
                
                // Set click listener for favorite icon
                favoriteIcon.setOnClickListener(v -> {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Contact contact = contactList.get(position);
                        toggleFavorite(contact);
                    }
                });
            }

            public void bind(Contact contact) {
                contactNameText.setText(contact.getDisplayName());
                
                // Set last seen text
                if (contact.getLastSeen() != null) {
                    long timeDiff = System.currentTimeMillis() - contact.getLastSeen().getTime();
                    String lastSeenStr;
                    
                    if (timeDiff < 60000) { // Less than 1 minute
                        lastSeenStr = "Just now";
                    } else if (timeDiff < 3600000) { // Less than 1 hour
                        long minutes = timeDiff / 60000;
                        lastSeenStr = minutes + " min ago";
                    } else if (timeDiff < 86400000) { // Less than 1 day
                        long hours = timeDiff / 3600000;
                        lastSeenStr = hours + " hours ago";
                    } else {
                        long days = timeDiff / 86400000;
                        lastSeenStr = days + " days ago";
                    }
                    
                    lastSeenText.setText(lastSeenStr);
                } else {
                    lastSeenText.setText("Never seen");
                }
                
                // Set favorite icon
                if (contact.isFavorite()) {
                    favoriteIcon.setImageResource(R.drawable.ic_star_filled);
                    favoriteIcon.setColorFilter(itemView.getContext().getColor(R.color.yellow));
                } else {
                    favoriteIcon.setImageResource(R.drawable.ic_star_outline);
                    favoriteIcon.setColorFilter(itemView.getContext().getColor(R.color.gray));
                }
            }
            
            private void toggleFavorite(Contact contact) {
                contact.setFavorite(!contact.isFavorite());
                callHistoryManager.updateContactFavorite(contact.getContactId(), contact.isFavorite());
                
                // Update UI
                if (contact.isFavorite()) {
                    favoriteIcon.setImageResource(R.drawable.ic_star_filled);
                    favoriteIcon.setColorFilter(itemView.getContext().getColor(R.color.yellow));
                } else {
                    favoriteIcon.setImageResource(R.drawable.ic_star_outline);
                    favoriteIcon.setColorFilter(itemView.getContext().getColor(R.color.gray));
                }
            }
        }
    }
}
