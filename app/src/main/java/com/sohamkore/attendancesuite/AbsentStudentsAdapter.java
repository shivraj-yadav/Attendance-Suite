package com.sohamkore.attendancesuite;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class AbsentStudentsAdapter extends RecyclerView.Adapter<AbsentStudentsAdapter.ViewHolder> {
    private List<Student> absentStudentsList;

    public AbsentStudentsAdapter(List<Student> absentStudentsList) {
        this.absentStudentsList = absentStudentsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_absent_student, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Student student = absentStudentsList.get(position);
        holder.nameTextView.setText(student.getName());
        holder.contactTextView.setText(student.getContact());
    }

    @Override
    public int getItemCount() {
        return absentStudentsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, contactTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            contactTextView = itemView.findViewById(R.id.contactTextView);
        }
    }
}