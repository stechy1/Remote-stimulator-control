package cz.zcu.fav.remotestimulatorcontrol.ui.stimulator;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import cz.zcu.fav.remotestimulatorcontrol.R;
import cz.zcu.fav.remotestimulatorcontrol.databinding.ActivityStimulatorControlBinding;
import cz.zcu.fav.remotestimulatorcontrol.model.bytes.BtPacket;
import cz.zcu.fav.remotestimulatorcontrol.model.bytes.StimulatorControl;
import cz.zcu.fav.remotestimulatorcontrol.service.BluetoothService;

public class StimulatorControlActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityStimulatorControlBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_stimulator_control);
        binding.setController(this);
        binding.executePendingBindings();

        setSupportActionBar(binding.toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        // TODO načíst vybranou konfiguraci a asi ji poslat do stimulátoru...

    }

    public void onStimulationStart(View view) {
        BluetoothService.sendData(this, StimulatorControl.getStartPacket());
    }

    public void onStimulationStop(View view) {
        BluetoothService.sendData(this, StimulatorControl.getStopPacket());
    }

    public void onStimulationRefresh(View view) {
        Toast.makeText(this, "Posílám příkaz k obnovení dat", Toast.LENGTH_SHORT).show();
        BtPacket packet  = new BtPacket();
        BluetoothService.sendData(this, packet);
    }
}
