package com.matm.matmsdk.Bluetooth;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.matm.matmsdk.CustomThemes;
import com.matm.matmsdk.Utils.SdkConstants;

import java.util.Objects;

import isumatm.androidsdk.equitas.R;


/**
 * Main Activity of this application.
 *
 * @author Donato Rimenti
 */
public class BluetoothConnectorActivity extends AppCompatActivity implements ListInteractionListener<BluetoothDevice> {


    /**
     * The controller for Bluetooth functionalities.
     */

    private BluetoothController bluetooth;

    /**
     * The Bluetooth discovery button.
     */
    private FloatingActionButton fab;

    /**
     * Progress dialog shown during the pairing process.
     */
    private ProgressDialog bondingProgressDialog;

    /**
     * Adapter for the recycler view.
     */
    private DeviceRecyclerViewAdapter recyclerViewAdapter;

    private RecyclerViewProgressEmptySupport recyclerView;

    public static BluetoothDevice selected_btdevice;

    SharePreferenceClass sharePreferenceClass;
    public static final String TAG = BluetoothConnectorActivity.class.getSimpleName();

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Changes the theme back from the splashscreen. It's very important that this is called
        // BEFORE onCreate.
        SystemClock.sleep(1500);
        /*setTheme(R.style.AppTheme_NoActionBar);*/

        new CustomThemes(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_print);
        // Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // setSupportActionBar(toolbar);
        setToolbar();
        sharePreferenceClass = new SharePreferenceClass(BluetoothConnectorActivity.this);

        // Sets up the RecyclerView.
        this.recyclerViewAdapter = new DeviceRecyclerViewAdapter(this);
        this.recyclerView = (RecyclerViewProgressEmptySupport) findViewById(R.id.list);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Sets the view to show when the dataset is empty. IMPORTANT : this method must be called
        // before recyclerView.setAdapter().
        View emptyView = findViewById(R.id.empty_list);
        this.recyclerView.setEmptyView(emptyView);

        // Sets the view to show during progress.
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        this.recyclerView.setProgressView(progressBar);

        this.recyclerView.setAdapter(recyclerViewAdapter);

