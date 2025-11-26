package com.example.coolioevents.Entrant;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.example.coolioevents.R;


public class EntrantSettingsFragment extends Fragment {

    private Switch notificiationsSwitch;
    private boolean userChecked = true;

    public EntrantSettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {

                super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_entrant_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);{
            // Initialize UI elements
            notificiationsSwitch = view.findViewById(R.id.notificationSwitch);


            // Check notification permissions - if user has permissoins on, have
            // the notification switch on, if not, have it off
            checkPermissions();

            // Notification switch to turn on/off notifications
            notificiationsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(@NonNull CompoundButton buttonView, boolean isChecked) {
                    if (userChecked){
                        // Checks to see if a real user, is clicking the notification
                        // or if its the program (if its the program don't prompt user with anything)
                        if (isChecked){
                            showSettingsPrompt("on");
                        }
                        else {
                            // If user turned switch off
                            showSettingsPrompt("off"); // Tell user they must go to app settings to turn off notifications
                        }
                    }
                    else {
                        userChecked = true;
                    }
                }
            });
        }
    }

    /**
     * This method creates and shows a new alert dialog which
     * informs the user that notificaitons can only be turned off via settings
     * and allows them to go to settings if they press "Go to settings" on the alert dialog
     *
     * @param state
     *  Tells the what the settings dialog should say (whether to tel user to turn off or on notifications)
     */
    private void showSettingsPrompt(String state){
        new AlertDialog.Builder(requireActivity())
                .setTitle(String.format("Turn Notifications %s In Settings", state))
                .setMessage(String.format("To turn %s notifications please turn %s post notifications in settings.", state, state))
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        checkPermissions(); // If cancelled, check permissions again and change switch as necessary
                        dialog.dismiss();
                    }
                })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        checkPermissions(); // If cancelled, check permissions again and change switch as necessary
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("Go to settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        /*Taken from: Google Gemini
                        Prompt: how to go to settings app from android app android studio java in an fragment action application settings
                        Taken by: Ethan Diep
                        Taken on: 11/25/25*/
                        // Sends user to settings to change notification settings
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", requireActivity().getPackageName(), null);
                        intent.setData(uri);
                        startActivity(intent);
                        dialog.dismiss();
                    }
                })
                .show();
    }



    @Override
    public void onResume() {
        super.onResume();
        // When user is back form settings or resumes fragment, checks
        // Permissions again to see if notification permissions are changed
        checkPermissions();
    }

    /**
     * This method checks perimssions of user - whether their notifications are turned
     * on or off - and switches notificationSwitch depending on what their current
     * notification settings are.
     */
    private void checkPermissions(){
        userChecked = false; // The next switch on the switch is not a "real user" switching the fragment
        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            // Notifications permissions are on - set switch to on
            notificiationsSwitch.setChecked(true);
        }
        else {
            // Notifications permissions are off - set switch to off
            notificiationsSwitch.setChecked(false);
        }
        userChecked = true; // Set userChecked back to true to allow user to switch the switch again
    }
}