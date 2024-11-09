package com.ninjaone.dundie_awards.model;

import com.ninjaone.dundie_awards.MessageBroker;

public class ActivityRunnable implements Runnable {
    private final Activity activity;
    private final MessageBroker messageBroker;

    public ActivityRunnable(Activity activity, MessageBroker messageBroker) {
        this.activity = activity;
        this.messageBroker = messageBroker;
    }

    public Activity getActivity() {
        return activity;
    }

    @Override
    public void run() {
        messageBroker.sendMessageInternal(activity); // Internal method to handle activity logic
    }
}
