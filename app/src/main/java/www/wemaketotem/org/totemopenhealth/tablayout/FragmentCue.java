package www.wemaketotem.org.totemopenhealth.tablayout;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import java.util.concurrent.Executor;

import www.wemaketotem.org.totemopenhealth.BLEDataServer;
import www.wemaketotem.org.totemopenhealth.CharacteristicName;
import www.wemaketotem.org.totemopenhealth.DeviceController;
import www.wemaketotem.org.totemopenhealth.Metronome;
import www.wemaketotem.org.totemopenhealth.Observer;
import www.wemaketotem.org.totemopenhealth.R;

public class FragmentCue extends Fragment implements BLEDataServer, Observer {

    private Metronome metronome;
    private Executor executor;
    private SeekBar sbFrequency;
    private Switch sPlayCue;
    private CheckBox cbVisual;
    private TextView tvVisual;
    private CheckBox cbAudible;
    private TextView tvAudible;
    private CheckBox cbHaptic;
    private TextView tvHaptic;
    private boolean isConnected;
    public Switch sSmartCue;

    public static FragmentCue newInstance() {
        return new FragmentCue();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.pager_cue, container, false);
    }

    @Override
    public void storeData(byte[] data)
    {
        if(sSmartCue.isChecked() == true)
        {
            if(data[0] > 1)
            {
                metronome.startPlayback();
                if(metronome.isThreadRunning() == false)
                    executor.execute(metronome);
            }
            else if(data[0] == 0)
            {
                metronome.stopPlayback();
            }
        }
    }

    @Override
    public void deviceConnected()
    {
        isConnected = true;
    }

    @Override
    public void deviceDisconnected()
    {
        metronome.stopPlayback();
        isConnected = false;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        metronome = Metronome.getInstance(getActivity());
        executor = new Executor() {
            @Override
            public void execute(Runnable command) {
                if(metronome.isThreadRunning() == false)
                    new Thread(command).start();
            }
        };
        sbFrequency = (SeekBar) getActivity().findViewById(R.id.sbFrequency);
        sPlayCue = (Switch) getActivity().findViewById(R.id.playCue);
        cbVisual = (CheckBox) getActivity().findViewById(R.id.cbVisual);
        tvVisual = (TextView) getActivity().findViewById(R.id.tvVisual);
        cbAudible = (CheckBox) getActivity().findViewById(R.id.cbAudible);
        tvAudible = (TextView) getActivity().findViewById(R.id.tvAudible);
        cbHaptic = (CheckBox) getActivity().findViewById(R.id.cbHaptic);
        tvHaptic = (TextView) getActivity().findViewById(R.id.tvHaptic);

        sSmartCue = (Switch) getActivity().findViewById(R.id.smartCue);
        sSmartCue.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                DeviceController deviceController = DeviceController.getInstance();
                if(!deviceController.setNotification(CharacteristicName.READCHAR)) // TODO single set
                {
                    Log.e("BLE", "Failed to set Characteristic 'READ'");
                }
                sPlayCue.setEnabled(!sSmartCue.isChecked());
                if(sSmartCue.isChecked() == false)
                {
                    metronome.stopPlayback();
                }
            }
        });

        cbVisual.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(cbVisual.isChecked())
                    metronome.setModeVisual(true);
                else
                    metronome.setModeVisual(false);
            }
        });

        cbAudible.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(cbAudible.isChecked())
                    metronome.setModeAudible(true);
                else
                    metronome.setModeAudible(false);
            }
        });

        cbHaptic.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if(cbHaptic.isChecked())
                    metronome.setModeHaptic(true);
                else
                    metronome.setModeHaptic(false);
            }
        });

        tvVisual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cbVisual.setChecked(!cbVisual.isChecked());
                cbVisual.callOnClick();
            }
        });
        tvAudible.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cbAudible.setChecked(!cbAudible.isChecked());
                cbAudible.callOnClick();
            }
        });
        tvHaptic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cbHaptic.setChecked(!cbHaptic.isChecked());
                cbHaptic.callOnClick();
            }
        });

        sbFrequency.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progressChanged = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressChanged = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                metronome.setBPM(progressChanged);
            }
        });

        sPlayCue.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView,
                                        boolean isChecked)
        {
            if(isConnected)
                sSmartCue.setEnabled(!sPlayCue.isChecked());
            if(isChecked)
            {
                metronome.startPlayback();
                if(metronome.isThreadRunning() == false)
                    executor.execute(metronome);
            }
            else
            {
                metronome.stopPlayback();
            }
        }});

        super.onViewCreated(view, savedInstanceState);

    }
}
