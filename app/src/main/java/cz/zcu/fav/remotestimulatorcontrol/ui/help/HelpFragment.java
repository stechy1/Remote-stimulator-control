package cz.zcu.fav.remotestimulatorcontrol.ui.help;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import cz.zcu.fav.remotestimulatorcontrol.R;
import cz.zcu.fav.remotestimulatorcontrol.databinding.FragmentHelpBinding;
import cz.zcu.fav.remotestimulatorcontrol.model.bytes.BtPacket;
import cz.zcu.fav.remotestimulatorcontrol.model.bytes.RemoteFileServer;
import cz.zcu.fav.remotestimulatorcontrol.service.BluetoothService;

import static cz.zcu.fav.remotestimulatorcontrol.model.bytes.RemoteFileServer.Codes.INDEX_COMMAND;
import static cz.zcu.fav.remotestimulatorcontrol.model.bytes.RemoteFileServer.Codes.INDEX_HELLO_DATA;
import static cz.zcu.fav.remotestimulatorcontrol.model.bytes.RemoteFileServer.Codes.INDEX_HELLO_VERSION;
import static cz.zcu.fav.remotestimulatorcontrol.model.bytes.RemoteFileServer.Codes.INDEX_ITER;
import static cz.zcu.fav.remotestimulatorcontrol.model.bytes.RemoteFileServer.Codes.OP_HELLO;
import static cz.zcu.fav.remotestimulatorcontrol.model.bytes.RemoteFileServer.Codes.PART_LAST;
import static cz.zcu.fav.remotestimulatorcontrol.model.bytes.RemoteFileServer.Codes.PREFIX;
import static cz.zcu.fav.remotestimulatorcontrol.model.bytes.RemoteFileServer.Codes.TYPE_REQUEST;

public class HelpFragment extends Fragment {

    private FragmentHelpBinding mBinding;
    // region Variables
    private final BroadcastReceiver mDataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothService.ACTION_DATA_RECEIVED)) {
                byte[] bytes = intent.getByteArrayExtra(BluetoothService.EXTRA_DATA_CONTENT);

                BtPacket packet = new BtPacket(bytes);
                Log.d("fjsdlkfasd", "Neco jsem dostal");

                mBinding.textView2.setText(new String(bytes, 0, bytes.length));
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

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mDataReceiver, new IntentFilter(BluetoothService.ACTION_DATA_RECEIVED));

        return mBinding.getRoot();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mDataReceiver);
        super.onDestroy();
    }

    public void onSend(View view) {
        BtPacket packet = RemoteFileServer.getServerPacket();
        byte[] data = new byte[62];
        data[INDEX_COMMAND - PREFIX] = TYPE_REQUEST + PART_LAST + OP_HELLO;
        data[INDEX_ITER - PREFIX] = 0;
        data[INDEX_HELLO_VERSION - PREFIX] = 0; // Verze protokolu
        String s = Build.MANUFACTURER + Build.MODEL;
        System.arraycopy(s.getBytes(), 0, data, INDEX_HELLO_DATA - PREFIX, s.length());
        packet.setData(data);

        BluetoothService.sendData(getActivity(), packet);
    }

}
