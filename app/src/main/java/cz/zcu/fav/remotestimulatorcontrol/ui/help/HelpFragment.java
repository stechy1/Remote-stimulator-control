package cz.zcu.fav.remotestimulatorcontrol.ui.help;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import cz.zcu.fav.remotestimulatorcontrol.R;
import cz.zcu.fav.remotestimulatorcontrol.databinding.FragmentHelpBinding;
import cz.zcu.fav.remotestimulatorcontrol.service.BluetoothService;

public class HelpFragment extends Fragment {

    private FragmentHelpBinding mBinding;
    // region Variables
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
    // endregion

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_help, container, false);
        mBinding.setController(this);
        mBinding.executePendingBindings();

        getActivity().registerReceiver(mDataReceiver, new IntentFilter(BluetoothService.ACTION_DATA_RECEIVED));

        return mBinding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
    }

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(mDataReceiver);
        super.onDestroy();
    }

    public void onSend(View view) {
        String text = mBinding.editText1.getText().toString();
        Intent intent = new Intent(BluetoothService.ACTION_SEND_DATA);
        intent.putExtra(BluetoothService.DATA_CONTENT, text.getBytes());
        getActivity().sendBroadcast(intent);
    }

}
