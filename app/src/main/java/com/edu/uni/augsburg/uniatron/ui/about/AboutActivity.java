package com.edu.uni.augsburg.uniatron.ui.about;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.edu.uni.augsburg.uniatron.R;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

/**
 * The about page of this app.
 *
 * @author Fabio Hellmann
 */
public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final View aboutPage = new AboutPage(this)
                .isRTL(false)
                .setImage(R.mipmap.ic_launcher)
                .setDescription(getString(R.string.about_description))
                .addItem(new Element().setTitle("Version " + getVersion()))
                .addGroup("Connect with us")
                .addPlayStore("com.edu.uni.augsburg.uniatron")
                .addGitHub("FHellmann", "Fabio Hellmann")
                .addGitHub("leonpoint", "Leon Wöhrl")
                .addGitHub("anja-h", "Anja Hager")
                .addGitHub("speedyhoopster3", "Danilo Hoss")
                .create();

        setContentView(aboutPage);
    }

    private String getVersion() {
        try {
            return getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "...";
        }
    }
}
