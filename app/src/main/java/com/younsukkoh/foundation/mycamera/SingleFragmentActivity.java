package com.younsukkoh.foundation.mycamera;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Younsuk on 8/28/2015.
 */
public abstract class SingleFragmentActivity extends AppCompatActivity {

    /**
     * @return the fragment will use the entire screen.
     */
    protected abstract Fragment createFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_fragment_activity);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.single_fragment_activity_frameLayout);

        if(fragment == null){
            fragment = createFragment();
            fm.beginTransaction().add(R.id.single_fragment_activity_frameLayout, fragment).commit();
        }
    }

}
