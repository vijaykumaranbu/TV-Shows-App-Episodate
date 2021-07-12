package com.example.episodate.utilities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.ArrayList;
import java.util.List;

public class NetworkStateReceiver extends BroadcastReceiver {

    private final List<NetworkStateListener> listeners;
    private Boolean connected;

    public NetworkStateReceiver(){
        listeners = new ArrayList<>();
        connected = null;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent == null && intent.getExtras() == null) return;

        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();

        connected = info != null && info.isConnected();

        notifyStateToAll();
    }

    private void notifyStateToAll(){
        for(NetworkStateListener listener : listeners){
            notifyState(listener);
        }
    }

    private void notifyState(NetworkStateListener listener){
        if(connected == null || listener == null) return;

        if(connected)
            listener.networkAvailable();
        else
            listener.networkUnAvailable();
    }

    public void addNetworkListener(NetworkStateListener listener){
        listeners.add(listener);
        notifyState(listener);
    }

    public void removeNetworkListener(NetworkStateListener listener){
        listeners.remove(listener);
    }

    public interface NetworkStateListener{
        void networkAvailable();
        void networkUnAvailable();
    }
}
