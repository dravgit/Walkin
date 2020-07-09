package com.example.walkin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.centerm.centermposoversealib.thailand.AidlIdCardTha;
import com.centerm.centermposoversealib.thailand.AidlIdCardThaListener;
import com.centerm.centermposoversealib.thailand.ThiaIdInfoBeen;
import com.centerm.smartpos.aidl.iccard.AidlICCard;
import com.centerm.smartpos.aidl.magcard.AidlMagCard;
import com.centerm.smartpos.aidl.magcard.AidlMagCardListener;
import com.centerm.smartpos.aidl.magcard.TrackData;
import com.centerm.smartpos.aidl.sys.AidlDeviceManager;
import com.centerm.smartpos.constant.Constant;
import com.centerm.smartpos.util.HexUtil;
import com.example.walkin.models.LoginResponseModel;
import com.example.walkin.utils.NetworkUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CheckInActivity extends BaseActivity {

    private static final int CAMERA_PERM_CODE = 101;
    private static final int CAMERA_USER_CODE = 102;
    private static final int CAMERA_CAR_CODE = 103;
    private static final int CAMERA_CARD_CODE = 104;
    private AidlMagCard magCard = null;
    private TextView expdate;
    private TextView name;
    private TextView bdate;
    private TextView xid;
    private Handler mHandler = new Handler();
    private String TAG = getClass().getSimpleName();
    private AidlIdCardTha aidlIdCardTha;
    private AidlICCard aidlIcCard;
    private ImageView photoImg;
    private TextView resultText;
    public long time1;
    public static long timestart;
    private boolean aidlReady = false;
    ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
    private ProgressDialog mLoading;
    private MediaPlayer mediaPlayer;
    private EditText tVnameTH, tVidcard;
    private List<String> months_eng = Arrays.asList("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec");
    private List<String> months_th = Arrays.asList("ม.ค.", "ก.พ.", "มี.ค.", "เม.ษ.", "พ.ค.", "มิ.ย.", "ก.ค.", "ส.ค.", "ก.ย.", "ต.ค.", "พ.ย.", "ธ.ค.");
    private ImageView iVphoto;
    public static String pic;
    private ImageButton capUser, capCar, capCard;
    private Button testBtn, cancleCheckin, okCheckin;
    private Spinner dropdownDepartment, dropdownObjective;
    String[] department = new String[]{"","1", "2", "three"};
    String[] objective = new String[]{"","4", "5", "five"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in);
        NetworkUtil.Companion.login("", "", new NetworkUtil.Companion.NetworkLisener<LoginResponseModel>() {
            @Override
            public void onResponse(LoginResponseModel response) {

            }
        });
        mLoading = new ProgressDialog(this);
        mLoading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mLoading.setCanceledOnTouchOutside(false);
        mLoading.setMessage("Reading...");
        mediaPlayer = MediaPlayer.create(this, R.raw.beep_sound);
        tVnameTH = findViewById(R.id.tVnameTH);
        tVidcard = findViewById(R.id.tVidcard);
        iVphoto = findViewById(R.id.iVphoto);
        Log.i("C", "Create2");
        bindService();
        bindServiceSwipe();
        capUser = (ImageButton) findViewById(R.id.user);
        capCar = (ImageButton) findViewById(R.id.car);
        capCard = (ImageButton) findViewById(R.id.card);
        cancleCheckin = (Button) findViewById(R.id.cancleCheckin);
        okCheckin = (Button) findViewById(R.id.okCheckin);
        dropdownDepartment = (Spinner) findViewById(R.id.dropdownDepartment);
        dropdownObjective = (Spinner) findViewById(R.id.dropdownObjective);
        ArrayAdapter<String> adapterDepartment = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, department);
        ArrayAdapter<String> adapterObjective = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, objective);
        dropdownDepartment.setAdapter(adapterDepartment);
        dropdownObjective.setAdapter(adapterObjective);
        capUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraUser();
            }
        });
        capCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraCar();
            }
        });
        capCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraCard();
            }
        });
        cancleCheckin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CheckInActivity.this, HomeActivity.class);
                CheckInActivity.this.startActivity(intent);
            }
        });
        okCheckin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CheckInActivity.this, HomeActivity.class);
                CheckInActivity.this.startActivity(intent);
                // call api
            }
        });
    }

    private void cameraUser() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.CAMERA}, CAMERA_PERM_CODE);
        }else{
            openCamera(CAMERA_USER_CODE);
        }
    }

    private void cameraCar() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.CAMERA}, CAMERA_PERM_CODE);
        }else{
            openCamera(CAMERA_CAR_CODE);
        }
    }

    private void cameraCard() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.CAMERA}, CAMERA_PERM_CODE);
        }else{
            openCamera(CAMERA_CARD_CODE);
        }
    }

    private void openCamera(int typeCode) {
        Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(camera, typeCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == CAMERA_USER_CODE){
            if (resultCode == RESULT_OK && data !=null ) {
                Bitmap image = (Bitmap) data.getExtras().get("data");
                capUser.setImageBitmap(image);
            }
        }
        if(requestCode == CAMERA_CAR_CODE){
            if (resultCode == RESULT_OK && data !=null ) {
                Bitmap image = (Bitmap) data.getExtras().get("data");
                capCar.setImageBitmap(image);
            }
        }
        if(requestCode == CAMERA_CARD_CODE){
            if (resultCode == RESULT_OK && data !=null ) {
                Bitmap image = (Bitmap) data.getExtras().get("data");
                capCard.setImageBitmap(image);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == CAMERA_PERM_CODE){
            if(grantResults.length < 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

            }else{
                Toast.makeText(this, "Camera Permission is Required to Use camera", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void bindService() {
        super.bindService();
        Intent intent = new Intent();
        intent.setPackage("com.centerm.centermposoverseaservice");
        intent.setAction("com.centerm.CentermPosOverseaService.MANAGER_SERVICE");
        bindService(intent, conn, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void bindServiceSwipe() {
        Intent intent1 = new Intent();
        intent1.setPackage("com.centerm.smartposservice");
        intent1.setAction("com.centerm.smartpos.service.MANAGER_SERVICE");
        bindService(intent1, conn1, Context.BIND_AUTO_CREATE);
    }


    @Override
    public void onDeviceConnected(AidlDeviceManager deviceManager) {
        try {
            IBinder device = deviceManager.getDevice(Constant.DEVICE_TYPE.DEVICE_TYPE_ICCARD);
            if (device != null) {
                aidlIcCard = AidlICCard.Stub.asInterface(device);
                if (aidlIcCard != null) {
                    Log.e("MY", "IcCard bind success!");
                    //This is the IC card service object!!!!
                    //I am do nothing now and it is not null.
                    //you can do anything by yourselef later.
                    d();
                } else {
                    Log.e("MY", "IcCard bind fail!");
                }
            }
            device = deviceManager.getDevice(com.centerm.centermposoversealib.constant.Constant.OVERSEA_DEVICE_CODE.OVERSEA_DEVICE_TYPE_THAILAND_ID);
            if (device != null) {
                aidlIdCardTha = AidlIdCardTha.Stub.asInterface(device);
                aidlReady = aidlIdCardTha != null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDeviceConnectedSwipe(AidlDeviceManager deviceManager) {
        try {
            magCard = AidlMagCard.Stub.asInterface(deviceManager.getDevice(Constant.DEVICE_TYPE.DEVICE_TYPE_MAGCARD));
        } catch (RemoteException e) {
            e.printStackTrace();
        } finally {
            try {
                if (magCard != null)
                    dd();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }


    private boolean stTestContinue = false;
    private long invokStart = 0;
    private int invokCount = 0;
    private long totalTime = 0;

    private void startStabilityTest() throws RemoteException {
        invokStart = System.currentTimeMillis();
        aidlIdCardTha.searchIDCard(60000, test);
    }

    AidlIdCardThaListener test = new AidlIdCardThaListener.Stub() {
        @Override
        public void onFindIDCard(ThiaIdInfoBeen idInfoThaBean) throws RemoteException {
            totalTime += (System.currentTimeMillis() - invokStart);
            invokCount++;
            if (stTestContinue) {
                if (checkInfo(idInfoThaBean)) {
                    displayResult("Testing...");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                try {
                                    Thread.sleep(200);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                startStabilityTest();
                            } catch (RemoteException e) {
                                e.printStackTrace();
                                displayResult("Exception...");
                            }
                        }
                    });
                } else {
                    displayResult("Check Info ERROR:");
                }
            } else {
                displayResult("Cancel:");
            }
        }

        @Override
        public void onTimeout() throws RemoteException {
            displayResult("Timeout:");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    stTestContinue = false;
                    testBtn.setText("Start Stability Test");
                }
            });
        }

        @Override
        public void onError(int i, String s) throws RemoteException {
            displayResult("Error:" + i + " " + s);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    stTestContinue = false;
                    testBtn.setText("Start Stability Test");
                }
            });
        }
    };

    private boolean save = false;
    private String jsonStr;

    private boolean checkInfo(ThiaIdInfoBeen info) {
        if (save) {
            if (jsonStr.equals(info.toJSONString())) {
                return true;
            } else {
                return false;
            }
        } else {
            save = true;
            jsonStr = info.toJSONString();
            showMsg(jsonFormat(jsonStr));
        }
        return true;
    }

    private void showInfo(final String msg, final String second) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jObject = new JSONObject(msg);
                    Log.i(TAG, jObject.toString());
                    String thName = jObject.getString("ThaiName");
                    String regex = "(#)+";
                    String output = thName.replaceAll(regex, " ");
                    tVnameTH.setText(output);
                    String id_card = jObject.getString("CitizenId");
                    id_card = id_card.charAt(0) + "-" + id_card.charAt(1) + id_card.charAt(2) +
                            id_card.charAt(3) + id_card.charAt(4) + "-" + id_card.charAt(5) +
                            id_card.charAt(6) + id_card.charAt(7) + id_card.charAt(8) + id_card.charAt(9) +
                            "-" + id_card.charAt(10) + id_card.charAt(11) + "-" + id_card.charAt(12);
                    id_card = id_card.substring(0, 11) + "X-XX-X";
                    tVidcard.setText(id_card);
                    Toast.makeText(CheckInActivity.this,
                            "time running is " + second + "s", Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private String getDateFromJson(String date, String reg) {

        String _day = "" + Integer.parseInt(date.substring(0, 2));
        String _month_eng;
        String _month_th;
        String _year_th;
        String _year_eng;
        String _birth_eng;
        String _birth_th;
        if (reg.equals("en")) {
            _month_eng = months_eng.get(Integer.parseInt(date.substring(2, 4)) - 1);
            _year_eng = "" + (Integer.parseInt(date.substring(4, 8)) - 543);
            _year_eng = _year_eng.substring(0, 2) + "XX";
            _birth_eng = _day + " " + _month_eng + " " + _year_eng;
            return _birth_eng;
        } else if (reg.equals("th")) {
            _month_th = months_th.get(Integer.parseInt(date.substring(2, 4)) - 1);
            _year_th = date.substring(4, 8);
            _year_th = _year_th.substring(0, 2) + "XX";
            _birth_th = _day + " " + _month_th + " " + _year_th;
            return _birth_th;
        }

        return "";
    }

    private void showMsg(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                resultText.setText(msg);
            }
        });
    }

    private void showPhoto(final Bitmap bmp) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                iVphoto.setImageBitmap(bmp);
            }
        });
    }

    private void displayResult(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                resultText.setText(msg);
                if (invokCount == 0) {
                    return;
                }
                resultText.append("\nInvok " + invokCount + " times\n");
                resultText.append("Total Consume " + (totalTime / 1000f) + " s\n");
                resultText.append("Average Consume " + (totalTime / invokCount) + " ms\n");
            }
        });
    }

    private String jsonFormat(String s) {
        int level = 0;
        StringBuffer jsonForMatStr = new StringBuffer();
        for (int index = 0; index < s.length(); index++) {
            //获取s中的每个字符
            char c = s.charAt(index);
            //level大于0并且jsonForMatStr中的最后一个字符为\n,jsonForMatStr加入\t
            if (level > 0 && '\n' == jsonForMatStr.charAt(jsonForMatStr.length() - 1)) {
                jsonForMatStr.append(getLevelStr(level));
            }
            //遇到"{"和"["要增加空格和换行，遇到"}"和"]"要减少空格，以对应，遇到","要换行
            switch (c) {
                case '{':
                case '[':
                    jsonForMatStr.append(c + "\n");
                    level++;
                    break;
                case ',':
                    jsonForMatStr.append(c + "\n");
                    break;
                case '}':
                case ']':
                    jsonForMatStr.append("\n");
                    level--;
                    jsonForMatStr.append(getLevelStr(level));
                    jsonForMatStr.append(c);
                    break;
                default:
                    jsonForMatStr.append(c);
                    break;
            }
        }
        return jsonForMatStr.toString();
    }

    private static String getLevelStr(int level) {
        StringBuffer levelStr = new StringBuffer();
        for (int levelI = 0; levelI < level; levelI++) {
            levelStr.append("\t");
        }
        return levelStr.toString();
    }

    public void d() throws InterruptedException, ExecutionException {
        Runnable job = new Runnable() {

            boolean _read = false;

            @Override
            public void run() {
                try {
                    aidlIcCard.open();
                    if (aidlIcCard.status() == 1) {
                        if (!_read) {
                            _read = true;
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    try {
                                        if (!(CheckInActivity.this).isFinishing()) {
                                            try {
                                                mLoading.show();
                                            } catch (WindowManager.BadTokenException e) {
                                                Log.e("WindowManagerBad ", e.toString());
                                            }
                                        }
                                        iVphoto.setImageBitmap(null);
                                        time1 = System.currentTimeMillis();
                                        timestart = time1;
                                        aidlIdCardTha.searchIDCard(6000, new AidlIdCardThaListener.Stub() {
                                            @Override
                                            public void onFindIDCard(final ThiaIdInfoBeen been) throws RemoteException {

                                                Log.e("DATA",been.getLaserNumber());
                                                String s = been.toJSONString();
                                                Log.i(TAG, s);
                                                long end = System.currentTimeMillis();
                                                Bitmap rebmp = Bitmap.createScaledBitmap(been.getPhoto(), 85, 100, false);
                                                pic = convert(rebmp);
                                                Log.i(TAG, pic);
                                                showPhoto(been.getPhoto());
                                                int b = (int) ((end - time1)/1000);
                                                int c = (int) (((end - time1)/100)%10);
                                                showInfo(jsonFormat(s), (b + "." + c));
                                                mediaPlayer.start();
                                                mLoading.dismiss();
                                            }

                                            @Override
                                            public void onTimeout() throws RemoteException {
                                                log("TIME OUT");
                                                mLoading.dismiss();
                                            }

                                            @Override
                                            public void onError(int i, String s) throws RemoteException {
                                                log("ERROR CODE:" + i + " msg:" + s);
                                                mLoading.dismiss();
                                            }
                                        });
                                    } catch (RemoteException e) {
                                        e.printStackTrace();
                                    }

                                }
                            });
                        }

                    } else {
                        _read = false;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mLoading.dismiss();
                            }
                        });
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };
        scheduledExecutor.scheduleAtFixedRate(job, 1000, 1000, TimeUnit.MILLISECONDS);
    }

    private void c() {
        tVidcard.setText("");
        tVnameTH.setText("");
        tVidcard.setText("");
        iVphoto.setImageBitmap(null);
        iVphoto.destroyDrawingCache();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        scheduledExecutor.shutdownNow();
        if (aidlIcCard != null) {
            try {
                aidlIcCard.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (magCard != null) {
            scheduledExecutor.shutdown();
            magCard = null;
        }
    }

    public void dd() throws InterruptedException, ExecutionException {
        Runnable job = new Runnable() {
            @Override
            public void run() {
                try {
                    magCard.open();
                    x();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            private void c() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tVnameTH.setText("");
                    }
                });
            }

            private String hextostring(String _hex) {
                try {
                    byte[] bytes = HexUtil.hexStringToByte(_hex);
                    return new String(bytes, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            public void x() {
                try {
                    magCard.swipeCard(30000, new AidlMagCardListener.Stub() {
                        @Override
                        public void onSwipeCardTimeout() throws RemoteException {
                        }

                        @Override
                        public void onSwipeCardSuccess(TrackData arg0)
                                throws RemoteException {
                            String _name = hextostring(arg0.getFirstTrackData());
                            _name = _name.replace(" ", "");
                            _name = _name.replace("^", "");
                            final String[] _xname = _name.split("\\$");
                            final String[] _second = hextostring(arg0.getSecondTrackData()).substring(6, hextostring(arg0.getSecondTrackData()).length()).split("=");
                            //final String _encry = hextostring(arg0.getEncryptTrackData());
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tVnameTH.setText(_xname[2] + " " + _xname[1] + " " + _xname[0]);
                                }
                            });
                        }

                        @Override
                        public void onSwipeCardFail() throws RemoteException {
                        }

                        @Override
                        public void onSwipeCardException(int arg0)
                                throws RemoteException {
                        }

                        @Override
                        public void onCancelSwipeCard() throws RemoteException {
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        };

        scheduledExecutor.scheduleAtFixedRate(job, 1000, 1000, TimeUnit.MILLISECONDS);
    }

    public static String convert(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);

        return Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP);
    }
}
