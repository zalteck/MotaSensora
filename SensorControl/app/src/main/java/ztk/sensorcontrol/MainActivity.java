package ztk.sensorcontrol;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import android.widget.ImageView;
import android.widget.EditText;


public class MainActivity extends ActionBarActivity {

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;

    public static final int ORIENTATION_SENSOR    = 0;
    public static final int ORIENTATION_PORTRAIT  = 1;
    public static final int ORIENTATION_LANDSCAPE = 2;


    private static TextView mTitle;

    // Name of the connected device
    private String mConnectedDeviceName = null;

    /**
     * Set to true to add debugging code and logging.
     */
    public static final boolean DEBUG = true;

    /**
     * Set to true to log each character received from the remote process to the
     * android log, which makes it easier to debug some kinds of problems with
     * emulating escape sequences and control codes.
     */
    public static final boolean LOG_CHARACTERS_FLAG = DEBUG && false;

    /**
     * Set to true to log unknown escape sequences.
     */
    public static final boolean LOG_UNKNOWN_ESCAPE_SEQUENCES = DEBUG && false;

    /**
     * The tag we use when logging, so that our messages can be distinguished
     * from other messages in the log. Public because it's used by several
     * classes.
     */
    public static final String LOG_TAG = "BlueTerm";

    // Message types sent from the BluetoothReadService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    private BluetoothAdapter mBluetoothAdapter = null;

    private static BluetoothSerialService mSerialService = null;

    private static InputMethodManager mInputManager;

    private boolean mEnablingBT;
    private boolean mLocalEcho = false;
    private int mFontSize = 9;
    private int mColorId = 2;
    private int mControlKeyId = 0;
    private boolean mAllowInsecureConnections = true;
    private int mIncomingEoL_0D = 0x0D;
    private int mIncomingEoL_0A = 0x0A;
    private int mOutgoingEoL_0D = 0x0D;
    private int mOutgoingEoL_0A = 0x0A;

    private int mScreenOrientation = 0;

    private static final String LOCALECHO_KEY = "localecho";
    private static final String FONTSIZE_KEY = "fontsize";
    private static final String COLOR_KEY = "color";
    private static final String CONTROLKEY_KEY = "controlkey";
    private static final String ALLOW_INSECURE_CONNECTIONS_KEY = "allowinsecureconnections";
    private static final String INCOMING_EOL_0D_KEY = "incoming_eol_0D";
    private static final String INCOMING_EOL_0A_KEY = "incoming_eol_0A";
    private static final String OUTGOING_EOL_0D_KEY = "outgoing_eol_0D";
    private static final String OUTGOING_EOL_0A_KEY = "outgoing_eol_0A";
    private static final String SCREENORIENTATION_KEY = "screenorientation";

    public static final int WHITE = 0xffffffff;
    public static final int BLACK = 0xff000000;
    public static final int BLUE = 0xff344ebd;

    private static final int[][] COLOR_SCHEMES = {
            {BLACK, WHITE}, {WHITE, BLACK}, {WHITE, BLUE}};

    private static final int[] CONTROL_KEY_SCHEMES = {
            KeyEvent.KEYCODE_DPAD_CENTER,
            KeyEvent.KEYCODE_AT,
            KeyEvent.KEYCODE_ALT_LEFT,
            KeyEvent.KEYCODE_ALT_RIGHT,
            KeyEvent.KEYCODE_VOLUME_DOWN,
            KeyEvent.KEYCODE_VOLUME_UP
    };
    //    private static final String[] CONTROL_KEY_NAME = {
//        "Ball", "@", "Left-Alt", "Right-Alt"
//    };

    private int mControlKeyCode;

    private SharedPreferences mPrefs;

    private MenuItem mMenuItemConnect;
    private MenuItem mMenuItemStartStopRecording;

    private Dialog mAboutDialog;

    ConsultasBD db;

    Button botonConectar;

    private static TextView actualHora;

    private static TextView actualTemp;
    private static ImageView imageTemp;
    private static EditText changeTemp;

