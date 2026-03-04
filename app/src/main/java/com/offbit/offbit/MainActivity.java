package com.offbit.offbit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.offbit.offbit.manager.CallHistoryManager;
import com.offbit.offbit.model.UserProfile;
import com.offbit.offbit.utils.UserPreferences;

public class MainActivity extends AppCompatActivity {

    private TextInputEditText usernameEditText;
    private Button startCallButton;
    private MaterialCardView contactsCard;
    private MaterialCardView favoritesCard;
    private MaterialCardView searchCard;
    private MaterialCardView settingsCard;
    private MaterialCardView callHistoryCard;
    private MaterialCardView browserCard;
    private TextView welcomeText;
    private Toolbar toolbar;
    private UserPreferences userPreferences;
    private UserProfile currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Check if user has set up their profile
        userPreferences = new UserPreferences(this);
        currentUser = userPreferences.getUserProfile();
        
        if (currentUser == null) {
            // Redirect to welcome screen
            Intent intent = new Intent(this, WelcomeActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        
        setContentView(R.layout.activity_main);

        initViews();
        setupToolbar();
        setupClickListeners();
        updateUIWithUserProfile();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        usernameEditText = findViewById(R.id.usernameEditText);
        startCallButton = findViewById(R.id.startCallButton);
        contactsCard = findViewById(R.id.contactsCard);
        favoritesCard = findViewById(R.id.favoritesCard);
        searchCard = findViewById(R.id.searchCard);
        settingsCard = findViewById(R.id.settingsCard);
        callHistoryCard = findViewById(R.id.callHistoryCard);
        browserCard = findViewById(R.id.browserCard);
        welcomeText = findViewById(R.id.welcomeText);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.app_name);
        }
    }

    private void updateUIWithUserProfile() {
        if (currentUser != null) {
            welcomeText.setText("Welcome, " + currentUser.getDisplayName() + "!");
        }
    }

    private void setupClickListeners() {
        startCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCall();
            }
        });

        contactsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openContacts();
            }
        });

        favoritesCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFavorites();
            }
        });

        searchCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSearch();
            }
        });

        settingsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSettings();
            }
        });
        
        callHistoryCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCallHistory();
            }
        });
        
        browserCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openBrowser();
            }
        });
    }

    private void startCall() {
        String username = usernameEditText.getText().toString().trim();
        if (username.isEmpty()) {
            usernameEditText.setError("Please enter a username");
            return;
        }
        
        // TODO: Implement actual call functionality
        Toast.makeText(this, "Calling " + username + "...", Toast.LENGTH_SHORT).show();
    }

    private void openContacts() {
        Intent intent = new Intent(this, ContactsActivity.class);
        startActivity(intent);
    }

    private void openFavorites() {
        Intent intent = new Intent(this, FavoritesActivity.class);
        startActivity(intent);
    }

    private void openSearch() {
        // TODO: Implement search functionality
        Toast.makeText(this, "Search feature coming soon", Toast.LENGTH_SHORT).show();
    }

    private void openSettings() {
        // TODO: Implement settings functionality
        Toast.makeText(this, "Settings feature coming soon", Toast.LENGTH_SHORT).show();
    }
    
    private void openCallHistory() {
        Intent intent = new Intent(this, CallHistoryActivity.class);
        startActivity(intent);
    }
    
    private void openBrowser() {
        Intent intent = new Intent(this, BrowserActivity.class);
        startActivity(intent);
    }
}
