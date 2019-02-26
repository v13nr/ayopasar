package nanang.application.lsp.iad;

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import nanang.application.lsp.customfonts.MyTextView;
import nanang.application.lsp.fragment.EditAsetFragment;
import nanang.application.lsp.fragment.EditDesaFragment;
import nanang.application.lsp.fragment.HomeFragment;
import nanang.application.lsp.fragment.IsianAsetFragment;
import nanang.application.lsp.fragment.LoginFragment;
import nanang.application.lsp.fragment.LspFragment;
import nanang.application.lsp.fragment.ManageFragment;
import nanang.application.lsp.fragment.ManageLokalFragment;
import nanang.application.lsp.fragment.PengaturanFragment;
import nanang.application.lsp.fragment.PinjamanFragment;
import nanang.application.lsp.fragment.RegistrasiFragment;
import nanang.application.lsp.fragment.TentangFragment;
import nanang.application.lsp.libs.CommonUtilities;
import nanang.application.lsp.libs.ConnectionDetector;
import nanang.application.lsp.libs.DatabaseHandler;
import nanang.application.lsp.libs.GalleryFilePath;
import nanang.application.lsp.libs.ServerUtilities;
import nanang.application.lsp.model.aset;
import nanang.application.lsp.model.user;

public class MainActivity extends AppCompatActivity {

    final int REQUEST_FROM_GALLERY = 1;
    final int REQUEST_FROM_CAMERA = 2;
    final int REQUEST_FROM_LOGIN = 3;

    Context context;
    ConnectionDetector cd;
    DatabaseHandler db;
    user data;

    String cicilan;

    Typeface fonts1, fonts2;

    Dialog dialog_logout;
    MyTextView btn_no, btn_yes;

    Dialog dialog_informasi;
    Dialog dialog_informasi2;
    MyTextView btn_ok;
    MyTextView text_title;
    MyTextView text_message;

    Dialog dialog_pilih_gambar;
    MyTextView from_camera, from_galery;

    String mImageCapturePath;
    String id_selected_aset;
    public static String action_add;

    Dialog dialog_delete_aset;
    MyTextView btn_delete_aset_no, btn_delete_aset_yes;

    public static int menu_selected = 0;
    static String upload_gambar, upload_info, upload_more;
    static int upload_status, upload_size;

    static InputStream inStream;

    private void checkGcmRegid() {
        String registrationId = getString(R.string.msg_token_fmt, FirebaseInstanceId.getInstance().getToken());
        registrationId = registrationId.equalsIgnoreCase("null") ? "" : registrationId;
        Log.d("Registration id", registrationId);
        //Toast.makeText(context, registrationId, Toast.LENGTH_SHORT).show(); 
        if (registrationId.length() > 0) {
            new prosesUpdateRegisterRegId(registrationId).execute();
        }
    }

    class prosesUpdateRegisterRegId extends AsyncTask<String, Void, JSONObject> {
        String registrationId;
        boolean success;
        String message;

        prosesUpdateRegisterRegId(String registrationId) {
            this.registrationId = registrationId;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(String... urls) {
            return ServerUtilities.register(context, registrationId, data.getId(), CommonUtilities.getGuestId(context));
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            success = false;
            message = "Gagal melakukan proses take action. Cobalah lagi.";
            if (result != null) {
                try {
                    success = result.isNull("success") ? false : result.getBoolean("success");
                    message = result.isNull("message") ? message : result.getString("message");
                } catch (JSONException e) {
                    // TODO Auto-generated catch block 
                    e.printStackTrace();
                }

                if (!success) {
                    new prosesUpdateRegisterRegId(registrationId).execute();
                }
            }
        }
    }

        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = MainActivity.this;
        cd = new ConnectionDetector(context);
        db = new DatabaseHandler(context);
        db.createTable();

        setContentView(R.layout.activity_main);
        data = CommonUtilities.getLoginUser(context);

        fonts1 = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Regular.ttf");
        fonts2 = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Semibold.ttf");

        dialog_informasi = new Dialog(context);
        dialog_informasi.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_informasi.setCancelable(true);
        dialog_informasi.setContentView(R.layout.informasi_dialog);

