package com.example.foxizz.navigation.activity.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.foxizz.navigation.R;
import com.example.foxizz.navigation.activity.LoginActivity;
import com.example.foxizz.navigation.activity.MainActivity;
import com.example.foxizz.navigation.activity.SettingsActivity;
import com.example.foxizz.navigation.view.AdaptationTextView;

/**
 * 用户页碎片
 */
public class UserFragment extends Fragment {

    //UserFragment实例
    @SuppressLint("StaticFieldLeak")
    private static UserFragment instance;
    public static UserFragment getInstance() {
        return instance;
    }

    public static int userId = 0;
    private FrameLayout portraitLayout;//头像布局
    private ImageView userPortrait;//用户头像
    private LinearLayout userInfoLayout;//信息布局
    private AdaptationTextView userName;//用户名
    private AdaptationTextView userEmail;//用户email

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        instance = this;//获取UserFragment实例

        initUserLayout(view);//初始化用户布局

        //初始化PreferenceScreen
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.user_preferences, new PreferenceScreen())
                .commit();

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        instance = null;//释放UserFragment实例
    }

    //初始化用户布局
    private void initUserLayout(View view) {
        portraitLayout = view.findViewById(R.id.portrait_layout);
        userPortrait = view.findViewById(R.id.user_portrait);

        userInfoLayout = view.findViewById(R.id.user_info_layout);
        userName = view.findViewById(R.id.user_name);
        userEmail = view.findViewById(R.id.user_email);

        portraitLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userId == 0) {
                    startActivity(new Intent(getContext(), LoginActivity.class));
                } else {

                }
            }
        });

        userInfoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (userId == 0) {
                    startActivity(new Intent(getContext(), LoginActivity.class));
                } else {

                }
            }
        });
    }

    //PreferenceScreen
    public static class PreferenceScreen extends PreferenceFragmentCompat {
        //创建PreferenceScreen
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.user_preferences, rootKey);
        }

        //设置PreferenceScreen的点击事件
        @Override
        public boolean onPreferenceTreeClick(Preference preference) {
            Intent browser = new Intent("android.intent.action.VIEW");
            switch (preference.getKey()) {
                case "to_settings":
                    Intent intent = new Intent(getContext(), SettingsActivity.class);

                    //寻找mainFragment
                    MainFragment mainFragment = ((MainActivity) requireActivity()).getMainFragment();
                    //传递mCity
                    if (mainFragment != null && mainFragment.mCity != null)
                        intent.putExtra("mCity", mainFragment.mCity);

                    startActivity(intent);
                    break;
                case "check_update":

                    break;
                case "sound_code":
                    startActivity(browser.setData(Uri.parse("https://github.com/BlueEra/Navigation")));
                    break;
                case "contact_me":
                    startActivity(browser.setData(Uri.parse("mailto:2872545042@qq.com")));
                    break;
                case "logout":
                    UserFragment.userId = 0;
                    break;
                default:
                    break;
            }
            return super.onPreferenceTreeClick(preference);
        }
    }

}
