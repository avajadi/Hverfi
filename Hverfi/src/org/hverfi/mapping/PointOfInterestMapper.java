package org.hverfi.mapping;

import java.util.UUID;

import org.avajadi.json.JSONMapper;
import org.hverfi.domain.PointOfInterest;
import org.json.JSONException;
import org.json.JSONObject;

import android.location.Location;
import android.util.Log;

public class PointOfInterestMapper implements
		JSONMapper<PointOfInterest, JSONObject> {
	private ExceptionMapper exceptionMapper;
	private static final String MAPPER_FOR = PointOfInterest.class
			.getSimpleName();
	private static final String TAG = PointOfInterestMapper.class.getName();

	public PointOfInterestMapper() {
		exceptionMapper = new ExceptionMapper();
	}

	@Override
	public PointOfInterest mapFromJSON(JSONObject json) throws JSONException {
		Log.d(TAG, "MAPPER_FOR: " + MAPPER_FOR);
		if (json.getString("type").equals(MAPPER_FOR)) {
			Log.d( TAG,"Mapping POI");
			PointOfInterest poi = new PointOfInterest();
			poi.setId(UUID.fromString(json.getString("id")));
			poi.setDescription(json.optString("description"));
			poi.setName(json.getString("name"));
			// Assumes tags are delivered from the server as a comma delimited
			// list
			// After 1.0 they should come as a regular list
			poi.setTags(json.optString("tags").split(","));

			poi.setLocation(createLocationFromCoordinates(
					json.getDouble("longitude"), json.getDouble("latitude")));
			Log.d( TAG,"POI ready to return");
			return poi;
		}
		Exception e = exceptionMapper.mapFromJSON(json);
		throw new JSONException(String.format(
				"Server responded with Exception:'%s'", e.getMessage()));
	}

	@Override
	public JSONObject mapToJSON(PointOfInterest poi) throws JSONException {
		JSONObject json = new JSONObject();
		json.put("id", poi.getId().toString());
		json.put("description", poi.getDescription());
		json.put("name", poi.getName());
		// Assumes tags are delivered to the server as a comma delimited list
		// After 1.0 they should be sent as a regular list
		StringBuilder sb = new StringBuilder();
		for (String tag : poi.getTags()) {
			sb.append(tag);
			sb.append(',');
		}
		json.put("tags", sb.substring(0, sb.length() - 2));
		json.put("longitude", poi.getLocation().getLongitude());
		json.put("latitude", poi.getLocation().getLatitude());

		return json;
	}

	private Location createLocationFromCoordinates(double longitude,
			double latitude) {
		Location location = new Location("Hverfi");
		location.setLongitude(longitude);
		location.setLatitude(latitude);
		return location;
	}

}