        btn_ok = (MyTextView) dialog_informasi.findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                dialog_informasi.dismiss();
            }
        });
        btn_ok.setTypeface(fonts2);

        text_title = (MyTextView) dialog_informasi.findViewById(R.id.text_title);
        text_message = (MyTextView) dialog_informasi.findViewById(R.id.text_dialog);
        text_message.setTypeface(fonts1);

        dialog_pilih_gambar = new Dialog(context);
        dialog_pilih_gambar.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_pilih_gambar.setCancelable(true);
        dialog_pilih_gambar.setContentView(R.layout.pilih_gambar_dialog);

        from_galery = (MyTextView) dialog_pilih_gambar.findViewById(R.id.txtFromGalley);
        from_galery.setTypeface(fonts1);
        from_galery.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                dialog_pilih_gambar.dismiss();
                fromGallery();
            }
        });

        from_camera = (MyTextView) dialog_pilih_gambar.findViewById(R.id.txtFromCamera);
        from_camera.setTypeface(fonts1);
        from_camera.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                dialog_pilih_gambar.dismiss();
                fromCamera();
            }
        });

        dialog_logout = new Dialog(context);
        dialog_logout.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_logout.setCancelable(true);
        dialog_logout.setContentView(R.layout.signout_dialog);

        btn_yes = (MyTextView) dialog_logout.findViewById(R.id.btn_yes);
        btn_yes.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                dialog_logout.dismiss();

                CommonUtilities.setLoginUser(context, new user(0, "", "", "", "", "", "", "", "", "", "", "", ""));
                data = CommonUtilities.getLoginUser(context);



                //v13nr catat sharedpref email this.email
                // 0 - for private mode`
                SharedPreferences pref = getApplicationContext().getSharedPreferences("AyoPasarPref", 0);

                SharedPreferences.Editor editor = pref.edit();

                editor.putString("sharedPref_email",  ""); // Storing string
                editor.apply();

                //to get
                //pref.getString("sharedPref_email", null); // getting String

                //pref.getInt("sharedPref_email", 0); // getting Integer



                displayView(0);

                boolean currentlyTracking = AsetService.is_running;
                if(currentlyTracking) {
                    stopTracking();
                }
            }
        });
        btn_yes.setTypeface(fonts2);

        btn_no = (MyTextView) dialog_logout.findViewById(R.id.btn_no);
        btn_no.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                dialog_logout.dismiss();

            }
        });
        btn_no.setTypeface(fonts2);


        dialog_delete_aset = new Dialog(context);
        dialog_delete_aset.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_delete_aset.setCancelable(true);
        dialog_delete_aset.setContentView(R.layout.delete_aset_dialog);

        btn_delete_aset_yes = (MyTextView) dialog_delete_aset.findViewById(R.id.btn_yes);
        btn_delete_aset_yes.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                dialog_delete_aset.dismiss();
                if(action_add.equalsIgnoreCase("server")) {
                    new prosesDeleteAset().execute();
                } else {
                    db.deleteAsetlist(id_selected_aset);
                    Toast.makeText(context, "Hapus data lokal berhasil.", Toast.LENGTH_LONG).show();
                    loadDataAsetLokal();
                }
            }
        });
        btn_delete_aset_yes.setTypeface(fonts2);

        btn_delete_aset_no = (MyTextView) dialog_delete_aset.findViewById(R.id.btn_no);
        btn_delete_aset_no.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                dialog_delete_aset.dismiss();

            }
        });
        btn_delete_aset_no.setTypeface(fonts2);
        
        if(data.getEmail().equals("")) {
            displayView(11);
        } else {
            displayView(3);
        }

        boolean currentlyTracking = AsetService.is_running;
        if (data.getId()>0 && !currentlyTracking) {
            startTracking();
        }

        if(data.getId()==0 && currentlyTracking) {
            stopTracking();
        }

        if(savedInstanceState==null) {
            checkGcmRegid();
        }
    }

    public void selectImage() {
        dialog_pilih_gambar.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog_pilih_gambar.show();
    }

    /*private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
        Crop.of(source, destination).asSquare().start(this);
    }*/

    private void fromGallery() {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_FROM_GALLERY);
    }

    private void fromCamera() {

        Intent intent = new Intent(context, AmbilFotoActivity.class);
        startActivityForResult(intent, REQUEST_FROM_CAMERA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_FROM_CAMERA:
                    if(action_add.equalsIgnoreCase("server")) {
                        prosesUpload(data.getStringExtra("path"));
                    } else {
                        mImageCapturePath = data.getStringExtra("path");
                        displayView(4);
                    }


                    break;
                case REQUEST_FROM_GALLERY:
                    Uri selectedUri = data.getData();
                    if(action_add.equalsIgnoreCase("server")) {
                        prosesUpload(GalleryFilePath.getPath(context, selectedUri));
                    } else {
                        mImageCapturePath = GalleryFilePath.getPath(context, selectedUri);
                        displayView(4);
                    }

                    break;
            }
        }
    }

    public void loadDataDesa() {
        EditDesaFragment.webView.loadUrl("javascript:setDataDesa('" + data.getEmail() + "', '" + data.getDesa() + "', '" + data.getLokal() + "', '" + data.getKades() + "', '" + data.getSekdes() + "', '" + data.getPengurus() + "', '" + data.getAlamatdesa() + "');");
    }

    public void addLokalAset() {
        action_add = "lokal";
        selectImage();
    }

    public void editLokalAset(String id) {
        action_add = "lokal";
        id_selected_aset = id;
        displayView(6);
    }

    public void deleteLokalAset(String id) {
        action_add = "lokal";
        id_selected_aset = id;
        dialog_delete_aset.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog_delete_aset.show();
    }


    public void addAset() {
        if(cd.isConnectingToInternet()) {
            action_add = "server";
            selectImage();
        } else {
            text_message.setText("Tidak ada koneksi internet.");
            text_title.setText("KESALAHAN");
            dialog_informasi.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog_informasi.show();
        }
    }

    public void editAset(String id) {
        action_add = "server";
        id_selected_aset = id;
        displayView(6);
    }

    public void deleteAset(String id) {
        action_add = "server";
        id_selected_aset = id;
        dialog_delete_aset.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog_delete_aset.show();
    }

    public void openDialogLogout() {
        dialog_logout.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog_logout.show();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

            if(menu_selected>3) {
                displayView(3);
            } else {
                setResult(RESULT_OK, new Intent());
                finish();
            }

            return false;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the form; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.form.menu_detail, form);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(RESULT_OK, new Intent());
                finish();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStop() {
        try {
            unregisterReceiver(mHandleloadGcmResponseReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }

        super.onStop();
    }

    @Override
    protected void onDestroy() {
        try {
            unregisterReceiver(mHandleloadGcmResponseReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }

        super.onDestroy();
    }

    @Override
    protected void onResume() {

        registerReceiver(mHandleloadGcmResponseReceiver,  new IntentFilter("com.application.siapp.GCM_RESPONSE"));

        super.onResume();
    }

    private final BroadcastReceiver mHandleloadGcmResponseReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");

            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        }
    };

    public void displayView(int position) {
        menu_selected = position;

        SharedPreferences pref = getApplicationContext().getSharedPreferences("AyoPasarPref", 0);

        Fragment fragment = null;
        switch (position) {
            case 0:

                //fragment = new LoginFragment();
                fragment = new LspFragment();
                break;

            case 1:

                fragment = new RegistrasiFragment();
                break;

            case 3:


                //v13nr catat sharedpref email this.email
                // 0 - for private mode`

                SharedPreferences.Editor editor = pref.edit();

                //editor.putString("sharedPref_email",  ""); // Storing string
                //editor.apply();

                //to get
                String statusemail = pref.getString("sharedPref_email", null); // getting String

                if(statusemail.equals("")){

                    //Log.w("tes", "tes"+statusAktivasi+"tes");
                    text_message.setText("Anda Sudah Logout ");

                    text_title.setText("Trying Home..");
                    dialog_informasi.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog_informasi.show();
                    fragment = new LoginFragment();
                    break;
                }



                //pref.getInt("sharedPref_email", 0); // getting Integer


                fragment = new HomeFragment();
                break;

            case 4:

                fragment = new IsianAsetFragment();
                break;

            case 5:

                fragment = new ManageFragment();
                break;

            case 6:

                fragment = new EditAsetFragment();
                break;

            case 7:

                fragment = new PengaturanFragment();
                break;

            case 8:

                fragment = new TentangFragment();
                break;

            case 9:

                fragment = new ManageLokalFragment();
                break;

            case 10:

                fragment = new EditDesaFragment();
                break;

            case 11:

                fragment = new LoginFragment();
                break;

            case 12:


                String statusAktivasi = pref.getString("sharedPref_aktivasi", "Tidak Valid"); // getting String

                //pref.getInt("sharedPref_email", 0); // getting Integer
                //Log.w("tes", statusAktivasi);
                if(statusAktivasi.equals("Tidak Valid")){

                    //Log.w("tes", "tes"+statusAktivasi+"tes");
                    text_message.setText("Status Aktivasi Asparindo Anda : " + statusAktivasi);

                    text_title.setText("Status Aktivasi");
                    dialog_informasi.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog_informasi.show();
                    break;
                }

                text_message.setText("Status Aktivasi Asparindo Anda : "+statusAktivasi);

                text_title.setText("Status Aktivasi");
                dialog_informasi.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog_informasi.show();


                if(statusAktivasi == "Tidak Valid"){
                    fragment = new HomeFragment();
                    break;
                } else {

                    fragment = new PinjamanFragment();
                    break;
                }

            default:

                break;
        }


        if (fragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
        }
    }

    public void prosesGantiPassword(String pass0, String pass1, String pass2) {
        if(!pass1.equalsIgnoreCase(pass2)) {
            text_message.setText("Silakan ulangi password baru.");
            text_title.setText("KESALAHAN");
            dialog_informasi.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog_informasi.show();
        } else {
            new prosesGantiPassword().execute(pass0, pass1, pass2);
        }
    }

    public  void gagalRegistrasi(String message) {
        text_message.setText(message);
        text_title.setText("GAGAL");
        dialog_informasi.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog_informasi.show();
    }

    class prosesGantiPassword extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            PengaturanFragment.load_masking.setVisibility(View.VISIBLE);
        }

        @Override
        protected JSONObject doInBackground(String... urls) {

            //catat posisi terakhir aktivasi
            // 0 - for private mode`
            SharedPreferences pref = getApplicationContext().getSharedPreferences("AyoPasarPref", 0);

            SharedPreferences.Editor editor = pref.edit();

            //editor.putString("sharedPref_aktivasi",  status_aktivasi); // Storing string
            //editor.apply();

            //to get
            String emailx = pref.getString("sharedPref_email", null); // getting String

            //pref.getInt("sharedPref_email", 0); // getting Integer


            try {
                Log.i("nng", emailx);
                Log.i("nng", urls[0]);
                String url = CommonUtilities.SERVER_HOME_URL + "/server2/services/publik/ganti_password.php";
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(url);

                MultipartEntity reqEntity = new MultipartEntity();
                reqEntity.addPart("pass0", new StringBody(urls[0]));
                reqEntity.addPart("pass1", new StringBody(urls[1]));
                reqEntity.addPart("pass2", new StringBody(urls[2]));
                reqEntity.addPart("action", new StringBody("setpass"));
                reqEntity.addPart("user", new StringBody(emailx));

                httppost.setEntity(reqEntity);
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity resEntity = response.getEntity();
                InputStream is = resEntity.getContent();

                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;


                while ((line = reader.readLine()) != null) {
                    Log.i("nng",line + "\n");
                    sb.append(line + "\n");
                }
                is.close();
                String json = sb.toString();
                System.out.println(json);

                JSONObject jobj = new JSONObject("{\"result\": " + json + "}");
                JSONArray jarry = jobj.isNull("result") ? null : jobj.getJSONArray("result");
                if (jarry != null) {
                    return jarry.getJSONObject(0);
                }

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            PengaturanFragment.load_masking.setVisibility(View.GONE);

            String error = "Proses ganti password gagal.";
            String pesan = "Inisialisasi";
            if(result!=null) {
                try {
                    error = result.isNull("error") ? "" : result.getString("error");
                     pesan = result.isNull("pesan") ? "" : result.getString("pesan");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if(error.length()>0) {
                text_message.setText(error);
                text_title.setText("LOGIN GAGAL");
                dialog_informasi.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog_informasi.show();
            } else {
                Toast.makeText(context, pesan, Toast.LENGTH_SHORT).show();
                displayView(3);
            }

        }
    }

    public void prosesLogin(String user_id, String password) {
        new prosesLogin().execute(user_id, password);
    }
    
    class prosesLogin extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            LoginFragment.load_masking.setVisibility(View.VISIBLE);
        }

        @Override
        protected JSONObject doInBackground(String... urls) {


            try {
                String url = CommonUtilities.SERVER_HOME_URL + "/server2/services/publik/login.php";
                Log.i("url", url);
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(url);

                MultipartEntity reqEntity = new MultipartEntity();
                reqEntity.addPart("user", new StringBody(urls[0]));
                reqEntity.addPart("pass", new StringBody(urls[1]));
                reqEntity.addPart("action", new StringBody("getAuth"));

                httppost.setEntity(reqEntity);
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity resEntity = response.getEntity();
                InputStream is = resEntity.getContent();

                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;


                while ((line = reader.readLine()) != null) {
                    Log.i("tes0", line + "\n");
                    sb.append(line + "\n");
                }
                is.close();
                String json = sb.toString();
                System.out.println(json);

                JSONObject jobj = new JSONObject("{\"result\": " + json + "}");
                JSONArray jarry = jobj.isNull("result") ? null : jobj.getJSONArray("result");
                if (jarry != null) {
                    return jarry.getJSONObject(0);
                }

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            LoginFragment.load_masking.setVisibility(View.GONE);

            String error = "Proses login gagal.";
            JSONObject user_login = null;

            if(result!=null) {
                try {
                    error = result.isNull("error")?error:result.getString("error");
                    user_login = result.isNull("0")?null:result.getJSONObject("0");

                    if(user_login!=null) {
                        //v13nr catat sharedpref email this.email
                        // 0 - for private mode`
                        SharedPreferences pref = getApplicationContext().getSharedPreferences("AyoPasarPref", 0);

                        SharedPreferences.Editor editor = pref.edit();

                        editor.putString("sharedPref_email",  user_login.getString("user")); // Storing string
                        editor.apply();

                        //to get
                        //pref.getString("sharedPref_email", null); // getting String

                        //pref.getInt("sharedPref_email", 0); // getting Integer

                        int id = user_login.isNull("id")?0:user_login.getInt("id");
                        String nama = user_login.isNull("nama")?"":user_login.getString("nama");
                        String email = user_login.isNull("user")?"":user_login.getString("user");
                        String kecamatan = user_login.isNull("kecamatan")?"":user_login.getString("kecamatan");
                        String kabupaten = user_login.isNull("kabupaten")?"":user_login.getString("kabupaten");
                        String propinsi = user_login.isNull("provinsi")?"":user_login.getString("provinsi");
                        String desa = user_login.isNull("desa")?"":user_login.getString("desa");
                        String lokal = user_login.isNull("lokal")?"":user_login.getString("lokal");
                        String kades = user_login.isNull("kades")?"":user_login.getString("kades");
                        String sekdes = user_login.isNull("sekdes")?"":user_login.getString("sekdes");
                        String pengurus = user_login.isNull("pengurus")?"":user_login.getString("pengurus");
                        String alamatdesa = user_login.isNull("alamatdesa")?"":user_login.getString("alamatdesa");
                        String photo = user_login.isNull("pass")?"":user_login.getString("pass");

                        data = new user(id, nama, email, kecamatan, kabupaten, propinsi, desa, lokal, kades, sekdes, pengurus, alamatdesa, photo);
                        CommonUtilities.setLoginUser(context, data);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if(user_login==null) {
                text_message.setText(error);
                text_title.setText("LOGIN GAGAL");
                dialog_informasi.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog_informasi.show();
            } else {
                Toast.makeText(context, "Proses Login Berhasil!", Toast.LENGTH_SHORT).show();
                displayView(3);

                boolean currentlyTracking = AsetService.is_running;
                if (!currentlyTracking) {
                    startTracking();
                }
            }

        }
    }


    /*class prosesLogout extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(menu_selected==3) HomeFragment.load_masking.setVisibility(View.VISIBLE);
            if(menu_selected==4) IsianAsetFragment.load_masking.setVisibility(View.VISIBLE);
            if(menu_selected==5) ManageFragment.load_masking.setVisibility(View.VISIBLE);
            if(menu_selected==6) EditAsetFragment.load_masking.setVisibility(View.VISIBLE);
            if(menu_selected==7) PengaturanFragment.load_masking.setVisibility(View.VISIBLE);
            if(menu_selected==8) TentangFragment.load_masking.setVisibility(View.VISIBLE);
            if(menu_selected==9) ManageLokalFragment.load_masking.setVisibility(View.VISIBLE);
        }

        @Override
        protected JSONObject doInBackground(String... urls) {


            try {
                String url = CommonUtilities.SERVER_HOME_URL + "/server/services/publik/login_desa.php";
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(url);

                MultipartEntity reqEntity = new MultipartEntity();
                reqEntity.addPart("user", new StringBody(urls[0]));
                reqEntity.addPart("pass", new StringBody(urls[1]));
                reqEntity.addPart("action", new StringBody("getAuth"));

                httppost.setEntity(reqEntity);
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity resEntity = response.getEntity();
                InputStream is = resEntity.getContent();

                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;


                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                is.close();
                String json = sb.toString();
                System.out.println(json);

                JSONObject jobj = new JSONObject("{\"result\": " + json + "}");
                JSONArray jarry = jobj.isNull("result") ? null : jobj.getJSONArray("result");
                if (jarry != null) {
                    return jarry.getJSONObject(0);
                }

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            if(menu_selected==3) HomeFragment.load_masking.setVisibility(View.GONE);
            if(menu_selected==4) IsianAsetFragment.load_masking.setVisibility(View.GONE);
            if(menu_selected==5) ManageFragment.load_masking.setVisibility(View.GONE);
            if(menu_selected==6) EditAsetFragment.load_masking.setVisibility(View.GONE);
            if(menu_selected==7) PengaturanFragment.load_masking.setVisibility(View.GONE);
            if(menu_selected==8) TentangFragment.load_masking.setVisibility(View.GONE);
            if(menu_selected==9) ManageLokalFragment.load_masking.setVisibility(View.GONE);

            String error = "Proses logout gagal.";
            JSONObject user_login = null;

            try {
                error = result.isNull("error")?error:result.getString("error");
                user_login = result.isNull("0")?null:result.getJSONObject("0");

                if(user_login!=null) {
                    int id = user_login.isNull("id")?0:user_login.getInt("id");
                    String nama = user_login.isNull("jeneng")?"":user_login.getString("jeneng");
                    String email = user_login.isNull("user")?"":user_login.getString("user");
                    String photo = user_login.isNull("pass")?"":user_login.getString("pass");

                    data = new user(id, nama, email, photo);
                    CommonUtilities.setLoginUser(context, data);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if(user_login==null) {
                text_message.setText(error);
                text_title.setText("LOGIN GAGAL");
                dialog_informasi.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog_informasi.show();
            } else {
                Toast.makeText(context, "Proses Login Berhasil!", Toast.LENGTH_SHORT).show();
                displayView(3);
            }

        }
    }*/
    
    public void editDataAset() {
        if(action_add.equalsIgnoreCase("server")) {
            new editDataAset().execute();
        } else {
            aset data_aset = db.getDataAset(id_selected_aset);
            mImageCapturePath = data_aset.getGambar();
            EditAsetFragment.webView.loadUrl("javascript:setDataAset('" + data_aset.getJenisbarang() + "', '" + data_aset.getKodebarang() + "', '" + data_aset.getIdentitasbarang() + "', '" + data_aset.getJumlah_barang() + "', '" + data_aset.getApbdesa() + "', '" + data_aset.getLain() + "', '" + data_aset.getKekayaan() + "', '" + data_aset.getTanggal_aset() + "', '" + data_aset.getKeterangan() + "', '" + data_aset.getGambar() + "');");
        }
    }

    class editDataAset extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            EditAsetFragment.load_masking.setVisibility(View.VISIBLE);
        }

        @Override
        protected JSONObject doInBackground(String... urls) {

            try {
                String url = CommonUtilities.SERVER_HOME_URL + "/server/services/publik/load_text_sekolah_edit.php?id="+id_selected_aset;
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(url);

                MultipartEntity reqEntity = new MultipartEntity();
                reqEntity.addPart("action", new StringBody("getDetail"));

                httppost.setEntity(reqEntity);
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity resEntity = response.getEntity();
                InputStream is = resEntity.getContent();

                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;

                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                is.close();
                String json = sb.toString();
                System.out.println(json);

                return new JSONObject(json);

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            String err_message = "Gagal edit data.";

            if(result!=null) {
                try {
                    err_message = "Data aset tidak ditemukan.";

                    JSONArray topics = result.isNull("topics")?null:result.getJSONArray("topics");
                    if(topics!=null) {
                        for(int i=0; i<topics.length(); i++) {
                            JSONObject rec = topics.getJSONObject(i);
                            int id = rec.isNull("id")?0:rec.getInt("id");
                            String jenisbarang = rec.isNull("jenisbarang") ? "" : rec.getString("jenisbarang");
                            String kodebarang = rec.isNull("kodebarang") ? "" : rec.getString("kodebarang");
                            String identitasbarang = rec.isNull("identitasbarang") ? "" : rec.getString("identitasbarang");
                            String jumlah_barang = rec.isNull("jumlah_barang") ? "" : rec.getString("jumlah_barang");
                            String apbdesa = rec.isNull("apbdesa") ? "" : rec.getString("apbdesa");
                            String lain = rec.isNull("lain") ? "" : rec.getString("lain");
                            String kekayaan = rec.isNull("kekayaan") ? "" : rec.getString("kekayaan");
                            String tanggal_asset = rec.isNull("tanggal_asset") ? "" : rec.getString("tanggal_asset");
                            String keterangan = rec.isNull("keterangan") ? "" : rec.getString("keterangan");
                            String gambar =  rec.isNull("logox") ? "" : rec.getString("logox");
                            if(id>0) {
                                err_message = "";
                                EditAsetFragment.webView.loadUrl("javascript:setDataAset('" + jenisbarang + "', '" + kodebarang + "', '" + identitasbarang + "', '" + jumlah_barang + "', '" + apbdesa + "', '" + lain + "', '" + kekayaan + "', '" + tanggal_asset + "', '" + keterangan + "', '"+CommonUtilities.SERVER_HOME_URL+"/smc/file/"+gambar+"');");
                            }
                            break;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            EditAsetFragment.load_masking.setVisibility(View.GONE);
            if(err_message.length()>0) {
                displayView(5);
                text_message.setText(err_message);
                text_title.setText("KESALAHAN");
                dialog_informasi.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog_informasi.show();
            }
        }
    }

    class prosesDeleteAset extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ManageFragment.load_masking.setVisibility(View.VISIBLE);
        }

        @Override
        protected JSONObject doInBackground(String... urls) {

            try {
                String url = CommonUtilities.SERVER_HOME_URL + "/server/services/publik/deletez.php";
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(url);

                MultipartEntity reqEntity = new MultipartEntity();
                reqEntity.addPart("id", new StringBody(id_selected_aset));
                reqEntity.addPart("action", new StringBody("'delete'"));

                httppost.setEntity(reqEntity);
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity resEntity = response.getEntity();
                InputStream is = resEntity.getContent();

                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;


                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                is.close();
                String json = sb.toString();
                System.out.println(json);

                JSONObject jobj = new JSONObject(json);

                return jobj;

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(JSONObject result) {

            boolean success = false;
            String message = "Proses hapus data gagal.";

            try {
                success = result.isNull("success")?false:result.getBoolean("success");
                message = result.isNull("message")?"":result.getString("message");

            } catch (JSONException e) {
                e.printStackTrace();
            }

            ManageFragment.load_masking.setVisibility(View.GONE);
            if(success) {
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                loadDataAset();
            } else {
                text_message.setText(message);
                text_title.setText(success?"BERHASIL":"GAGAL");
                dialog_informasi.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog_informasi.show();
            }
        }
    }


    public void prosesSaveAset(String jenisbarang, String kodebarang, String identitasbarang, String jumlah_barang, String apbdesa, String lain, String kekayaan, String tanggal_asset, String keterangan) {
        if(action_add.equalsIgnoreCase("server")) {
            new prosesSaveAset(jenisbarang, kodebarang, identitasbarang, jumlah_barang, apbdesa, lain, kekayaan, tanggal_asset, keterangan).execute();
        } else {
            aset data = new aset(0, jenisbarang, kodebarang, identitasbarang, jumlah_barang, apbdesa, lain, kekayaan, tanggal_asset, keterangan, mImageCapturePath);
            db.insertDataAset(data);
            Toast.makeText(context, "Simpan data lokal berhasil.", Toast.LENGTH_LONG).show();
            displayView(9);
        }

    }

    public class prosesSaveAset extends AsyncTask<String, Void, JSONObject> {

        String jenisbarang;
        String kodebarang;
        String identitasbarang;
        String jumlah_barang;
        String apbdesa;
        String lain;
        String kekayaan;
        String tanggal_asset;
        String keterangan;

        prosesSaveAset(String jenisbarang, String kodebarang, String identitasbarang, String jumlah_barang, String apbdesa, String lain, String kekayaan, String tanggal_asset, String keterangan) {
            this.jenisbarang = jenisbarang;
            this.kodebarang = kodebarang;
            this.identitasbarang = identitasbarang;
            this.jumlah_barang = jumlah_barang;
            this.apbdesa = apbdesa;
            this.lain = lain;
            this.kekayaan = kekayaan;
            this.tanggal_asset = tanggal_asset;
            this.keterangan = keterangan;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            IsianAsetFragment.load_masking.setVisibility(View.VISIBLE);
        }

        @Override
        protected JSONObject doInBackground(String... urls) {

            try {
                String url = CommonUtilities.SERVER_HOME_URL + "/server2/smc/android_add_barang.php";
                //Log.i("tes0", url);
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(url);

                MultipartEntity reqEntity = new MultipartEntity();
                reqEntity.addPart("user_id", new StringBody(data.getId()+""));
                reqEntity.addPart("gambar", new StringBody(upload_gambar));
                reqEntity.addPart("jenisbarang", new StringBody(jenisbarang));
                reqEntity.addPart("kodebarang", new StringBody(kodebarang));
                reqEntity.addPart("identitasbarang", new StringBody(identitasbarang));
                reqEntity.addPart("jumlah_barang", new StringBody(jumlah_barang));
                reqEntity.addPart("apbdesa", new StringBody(apbdesa));
                reqEntity.addPart("lain", new StringBody(lain));
                reqEntity.addPart("kekayaan", new StringBody(kekayaan));
                reqEntity.addPart("tanggal_asset", new StringBody(tanggal_asset));
                reqEntity.addPart("keterangan", new StringBody(keterangan));


                httppost.setEntity(reqEntity);
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity resEntity = response.getEntity();
                InputStream is = resEntity.getContent();

                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;


                while ((line = reader.readLine()) != null) {
                    //Log.i("tes", line + "\n");
                    sb.append(line + "\n");
                }
                is.close();
                String json = sb.toString();
                System.out.println(json);

                return new JSONObject(json);

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(JSONObject result) {

            boolean success = false;
            String message = "Proses kirim gagal.";

            try {
                success = result.isNull("success")?false:result.getBoolean("success");
                message = result.isNull("message")?"":result.getString("message");

            } catch (JSONException e) {
                e.printStackTrace();
            }

            IsianAsetFragment.load_masking.setVisibility(View.GONE);
            if(success) {
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                displayView(3);
            } else {
                text_message.setText(message);
                text_title.setText(success?"BERHASIL":"GAGAL");
                dialog_informasi.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog_informasi.show();
            }

        }
    }


    public void prosesRegister(String jenis_lapak, String nama_bank, String norek, String npwp, String nama_anggota, String nik, String hp, String email, String password, String namapasar, String namalapak, String alamat) {

        new prosesSaveRegister(jenis_lapak, nama_bank, norek, npwp, nama_anggota, nik, hp, email, password, namapasar, namalapak, alamat).execute();

    }

    public class prosesSaveRegister extends AsyncTask<String, Void, JSONObject> {

        String jenis_lapak;

        String nama_bank, password, namapasar, namalapak, alamat;

        String norek;

        String npwp, nama_anggota, nik, hp, email;

        prosesSaveRegister(String jenis_lapak, String nama_bank, String norek, String npwp, String nama_anggota, String nik, String hp, String email, String password, String namapasar, String namalapak, String alamat) {
            this.jenis_lapak = jenis_lapak;
            this.nama_bank = nama_bank;
            this.norek = norek;
            this.npwp = npwp;
            this.nik = npwp;
            this.nama_anggota = nama_anggota;
            this.hp = hp;
            this.email = email;
            this.password = password;
            this.namapasar = namapasar;
            this.namalapak = namalapak;
            this.alamat = alamat;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            LspFragment.load_masking.setVisibility(View.VISIBLE);
        }

        @Override
        protected JSONObject doInBackground(String... urls) {

            try {
                String url = CommonUtilities.SERVER_HOME_URL + "/server2/smc/register.php";
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(url);

                MultipartEntity reqEntity = new MultipartEntity();
                reqEntity.addPart("user_id", new StringBody(data.getId()+""));
//                reqEntity.addPart("gambar", new StringBody(upload_gambar));
                reqEntity.addPart("jenis_lapak", new StringBody(jenis_lapak));
                reqEntity.addPart("nama_bank", new StringBody(nama_bank));
                reqEntity.addPart("norek", new StringBody(norek));
                reqEntity.addPart("npwp", new StringBody(npwp));
                reqEntity.addPart("nik", new StringBody(nik));
                reqEntity.addPart("nama_anggota", new StringBody(nama_anggota));
                reqEntity.addPart("hp", new StringBody(hp));
                reqEntity.addPart("email", new StringBody(email));
                reqEntity.addPart("password", new StringBody(password));
                reqEntity.addPart("namapasar", new StringBody(namapasar));
                reqEntity.addPart("namalapak", new StringBody(namalapak));
                reqEntity.addPart("alamat", new StringBody(alamat));


                httppost.setEntity(reqEntity);
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity resEntity = response.getEntity();
                InputStream is = resEntity.getContent();

                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;


                while ((line = reader.readLine()) != null) {
                    Log.i("Line", line+"\n");
                    sb.append(line + "\n");
                }
                is.close();
                String json = sb.toString();
                System.out.println(json);

                return new JSONObject(json);

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(JSONObject result) {

            boolean success = false;
            String message = "Proses kirim gagal.";

            try {
                success = result.isNull("success")?false:result.getBoolean("success");
                message = result.isNull("message")?"":result.getString("message");

            } catch (JSONException e) {
                e.printStackTrace();
            }

            LspFragment.load_masking.setVisibility(View.GONE);
            if(success) {
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                if(message=="Data GAGAL terkirim."){
                    displayView(0);
                } else {
                    //v13nr catat sharedpref email this.email
                    // 0 - for private mode`
                    SharedPreferences pref = getApplicationContext().getSharedPreferences("AyoPasarPref", 0);

                    SharedPreferences.Editor editor = pref.edit();

                    editor.putString("sharedPref_email",  this.email); // Storing string
                    editor.apply();

                    //to get
                    //pref.getString("sharedPref_email", null); // getting String

                    //pref.getInt("sharedPref_email", 0); // getting Integer

                    text_message.setText("ingat email Anda : "+pref.getString("sharedPref_email", null));
                    text_title.setText(success?"BERHASIL":"GAGAL");
                    dialog_informasi.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog_informasi.show();

                    displayView(11);
                }

            } else {
                text_message.setText(message);
                text_title.setText(success?"BERHASIL":"GAGAL");
                dialog_informasi.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog_informasi.show();
            }

        }
    }



    public void prosescalc(String jumlah, String masa) {

        new prosescalc(jumlah, masa).execute();

    }

    public class prosescalc extends AsyncTask<String, Void, JSONObject> {

        String jumlah;

        String masa;


        prosescalc(String jumlah, String masa) {
            this.jumlah = jumlah;
            this.masa = masa;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            LspFragment.load_masking.setVisibility(View.VISIBLE);

        }

        @Override
        protected JSONObject doInBackground(String... urls) {

            try {
                String url = CommonUtilities.SERVER_HOME_URL + "/server2/smc/registercalc.php";
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(url);

                MultipartEntity reqEntity = new MultipartEntity();
                reqEntity.addPart("user_id", new StringBody(data.getId()+""));
//                reqEntity.addPart("gambar", new StringBody(upload_gambar));
                reqEntity.addPart("jumlah", new StringBody(jumlah));
                reqEntity.addPart("masa", new StringBody(masa));


                httppost.setEntity(reqEntity);
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity resEntity = response.getEntity();
                InputStream is = resEntity.getContent();

                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;


                while ((line = reader.readLine()) != null) {
                    Log.i("nng2", line+"\n");
                    sb.append(line + "\n");
                }
                is.close();
                String json = sb.toString();
                System.out.println(json);

                return new JSONObject(json);

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            Toast.makeText(context, "KALKULATOR AKAN MENGHITUNG PINJAMAN", Toast.LENGTH_LONG).show();
            try {
                cicilan = result.isNull("cicilan")?"":result.getString("cicilan");

            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (result!=null) {
                LspFragment.webView.loadUrl("javascript:resetTabelAA();");
                try {
                    JSONArray data = result.isNull("topics")?null:result.getJSONArray("topics");
                    if(data!=null) {
                        for(int i=0; i<data.length(); i++) {
                            JSONObject rec = data.getJSONObject(i);
                            String tanggal = rec.isNull("tanggal")?"":rec.getString("tanggal");
                            //String cicilan = rec.isNull("cicilan")?"":rec.getString("cicilan");
                            //Log.i("nng2", tanggal);
                            LspFragment.webView.loadUrl("javascript:setTabelAA('"+tanggal+"','"+cicilan+"');");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }



           LspFragment.webView.loadUrl("javascript:showTabelAA();");
        }
    }


    public void prosesPinjam(String paket, String cicilan) {

        new prosesPinjam(paket, cicilan).execute();

    }

    public class prosesPinjam extends AsyncTask<String, Void, JSONObject> {

        String paket;

        String cicilan;


        prosesPinjam(String paket, String cicilan) {
            this.paket = paket;
            this.cicilan = cicilan;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            PinjamanFragment.load_masking.setVisibility(View.VISIBLE);
        }

        @Override
        protected JSONObject doInBackground(String... urls) {

            try {
                String url = CommonUtilities.SERVER_HOME_URL + "/server2/smc/android_pinjam.php";
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(url);



                //catat posisi terakhir aktivasi
                // 0 - for private mode`
                SharedPreferences pref = getApplicationContext().getSharedPreferences("AyoPasarPref", 0);

                SharedPreferences.Editor editor = pref.edit();

                //editor.putString("sharedPref_aktivasi",  status_aktivasi); // Storing string
                //editor.apply();

                //to get
                String emailx = pref.getString("sharedPref_email", null); // getting String

                //pref.getInt("sharedPref_email", 0); // getting Integer



                MultipartEntity reqEntity = new MultipartEntity();
                reqEntity.addPart("user_id", new StringBody(data.getId()+""));
//                reqEntity.addPart("gambar", new StringBody(upload_gambar));
                reqEntity.addPart("paket", new StringBody(paket));
                reqEntity.addPart("cicilan", new StringBody(cicilan));
                reqEntity.addPart("email", new StringBody(emailx));


                httppost.setEntity(reqEntity);
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity resEntity = response.getEntity();
                InputStream is = resEntity.getContent();

                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;


                while ((line = reader.readLine()) != null) {
                    //Log.i("Line", line+"\n");
                    sb.append(line + "\n");
                }
                is.close();
                String json = sb.toString();
                System.out.println(json);

                return new JSONObject(json);

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(JSONObject result) {

            boolean success = false;
            String message = "Proses kirim gagal.";

            try {
                success = result.isNull("success")?false:result.getBoolean("success");
                message = result.isNull("message")?"":result.getString("message");

            } catch (JSONException e) {
                e.printStackTrace();
            }

            PinjamanFragment.load_masking.setVisibility(View.GONE);
            if(success) {
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                if(message=="Data GAGAL terkirim."){
                    displayView(0);
                } else {

                    text_message.setText("Akan Diproses : ");
                    text_title.setText(success?"BERHASIL":"GAGAL");
                    dialog_informasi.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog_informasi.show();
                    displayView(3);
                }

            } else {
                text_message.setText(message);
                text_title.setText(success?"BERHASIL":"GAGAL");
                dialog_informasi.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog_informasi.show();
            }

        }
    }

    public void prosesEditAset(String jenisbarang, String kodebarang, String identitasbarang, String jumlah_barang, String apbdesa, String lain, String kekayaan, String tanggal_asset, String keterangan) {
        if(action_add.equalsIgnoreCase("server")) {
            new prosesEditAset(jenisbarang, kodebarang, identitasbarang, jumlah_barang, apbdesa, lain, kekayaan, tanggal_asset, keterangan).execute();
        } else {
            aset data = new aset(Integer.parseInt(id_selected_aset), jenisbarang, kodebarang, identitasbarang, jumlah_barang, apbdesa, lain, kekayaan, tanggal_asset, keterangan, mImageCapturePath);
            db.insertDataAset(data);
            Toast.makeText(context, "Update data lokal berhasil.", Toast.LENGTH_LONG).show();
            displayView(9);
        }

    }

    public class prosesEditAset extends AsyncTask<String, Void, JSONObject> {

        String jenisbarang;
        String kodebarang;
        String identitasbarang;
        String jumlah_barang;
        String apbdesa;
        String lain;
        String kekayaan;
        String tanggal_asset;
        String keterangan;

        prosesEditAset(String jenisbarang, String kodebarang, String identitasbarang, String jumlah_barang, String apbdesa, String lain, String kekayaan, String tanggal_asset, String keterangan) {
            this.jenisbarang = jenisbarang;
            this.kodebarang = kodebarang;
            this.identitasbarang = identitasbarang;
            this.jumlah_barang = jumlah_barang;
            this.apbdesa = apbdesa;
            this.lain = lain;
            this.kekayaan = kekayaan;
            this.tanggal_asset = tanggal_asset;
            this.keterangan = keterangan;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            EditAsetFragment.load_masking.setVisibility(View.VISIBLE);
        }

        @Override
        protected JSONObject doInBackground(String... urls) {

            try {
                String url = CommonUtilities.SERVER_HOME_URL + "/smc/android_upload_text_sekolah_edit.php";
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(url);

                MultipartEntity reqEntity = new MultipartEntity();
                reqEntity.addPart("user_id", new StringBody(data.getId()+""));
                reqEntity.addPart("id", new StringBody(id_selected_aset));
                reqEntity.addPart("jenisbarang", new StringBody(jenisbarang));
                reqEntity.addPart("kodebarang", new StringBody(kodebarang));
                reqEntity.addPart("identitasbarang", new StringBody(identitasbarang));

                reqEntity.addPart("jenisbarang", new StringBody(jenisbarang));
                reqEntity.addPart("kodebarang", new StringBody(kodebarang));
                reqEntity.addPart("identitasbarang", new StringBody(identitasbarang));

                reqEntity.addPart("jumlah_barang", new StringBody(jumlah_barang));
                reqEntity.addPart("apbdesa", new StringBody(apbdesa));
                reqEntity.addPart("lain", new StringBody(lain));

                reqEntity.addPart("kekayaan", new StringBody(kekayaan));
                reqEntity.addPart("tanggal_asset", new StringBody(tanggal_asset));
                reqEntity.addPart("keterangan", new StringBody(keterangan));


                httppost.setEntity(reqEntity);
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity resEntity = response.getEntity();
                InputStream is = resEntity.getContent();

                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;


                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                is.close();
                String json = sb.toString();
                System.out.println(json);

                return new JSONObject(json);

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(JSONObject result) {

            boolean success = false;
            String message = "Proses edit data aset gagal.";

            try {
                success = result.isNull("success")?false:result.getBoolean("success");
                message = result.isNull("message")?"":result.getString("message");

            } catch (JSONException e) {
                e.printStackTrace();
            }

            EditAsetFragment.load_masking.setVisibility(View.GONE);
            if(success) {
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                displayView(5);
            } else {
                text_message.setText(message);
                text_title.setText(success?"BERHASIL":"GAGAL");
                dialog_informasi.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog_informasi.show();
            }

        }
    }


    public void prosesUpdateDesa(String kecamatan, String desa, String lokal, String kades, String sekdes, String pengurus, String alamatdesa) {
        new prosesUpdateDesa(kecamatan, desa, lokal, kades, sekdes, pengurus, alamatdesa).execute();
    }

    public class prosesUpdateDesa extends AsyncTask<String, Void, JSONObject> {

        String kecamatan;
        String desa;
        String lokal;
        String kades;
        String sekdes;
        String pengurus;
        String alamatdesa;

        prosesUpdateDesa(String kecamatan, String desa, String lokal, String kades, String sekdes, String pengurus, String alamatdesa) {
            this.kecamatan = kecamatan;
            this.desa = desa;
            this.lokal = lokal;
            this.kades = kades;
            this.sekdes = sekdes;
            this.pengurus = pengurus;
            this.alamatdesa = alamatdesa;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            EditDesaFragment.load_masking.setVisibility(View.VISIBLE);
        }

        @Override
        protected JSONObject doInBackground(String... urls) {

            try {
                String url = CommonUtilities.SERVER_HOME_URL + "/server2/smc/android_edit_profile.php";
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(url);

                MultipartEntity reqEntity = new MultipartEntity();
                reqEntity.addPart("user", new StringBody(data.getEmail()));
                reqEntity.addPart("kecamatan", new StringBody(kecamatan));
                reqEntity.addPart("desa", new StringBody(desa));
                reqEntity.addPart("lokal", new StringBody(lokal));
                reqEntity.addPart("kades", new StringBody(kades));
                reqEntity.addPart("sekdes", new StringBody(sekdes));
                reqEntity.addPart("pengurus", new StringBody(pengurus));
                reqEntity.addPart("alamatdesa", new StringBody(alamatdesa));

                httppost.setEntity(reqEntity);
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity resEntity = response.getEntity();
                InputStream is = resEntity.getContent();

                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;


                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                is.close();
                String json = sb.toString();
                System.out.println(json);

                return new JSONObject(json);

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(JSONObject result) {

            boolean success = false;
            String message = "Proses edit data aset gagal.";

            try {
                success = result.isNull("success")?false:result.getBoolean("success");
                message = result.isNull("message")?"":result.getString("message");

            } catch (JSONException e) {
                e.printStackTrace();
            }

            EditDesaFragment.load_masking.setVisibility(View.GONE);
            if(success) {
                CommonUtilities.setLoginUser(context, new user(
                    data.getId(),
                    data.getNama(),
                    data.getEmail(),
                    kecamatan,
                    data.getKabupaten(),
                    data.getPropinsi(),
                    desa,
                    lokal,
                    kades,
                    sekdes,
                    pengurus,
                    alamatdesa,
                    data.getPhoto()
                ));

                data = CommonUtilities.getLoginUser(context);
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                displayView(3);
            } else {
                text_message.setText(message);
                text_title.setText(success?"BERHASIL":"GAGAL");
                dialog_informasi.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog_informasi.show();
            }

        }
    }
    
    public void loadDataAset() {
        new loadDataAset().execute();
    }

    public class loadDataAset extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ManageFragment.load_masking.setVisibility(View.VISIBLE);
            ManageFragment.webView.loadUrl("javascript:resetTabel();");
        }

        @Override
        protected JSONObject doInBackground(String... urls) {

            try {
                String url = CommonUtilities.SERVER_HOME_URL + "/server2/services/publik/tabelPinjaman.php";
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(url);

                MultipartEntity reqEntity = new MultipartEntity();
                reqEntity.addPart("user", new StringBody(data.getEmail()));
                reqEntity.addPart("action", new StringBody("getDetail"));

                httppost.setEntity(reqEntity);
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity resEntity = response.getEntity();
                InputStream is = resEntity.getContent();

                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;


                while ((line = reader.readLine()) != null) {
                    Log.i("Line", line+"\n");
                    sb.append(line + "\n");
                }
                is.close();
                String json = sb.toString();

                return new JSONObject(json);

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(JSONObject result) {

            ManageFragment.load_masking.setVisibility(View.GONE);

            if (result!=null) {
                try {
                    JSONArray data = result.isNull("topics")?null:result.getJSONArray("topics");
                    if(data!=null) {
                        for(int i=0; i<data.length(); i++) {
                            JSONObject rec = data.getJSONObject(i);
                            int id = rec.isNull("id")?0:rec.getInt("id");
                            String jenisbarang = rec.isNull("status")?"":rec.getString("status");
                            String kodebarang = rec.isNull("jatuh_tempo")?"":rec.getString("jatuh_tempo");
                            String jumlah_barang = rec.isNull("angsuran")?"":rec.getString("angsuran");

                            ManageFragment.webView.loadUrl("javascript:setTabel('"+id+"','"+jenisbarang+"','"+kodebarang+"','"+jumlah_barang+"');");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                ManageFragment.webView.loadUrl("javascript:showTabel();");
            }

        }
    }


    public void loadDataAsetJnsLapak() {
        new loadDataAsetJnsLapak().execute();
    }

    public class loadDataAsetJnsLapak extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //           ManageFragment.load_masking.setVisibility(View.VISIBLE);
            //ManageFragment.webView.loadUrl("javascript:resetTabel();");
        }

        @Override
        protected JSONObject doInBackground(String... urls) {

            try {
                String url = CommonUtilities.SERVER_HOME_URL + "/server2/services/publik/tabel_jenis_lapak.php";
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(url);

                MultipartEntity reqEntity = new MultipartEntity();
                reqEntity.addPart("user", new StringBody(data.getEmail()));
                reqEntity.addPart("action", new StringBody("getDetail"));

                httppost.setEntity(reqEntity);
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity resEntity = response.getEntity();
                InputStream is = resEntity.getContent();

                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;


                while ((line = reader.readLine()) != null) {
                    //Log.i("Line", line+"\n");
                    sb.append(line + "\n");
                }
                is.close();
                String json = sb.toString();

                return new JSONObject(json);

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(JSONObject result) {

//            ManageFragment.load_masking.setVisibility(View.GONE);

            if (result!=null) {
                try {
                    JSONArray data = result.isNull("topics")?null:result.getJSONArray("topics");
                    if(data!=null) {
                        for(int i=0; i<data.length(); i++) {
                            JSONObject rec = data.getJSONObject(i);
                            int id = rec.isNull("id")?0:rec.getInt("id");
                            String jenis_lapak = rec.isNull("jenis_lapak")?"kosong":rec.getString("jenis_lapak");

                            LspFragment.webView.loadUrl("javascript:setTabel('"+id+"','"+jenis_lapak+"');");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                LspFragment.webView.loadUrl("javascript:showTabel();");
            }

        }
    }



    public void LoadDataPaketPinjaman() {
        new LoadDataPaketPinjaman().execute();
    }

    public class LoadDataPaketPinjaman extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //           ManageFragment.load_masking.setVisibility(View.VISIBLE);
            //ManageFragment.webView.loadUrl("javascript:resetTabel();");
        }

        @Override
        protected JSONObject doInBackground(String... urls) {

            try {
                String url = CommonUtilities.SERVER_HOME_URL + "/server2/services/publik/tabel_paket_pinjaman.php";
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(url);

                MultipartEntity reqEntity = new MultipartEntity();
                reqEntity.addPart("user", new StringBody(data.getEmail()));
                reqEntity.addPart("action", new StringBody("getDetail"));

                httppost.setEntity(reqEntity);
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity resEntity = response.getEntity();
                InputStream is = resEntity.getContent();

                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;


                while ((line = reader.readLine()) != null) {
                    //Log.i("Line", line+"\n");
                    sb.append(line + "\n");
                }
                is.close();
                String json = sb.toString();

                return new JSONObject(json);

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(JSONObject result) {

//            ManageFragment.load_masking.setVisibility(View.GONE);

            if (result!=null) {
                try {
                    JSONArray data = result.isNull("topics")?null:result.getJSONArray("topics");
                    if(data!=null) {
                        for(int i=0; i<data.length(); i++) {
                            JSONObject rec = data.getJSONObject(i);
                            int id = rec.isNull("id")?0:rec.getInt("id");
                            String masa_cicilan = rec.isNull("masa_cicilan")?"kosong":rec.getString("masa_cicilan");

                            PinjamanFragment.webView.loadUrl("javascript:setTabel('"+id+"','"+masa_cicilan+"');");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                PinjamanFragment.webView.loadUrl("javascript:showTabel();");
            }

        }
    }





    public void LoadDataPinjaman() {
        new LoadDataPinjaman().execute();
    }

    public class LoadDataPinjaman extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //           ManageFragment.load_masking.setVisibility(View.VISIBLE);
            //ManageFragment.webView.loadUrl("javascript:resetTabel();");
        }

        @Override
        protected JSONObject doInBackground(String... urls) {

            try {
                String url = CommonUtilities.SERVER_HOME_URL + "/server2/services/publik/tabel_data_pinjaman.php";
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(url);

                MultipartEntity reqEntity = new MultipartEntity();
                reqEntity.addPart("user", new StringBody(data.getEmail()));
                reqEntity.addPart("action", new StringBody("getDetail"));

                httppost.setEntity(reqEntity);
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity resEntity = response.getEntity();
                InputStream is = resEntity.getContent();

                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;


                while ((line = reader.readLine()) != null) {
                    Log.w("nng", line+"\n");
                    sb.append(line + "\n");
                }
                is.close();
                String json = sb.toString();

                return new JSONObject(json);

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(JSONObject result) {

//            ManageFragment.load_masking.setVisibility(View.GONE);

            if (result!=null) {
                try {
                    JSONArray data = result.isNull("topics")?null:result.getJSONArray("topics");
                    if(data!=null) {
                        for(int i=0; i<data.length(); i++) {
                            JSONObject rec = data.getJSONObject(i);
                            int id = rec.isNull("id")?0:rec.getInt("id");
                            String status = rec.isNull("status")?"kosong":rec.getString("status");
                            String jumlah = rec.isNull("status")?"paket":rec.getString("paket");
                            String masa = rec.isNull("status")?"cicilan":rec.getString("cicilan");

                            PinjamanFragment.webView.loadUrl("javascript:setTabelData('"+id+"','"+jumlah+"','"+masa+"','"+status+"');");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                PinjamanFragment.webView.loadUrl("javascript:showTabelData();");
            }

        }
    }





    public void LoadDataAsparindo() {
        new LoadDataAsparindo().execute();
    }

    public class LoadDataAsparindo extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //           ManageFragment.load_masking.setVisibility(View.VISIBLE);
            //ManageFragment.webView.loadUrl("javascript:resetTabel();");
        }

        @Override
        protected JSONObject doInBackground(String... urls) {

            try {
                String url = CommonUtilities.SERVER_HOME_URL + "/server2/services/publik/tabel_aktivasi.php";
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(url);


                //catat posisi terakhir aktivasi
                // 0 - for private mode`
                SharedPreferences pref = getApplicationContext().getSharedPreferences("AyoPasarPref", 0);

                SharedPreferences.Editor editor = pref.edit();

                //editor.putString("sharedPref_aktivasi",  status_aktivasi); // Storing string
                //editor.apply();

                //to get
                String emailx = pref.getString("sharedPref_email", null); // getting String

                //pref.getInt("sharedPref_email", 0); // getting Integer



                MultipartEntity reqEntity = new MultipartEntity();
                reqEntity.addPart("user", new StringBody(data.getEmail()));
                reqEntity.addPart("action", new StringBody("getDetail"));
                reqEntity.addPart("email", new StringBody(emailx));

                httppost.setEntity(reqEntity);
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity resEntity = response.getEntity();
                InputStream is = resEntity.getContent();

                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;


                while ((line = reader.readLine()) != null) {
                    //Log.i("Line", line+"\n");
                    sb.append(line + "\n");
                }
                is.close();
                String json = sb.toString();

                return new JSONObject(json);

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(JSONObject result) {

//            ManageFragment.load_masking.setVisibility(View.GONE);

            if (result!=null) {
                try {
                    JSONArray data = result.isNull("topics")?null:result.getJSONArray("topics");
                    if(data!=null) {
                        for(int i=0; i<data.length(); i++) {
                            JSONObject rec = data.getJSONObject(i);
                            int id = rec.isNull("id")?0:rec.getInt("id");
                            String status_aktivasi = rec.isNull("status_aktivasi")?"kosong":rec.getString("status_aktivasi");

                            //catat posisi terakhir aktivasi
                            // 0 - for private mode`
                            SharedPreferences pref = getApplicationContext().getSharedPreferences("AyoPasarPref", 0);

                            SharedPreferences.Editor editor = pref.edit();

                            editor.putString("sharedPref_aktivasi",  status_aktivasi); // Storing string
                            editor.apply();

                            //to get
                            //pref.getString("sharedPref_aktivasi", null); // getting String

                            //pref.getInt("sharedPref_email", 0); // getting Integer

                            /*
                            text_message.setText("ingat email Anda : "+pref.getString("sharedPref_email", null));

                            text_title.setText(success?"BERHASIL":"GAGAL");
                            dialog_informasi.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            dialog_informasi.show();
                            */
                            PengaturanFragment.webView.loadUrl("javascript:setTabel('"+id+"','"+status_aktivasi+"');");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                PengaturanFragment.webView.loadUrl("javascript:showTabel();");
            }

        }
    }


    /*public void loadKabupaten() {
        new loadKabupaten().execute();
    }

    public class loadKabupaten extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            RegistrasiFragment.load_masking.setVisibility(View.VISIBLE);
            RegistrasiFragment.webView.loadUrl("javascript:resetKabupaten();");
        }

        @Override
        protected JSONObject doInBackground(String... urls) {

            try {
                String url = "http://indowebit.web.id/kotata/server/services/sekolah/kabupaten.php";
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(url);

                MultipartEntity reqEntity = new MultipartEntity();
                reqEntity.addPart("limit", new StringBody("1000000"));
                reqEntity.addPart("page", new StringBody("1"));

                httppost.setEntity(reqEntity);
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity resEntity = response.getEntity();
                InputStream is = resEntity.getContent();

                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;


                while ((line = reader.readLine()) != null) {
                    //Log.i("Line", line+"\n");
                    sb.append(line + "\n");
                }
                is.close();
                String json = sb.toString();

                return new JSONObject("{\"topics\": " + json + "}");

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(JSONObject result) {

            RegistrasiFragment.load_masking.setVisibility(View.GONE);

            if (result!=null) {
                try {
                    JSONArray data = result.isNull("topics")?null:result.getJSONArray("topics");
                    if(data!=null) {
                        for(int i=0; i<data.length(); i++) {
                            JSONObject rec = data.getJSONObject(i);
                            String nama      = rec.isNull("nama")?"":rec.getString("nama");
                            String direktori = rec.isNull("direktori")?"":rec.getString("direktori");

                            RegistrasiFragment.webView.loadUrl("javascript:setKabupaten('"+nama+"','"+direktori+"');");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
    }*/

    public void loadDataAsetLokal() {
        ManageLokalFragment.webView.loadUrl("javascript:resetTabel();");
        for (aset data: db.getAsetlist()) {
            ManageLokalFragment.webView.loadUrl("javascript:setTabel('"+data.getId()+"','"+data.getJenisbarang()+"','"+data.getKodebarang()+"','"+data.getJumlah_barang()+"');");
        }
        ManageLokalFragment.webView.loadUrl("javascript:showTabel();");
    }

    public void prosesUpload(String pathFile) {
        try {
            File file = new File ( pathFile );
            int file_size = (int) file.length();
            inStream = new BufferedInputStream( new FileInputStream( file ));

            //webView.loadUrl("javascript:setProses('"+kolom_index+"', '"+upload_dest+"', 0, '"+file.getName()+"', "+file_size+", 1, 'Start Upload', '');");
            new prosesUploadChunkFile(0, pathFile, "../../../smc/file", "png|jpg").execute();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    class prosesUploadChunkFile extends AsyncTask<String, Void, JSONObject> {

        int CHUNK_SIZE = 1024 * 1024 * 1; // 1MB
        String pathFile;
        String destination;
        String ext;
        int start;
        int end;
        int file_size;

        prosesUploadChunkFile(int start, String pathFile, String destination, String ext) {
            this.pathFile = pathFile;
            this.start = start;
            this.destination = destination;
            this.ext = ext;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            HomeFragment.load_masking.setVisibility(View.VISIBLE);
        }

        @Override
        protected JSONObject doInBackground(String... urls) {
            JSONObject result = null;
            //String url = CommonUtilities.SERVER_HOME_URL  + "/server2/services/public/upload.php";
            //String url = "http://sogi-online.com/android/server2/services/publik/upload.php";
            String url = "https://nanangprogrammer.000webhostapp.com/server/server/services/publik/upload.php";
            File file = new File ( pathFile );
            String file_name = file.getName();
            file_size = (int) file.length();
            end = (start+CHUNK_SIZE)>=file_size?file_size:(start+CHUNK_SIZE);

            try {

                byte[] temporary = new byte[end-start]; //Temporary Byte Array
                inStream.read(temporary, 0, end-start);

                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(url);
                MultipartEntity reqEntity = new MultipartEntity();

                ByteArrayInputStream arrayStream = new ByteArrayInputStream(temporary);
                reqEntity.addPart("ax_file_input", new InputStreamBody(arrayStream, pathFile));
                reqEntity.addPart("ax-file-path", new StringBody(destination));
                reqEntity.addPart("ax-allow-ext", new StringBody(ext==null?"":ext));
                reqEntity.addPart("ax-file-name", new StringBody(file_name));
                reqEntity.addPart("ax-max-file-size", new StringBody("10G"));
                reqEntity.addPart("ax-start-byte", new StringBody(end+""));
                reqEntity.addPart("ax-last-chunk", new StringBody(end==file_size?"true":"false"));

                httppost.setEntity(reqEntity);
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity resEntity = response.getEntity();

                InputStream is = resEntity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;

                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                is.close();

                //Log.i("UPLOAD CHUNK", sb.toString());
                result = new JSONObject(sb.toString());

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            upload_gambar = "";
            upload_status = 0;
            upload_size = 0;
            upload_info = "";
            upload_more = "";

            if(result!=null) {
                try {
                    upload_gambar = result.isNull("name")?upload_gambar:result.getString("name");
                    upload_status = result.isNull("status")?upload_status:result.getInt("status");
                    upload_size   = result.isNull("size")?upload_size:result.getInt("size");
                    upload_info   = result.isNull("info")?upload_info:result.getString("info");
                    upload_more   = result.isNull("more")?upload_more:result.getString("more");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(end<file_size && upload_status==1) {
                    new prosesUploadChunkFile(end, pathFile, destination, ext).execute();
                } else {
                    try {
                        inStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    HomeFragment.load_masking.setVisibility(View.GONE);
                    if(upload_status!=1) {
                        text_message.setText(upload_info);
                        text_title.setText("UPLOAD GAGAL Kode 01");
                        dialog_informasi.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog_informasi.show();
                    } else {
                        Toast.makeText(context, "Upload file berhasil!", Toast.LENGTH_SHORT).show();
                        displayView(4);
                    }

                }

            } else {
                //new prosesUploadChunkFile(start, pathFile, destination, ext).execute();
                text_message.setText("Tidak ada koneksi internet.");
                text_title.setText("UPLOAD GAGAL Kode 02");
                dialog_informasi.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog_informasi.show();
            }
        }
    }

    private void startTracking() {
        //Log.d("ASET DATA", "startTracking");

        CommonUtilities.setCurentlyTracking(context, true);
        Intent i = new Intent(context, AsetService.class);
        i.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
        startService(i);
    }

    private void stopTracking() {
        //Log.d("ASET DATA", "stopTracking");
        CommonUtilities.setCurentlyTracking(context, false);
        Intent i = new Intent(context, AsetService.class);
        i.setAction(Constants.ACTION.STOPFOREGROUND_ACTION);
        startService(i);
    }






    public void kalkulator2() {
        new kalkulator2().execute();
    }

    public class kalkulator2 extends AsyncTask<String, Void, JSONObject> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //           ManageFragment.load_masking.setVisibility(View.VISIBLE);
            //ManageFragment.webView.loadUrl("javascript:resetTabel();");
        }

        @Override
        protected JSONObject doInBackground(String... urls) {

            try {
                String url = CommonUtilities.SERVER_HOME_URL + "/server2/services/publik/kalkulator.php";
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(url);


                //catat posisi terakhir aktivasi
                // 0 - for private mode`
                SharedPreferences pref = getApplicationContext().getSharedPreferences("AyoPasarPref", 0);

                SharedPreferences.Editor editor = pref.edit();

                //editor.putString("sharedPref_aktivasi",  status_aktivasi); // Storing string
                //editor.apply();

                //to get
                String emailx = pref.getString("sharedPref_email", null); // getting String

                //pref.getInt("sharedPref_email", 0); // getting Integer



                MultipartEntity reqEntity = new MultipartEntity();
                reqEntity.addPart("user", new StringBody(data.getEmail()));
                reqEntity.addPart("action", new StringBody("getDetail"));
                reqEntity.addPart("email", new StringBody(emailx));

                httppost.setEntity(reqEntity);
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity resEntity = response.getEntity();
                InputStream is = resEntity.getContent();

                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;


                while ((line = reader.readLine()) != null) {
                    //Log.i("Line", line+"\n");
                    sb.append(line + "\n");
                }
                is.close();
                String json = sb.toString();

                return new JSONObject(json);

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(JSONObject result) {

//            ManageFragment.load_masking.setVisibility(View.GONE);

            if (result!=null) {
                try {
                    JSONArray data = result.isNull("topics")?null:result.getJSONArray("topics");
                    if(data!=null) {
                        for(int i=0; i<data.length(); i++) {
                            JSONObject rec = data.getJSONObject(i);
                            int id = rec.isNull("id")?0:rec.getInt("id");
                            String status_aktivasi = rec.isNull("status_aktivasi")?"kosong":rec.getString("status_aktivasi");

                            //catat posisi terakhir aktivasi
                            // 0 - for private mode`
                            SharedPreferences pref = getApplicationContext().getSharedPreferences("AyoPasarPref", 0);

                            SharedPreferences.Editor editor = pref.edit();

                            editor.putString("sharedPref_aktivasi",  status_aktivasi); // Storing string
                            editor.apply();

                            //to get
                            //pref.getString("sharedPref_aktivasi", null); // getting String

                            //pref.getInt("sharedPref_email", 0); // getting Integer

                            /*
                            text_message.setText("ingat email Anda : "+pref.getString("sharedPref_email", null));

                            text_title.setText(success?"BERHASIL":"GAGAL");
                            dialog_informasi.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            dialog_informasi.show();
                            */
                            LspFragment.webView.loadUrl("javascript:setTabelA('"+id+"','"+status_aktivasi+"');");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                LspFragment.webView.loadUrl("javascript:showTabelA();");
            }

        }
    }


    public void kalkulator() {
/*
        dialog_informasi = new Dialog(context);
        dialog_informasi.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_informasi.setCancelable(true);
        dialog_informasi.setContentView(R.layout.informasi_dialog);

        btn_ok = (MyTextView) dialog_informasi.findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                dialog_informasi.dismiss();
            }
        });
        btn_ok.setTypeface(fonts2);

        text_title = (MyTextView) dialog_informasi.findViewById(R.id.text_title);
        text_message = (MyTextView) dialog_informasi.findViewById(R.id.text_dialog);
        text_message.setTypeface(fonts1);


*/

        /*Log.d("nng", "kalkulasi");
        text_message.setText("Kalkulator Pinjaman ");
        text_title.setText("Menghitung Pinjaman");
        dialog_informasi.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog_informasi.show();*/

        //String tes = "SDF";
        //LspFragment.webView.loadUrl("javascript:setTabelKalkulator('tes dari java');");
        LspFragment.webView.loadUrl("javascript:showKalkulator('sdf');");

    }


    public void goto_caripinjaman() {

        text_message.setText("Silakan Login ");
        text_title.setText("Login Lebih Dulu");
        dialog_informasi.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog_informasi.show();

    }


}
