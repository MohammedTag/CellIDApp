package tag.cellidapp100;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.CellIdentityGsm;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Formatter;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button gotomap;
    TextView show,showresponse;
    JSONObject reqJsonformat,result;
    Double latit,longtit;

    TelephonyManager tm;

    RequestQueue rq;
    JsonObjectRequest requested;

    String url = "    https://ap1.unwiredlabs.com/v2/process.php";

    private Handler handler;

    private final static int ACCESS_COARSE_ID = 109;
    private final static int READ_STATE_ID = 100;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //permission check
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, ACCESS_COARSE_ID);
        }




        //intiallization
        show = (TextView) findViewById(R.id.show);
        showresponse = (TextView) findViewById(R.id.showResult);
        gotomap = (Button)findViewById(R.id.gotomap);


        //requesting the form of request body
       // reqJsonformat = formTheRequest(this);

        //post request initiallization
        rq = Volley.newRequestQueue(this);


                //requesting
                /*JsonObjectRequest requested =new JsonObjectRequest(Request.Method.POST, url, reqJsonformat, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        result=response;
                        try {
                            latit=result.getDouble("lat");
                            longtit =result.getDouble("lon");
                        }catch (Exception e){}
                        //showresponse.setText(String.valueOf(latit));

                        show.setText(reqJsonformat.toString());
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

                rq.add(requested);*/





       /* gotomap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(MainActivity.this,MapsActivity.class);
               intent.putExtra("latit",latit);
               intent.putExtra("longtit",longtit);
                startActivity(intent);

            }
        });*/

        //show.setText(reqJsonformat.toString());


        handl.post(thread);

    }



    Handler handl= new Handler();
    Runnable thread = new Runnable() {
        @Override
        public void run() {

            //requesting the form of request body
            reqJsonformat = formTheRequest(getApplicationContext());

             requested =new JsonObjectRequest(Request.Method.POST, url, reqJsonformat, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    result=response;
                    try {
                        latit=result.getDouble("lat");
                        longtit =result.getDouble("lon");
                    }catch (Exception e){}
                    //showresponse.setText(String.valueOf(latit));

                   show.setText(result.toString());
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });

            rq.add(requested);
            showresponse.setText(reqJsonformat.toString());


            handl.postDelayed(thread,2000);
        }
    };



    //permission check method
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == ACCESS_COARSE_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                result = formTheRequest(this);

            }
        }
    }

    public JSONObject formTheRequest(Context ctx) {

        if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {


        JSONObject requestbody = new JSONObject();


        tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        WifiManager wm = (WifiManager) ctx.getSystemService(WIFI_SERVICE);


        int phoneTypeInt = tm.getPhoneType();
        String phoneType = null;
        phoneType = phoneTypeInt == TelephonyManager.PHONE_TYPE_GSM ? "gsm" : phoneType;
        phoneType = phoneTypeInt == TelephonyManager.PHONE_TYPE_NONE ? "umts" : phoneType;

        String networkOperator = tm.getNetworkOperator();
        if (!TextUtils.isEmpty(networkOperator)) {
            int mcc = Integer.parseInt(networkOperator.substring(0, 3));
            int mnc = Integer.parseInt(networkOperator.substring(3));

            GsmCellLocation gsm = (GsmCellLocation)tm.getCellLocation();

        /*    //requires api level 17
            CellInfoGsm getinfo = new CellInfoGsm();
            //requires api level 17
            CellIdentityGsm identityGsm = new CellIdentityGsm();

        */

            if (phoneType.equals("gsm")) {

                        try {
                            requestbody.put("token", "9d57a03fee18e8");
                            requestbody.put("radio", "gsm");
                            requestbody.put("mcc", mcc);
                            requestbody.put("mnc", mnc);
                            JSONArray cellsTowers = new JSONArray();
                            JSONObject cells = new JSONObject();
                            cells.put("lac", gsm.getLac());
                            cells.put("cid", gsm.getCid());
                            cellsTowers.put(cells);
                            requestbody.put("cells", (Object) cellsTowers);
                            requestbody.put("address", 1);
                        } catch (Exception e) {
                        }

                } else {

                try {
                    requestbody.put("token", "9d57a03fee18e8");
                    requestbody.put("radio", "umts");
                    requestbody.put("mcc", mcc);
                    requestbody.put("mnc", mnc);
                    JSONArray cellsTowers = new JSONArray();
                    JSONObject cells = new JSONObject();
                    cells.put("lac", gsm.getLac());
                    cells.put("cid", gsm.getCid());
                    cells.put("psc", gsm.getPsc());
                    cellsTowers.put(cells);
                    requestbody.put("cells", (Object) cellsTowers);
                    requestbody.put("address", 1);
                } catch (Exception e) {
                }

            }



        }

        return requestbody;

        }else
        {
            return null;
        }
    }
}
