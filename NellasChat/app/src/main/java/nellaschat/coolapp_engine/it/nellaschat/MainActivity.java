package nellaschat.coolapp_engine.it.nellaschat;

import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.ArrayList;

import nellaschat.coolapp_engine.it.nellaschat.hostedserver.HostedServer;
import nellaschat.coolapp_engine.it.nellaschat.tablayouts.PagerAdapter;
import nellaschat.coolapp_engine.it.nellaschat.fragments.TabFragment;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.chat_toolbar);
        setSupportActionBar(toolbar);

        tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager = findViewById(R.id.pager);
        pagerAdapter = new PagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                TabFragment f = (TabFragment) pagerAdapter.getItem(tab.getPosition());
                if(f != null) {
                    toolbar.setTitle(f.getNomeClient() + "'s Chat");
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        MaterialDialog md = new MaterialDialog.Builder(this)
            .title("Inserisci il tuo nome")
            .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES)
            .cancelable(false)
            .positiveText("Aggiungi")
            .input("Nome :D", "", new MaterialDialog.InputCallback() {
                @Override
                public void onInput(MaterialDialog dialog, CharSequence input) {
                    addTab(input.toString());
                }
            }).show();
    }

    public void addTab(String nomeClient) {
        TabFragment fragment = new TabFragment();
        fragment.setNomeClient(nomeClient);
        viewPager.setOffscreenPageLimit(pagerAdapter.getCount()+1);
        pagerAdapter.addTab(fragment, nomeClient);
        viewPager.setCurrentItem(pagerAdapter.getPosition(fragment));
    }

    public void removeTab(int position) {
        pagerAdapter.removeTab(position);
    }

    public void removeTab(TabFragment tab) {
        pagerAdapter.removeTab(tab);
    }

    public int getNewPortServer(){
        ArrayList<Integer> ports = new ArrayList<>();
        for(int i=0; i<tabLayout.getTabCount(); i++){
            TabFragment tf = (TabFragment) pagerAdapter.getItem(i);
            HostedServer hs = tf.getHostedServer();
            if(hs != null)
                ports.add(hs.getPortServer());
        }
        int currentPort = 9000;
        for(int i=0; i<ports.size(); i++){
            if(ports.get(i) == currentPort)
                currentPort++;
            else return currentPort;
        }
        return currentPort;
    }

    public int[] getPortServers(){
        int[] ports = new int[tabLayout.getTabCount()];
        for(int i=0; i<ports.length; i++){
            TabFragment tf = (TabFragment) pagerAdapter.getItem(i);
            HostedServer hs = tf.getHostedServer();
            ports[i] = hs.getPortServer();
        }
        return ports;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_add_person) {

            MaterialDialog md = new MaterialDialog.Builder(this)
                .title("Inserisci nome Client")
                .cancelable(true)
                .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES)
                .negativeText("Annulla")
                .positiveText("Aggiungi")
                .input("Nome Client :D", "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        addTab(input.toString());
                    }
                }).show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
