package wilp.bits.iotmanufacturing.Provider;


import android.util.Log;


import com.android.volley.BuildConfig;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;


import java.io.UnsupportedEncodingException;

import wilp.bits.iotmanufacturing.Model.EventResponse;

public class VolleyReq<T> extends JsonRequest<T> {
    protected static final String TAG = VolleyReq.class.getSimpleName();
    private final static Gson GSON = new Gson();
    private static final int MAX_NUM_RETRIES = 3;
    private static final int INITIAL_TIMEOUT_MS = 5000;
    private static final float BACKOFF_MULTIPLIER = 1.0f;
    private static final String HOST_NAME = "http://13.233.0.158:8080/";
    private static final String URL_PREFIX = HOST_NAME + "aara/event/";
    private static final String ALL_VALUES = URL_PREFIX + "allManu";


    private Class<T> responseClass;

    public VolleyReq(String url, Response.Listener<T> listener, Response.ErrorListener errorListener) {
        this(Method.GET, url, listener, errorListener);
    }

    public VolleyReq(int httpMethod, String url, Response.Listener<T> listener,
                     Response.ErrorListener errorListener) {
        this(httpMethod, url, null, listener, errorListener);
    }

    public VolleyReq(int httpMethod, String url, Object requestBody, Class<T> responseClass, Response.Listener<T> listener,
                     Response.ErrorListener errorListener) {
        this(httpMethod, url, requestBody, listener, errorListener);
        this.responseClass = responseClass;
    }


    public VolleyReq(int httpMethod, String url, Object requestBody, Response.Listener<T> listener,
                     Response.ErrorListener errorListener) {
        super(httpMethod, url, GSON.toJson(requestBody), listener, errorListener);
        setRetryPolicy(new DefaultRetryPolicy(INITIAL_TIMEOUT_MS, MAX_NUM_RETRIES, BACKOFF_MULTIPLIER));
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Method:" + httpMethod + " URL=" + url);
        }
        setShouldCache(false); // do not cache any request.
    }


    public static VolleyReq get_events(Response.Listener<EventResponse> listener,
                                       Response.ErrorListener errorListener) {
        String url = String.format(ALL_VALUES);
        return new VolleyReq(url, listener, errorListener)
                .setTag("Get Values")
                .setClass(EventResponse.class);
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(
                    response.data, HttpHeaderParser.parseCharset(response.headers));
            T jsonResponse = GSON.fromJson(json, responseClass);
            return Response.success(
                    jsonResponse, HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        }
    }

    public VolleyReq setTag(String tag) {
        super.setTag(tag);
        return this;
    }

    public VolleyReq setClass(Class<T> c) {
        responseClass = c;
        return this;
    }

    public void enqueue(RequestQueue requestQueue) {
        requestQueue.cancelAll(this.getTag());
        requestQueue.add(this);
    }
}
