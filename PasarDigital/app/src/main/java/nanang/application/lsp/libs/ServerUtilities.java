package nanang.application.lsp.libs;

import android.app.WallpaperManager;
import android.content.Context;
import android.util.Log;


import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class ServerUtilities {
    private static final int MAX_ATTEMPTS = 5;
    private static final int BACKOFF_MILLI_SECONDS = 2000;
    private static final Random random = new Random();
    private  static  final  String TAG = "UIUAA";
    
    public static JSONObject register(final Context context, String regId, int user_id, int guest_id) {
        //Log.i("UIUAA", "registering device (regId = " + regId + ")");
        String url = CommonUtilities.SERVER_HOME_URL+ "/server2/smc/androidRegistrasiRegid.php";   //
        List<NameValuePair> params = new ArrayList<>();
		params.add(new BasicNameValuePair("user_id", user_id+""));
		params.add(new BasicNameValuePair("guest_id", guest_id+""));
		params.add(new BasicNameValuePair("reg_id", regId));
		
		JSONObject result = null;
		JSONParser jpar = new JSONParser();
		
		
		long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);
        // Once GCM returns a registration id, we need to register on our server
        // As the server might be down, we will retry it a couple
        // times.
        for (int i = 1; i <= MAX_ATTEMPTS; i++) {
            Log.d(TAG, "Attempt #" + i + " to register");
            //displayMessage(context, context.getString(R.string.server_registering, i, MAX_ATTEMPTS));

            result = jpar.getJSONFromUrl(url, params, null);
            if (result!=null) {

            	boolean success = false;
				try {
					success = result.isNull("success")?false:result.getBoolean("success");
					guest_id = result.isNull("guest_id")?guest_id:result.getInt("guest_id");

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            	if(success) {
	            	CommonUtilities.setGcm_regid(context, regId);
		            CommonUtilities.setGuestId(context, guest_id);

		            return result;
            	}
            } 

            	// Here we are simplifying and retrying on any error; in a real
	            // application, it should retry only on unrecoverable errors
	            // (like HTTP error code 503).
	            Log.e(TAG, "Failed to register on attempt " + i + ": Response NULL!");
	            if (i == MAX_ATTEMPTS) {
	            	break;
	            }

	            try {
	            	Log.d(TAG, "Sleeping for " + backoff + " ms before retry");
	                Thread.sleep(backoff);
	            } catch (InterruptedException e1) {
	            	// Activity finished before we complete - exit.
	            	Log.d(TAG, "Thread interrupted: abort remaining retries!");
	                Thread.currentThread().interrupt();
	                return null;
	            }
	                // increase backoff exponentially
	                backoff *= 2;
                
        }
        
		return null;        
    }
    
    

    
}