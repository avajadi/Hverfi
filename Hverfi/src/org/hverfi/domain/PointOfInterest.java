package org.hverfi.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.location.Location;

public class PointOfInterest {
	private String description;
	private UUID id;
	private Location location;
	private String name;
	private List<String> tags;

	public PointOfInterest() {
		tags = new ArrayList<String>();
	}

	public void addTag(String tag) {
		tags.add(tag);
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @return the id
	 */
	public UUID getId() {
		return id;
	}

	/**
	 * @return the location
	 */
	public Location getLocation() {
		return location;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the tags
	 */
	public List<String> getTags() {
		return tags;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(UUID id) {
		this.id = id;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @param tags
	 *            the tags to set
	 */
	public void setTags(String[] tags) {
		this.tags.clear();
		for (String tag : tags) {
			this.tags.add(tag);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getName();
	}
	public String content() {
		StringBuilder sb = new StringBuilder();
		sb.append(location.getLatitude());
		sb.append(',');
		sb.append(location.getLongitude());
		sb.append('\n');
		sb.append( getDescription());
		return sb.toString();
	}

}
