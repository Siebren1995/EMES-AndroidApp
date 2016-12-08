package www.wemaketotem.org.totemopenhealth.tablayout;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;

import java.util.concurrent.Executor;

import www.wemaketotem.org.totemopenhealth.Metronome;
import www.wemaketotem.org.totemopenhealth.R;

public class FragmentCue extends Fragment {

    private Metronome metronome;
    private Executor executor;
    private SeekBar sbFrequency;
    private Switch sPlayCue;
    private Spinner spCueMode;

    public static FragmentCue newInstance() {
        return new FragmentCue();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.pager_cue, container, false);
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
        spCueMode = (Spinner) getActivity().findViewById(R.id.spMode);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.spCueMode,
                                                                            android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCueMode.setAdapter(adapter);

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
            if(isChecked)
            {
                if(spCueMode.getSelectedItem().toString().equals("Audible"))
                    metronome.startPlayback(0);
                if(spCueMode.getSelectedItem().toString().equals("Visual"))
                    metronome.startPlayback(1);
                if(spCueMode.getSelectedItem().toString().equals("Haptic"))
                    metronome.startPlayback(2);

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
