package com.safetyar.app.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.safetyar.app.data.sync.DataSyncWorker;

public class NetworkMonitor {
    private static final String TAG = "NetworkMonitor";
    private static volatile NetworkMonitor INSTANCE;

    private final Context context;
    private final ConnectivityManager connectivityManager;
    private final MutableLiveData<Boolean> isOnline = new MutableLiveData<>(false);

    private NetworkMonitor(Context context) {
        this.context = context.getApplicationContext();
        this.connectivityManager = (ConnectivityManager) this.context.getSystemService(Context.CONNECTIVITY_SERVICE);
        init();
    }

    public static NetworkMonitor getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (NetworkMonitor.class) {
                if (INSTANCE == null) {
                    INSTANCE = new NetworkMonitor(context);
                }
            }
        }
        return INSTANCE;
    }

    private void init() {
        checkInitialState();

        NetworkRequest request = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build();

        connectivityManager.registerNetworkCallback(request, new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(@NonNull Network network) {
                Log.i(TAG, "Network connection restored. Dispatching background synchronization...");
                isOnline.postValue(true);
                // Trigger immediate sync when network restored
                DataSyncWorker.scheduleImmediateSync(context);
            }

            @Override
            public void onLost(@NonNull Network network) {
                Log.w(TAG, "Network connection lost. Operating in offline-first mode.");
                isOnline.postValue(false);
            }
        });
    }

    private void checkInitialState() {
        Network activeNetwork = connectivityManager.getActiveNetwork();
        if (activeNetwork != null) {
            NetworkCapabilities caps = connectivityManager.getNetworkCapabilities(activeNetwork);
            boolean connected = caps != null && caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
            isOnline.postValue(connected);
        } else {
            isOnline.postValue(false);
        }
    }

    public LiveData<Boolean> getIsOnline() {
        return isOnline;
    }
}
