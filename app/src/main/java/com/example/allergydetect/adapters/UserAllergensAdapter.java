package com.example.allergydetect.adapters;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.allergydetect.R;
import com.example.allergydetect.models.Allergen;

import java.util.ArrayList;

public class UserAllergensAdapter extends RecyclerView.Adapter<UserAllergensAdapter.MyViewHolder> {

    private ArrayList<Allergen> userAllergens;
    private Context context;
    private OnItemClickListener userListener;

    public interface OnItemClickListener {
        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        userListener = listener;
    }

    public UserAllergensAdapter(ArrayList<Allergen> allergens, Context context) {
        this.context = context;
        this.userAllergens = allergens;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_user_allergens_addapter, parent, false);
        MyViewHolder holder = new MyViewHolder(view, userListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.tvAllergenAdapter.setText(userAllergens.get(position).getAllergenName());
    }

    @Override
    public int getItemCount() {
        return userAllergens.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageButton ibTrash;
        TextView tvAllergenAdapter;

        public MyViewHolder(View view, final OnItemClickListener listener){
            super(view);

            ibTrash = itemView.findViewById(R.id.ibTrash);
            tvAllergenAdapter = itemView.findViewById(R.id.tvAllergenAdapter);

            ibTrash.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onDeleteClick(position);
                        }
                    }
                }
            });
        }
    }
}