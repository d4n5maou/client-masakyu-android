//package com.pab.masakyuapp.view;
//
//import androidx.annotation.NonNull;
//import androidx.fragment.app.Fragment;
//import androidx.fragment.app.FragmentActivity;
//import androidx.viewpager2.adapter.FragmentStateAdapter;
//import java.util.List;
//
//public class ViewPagerAdapter extends FragmentStateAdapter {
//
//    private final List<List<String>> data;
//
//    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, List<List<String>> data) {
//        super(fragmentActivity);
//        this.data = data;
//    }
//
//    @NonNull
//    @Override
//    public Fragment createFragment(int position) {
//        return PageFragment.newInstance(data.get(position));
//    }
//
//    @Override
//    public int getItemCount() {
//        return data.size();
//    }
//}