    private static TextView actualHum;
    private static ImageView imageHum;
    private static EditText changeHum;

    private static TextView actualLuz;
    private static ImageView imageLuz;
    private static EditText changeLuz;

    private static EditText changeInt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new ConsultasBD(this);

        if (DEBUG)
            Log.e(LOG_TAG, "+++ ON CREATE +++");

        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        readPrefs();

        mInputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter == null) {
            finishDialogNoBluetooth();
            return;
        }

        mSerialService = new BluetoothSerialService(this, mHandlerBT, db);

        if (DEBUG)
            Log.e(LOG_TAG, "+++ DONE IN ON CREATE +++");

        botonConectar = (Button) findViewById(R.id.botonConectar);
        botonConectar.setText("CONECTAR");

        actualHora = (TextView) findViewById(R.id.titleHora);

        actualTemp = (TextView) findViewById(R.id.textTem);
        actualTemp.setText("-");
        imageTemp = (ImageView) findViewById(R.id.imgTem);
        changeTemp = (EditText) findViewById(R.id.modValueTemp);

        actualHum = (TextView) findViewById(R.id.textHum);
        actualHum.setText("-");
        imageHum = (ImageView) findViewById(R.id.imgHum);
        changeHum = (EditText) findViewById(R.id.modValueHum);

        actualLuz = (TextView) findViewById(R.id.textLuz);
        actualLuz.setText("-");
        imageLuz = (ImageView) findViewById(R.id.imgLuz);
        changeLuz = (EditText) findViewById(R.id.modValueLuz);

        changeInt = (EditText) findViewById(R.id.modValueInt);

    }

    @Override
    public void onStart() {
        super.onStart();
        if (DEBUG)
            Log.e(LOG_TAG, "++ ON START ++");

        mEnablingBT = false;
    }

    @Override
    public synchronized void onResume() {
        super.onResume();

        if (DEBUG) {
            Log.e(LOG_TAG, "+ ON RESUME +");
        }

        if (!mEnablingBT) { // If we are turning on the BT we cannot check if it's enable
            if ( (mBluetoothAdapter != null)  && (!mBluetoothAdapter.isEnabled()) ) {

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.alert_dialog_turn_on_bt)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(R.string.alert_dialog_warning_title)
                        .setCancelable( false )
                        .setPositiveButton(R.string.alert_dialog_yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                mEnablingBT = true;
                                Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                                startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
                            }
                        })
                        .setNegativeButton(R.string.alert_dialog_no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                finishDialogNoBluetooth();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }

            if (mSerialService != null) {
                // Only if the state is STATE_NONE, do we know that we haven't started already
                if (mSerialService.getState() == BluetoothSerialService.STATE_NONE) {
                    // Start the Bluetooth chat services
                    mSerialService.start();
                }
            }

            if (mBluetoothAdapter != null) {
                readPrefs();
                updatePrefs();
            }
        }
        refres();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public synchronized void onPause() {
        super.onPause();
        if (DEBUG)
            Log.e(LOG_TAG, "- ON PAUSE -");
    }

    @Override
    public void onStop() {
        super.onStop();
        if(DEBUG)
            Log.e(LOG_TAG, "-- ON STOP --");
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (DEBUG)
            Log.e(LOG_TAG, "--- ON DESTROY ---");

        if (mSerialService != null)
            mSerialService.stop();

    }

    private void readPrefs() {
        mLocalEcho = mPrefs.getBoolean(LOCALECHO_KEY, mLocalEcho);
        mFontSize = readIntPref(FONTSIZE_KEY, mFontSize, 20);
        mColorId = readIntPref(COLOR_KEY, mColorId, COLOR_SCHEMES.length - 1);
        mControlKeyId = readIntPref(CONTROLKEY_KEY, mControlKeyId, CONTROL_KEY_SCHEMES.length - 1);
        mAllowInsecureConnections = mPrefs.getBoolean( ALLOW_INSECURE_CONNECTIONS_KEY, mAllowInsecureConnections);

        mIncomingEoL_0D = readIntPref(INCOMING_EOL_0D_KEY, mIncomingEoL_0D, 0x0D0A);
        mIncomingEoL_0A = readIntPref(INCOMING_EOL_0A_KEY, mIncomingEoL_0A, 0x0D0A);
        mOutgoingEoL_0D = readIntPref(OUTGOING_EOL_0D_KEY, mOutgoingEoL_0D, 0x0D0A);
        mOutgoingEoL_0A = readIntPref(OUTGOING_EOL_0A_KEY, mOutgoingEoL_0A, 0x0D0A);

        mScreenOrientation = readIntPref(SCREENORIENTATION_KEY, mScreenOrientation, 2);
    }

    private void updatePrefs() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        //setColors();
        mControlKeyCode = CONTROL_KEY_SCHEMES[mControlKeyId];
        mSerialService.setAllowInsecureConnections( mAllowInsecureConnections );

        switch (mScreenOrientation) {
            case ORIENTATION_PORTRAIT:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                break;
            case ORIENTATION_LANDSCAPE:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                break;
            default:
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }
    }

    private int readIntPref(String key, int defaultValue, int maxValue) {
        int val;
        try {
            val = Integer.parseInt( mPrefs.getString(key, Integer.toString(defaultValue)) );
        } catch (NumberFormatException e) {
            val = defaultValue;
        }
        val = Math.max(0, Math.min(val, maxValue));
        return val;
    }

    public int getConnectionState() {
        return mSerialService.getState();
    }

    private byte[] handleEndOfLineChars( int outgoingEoL ) {
        byte[] out;

        if ( outgoingEoL == 0x0D0A ) {
            out = new byte[2];
            out[0] = 0x0D;
            out[1] = 0x0A;
        }
        else {
            if ( outgoingEoL == 0x00 ) {
                out = new byte[0];
            }
            else {
                out = new byte[1];
                out[0] = (byte)outgoingEoL;
            }
        }

        return out;
    }

    public void send(byte[] out) {

        if ( out.length == 1 ) {

            if ( out[0] == 0x0D ) {
                out = handleEndOfLineChars( mOutgoingEoL_0D );
            }
            else {
                if ( out[0] == 0x0A ) {
                    out = handleEndOfLineChars( mOutgoingEoL_0A );
                }
            }
        }

        if ( out.length > 0 ) {
            mSerialService.write( out );
        }

    }

    public void toggleKeyboard() {
        mInputManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    public int getTitleHeight() {
        return mTitle.getHeight();
    }

    // The Handler that gets information back from the BluetoothService
    private final Handler mHandlerBT = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    if(DEBUG) Log.i(LOG_TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                    switch (msg.arg1) {
                        case BluetoothSerialService.STATE_CONNECTED:
                            if (mMenuItemConnect != null) {
                                mMenuItemConnect.setIcon(android.R.drawable.ic_menu_close_clear_cancel);
                                mMenuItemConnect.setTitle(R.string.disconnect);
                            }
                            cambiarEstadoConectado();
                            break;

                        case BluetoothSerialService.STATE_CONNECTING:
                            cambiarEstadoDesconectado();
                            cambiarEstadoConectando();
                            break;

                        case BluetoothSerialService.STATE_LISTEN:
                        case BluetoothSerialService.STATE_NONE:
                            if (mMenuItemConnect != null) {
                                mMenuItemConnect.setIcon(android.R.drawable.ic_menu_search);
                                mMenuItemConnect.setTitle(R.string.connect);
                            }
                            cambiarEstadoDesconectado();
                            break;
                    }
                    break;
                /*
                case MESSAGE_WRITE:
                    if (mLocalEcho) {
                        byte[] writeBuf = (byte[]) "h".getBytes();
                        Log.d("PROPIOS", "MANDADO log " + writeBuf.toString());
                    }

                    break;
                */

                case MESSAGE_READ:
                    refres();
                    Log.d("PROPIOS", "Recibido en MAIN");
                    break;


                case MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    Toast.makeText(getApplicationContext(), getString(R.string.toast_connected_to) + " "
                            + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };


    public void finishDialogNoBluetooth() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.alert_dialog_no_bt)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setTitle(R.string.app_name)
                .setCancelable( false )
                .setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(DEBUG) Log.d(LOG_TAG, "onActivityResult " + resultCode);
        switch (requestCode) {

            case REQUEST_CONNECT_DEVICE:

                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    // Get the device MAC address
                    String address = data.getExtras()
                            .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    // Get the BLuetoothDevice object
                    BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                    // Attempt to connect to the device
                    mSerialService.connect(device);
                }
                break;

            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode != Activity.RESULT_OK) {
                    Log.d(LOG_TAG, "BT not enabled");

                    finishDialogNoBluetooth();
                }
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (handleControlKey(keyCode, true)) {
            return true;
        } else if (isSystemKey(keyCode, event)) {
            // Don't intercept the system keys
            return super.onKeyDown(keyCode, event);
        } else if (handleDPad(keyCode, true)) {
            return true;
        }
