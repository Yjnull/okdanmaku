package com.okdanmaku.demo.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.okdanmaku.core.callback.DrawHandlerCallback;
import com.okdanmaku.core.danmaku.mdoel.BaseDanmaku;
import com.okdanmaku.core.danmaku.mdoel.DanmakuConfig;
import com.okdanmaku.core.danmaku.mdoel.DanmakuTimer;
import com.okdanmaku.core.danmaku.mdoel.IDanmakuCollection;
import com.okdanmaku.core.danmaku.parser.BaseDanmakuParser;
import com.okdanmaku.core.ui.DanmakuView;
import com.okdanmaku.demo.R;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        final TextView textView = root.findViewById(R.id.text_home);
        final DanmakuView danmakuView = root.findViewById(R.id.danmaku_home);
        homeViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        Log.e("yjnull", "HomeFragmetn preparing ... " + danmakuView);

        danmakuView.setCallback(new DrawHandlerCallback() {
            @Override
            public void prepared() {
                Log.e("yjnull", "HomeFragmetn prepared");
                danmakuView.start();
            }

            @Override
            public void updateTimer(DanmakuTimer timer) {

            }

            @Override
            public void danmakuShown(BaseDanmaku danmaku) {

            }

            @Override
            public void drawingFinished() {

            }
        });
        danmakuView.prepare(new BaseDanmakuParser() {
            @Override
            protected IDanmakuCollection parse() {
                return null;
            }
        }, new DanmakuConfig());
        danmakuView.enableDanmakuDrawingCache(false);

        return root;
    }
}