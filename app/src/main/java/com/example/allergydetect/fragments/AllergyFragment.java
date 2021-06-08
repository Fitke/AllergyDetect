package com.example.allergydetect.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.allergydetect.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class AllergyFragment extends BottomSheetDialogFragment {

    TextView tvHighRisk;
    TextView tvCrossAllergens;

    public AllergyFragment() {
    }
    public static AllergyFragment newInstance(String param1, String param2) {
        AllergyFragment fragment = new AllergyFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.BottomSheetDialogTheme);
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
        View v = localInflater.inflate(R.layout.fragment_allergy, container, false);

        tvHighRisk = v.findViewById(R.id.tvHighRisk);
        tvCrossAllergens = v.findViewById(R.id.tvCrossAllergens);

        tvHighRisk.setText(getArguments().getString("tvHighRisk"));
        tvCrossAllergens.setText(getArguments().getString("tvCrossAllergens"));

        return v;
    }


}