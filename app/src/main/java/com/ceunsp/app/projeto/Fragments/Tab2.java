package com.ceunsp.app.projeto.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ceunsp.app.projeto.R;

public class Tab2 extends Fragment {


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View historicTab = inflater.inflate(R.layout.tab_2,container,false);
        return historicTab;
    }
}
