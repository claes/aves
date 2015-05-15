/*
 * Copyright (c) Claes Holmerson 2013, 2015. The program is licensed under GNU GPL v3. See LICENSE.txt for details.
 */

package se.eliga.aves.model;

import java.util.ArrayList;
import java.util.List;

import se.eliga.aves.photos.FlickrPhoto;
import se.eliga.aves.songs.XenoCantoAudio;

/**
 * Created by Claes on 2013-07-19.
 */
public class Bird implements Taxon {

    private int phylogeneticSortId;    
    private String latinOrder;
    private String latinFamily;
    private String latinSpecies;
    private String swedishOrder;
    private String swedishFamily;
    private String swedishSpecies;
    private String englishSpecies;
    
    private List<FlickrPhoto> photos;
    private List<XenoCantoAudio> audios;

    private Status status;
    
	public static enum Status {
		BREEDING("B"),
		BREEDING_UNCLEAR("b"),
		MIGRANT("M"),
		REGULAR_VISITOR("R"),
		RARE("S"),
		NON_SPONTANEOUS("I"),
		UNSEEN("U");		
		
		 private String text;

		  Status(String text) {
		    this.text = text;
		  }

		  public String getText() {
		    return this.text;
		  }

		  public static Status fromString(String text) {
		    if (text != null) {
		      for (Status b : Status.values()) {
		        if (text.equals(b.text)) {
		          return b;
		        }
		      }
		    }
		    return null;
		  }
		
	}
        
    public Bird() {
        photos = new ArrayList<FlickrPhoto>();
        audios = new ArrayList<XenoCantoAudio>();
    }

    public int getPhylogeneticSortId() {
		return phylogeneticSortId;
	}

	public void setPhylogeneticSortId(int phylogenicSortId) {
		this.phylogeneticSortId = phylogenicSortId;
	}

	public String getLatinSpecies() {
        return latinSpecies;
    }

    public String getSwedishSpecies() {
        return swedishSpecies;
    }

    public void setSwedishSpecies(String swedishSpecies) {
        this.swedishSpecies = swedishSpecies;
    }

    public String getEnglishSpecies() {
        return englishSpecies;
    }

    public void setEnglishSpecies(String englishSpecies) {
        this.englishSpecies = englishSpecies;
    }

    void setLatinSpecies(String latinSpecies) {
        this.latinSpecies = latinSpecies;
    }

    public List<FlickrPhoto> getPhotos() {
        return photos;
    }

    public void setPhotos(List<FlickrPhoto> photos) {
        this.photos = photos;
    }    
    
    public List<XenoCantoAudio> getAudios() {
		return audios;
	}

	public void setAudios(List<XenoCantoAudio> audios) {
		this.audios = audios;
	}

	public String getLatinOrder() {
		return latinOrder;
	}

	public void setLatinOrder(String latinOrder) {
		this.latinOrder = latinOrder;
	}

	public String getLatinFamily() {
		return latinFamily;
	}

	public void setLatinFamily(String latinFamily) {
		this.latinFamily = latinFamily;
	}

	public String getSwedishOrder() {
		return swedishOrder;
	}

	public void setSwedishOrder(String swedishOrder) {
		this.swedishOrder = swedishOrder;
	}

	public String getSwedishFamily() {
		return swedishFamily;
	}

	public void setSwedishFamily(String swedishFamily) {
		this.swedishFamily = swedishFamily;
	}
		
	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "Bird [phylogeneticSortId=" + phylogeneticSortId
				+ ", latinOrder=" + latinOrder + ", latinFamily=" + latinFamily
				+ ", latinSpecies=" + latinSpecies + ", swedishOrder="
				+ swedishOrder + ", swedishFamily=" + swedishFamily
				+ ", swedishSpecies=" + swedishSpecies + ", englishSpecies="
				+ englishSpecies + ", status=" + status + ", photos=" + photos
				+ "]";
	}


	
}


