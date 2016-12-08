package www.wemaketotem.org.totemopenhealth.tablayout;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.ByteBuffer;
import java.util.Arrays;

import www.wemaketotem.org.totemopenhealth.CharacteristicName;
import www.wemaketotem.org.totemopenhealth.DeviceController;
import www.wemaketotem.org.totemopenhealth.Observer;
import www.wemaketotem.org.totemopenhealth.R;

/**
 * Fragment for the Device page.
 */
public class FragmentDevice extends Fragment implements Observer {

    private Switch sLog, sAccelero, sGyro, sTemp;
    private TextView tAccelero, tGyro, tTemp, tTime;
    private Button bDisconnect;
//    private DiscreteSeekBar mSeekBar;
    private EditText eTime;
    private TextView tName;
    private DeviceController mDeviceController;
    private boolean measuring = false;

    /**
     * Creates a new instance of the fragment
     * @return new instance of Device fragment
     */
    public static FragmentDevice newInstance() {
        return new FragmentDevice();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.pager_device, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mDeviceController = DeviceController.getInstance();
        initializeWidgets(view);
        initSwitches();
//        initSeekBar();
        initButton();
        initEditText();
    }

    /**
     * Initializes all the widgets used in the view
     * @param view main view where the widgets exist
     */
    private void initializeWidgets(View view) {
        tName = (TextView) view.findViewById(R.id.device_device_name);
        tName.setText("not connected");

        bDisconnect = (Button) view.findViewById(R.id.device_disconnect);

        sLog = (Switch) view.findViewById(R.id.switch_log_data);
        sAccelero = (Switch) view.findViewById(R.id.switch_accelero);
        sGyro = (Switch) view.findViewById(R.id.switch_gyro);
        sTemp = (Switch) view.findViewById(R.id.switch_temp);

        tAccelero = (TextView) view.findViewById(R.id.device_text_accelero);
        tGyro = (TextView) view.findViewById(R.id.device_text_gyro);
        tTemp = (TextView) view.findViewById(R.id.device_text_temp);
        tTime = (TextView) view.findViewById(R.id.device_time);
        eTime = (EditText) view.findViewById(R.id.device_edit_time);
    }

    /**
     * Init for the switches.
     * Activates all functions except the log data.
     * Sets also for each switch his listener.
     */
    private void initSwitches() {
        sLog.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                byte command = 0;
                if(sTemp.isChecked())
                    command |= (1 << 4);
                if(sAccelero.isChecked())
                    command |= (1 << 5);
                if(sGyro.isChecked())
                    command |= (1 << 6);
                if (isChecked)
                {
                    command |= (1 << 7);
                    setClickableSwitches(false);
                    measuring = true;
                } else
                {
                    setClickableSwitches(true);
                    measuring = false;
                }

                byte[] msg = new byte[3];
                msg[0] = command;
//                msg[1] = timeHB;
//                msg[2] = timeLB;
                String debugLog = Arrays.toString(msg);
                Log.d("message", debugLog);
                mDeviceController.writeCharacteristic(CharacteristicName.WRITECHAR, msg);
            }
        });
    }

    /**
     * Reset the widgets to the default settings
     */
    private void resetWidgets() {
        sLog.setChecked(false);
        sAccelero.setChecked(true);
        sTemp.setChecked(true);
        sGyro.setChecked(true);
//        mSeekBar.setEnabled(true);
    }

    /**
     * Init of the seekbar.
     * It sets the listeners.
     */
//    private void initSeekBar() {
//        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            int progressChanged = 0;
//
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                progressChanged = progress;
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//                // TODO Auto-generated method stub
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {}
//        });
//    }

    /**
     * enable or disable all widgets except the log data.
     * @param status true if enabled, false if disabled.
     */
    private void setClickableSwitches(final boolean status) {
        sAccelero.setClickable(status);
        sGyro.setClickable(status);
        sTemp.setClickable(status);
        tAccelero.setEnabled(status);
        tGyro.setEnabled(status);
        tTemp.setEnabled(status);
        tTime.setEnabled(status);
    }

    /**
     * sets the listener of the disconnect button
     */
    private void initButton() {
        bDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (measuring) {
                    Toast.makeText(getContext(), "measurement in progress, can't disconnect", Toast.LENGTH_SHORT).show();
                } else {
                    mDeviceController.disconnectDevice();
                }
            }
        });
    }

    private void initEditText()
    {
        eTime.addTextChangedListener(new TextWatcher(){
            @Override
            public void onTextChanged(CharSequence s, int start, int count, int after)
            {
//                if(s.length() > 0)
//                {
//                    int intValue;
//                    intValue = Integer.parseInt(s.toString());
//                }
            }

            public void afterTextChanged(Editable s)
            {

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }
        });
    }

    /**
     * Observer method.
     * Sets all widgets to the initial value.
     */
    @Override
    public void deviceConnected() {
        tName.setText(mDeviceController.getBluetoothDevice().getName());
        long time = System.currentTimeMillis();
        Log.d("Time", "time is " + time);

        ByteBuffer buffer = ByteBuffer.allocate(Long.SIZE);
        buffer.putLong(time);

        mDeviceController.writeCharacteristic(CharacteristicName.WRITECHAR, buffer.array());
        resetWidgets();
    }

    /**
     * Observer method.
     * Resets all widgets values.
     */
    @Override
    public void deviceDisconnected() {
        tName.setText("not connected");
        setClickableSwitches(true);
        mDeviceController.closeConnection();
    }
}