        // [#11] Ensures that the Bluetooth is available on this device before proceeding.
        boolean hasBluetooth = getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH);
        if (!hasBluetooth) {
            AlertDialog dialog = new AlertDialog.Builder(BluetoothConnectorActivity.this).create();
            dialog.setTitle(getString(R.string.bluetooth_not_available_title));
            dialog.setMessage(getString(R.string.bluetooth_not_available_message));
            dialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Closes the dialog and terminates the activity.
                            dialog.dismiss();
                            BluetoothConnectorActivity.this.finish();
                        }
                    });
            dialog.setCancelable(false);
            dialog.show();
        }

        // Sets up the bluetooth controller.
        this.bluetooth = new BluetoothController(this, BluetoothAdapter.getDefaultAdapter(), recyclerViewAdapter);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // If the bluetooth is not enabled, turns it on.
                if (!bluetooth.isBluetoothEnabled()) {
                    Snackbar.make(view, R.string.enabling_bluetooth, Snackbar.LENGTH_SHORT).show();
                    bluetooth.turnOnBluetoothAndScheduleDiscovery();
                } else {
                    //Prevents the user from spamming the button and thus glitching the UI.
                    if (!bluetooth.isDiscovering()) {
                        // Starts the discovery.
                        Snackbar.make(view, R.string.device_discovery_started, Snackbar.LENGTH_SHORT).show();
                        bluetooth.startDiscovery();
                    } else {
                        Snackbar.make(view, R.string.device_discovery_stopped, Snackbar.LENGTH_SHORT).show();
                        bluetooth.cancelDiscovery();
                    }
                }
            }
        });

        if (!bluetooth.isBluetoothEnabled()) {
            bluetooth.turnOnBluetoothAndScheduleDiscovery();
        } else {
            //Prevents the user from spamming the button and thus glitching the UI.
            if (!bluetooth.isDiscovering()) {
                // Starts the discovery.
                bluetooth.startDiscovery();
            } else {
                bluetooth.cancelDiscovery();
            }
        }
    }

    private void setToolbar() {
        Toolbar mToolbar = findViewById(R.id.toolbar);
        mToolbar.setTitle("Select Device");
        mToolbar.inflateMenu(R.menu.bank_menu);
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.action_close) {
                    // finish ();
                    onBackPressed();
                }
                return false;
            }
        });
    }



    /**
     * {@inheritDoc}
     */
    @Override
    public void onItemClick(BluetoothDevice device) {
        try {
            Log.v("Connector", "Item clicked : " + BluetoothController.deviceToString(device));
            String name = Objects.requireNonNull(device.getName());
            if (name != null) {
                if (name.length() > 9) {
                    name = name.substring(0, 9);
                }
                if (name.contains("BTprinter")) {
                    if (bluetooth.isAlreadyPaired(device)) {
                        Log.d(TAG, "Device already paired!");
                        SdkConstants.selected_btdevice = device;
                        sharePreferenceClass.setBluetoothInstance(device);
                        Toast.makeText(this, R.string.device_already_paired, Toast.LENGTH_SHORT).show();
                        finish();

                    } else {
                        Log.d("Connector", "Device not paired. Pairing.");
                        boolean outcome = bluetooth.pair(device);
                        // Prints a message to the user.
                        String deviceName = BluetoothController.getDeviceName(device);
                        if (outcome) {
                            // The pairing has started, shows a progress dialog.
                            Log.d("Connector", "Showing pairing dialog");
                            bondingProgressDialog = ProgressDialog.show(this, "", "Pairing with device " + deviceName + "...", true, false);
                        } else {
                            Log.d("Connector", "Error while pairing with device " + deviceName + "!");
                            Toast.makeText(this, "Error while pairing with device " + deviceName + "!", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else{
                    Toast.makeText(this, "Please select the required Bluetooth Device", Toast.LENGTH_SHORT).show();

                }
            } else {
                Toast.makeText(this, "Selected device name not found !", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
        }
    }






    /*@Overrides
    public void onItemClick(BluetoothDevice device) {
        try {
            Log.v("Connector", "Item clicked : " + BluetoothController.deviceToString(device));
            String name = Objects.requireNonNull(device.getName());

            if (name != null) {

                if (name.length() > 9) {
                    name = name.substring(0, 9);
                }

                if (name.contains("BTprinter")) {
                    if (bluetooth.isAlreadyPaired(device)) {
                        Log.d("Connector", "Device already paired!");
                        Constants.selected_btdevice = device;
                        sharePreferenceClass.setBluetoothInstance(device);

                        Toast.makeText(this, R.string.device_already_paired, Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Log.d("Connector", "Device not paired. Pairing.");
                        boolean outcome = bluetooth.pair(device);

                        // Prints a message to the user.
                        String deviceName = BluetoothController.getDeviceName(device);
                        if (outcome) {
                            // The pairing has started, shows a progress dialog.
                            Log.d("Connector", "Showing pairing dialog");
                            bondingProgressDialog = ProgressDialog.show(this, "", "Pairing with device " + deviceName + "...", true, false);
                        } else {
                            Log.d("Connector", "Error while pairing with device " + deviceName + "!");
                            Toast.makeText(this, "Error while pairing with device " + deviceName + "!", Toast.LENGTH_SHORT).show();
                        }
                    }

                } else {
                    Toast.makeText(this, "Please select BluPrint Device", Toast.LENGTH_SHORT).show();
                }

                if (name.contains("ESIAF3996")) {
                    if (bluetooth.isAlreadyPaired(device)) {
                        Log.d("Connector", "Device already paired!");
                        Constants.selected_fingerPrint = device;
                        bluetoothConnector = true;
                        sharePreferenceClass.setBluetoothInstance(device);


                        Toast.makeText(this, R.string.device_already_paired, Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Log.d("Connector", "Device not paired. Pairing.");
                        boolean outcome = bluetooth.pair(device);

                        // Prints a message to the user.
                        String deviceName = BluetoothController.getDeviceName(device);
                        if (outcome) {
                            // The pairing has started, shows a progress dialog.
                            Log.d("Connector", "Showing pairing dialog");
                            bondingProgressDialog = ProgressDialog.show(this, "", "Pairing with device " + deviceName + "...", true, false);
                        } else {
                            Log.d("Connector", "Error while pairing with device " + deviceName + "!");
                            Toast.makeText(this, "Error while pairing with device " + deviceName + "!", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(this, "Please select Biometric Device", Toast.LENGTH_SHORT).show();

                }

            } else {
                Toast.makeText(this, "Selected device name not found !", Toast.LENGTH_SHORT).show();

            }

        } catch (Exception e) {

        }
    }
*/
    /**
     * {@inheritDoc}
     */
    @Override
    public void startLoading() {
        this.recyclerView.startLoading();

        // Changes the button icon.
        this.fab.setImageResource(R.drawable.ic_bluetooth_searching_white_24dp);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void endLoading(boolean partialResults) {
        this.recyclerView.endLoading();

        // If discovery has ended, changes the button icon.
        if (!partialResults) {
            fab.setImageResource(R.drawable.ic_bluetooth_white_24dp);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void endLoadingWithDialog(boolean error, BluetoothDevice device) {
        if (this.bondingProgressDialog != null) {
            View view = findViewById(R.id.main_content);
            String message;
            String deviceName = BluetoothController.getDeviceName(device);

            // Gets the message to print.
            if (error) {
                message = "Failed pairing with device " + deviceName + "!";
            } else {
                SdkConstants.selected_btdevice = device;
                SdkConstants.selected_fingerPrint = device;
                sharePreferenceClass.setBluetoothInstance(device);

                message = "Succesfully paired with device " + deviceName + "!";
            }

            // Dismisses the progress dialog and prints a message to the user.
            this.bondingProgressDialog.dismiss();
            Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();

            // Cleans up state.
            this.bondingProgressDialog = null;
            finish();
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onDestroy() {
        bluetooth.close();
        super.onDestroy();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onRestart() {
        super.onRestart();
        // Stops the discovery.
        if (this.bluetooth != null) {
            this.bluetooth.cancelDiscovery();
        }
        // Cleans the view.
        if (this.recyclerViewAdapter != null) {
            this.recyclerViewAdapter.cleanView();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onStop() {
        super.onStop();
        // Stoops the discovery.
        if (this.bluetooth != null) {
            this.bluetooth.cancelDiscovery();
        }
    }


}
