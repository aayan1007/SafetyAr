package com.safetyar.app.ui.modules;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.safetyar.app.R;
import com.safetyar.app.data.local.entity.TrainingModuleEntity;

import java.util.ArrayList;
import java.util.List;

public class ModuleAdapter extends RecyclerView.Adapter<ModuleAdapter.ModuleViewHolder> {

    private List<TrainingModuleEntity> modules = new ArrayList<>();
    private final OnModuleClickListener listener;

    public interface OnModuleClickListener {
        void onModuleClick(TrainingModuleEntity module);
    }

    public ModuleAdapter(OnModuleClickListener listener) {
        this.listener = listener;
    }

    public void setModules(List<TrainingModuleEntity> newModules) {
        this.modules = newModules != null ? newModules : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ModuleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_module, parent, false);
        return new ModuleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ModuleViewHolder holder, int position) {
        TrainingModuleEntity module = modules.get(position);
        holder.bind(module, listener);
    }

    @Override
    public int getItemCount() {
        return modules.size();
    }

    static class ModuleViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvRegulation;
        private final TextView tvStatus;
        private final TextView tvTitle;
        private final TextView tvSubtitle;
        private final TextView tvMeta;

        public ModuleViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRegulation = itemView.findViewById(R.id.tvItemRegulationCode);
            tvStatus = itemView.findViewById(R.id.tvItemStatus);
            tvTitle = itemView.findViewById(R.id.tvItemTitle);
            tvSubtitle = itemView.findViewById(R.id.tvItemSubtitle);
            tvMeta = itemView.findViewById(R.id.tvItemMeta);
        }

        public void bind(TrainingModuleEntity module, OnModuleClickListener listener) {
            tvRegulation.setText(module.getDgmsRegulationCode());
            tvTitle.setText(module.getTitle());
            tvSubtitle.setText(module.getSubtitle());

            if (module.isCompleted()) {
                tvStatus.setText("COMPLETED \u2713");
                tvStatus.setTextColor(itemView.getContext().getColor(R.color.hazard_green));
            } else {
                tvStatus.setText("PENDING");
                tvStatus.setTextColor(itemView.getContext().getColor(R.color.hazard_orange));
            }

            tvMeta.setText(module.getHazardCount() + " Hazards \u2022 " +
                    module.getEstimatedMinutes() + " Mins \u2022 Pass " +
                    module.getPassingScore() + "%");

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onModuleClick(module);
                }
            });
        }
    }
}
