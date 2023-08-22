package com.example.androidapp.ui.car;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.androidapp.R;

public class CarFragment extends Fragment {

    TextView tv;

    private CarViewModel carViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        carViewModel =
                ViewModelProviders.of(this).get(CarViewModel.class);
        View root = inflater.inflate(R.layout.fragment_car, container, false);

        tv=root.findViewById(R.id.text_tools);
        final ImageView car = root.findViewById(R.id.carImage);
        final AnimationDrawable animation = (AnimationDrawable) car.getDrawable();
        animation.start();

        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoUrl("https://e-amrit.niti.gov.in/co2-calculator");
            }
        });


        return root;
    }

    private void gotoUrl(String s) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(s));
        startActivity(intent);
    }
}