/*
        // Translate the keyCode into an ASCII character.
        int letter = mKeyListener.keyDown(keyCode, event);

        if (letter >= 0) {
            byte[] buffer = new byte[1];
            buffer[0] = (byte)letter;

            send( buffer );
            //mSerialService.write(buffer);
        }
        */
        return true;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (handleControlKey(keyCode, false)) {
            return true;
        } else if (isSystemKey(keyCode, event)) {
            // Don't intercept the system keys
            return super.onKeyUp(keyCode, event);
        } else if (handleDPad(keyCode, false)) {
            return true;
        }

        //mKeyListener.keyUp(keyCode);
        return true;
    }

    private boolean handleControlKey(int keyCode, boolean down) {
        if (keyCode == mControlKeyCode) {
            //mKeyListener.handleControlKey(down);
            return true;
        }
        return false;
    }

    /**
     * Handle dpad left-right-up-down events. Don't handle
     * dpad-center, that's our control key.
     * @param keyCode
     * @param down
     */
    private boolean handleDPad(int keyCode, boolean down) {
        byte[] buffer = new byte[1];

        if (keyCode < KeyEvent.KEYCODE_DPAD_UP ||
                keyCode > KeyEvent.KEYCODE_DPAD_CENTER) {
            return false;
        }

        if (down) {
            if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
                buffer[0] = '\r';
                //mSerialService.write( buffer );
                send( buffer );
            } else {
                char code;
                switch (keyCode) {
                    case KeyEvent.KEYCODE_DPAD_UP:
                        code = 'A';
                        break;
                    case KeyEvent.KEYCODE_DPAD_DOWN:
                        code = 'B';
                        break;
                    case KeyEvent.KEYCODE_DPAD_LEFT:
                        code = 'D';
                        break;
                    default:
                    case KeyEvent.KEYCODE_DPAD_RIGHT:
                        code = 'C';
                        break;
                }
                buffer[0] = 27; // ESC
                //mSerialService.write( buffer );
                send( buffer );
                /*
                if (mEmulatorView.getKeypadApplicationMode()) {
                    buffer[0] = 'O';
                    //mSerialService.write( buffer );
                    send( buffer );
                } else {
                    buffer[0] = '[';
                    //mSerialService.write( buffer );
                    send( buffer );
                }
                */
                buffer[0] = (byte)code;
                //mSerialService.write( buffer );
                send( buffer );
            }
        }
        return true;
    }

    private boolean isSystemKey(int keyCode, KeyEvent event) {
        return event.isSystem();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        mMenuItemConnect = menu.getItem(0);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.connect:
                conectarse();
                return true;
        }
        return false;
    }

    private void doPreferences() {
        startActivity(new Intent(this, TermPreferences.class));
    }

    public void doOpenOptionsMenu() {
        openOptionsMenu();
    }

    private void doStartRecording() {
        File sdCard = Environment.getExternalStorageDirectory();

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String currentDateTimeString = format.format(new Date());
        String fileName = sdCard.getAbsolutePath() + "/blueTerm_" + currentDateTimeString + ".log";

        mMenuItemStartStopRecording.setTitle(R.string.menu_stop_logging);
        Toast.makeText(getApplicationContext(), getString(R.string.menu_logging_started) + "\n\n" + fileName, Toast.LENGTH_LONG).show();
    }

    private void doStopRecording() {
        mMenuItemStartStopRecording.setTitle(R.string.menu_start_logging);
        Toast.makeText(getApplicationContext(), getString(R.string.menu_logging_stopped), Toast.LENGTH_SHORT).show();
    }

    /*********************/
    /*********************/
    /*********************/
    /*********************/
    /*********************/

    public void conectarse(){
        if (getConnectionState() == BluetoothSerialService.STATE_NONE) {
            // Launch the DeviceListActivity to see devices and do scan
            Intent serverIntent = new Intent(this, DeviceListActivity.class);
            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
        }
        else
        if (getConnectionState() == BluetoothSerialService.STATE_CONNECTED) {
            mSerialService.stop();
            mSerialService.start();
        }
    }

    /*********************/

    public void refres(){
        refresTemp();
        refresHum();
        refresLuz();
        changeInt.setHint(mSerialService.Iini);
    }

    public void refresTemp(){
        Temperatura ultimaTemperatura = db.getLastTemp();
        if (ultimaTemperatura != null) {
            actualTemp.setText(ultimaTemperatura.valor);
            if (ultimaTemperatura.estado.equals("hi")){
                imageTemp.setImageResource(R.drawable.temalta);
            }
            else if (ultimaTemperatura.estado.equals("lo")){
                imageTemp.setImageResource(R.drawable.tembaja);
            }
            else{
                imageTemp.setImageResource(R.drawable.tembien);
            }
            changeTemp.setHint(mSerialService.Tini);

            actualHora.setText(R.string.Recibida+ultimaTemperatura.hora);
        }
    }

    public void refresHum(){
        Humedad ultimaHumedad = db.getLastHum();
        if (ultimaHumedad != null) {
            actualHum.setText(ultimaHumedad.valor);
            if (ultimaHumedad.estado.equals("hi")){
                imageHum.setImageResource(R.drawable.humalta);
            }
            else if (ultimaHumedad.estado.equals("lo")){
                imageHum.setImageResource(R.drawable.humbaja);
            }
            else{
                imageHum.setImageResource(R.drawable.humbien);
            }
            changeHum.setHint(mSerialService.Hini);
        }
    }

    public void refresLuz(){
        Luz ultimaLuz = db.getLastLuz();
        if (ultimaLuz != null) {
            actualLuz.setText(ultimaLuz.valor);
            if (ultimaLuz.estado.equals("hi")){
                imageLuz.setImageResource(R.drawable.luzalta);
            }
            else{
                imageLuz.setImageResource(R.drawable.luzbien);
            }
            changeLuz.setHint(mSerialService.Lini);
        }
    }

    /*********************/

    public void pedirDatos(){

        try{
            String comando = "AskQ*";
            byte[] buffer = comando.getBytes("UTF-8");
            send(buffer);
            Log.d("PROPIOS", "PEDIR DATOS");
        }catch(Exception e){
            Log.d("PROPIOS", "ERROR al pedir Task");
        }

        changeTemp.setText("");
        changeHum.setText("");
        changeLuz.setText("");
        changeInt.setText("");
    }


    /*********************/

    public void cambiarEstadoConectado(){
        botonConectar.setText(R.string.BttnDESCONECTAR);
        pedirDatos();
    }

    public void cambiarEstadoConectando(){
        botonConectar.setText(R.string.BttnCONECTANDO);
    }

    public void cambiarEstadoDesconectado(){
        botonConectar.setText(R.string.BttnCONECTAR);

        actualHora.setText(R.string.Recibida);

        actualTemp.setText("-");
        imageTemp.setImageResource(R.drawable.tem);
        changeTemp.setText("-");

        actualHum.setText("-");
        imageHum.setImageResource(R.drawable.hum);
        changeHum.setText("-");

        actualLuz.setText("-");
        imageLuz.setImageResource(R.drawable.luz);
        changeLuz.setText("-");

        changeInt.setText("-");


    }

    /*********************/

    public void actionTemp(View view){
        Intent goHistorialTemp = new Intent(this, HistoricoTemperatura.class);
        startActivity(goHistorialTemp);
    }

    public void actionHum(View view){
        Intent goHistorialHum = new Intent(this, HistoricoHumedad.class);
        startActivity(goHistorialHum);
    }

    public void actionLuz(View view){
        Intent goHistorialLuz = new Intent(this, HistoricoLuz.class);
        startActivity(goHistorialLuz);
    }

    public void actionConectar(View view){
        conectarse();
    }

    /*********************/

    public void modificarTemperaturaCentral(View view){
        try{
            float valor = Float.parseFloat(changeTemp.getText().toString());
            if (valor>=18.0f && valor<=26.0f) {
                String comando = "Tset*";
                byte[] buffer = comando.getBytes("UTF-8");
                send(buffer);

                byte[] buffer2 = changeTemp.getText().toString().getBytes("UTF-8");
                send(buffer2);

                send("*".getBytes("UTF-8"));

                changeTemp.setHint(changeTemp.getText().toString());
                Toast.makeText(getApplicationContext(), "Valor enviado", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(getApplicationContext(), "Debe de ser un valor entre 18 C y 26 C", Toast.LENGTH_SHORT).show();
            }
        }catch(Exception e){
            Log.d("PROPIOS", "ERROR al mandar el comando de temp");
        }
    }

    public void modificarHumedadCentral(View view){
        try{
            float valor = Float.parseFloat(changeHum.getText().toString());
            if (valor>=35.0f && valor<=65.0f) {
                String comando = "Hset*";
                byte[] buffer = comando.getBytes("UTF-8");
                send(buffer);

                byte[] buffer2 = changeHum.getText().toString().getBytes("UTF-8");
                send(buffer2);

                send("*".getBytes("UTF-8"));

                changeHum.setHint(changeHum.getText().toString());
                Toast.makeText(getApplicationContext(), "Valor enviado", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(getApplicationContext(), "Debe de ser un valor entre 35 % y 65 %", Toast.LENGTH_SHORT).show();
            }
        }catch(Exception e){
            Log.d("PROPIOS", "ERROR al mandar el comando de hum");
        }
    }

    public void modificarLuzMax(View view){
        try{
            float valor = Float.parseFloat(changeLuz.getText().toString());
            if (valor>0f && valor<=5000f) {
                String comando = "Lset*";
                byte[] buffer = comando.getBytes("UTF-8");
                send(buffer);

                byte[] buffer2 = changeLuz.getText().toString().getBytes("UTF-8");
                send(buffer2);

                send("*".getBytes("UTF-8"));

                changeLuz.setHint(changeLuz.getText().toString());
                Toast.makeText(getApplicationContext(), "Valor enviado", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(getApplicationContext(), "Debe de ser un valor entre 0 y 5000 lux", Toast.LENGTH_SHORT).show();
            }
        }catch(Exception e){
            Log.d("PROPIOS", "ERROR al mandar el comando de luz");
        }
    }

    public void modificarIntervalo(View view){
        try{
            float valor = Float.parseFloat(changeInt.getText().toString());
            if (valor>=30f && valor<=3600f) {
                String comando = "Iset*";
                byte[] buffer = comando.getBytes("UTF-8");
                send(buffer);

                byte[] buffer2 = changeInt.getText().toString().getBytes("UTF-8");
                send(buffer2);

                send("*".getBytes("UTF-8"));

                changeInt.setHint(changeInt.getText().toString());
                Toast.makeText(getApplicationContext(), "Valor enviado", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(getApplicationContext(), "Debe de ser un valor entre 30 y 3600 segundos", Toast.LENGTH_SHORT).show();
            }
        }catch(Exception e){
            Log.d("PROPIOS", "ERROR al mandar el comando de intervalo");
        }
    }
}
