package br.com.clnrp.eqpi;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import org.json.JSONObject;

import br.com.clnrp.eqpi.databinding.FragmentMainBinding;

public class MainFragment extends Fragment {
    private static final int steps = 400;
    private static final int driver_factor = 32;
    private static final int pulleys_factor = 3;
    private static final int worm_wheel = 135;
    private int steps_per_degree;
    private int steps_per_second;
    private int direction = 1;
    private FragmentMainBinding binding;

    private Boolean thread = true;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_main, container, false);
        //View view = inflater.inflate(R.layout.fragment_main, container, false);
        //return view;

        binding = FragmentMainBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        steps_per_degree = (steps*driver_factor*pulleys_factor*worm_wheel)/360;
        steps_per_second = (steps*driver_factor*pulleys_factor*worm_wheel)/86400;
        binding.textInputEditTextFreq.setText(String.valueOf(steps_per_second));

        binding.switchDirection.setChecked(true);

        binding.buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    steps_per_second = Integer.valueOf(binding.textInputEditTextFreq.getText().toString());
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("type", "start");
                    jsonObject.put("frequency", steps_per_second);
                    jsonObject.put("direction", direction);
                    TCPClient tcpClient = ((MainActivity) getActivity()).getTcpClient();
                    tcpClient.sendMessage(jsonObject.toString());
                } catch (Exception e) {
                    Log.e("Start", "Error", e);
                }
            }
        });

        binding.buttonIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    steps_per_second += 1;
                    binding.textInputEditTextFreq.setText(String.valueOf(steps_per_second));
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("type", "steps_per_second");
                    jsonObject.put("frequency", steps_per_second);
                    TCPClient tcpClient = ((MainActivity) getActivity()).getTcpClient();
                    tcpClient.sendMessage(jsonObject.toString());
                } catch (Exception e) {
                    Log.e("Increase", "Error", e);
                }
            }
        });

        binding.buttonDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    steps_per_second -= 1;
                    if(steps_per_second < 0) {
                        steps_per_second = 0;
                    }
                    binding.textInputEditTextFreq.setText(String.valueOf(steps_per_second));
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("type", "steps_per_second");
                    jsonObject.put("frequency", steps_per_second);
                    TCPClient tcpClient = ((MainActivity) getActivity()).getTcpClient();
                    tcpClient.sendMessage(jsonObject.toString());
                } catch (Exception e) {
                    Log.e("Decrease", "Error", e);
                }
            }
        });

        binding.buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("type", "stop");
                    TCPClient tcpClient = ((MainActivity) getActivity()).getTcpClient();
                    tcpClient.sendMessage(jsonObject.toString());
                } catch (Exception e) {
                    Log.e("Stop", "Error", e);
                }
            }
        });

        binding.switchDirection.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                try {
                    if(isChecked){
                        direction = 1;
                    }else{
                        direction = 0;
                    }

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("type", "ra_direction");
                    jsonObject.put("direction", direction);
                    TCPClient tcpClient = ((MainActivity) getActivity()).getTcpClient();
                    tcpClient.sendMessage(jsonObject.toString());
                } catch (Exception e) {
                    Log.e("Direction", "Error", e);
                }

            }
        });

        thread = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(thread && !Thread.interrupted()) {
                    try {
                        Thread.sleep(1000);

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        }).start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        thread = false;
    }
}