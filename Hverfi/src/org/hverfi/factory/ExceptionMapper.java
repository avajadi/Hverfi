package org.hverfi.factory;

import org.avajadi.json.JSONMapper;
import org.json.JSONException;
import org.json.JSONObject;

public class ExceptionMapper implements JSONMapper<Exception, JSONObject> {

	@Override
	public Exception mapFromJSON(JSONObject json) throws JSONException {
		if (json.getString("type").equals("Exception")) {
			return new Exception(json.getString("message"));
		}
		throw new JSONException(String.format("Unknown type in response: %s",
				json.getString("type")));
	}

	@Override
	public JSONObject mapToJSON(Exception e) throws JSONException {
		JSONObject json = new JSONObject();
		json.put("message", e.getMessage());
		return null;
	}

}
