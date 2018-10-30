package nellaschat.coolapp_engine.it.nellaschat.tablayouts;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;

import java.util.ArrayList;

public class PagerAdapter extends FragmentStatePagerAdapter {

    private ArrayList<Fragment> fragments = new ArrayList<>();
    private ArrayList<String> titoli = new ArrayList<>();

    public PagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (position < fragments.size() && position >= 0)
            return fragments.get(position);
        else return null;
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    public void addTab(Fragment tab, String titolo){
        fragments.add(tab);
        titoli.add(titolo);
        notifyDataSetChanged();
    }

    public void removeTab(Fragment tab){
        int pos = getPosition(tab);
        removeTab(pos);
    }

    public void removeTab(int position){
        fragments.remove(position);
        titoli.remove(position);
        notifyDataSetChanged();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titoli.get(position);
    }

    public int getPosition(Fragment tab){
        return fragments.indexOf(tab);
    }

    @Override
    public int getItemPosition(Object object) {
        int pos = fragments.indexOf(object);
        if(pos >= 0) return POSITION_UNCHANGED;
        else return POSITION_NONE;
    }
}