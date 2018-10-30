package instasaver.coolapp_engine.it.instasaver.Activity;

import android.support.design.widget.TabItem;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

import instasaver.coolapp_engine.it.instasaver.R;
import instasaver.coolapp_engine.it.instasaver.Utils.PageAdapter;
import instasaver.coolapp_engine.it.instasaver.Utils.WebViewPers;

public class SearchAccount extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_account);

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        TabItem tabFollowers = findViewById(R.id.tab_followers);
        TabItem tabUsername = findViewById(R.id.tab_username);
        TabItem tabLinkUsername = findViewById(R.id.tab_link_username);
        TabItem tabLinkPost = findViewById(R.id.tab_link_post);
        ViewPager viewPager = findViewById(R.id.page_viewer);

        PageAdapter pageAdapter = new PageAdapter(getSupportFragmentManager(), tabLayout.getTabCount());

        viewPager.setAdapter(pageAdapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        viewPager.setOffscreenPageLimit(tabLayout.getTabCount());

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}
