package com.example.allergydetect.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.allergydetect.R;
import com.example.allergydetect.models.Allergen;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ListOfAllergensAdapter extends RecyclerView.Adapter<ListOfAllergensAdapter.MyViewHolder> implements Filterable {

    private ArrayList<Allergen> allergensFull;
    private ArrayList<Allergen> allergens;
    private Context context;
    private ListOfAllergensAdapter.OnAddClickListener mListener;

    @Override
    public Filter getFilter() {
        return allergenFilter;
    }

    private Filter allergenFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<Allergen> filteredList = new ArrayList<>();

            if(constraint == null || constraint.length() ==0){
                filteredList.addAll(allergensFull);
            }else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for(Allergen allergen : allergensFull){
                    if(allergen.getAllergenName().toLowerCase().contains(filterPattern)){
                        filteredList.add(allergen);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            allergens.clear();
            allergens.addAll( (ArrayList) results.values);
            //allergens = (ArrayList<Allergen>) results.values;
            notifyDataSetChanged();
        }
    };

    public void updateList(ArrayList<Allergen> allergens){
        this.allergens.clear();
        this.allergens.addAll(allergens);
    }

    public interface OnAddClickListener {
        void onAddClick(int position);
    }

    public void setOnAddClickListener(ListOfAllergensAdapter.OnAddClickListener listener) {
        mListener = listener;
    }

    public ListOfAllergensAdapter(ArrayList<Allergen> allergens, Context context) {
        this.context = context;
        this.allergens = allergens;
        this.allergensFull = new ArrayList<>(allergens);
    }

    @NonNull
    @Override
    public ListOfAllergensAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_of_allergens_adapter, parent, false);
        ListOfAllergensAdapter.MyViewHolder holder = new ListOfAllergensAdapter.MyViewHolder(view, mListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(ListOfAllergensAdapter.MyViewHolder holder, int position) {
        holder.tvAllergenAdapter.setText(allergens.get(position).getAllergenName());
    }

    @Override
    public int getItemCount() {
        return allergens.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageButton ibAdd;
        TextView tvAllergenAdapter;

        public MyViewHolder(View view, final ListOfAllergensAdapter.OnAddClickListener listener){
            super(view);

            ibAdd = itemView.findViewById(R.id.ibAdd);
            tvAllergenAdapter = itemView.findViewById(R.id.tvListAllergen);

            ibAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onAddClick(position);
                        }
                    }
                }
            });
        }
    }
}
