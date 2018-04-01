package com.nailiqi.travellingpaws.Utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PartStatePagerAdapter extends FragmentStatePagerAdapter{

    //list of fragments
    private List<Fragment> fragmentList = new ArrayList<>();
    private final HashMap<Fragment, Integer> fragments = new HashMap<>();
    private final HashMap<String, Integer> fragmentNumbers = new HashMap<>();
    private final HashMap<Integer, String> fragmentNames = new HashMap<>();


    public PartStatePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    public void addFragment(Fragment fragment, String fragmentName){
        fragmentList.add(fragment);
        fragments.put(fragment, fragmentList.size()-1);
        fragmentNumbers.put(fragmentName, fragmentList.size()-1);
        fragmentNames.put(fragmentList.size()-1, fragmentName);
    }

    public Integer getFragmentNum(String fragmentName){
        if(fragmentNumbers.containsKey(fragmentName)){
            return fragmentNumbers.get(fragmentName);
        }else{
            return null;
        }
    }

    public Integer getFragmentNum(Fragment fragment){
        if(fragmentNumbers.containsKey(fragment)){
            return fragmentNumbers.get(fragment);
        }else{
            return null;
        }
    }

    public String getFragmentName(Integer fragmentNum){
        if(fragmentNames.containsKey(fragmentNum)){
            return fragmentNames.get(fragmentNum);
        }else{
            return null;
        }
    }
}
