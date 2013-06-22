/**
 * 
 */
package org.hverfi.android;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.net.Uri;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

/**
 * @author eddie
 * 
 */
public class PointOfInterestLoader extends
		AsyncTaskLoader<PointOfInterestLoader.RESTResponse> {

	public static class RESTResponse {
		private int code;
		private String data;

		public RESTResponse() {
		}

		public RESTResponse(String data, int code) {
			this.data = data;
			this.code = code;
		}

		public int getCode() {
			return code;
		}

		public String getData() {
			return data;
		}
	}

	// 10 minutes between forced reloads
	private static final long STALE_DELTA = 600000;
	private static final String TAG = PointOfInterestLoader.class.getName();
	private long lastLoad = 0;

	private RESTResponse restResponse;
	private Uri actionUri;

	public PointOfInterestLoader(Context context, Uri actionUri) {
		super(context);
		this.actionUri = actionUri;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.content.Loader#deliverResult(java.lang.Object)
	 */
	@Override
	public void deliverResult(RESTResponse restResponse) {
		this.restResponse = restResponse;
		super.deliverResult(restResponse);
	}

	@Override
	public RESTResponse loadInBackground() {
        if (actionUri == null) {
            Log.e(TAG, "You did not define an action. REST call canceled.");
            return new RESTResponse(); 
        }
        HttpRequestBase request = new HttpGet();
        try {
			request.setURI(new URI(actionUri.toString()));
	        HttpClient client = new DefaultHttpClient();
	        HttpResponse response = client.execute(request);
	        
	        HttpEntity responseEntity = response.getEntity();
	        StatusLine responseStatus = response.getStatusLine();
	        int        statusCode     = responseStatus != null ? responseStatus.getStatusCode() : 0;
	        
	        // Here we create our response and send it back to the LoaderCallbacks<RESTResponse> implementation.
	        RESTResponse restResponse = new RESTResponse(responseEntity != null ? EntityUtils.toString(responseEntity) : null, statusCode);
	        return restResponse;
		} catch (URISyntaxException e) {
            Log.e(TAG, String.format("Invalid action URL: %s", actionUri.toString()));
            return new RESTResponse(); 
		} catch (ClientProtocolException e) {
            Log.e(TAG, String.format("Protocol error: %s", e.getMessage()));
            return new RESTResponse(); 
		} catch (IOException e) {
            Log.e(TAG, String.format("Communication error: %s", e.getMessage()));
            return new RESTResponse(); 
		}
	}

	/* (non-Javadoc)
	 * @see android.support.v4.content.Loader#onReset()
	 */
	@Override
	protected void onReset() {
		super.onReset();
		onStopLoading();
		restResponse = null;
		lastLoad = 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.content.Loader#onStartLoading()
	 */
	@Override
	protected void onStartLoading() {
		if (restResponse != null) {
			// We already have the data, move on with that
			super.deliverResult(restResponse);
		}
        if (restResponse == null || System.currentTimeMillis() - lastLoad >= STALE_DELTA) forceLoad();
        lastLoad = System.currentTimeMillis();

	}

	/* (non-Javadoc)
	 * @see android.support.v4.content.Loader#onStopLoading()
	 */
	@Override
	protected void onStopLoading() {
		cancelLoad();
	}

}
