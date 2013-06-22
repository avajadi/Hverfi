package org.hverfi.factory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.hverfi.domain.PointOfInterest;

public class PointOfInterestFactory {

	private static final Map<UUID, PointOfInterest> poiMap = new HashMap<UUID, PointOfInterest>();

	public static void add(PointOfInterest poi) {
		poiMap.put(poi.getId(), poi);
	}

	public static PointOfInterest get(String id) {
		return poiMap.get(UUID.fromString(id));
	}

	public static PointOfInterest get(UUID id) {
		return poiMap.get(id);
	}

	public static PointOfInterest remove(UUID id) {
		return poiMap.remove(id);
	}

	public static PointOfInterest remove(String id) {
		return poiMap.remove(UUID.fromString(id));
	}
}
