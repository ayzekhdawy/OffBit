package com.offbit.offbit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.offbit.offbit.manager.CallHistoryManager;
import com.offbit.offbit.model.CallRecord;

import java.util.ArrayList;
import java.util.List;

public class CallHistoryActivity extends AppCompatActivity {
    private RecyclerView callHistoryRecyclerView;
    private CallHistoryAdapter callHistoryAdapter;
    private List<CallRecord> callRecordList;
    private CallHistoryManager callHistoryManager;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_history);

        initViews();
        setupToolbar();
        setupRecyclerView();
        loadCallHistory();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        callHistoryRecyclerView = findViewById(R.id.callHistoryRecyclerView);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Call History");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupRecyclerView() {
        callRecordList = new ArrayList<>();
        callHistoryAdapter = new CallHistoryAdapter(callRecordList);
        callHistoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        callHistoryRecyclerView.setAdapter(callHistoryAdapter);
    }

    private void loadCallHistory() {
        callHistoryManager = CallHistoryManager.getInstance(this);
        List<CallRecord> records = callHistoryManager.getRecentCallRecords(50); // Load last 50 calls
        
        if (records != null) {
            callRecordList.clear();
            callRecordList.addAll(records);
            callHistoryAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Adapter for call history recycler view
     */
    private static class CallHistoryAdapter extends RecyclerView.Adapter<CallHistoryAdapter.ViewHolder> {
        private List<CallRecord> callRecordList;

        public CallHistoryAdapter(List<CallRecord> callRecordList) {
            this.callRecordList = callRecordList;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_call_record, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            CallRecord callRecord = callRecordList.get(position);
            holder.bind(callRecord);
        }

        @Override
        public int getItemCount() {
            return callRecordList.size();
        }

        /**
         * View holder for call record items
         */
        static class ViewHolder extends RecyclerView.ViewHolder {
            private TextView peerNameText;
            private TextView callTypeText;
            private TextView timeText;
            private TextView durationText;
            private TextView statusText;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                peerNameText = itemView.findViewById(R.id.peerNameText);
                callTypeText = itemView.findViewById(R.id.callTypeText);
                timeText = itemView.findViewById(R.id.timeText);
                durationText = itemView.findViewById(R.id.durationText);
                statusText = itemView.findViewById(R.id.statusText);
            }

            public void bind(CallRecord callRecord) {
                peerNameText.setText(callRecord.getPeerName());
                
                // Set call type
                if (callRecord.getCallType() == CallRecord.CallType.INCOMING) {
                    callTypeText.setText("Incoming");
                    callTypeText.setTextColor(itemView.getContext().getColor(R.color.green));
                } else {
                    callTypeText.setText("Outgoing");
                    callTypeText.setTextColor(itemView.getContext().getColor(R.color.blue));
                }
                
                // Set time
                timeText.setText(callRecord.getTimeString());
                
                // Set duration
                if (callRecord.getDuration() > 0) {
                    durationText.setText(callRecord.getDurationString());
                    durationText.setVisibility(View.VISIBLE);
                } else {
                    durationText.setVisibility(View.GONE);
                }
                
                // Set status
                switch (callRecord.getCallStatus()) {
                    case COMPLETED:
                        statusText.setText("Completed");
                        statusText.setTextColor(itemView.getContext().getColor(R.color.green));
                        break;
                    case MISSED:
                        statusText.setText("Missed");
                        statusText.setTextColor(itemView.getContext().getColor(R.color.red));
                        break;
                    case REJECTED:
                        statusText.setText("Rejected");
                        statusText.setTextColor(itemView.getContext().getColor(R.color.orange));
                        break;
                    case FAILED:
                        statusText.setText("Failed");
                        statusText.setTextColor(itemView.getContext().getColor(R.color.red));
                        break;
                }
            }
        }
    }
}
