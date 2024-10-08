package com.www233.uitest.charttest;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.www233.uitest.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PracticeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PracticeFragment extends Fragment {

    private static final String TAG = "Practice";
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "color";
    private static final String ARG_PARAM2 = "text";

    // TODO: Rename and change types of parameters
    private String text;
    private int color;

    public PracticeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PracticeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PracticeFragment newInstance(String param1, int param2) {
        PracticeFragment fragment = new PracticeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putInt(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            text = getArguments().getString(ARG_PARAM1);
            color = getArguments().getInt(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_practice, container, false);
        TextView tv = v.findViewById(R.id.tv);
        FrameLayout fl = v.findViewById(R.id.bg);
        tv.setText(text);
        if (color != -1) {
            fl.setBackgroundColor(color);
        }
        return v;
    }
}