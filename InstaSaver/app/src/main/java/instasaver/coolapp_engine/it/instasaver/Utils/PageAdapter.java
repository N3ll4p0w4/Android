package instasaver.coolapp_engine.it.instasaver.Utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import instasaver.coolapp_engine.it.instasaver.Search.FollowingFragment;
import instasaver.coolapp_engine.it.instasaver.Search.LinkPostFragment;
import instasaver.coolapp_engine.it.instasaver.Search.LinkUsernameFragment;
import instasaver.coolapp_engine.it.instasaver.Search.UsernameFragment;

public class PageAdapter extends FragmentPagerAdapter {

    private int numOfTabs;

    public PageAdapter(FragmentManager fm, int numOfTabs) {
        super(fm);
        this.numOfTabs = numOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch(position) {
            case 0:
                return new FollowingFragment();
            case 1:
                return new UsernameFragment();
            case 2:
                return new LinkUsernameFragment();
            case 3:
                return new LinkPostFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }
}
