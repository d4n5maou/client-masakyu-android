//package com.pab.masakyuapp.view;
//
//import android.os.Bundle;
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import com.pab.masakyuapp.R;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class PageFragment extends Fragment {
//
//    private static final String ARG_DATA = "data";
//    private List<String> dataList;
//
//    public static PageFragment newInstance(List<String> data) {
//        PageFragment fragment = new PageFragment();
//        Bundle args = new Bundle();
//        args.putStringArrayList(ARG_DATA, new ArrayList<>(data));
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            dataList = getArguments().getStringArrayList(ARG_DATA);
//        }
//    }
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_page, container, false);
//        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//        recyclerView.setAdapter(new RecyclerViewAdapter(dataList));
//        return view;
//    }
//}
