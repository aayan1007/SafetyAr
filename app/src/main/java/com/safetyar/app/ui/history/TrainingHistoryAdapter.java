package com.safetyar.app.ui.history;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.safetyar.app.R;
import com.safetyar.app.data.local.entity.AssessmentRecordEntity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TrainingHistoryAdapter extends RecyclerView.Adapter<TrainingHistoryAdapter.HistoryViewHolder> {

    public interface OnHistoryActionListener {
        void onViewCertificate(AssessmentRecordEntity record);
        void onReviewTraining(AssessmentRecordEntity record);
    }

    private List<AssessmentRecordEntity> records = new ArrayList<>();
    private OnHistoryActionListener actionListener;

    public TrainingHistoryAdapter() {
    }

    public TrainingHistoryAdapter(OnHistoryActionListener listener) {
        this.actionListener = listener;
    }

    public void setActionListener(OnHistoryActionListener listener) {
        this.actionListener = listener;
    }

    public void setRecords(List<AssessmentRecordEntity> newRecords) {
        this.records = newRecords != null ? newRecords : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history_record, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        holder.bind(records.get(position), actionListener);
    }

    @Override
    public int getItemCount() {
        return records.size();
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvId;
        private final TextView tvBadge;
        private final TextView tvModuleName;
        private final TextView tvSectorTitle;
        private final TextView tvScore;
        private final TextView tvDate;
        private final TextView tvAttempts;
        private final TextView tvCertValidity;
        private final MaterialButton btnViewCertificate;
        private final MaterialButton btnReviewTraining;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvId = itemView.findViewById(R.id.tvHistoryAssessmentId);
            tvBadge = itemView.findViewById(R.id.tvHistoryStatusBadge);
            tvModuleName = itemView.findViewById(R.id.tvHistoryModuleName);
            tvSectorTitle = itemView.findViewById(R.id.tvHistorySectorTitle);
            tvScore = itemView.findViewById(R.id.tvHistoryScore);
            tvDate = itemView.findViewById(R.id.tvHistoryDate);
            tvAttempts = itemView.findViewById(R.id.tvHistoryAttempts);
            tvCertValidity = itemView.findViewById(R.id.tvHistoryCertValidity);
            btnViewCertificate = itemView.findViewById(R.id.btnViewCertificate);
            btnReviewTraining = itemView.findViewById(R.id.btnReviewTraining);
        }

        public void bind(AssessmentRecordEntity record, OnHistoryActionListener listener) {
            tvId.setText("AUDIT ID: " + record.getAssessmentId());

            // Module Title Resolution
            String moduleId = record.getModuleId() != null ? record.getModuleId() : "";
            String moduleName;
            String standardCode;
            if (moduleId.contains("FIRE")) {
                moduleName = "Fire & Explosion Emergency Response";
                standardCode = "DGMS-COAL-R118-FIRE";
            } else if (moduleId.contains("GAS")) {
                moduleName = "Gas Leak & Confined Space Safety";
                standardCode = "DGMS-CS-S54:2024";
            } else if (moduleId.contains("STEEL")) {
                moduleName = "Blast Furnace Tapping & Molten Metal Splash";
                standardCode = "IS-14489:2018";
            } else if (moduleId.contains("MICA")) {
                moduleName = "Respirable Mica Dust & Silicosis Prevention";
                standardCode = "DGMS-OCC-H98";
            } else {
                moduleName = "Underground Mine Safety Practical Audit";
                standardCode = "DGMS-COAL-R115";
            }

            tvModuleName.setText(moduleName);
            tvSectorTitle.setText(record.getSector() + " \u2022 " + standardCode);
            tvScore.setText("Score: " + record.getScore() + "% (" + record.getHazardsMitigated() + "/" + record.getTotalHazards() + " Neutralized)");

            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault());
            tvDate.setText(sdf.format(new Date(record.getCompletionTimestamp())));

            long durationMin = record.getDurationSeconds() / 60;
            long durationSec = record.getDurationSeconds() % 60;
            String durationStr = String.format(Locale.getDefault(), "%dm %02ds", durationMin, durationSec);
            tvAttempts.setText("Duration: " + durationStr + " \u2022 Attempt 1 \u2022 DGMS Verified");

            if (record.isPassed()) {
                tvBadge.setText("PASSED \u2713");
                tvBadge.setTextColor(itemView.getContext().getColor(R.color.hazard_green));
                tvBadge.setBackgroundResource(R.drawable.badge_certified);

                SimpleDateFormat expireSdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
                long expiryDate = record.getCompletionTimestamp() + (365L * 24 * 60 * 60 * 1000L);
                tvCertValidity.setText("Certificate Status: Valid until " + expireSdf.format(new Date(expiryDate)) + " (JISC Issued)");
                tvCertValidity.setTextColor(itemView.getContext().getColor(R.color.hazard_green));
                tvCertValidity.setVisibility(View.VISIBLE);

                btnViewCertificate.setVisibility(View.VISIBLE);
                btnReviewTraining.setText("Review Training");
            } else {
                tvBadge.setText("FAILED \u2717");
                tvBadge.setTextColor(itemView.getContext().getColor(R.color.hazard_red));
                tvBadge.setBackgroundResource(R.drawable.badge_warning);

                tvCertValidity.setText("Certificate Status: Not Issued (Retest Required)");
                tvCertValidity.setTextColor(itemView.getContext().getColor(R.color.hazard_red));
                tvCertValidity.setVisibility(View.VISIBLE);

                btnViewCertificate.setVisibility(View.GONE);
                btnReviewTraining.setText("Retry Assessment");
            }

            btnViewCertificate.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onViewCertificate(record);
                }
            });

            btnReviewTraining.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onReviewTraining(record);
                }
            });
        }
    }
}
