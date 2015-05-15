/*
 * Copyright (c) Claes Holmerson 2013, 2015. The program is licensed under GNU GPL v3. See LICENSE.txt for details.
 */

package se.eliga.aves.songs;

import se.eliga.aves.model.License;

public class XenoCantoAudio implements Comparable<XenoCantoAudio> {
	private String xenoCantoIdNumber; 
	private String genus; 
	private String species; 
	private String english; 
	private String subspecies; 
	private String recordist; 
	private String country; 
	private String location; 
	private String latitude; 
	private String longitude; 
	private String songtype;
	private String audioURL;
	private License license;

	private boolean isPlaying = false;


	public XenoCantoAudio() {
		
	}
	
	public String getXenoCantoIdNumber() {
		return xenoCantoIdNumber;
	}
	public void setXenoCantoIdNumber(String xenoCantoIDumber) {
		this.xenoCantoIdNumber = xenoCantoIDumber;
	}
	public String getGenus() {
		return genus;
	}
	public void setGenus(String genus) {
		this.genus = genus;
	}
	public String getSpecies() {
		return species;
	}
	public void setSpecies(String species) {
		this.species = species;
	}
	public String getEnglish() {
		return english;
	}
	public void setEnglish(String english) {
		this.english = english;
	}
	public String getSubspecies() {
		return subspecies;
	}
	public void setSubspecies(String subspecies) {
		this.subspecies = subspecies;
	}
	public String getRecordist() {
		return recordist;
	}
	public void setRecordist(String recordist) {
		this.recordist = recordist;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public String getSongtype() {
		return songtype;
	}
	public void setSongtype(String songtype) {
		this.songtype = songtype;
	}
	public String getAudioURL() {
		return audioURL;
	}
	public void setAudioURL(String audioURL) {
		this.audioURL = audioURL;
	}

	public License getLicense() {
		return license;
	}

	public void setLicense(License license) {
		this.license = license;
	}

	public boolean isPlaying() {
		return isPlaying;
	}

	public void setIsPlaying(boolean isPlaying) {
		this.isPlaying = isPlaying;
	}

	@Override
	public int compareTo(XenoCantoAudio another) {
		
		return 0;
	}
	
}
