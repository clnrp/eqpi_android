package br.com.clnrp.eqpi;

import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;

import androidx.core.view.WindowCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import br.com.clnrp.eqpi.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements OnMessageReceived{

    private MenuItem menuConnect;
    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    NavController navController;
    public TCPClient tcpClient;
    public Boolean connected = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        tcpClient = new TCPClient(this);

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAnchorView(R.id.fab)
                        .setAction("Action", null).show();
            }
        });

        Runnable runnable = new Runnable() {
            public void run() {

                while(true){
                    synchronized (this) {
                        try {
                            Thread.sleep(1000);

                            runOnUiThread (new Thread(new Runnable() {
                                public void run() {
                                    try {
                                        if(tcpClient.isConnected() && !connected){
                                            connected = true;
                                            menuConnect.setIcon(R.drawable.ic_call_end_disconnect_24);
                                        }else if(!tcpClient.isConnected() && connected){
                                            connected = false;
                                            menuConnect.setIcon(R.drawable.ic_call_connect_24);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        menuConnect = menu.findItem(R.id.action_connect);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            navController.navigate(R.id.action_open_SettingsFragment);
            return true;
        }else if(id == R.id.action_connect){
            if(!tcpClient.isConnected()) {
                tcpClient.connect("10.1.1.100", 2020);
            }else{
                tcpClient.disconnect();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public TCPClient getTcpClient() {
        return this.tcpClient;
    }
    public Boolean isConnected() {
        return this.tcpClient.isConnected();
    }

    public void messageReceived(String message) {
        Log.i("messageReceived", message);
    }

}