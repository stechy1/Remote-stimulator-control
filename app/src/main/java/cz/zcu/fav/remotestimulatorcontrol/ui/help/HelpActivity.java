package cz.zcu.fav.remotestimulatorcontrol.ui.help;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import cz.zcu.fav.remotestimulatorcontrol.R;
import cz.zcu.fav.remotestimulatorcontrol.databinding.ActivityHelpBinding;
import cz.zcu.fav.remotestimulatorcontrol.service.BluetoothService;

public class HelpActivity extends AppCompatActivity {

    ActivityHelpBinding mBinding;

    private final BroadcastReceiver mDataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothService.ACTION_DATA_RECEIVED)) {
                int length = intent.getIntExtra(BluetoothService.DATA_RECEIVED_BYTES, -1);
                byte[] bytes = intent.getByteArrayExtra(BluetoothService.DATA_RECEIVED_BUFFER);
                if (length == -1) {
                    return;
                }

                mBinding.textView2.setText(new String(bytes, 0, length));
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         mBinding = DataBindingUtil.setContentView(this, R.layout.activity_help);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        registerReceiver(mDataReceiver, new IntentFilter(BluetoothService.ACTION_DATA_RECEIVED));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mDataReceiver);
        super.onDestroy();
    }

    public void onSend(View view) {
        String text = mBinding.editText1.getText().toString();
        Intent intent = new Intent(BluetoothService.ACTION_SEND_DATA);
        intent.putExtra(BluetoothService.DATA_CONTENT, text.getBytes());
        sendBroadcast(intent);
    }
}
