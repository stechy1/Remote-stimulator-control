package cz.zcu.fav.remotestimulatorcontrol.ui.help;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import cz.zcu.fav.remotestimulatorcontrol.R;

public class HelpFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_help, container, false);

        WebView browser = (WebView)v.findViewById(R.id.webView);
        browser.loadUrl("file:///android_asset/help.html");

        return v;
    }
}
