package com.example.androidapp.ui.water;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidapp.CardAdapter;
import com.example.androidapp.Database.CarbonEmissions;
import com.example.androidapp.R;
import com.example.androidapp.ui.car.CarViewModel;
import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.List;

public class WaterUsageFragment extends Fragment {
   // TextView textView;

    private WaterUsageViewModel waterUsageViewModel;

    private MutableLiveData<List<String>> list = new MutableLiveData<>();
/*
    public WaterUsageFragment() {
        textView = textView.findViewById(R.id.textview);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }*/

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        waterUsageViewModel =
                ViewModelProviders.of(this).get(WaterUsageViewModel.class);
        View root = inflater.inflate(R.layout.fragment_waterusage, container, false);
        final RecyclerView recyclerView = root.findViewById(R.id.valuesView);
        final TextView textView = root.findViewById(R.id.text_send);
        final EditText waterText = root.findViewById(R.id.waterText);
        final Spinner spinner = root.findViewById(R.id.spinner);
        final Button button = root.findViewById(R.id.button);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) final TextView link2=root.findViewById(R.id.link2Btn);

        final ImageView loading = root.findViewById(R.id.loading);
        final AnimationDrawable animation = (AnimationDrawable) loading.getDrawable();

        final InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);


        recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));
        recyclerView.setHasFixedSize(true);

        final CardAdapter cardAdapter = new CardAdapter();
        recyclerView.setAdapter(cardAdapter);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mgr.hideSoftInputFromWindow(waterText.getWindowToken(), 0);
                loading.setVisibility(View.VISIBLE);
                animation.start();
                try {
                    waterUsageViewModel.getWaterUsage(spinner.getSelectedItem().toString(), waterText.getText().toString());
                    waterUsageViewModel.getText();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(root.getContext(), "Input values", Toast.LENGTH_SHORT).show();
                    animation.stop();
                    loading.setVisibility(View.INVISIBLE);
                }
            }
        });

        link2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoUrl("https://www.dar.com/insights/details/the-hidden-carbon-footprint-of-water-");
            }
        });


        waterUsageViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                try {
                    textView.setText(s);
                    waterUsageViewModel.insert(new CarbonEmissions(FirebaseAuth.getInstance().getCurrentUser().getEmail(), Float.parseFloat(s), Calendar.getInstance().getTime(), waterUsageViewModel.EMISSION_TYPE_WATER, spinner.getSelectedItem().toString()));
                    waterText.getText().clear();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(root.getContext(), "error ocurred", Toast.LENGTH_SHORT).show();
                }
                animation.stop();
                loading.setVisibility(View.INVISIBLE);
            }
        });

        waterUsageViewModel.getAll().observe(getViewLifecycleOwner(), new Observer<List<CarbonEmissions>>() {
            @Override
            public void onChanged(List<CarbonEmissions> carbonEmissions) {
                cardAdapter.setCards(carbonEmissions);
            }
        });


        waterUsageViewModel.getSpinnerData().observe(getViewLifecycleOwner(), new Observer<List<String>>() {
            @Override
            public void onChanged(@Nullable List<String> strings) {
                list.setValue(strings);
                try {
                    ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(root.getContext(), android.R.layout.simple_spinner_item, list.getValue());
                    spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(spinnerArrayAdapter);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                waterUsageViewModel.delete(cardAdapter.getCarbonEmissionAt(viewHolder.getAdapterPosition()));
                Toast.makeText(root.getContext(), "Emission deleted", Toast.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(recyclerView);

        return root;
    }

    private void gotoUrl(String s) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(s));
        startActivity(intent);
    }

    }


