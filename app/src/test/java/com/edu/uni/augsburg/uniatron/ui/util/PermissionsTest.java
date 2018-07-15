package com.edu.uni.augsburg.uniatron.ui.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PermissionsTest {

    /*
    @Mock
    private Context mContext;
    @Mock
    private AppOpsManager mAppOpsManager;
    @Mock
    private PackageManager mPackageManager;

    @Test
    public void requestUsageAccessPreLollipop() {
        when(mContext.getSystemService(anyString())).thenReturn(null);
        assertThat(Permissions.USAGE_ACCESS_SETTINGS.isNotGranted(mContext), is(true));
    }

    @Test
    public void requestUsageAccessPostLollipopNameNotFound() throws PackageManager.NameNotFoundException {
        when(mContext.getSystemService(anyString())).thenReturn(mAppOpsManager);
        when(mContext.getPackageManager()).thenReturn(mPackageManager);
        when(mPackageManager.getApplicationInfo(anyString(), anyInt())).thenThrow(new PackageManager.NameNotFoundException());

        assertThat(Permissions.USAGE_ACCESS_SETTINGS.isNotGranted(mContext), is(true));
    }

    @Test
    public void requestUsageAccessPostLollipopNotGranted() {
        when(mContext.getSystemService(anyString())).thenReturn(mAppOpsManager);
        when(mAppOpsManager.checkOpNoThrow(anyString(), anyInt(), anyString())).thenReturn(AppOpsManager.MODE_DEFAULT);
        when(mContext.getPackageManager()).thenReturn(mPackageManager);

        assertThat(Permissions.USAGE_ACCESS_SETTINGS.isNotGranted(mContext), is(true));
    }

    @Test
    public void requestUsageAccessPostLollipopGranted() {
        when(mContext.getSystemService(anyString())).thenReturn(mAppOpsManager);
        when(mAppOpsManager.checkOpNoThrow(anyString(), anyInt(), anyString())).thenReturn(AppOpsManager.MODE_ALLOWED);
        when(mContext.getPackageManager()).thenReturn(mPackageManager);

        assertThat(Permissions.USAGE_ACCESS_SETTINGS.isNotGranted(mContext), is(false));
    }
    */

    @Test
    public void requestIgnoreBatterOptimization() {
    }

    @Test
    public void needBatteryWhitelistPermission() {
    }

    @Test
    public void needUsageAccessPermission() {
    }
}