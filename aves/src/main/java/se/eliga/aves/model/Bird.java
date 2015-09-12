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
	private String spcRecid;
	private String dyntaxaTaxonId;
	private RedlistCategory swedishRedlistCategory;
	private int minPopulationEstimate;
	private int maxPopulationEstimate;
	private int bestPopulationEstimate;
	private PopulationUnit populationUnit;
	private String populationType;



    private List<FlickrPhoto> photos;
    private List<XenoCantoAudio> audios;

    private SofStatus sofStatus;


	public static enum PopulationUnit {

		PAIRS("P"),
		MALES("males"),
		CALLING_MALES("cmales"),
		BREEDING_FEMALES("bfemales"),
		INDIVIDUALS("i");

		private String text;

		PopulationUnit(String text) {
			this.text = text;
		}

		public String getText() {
			return this.text;
		}

		public static PopulationUnit fromString(String text) {
			if (text != null) {
				for (PopulationUnit b : PopulationUnit.values()) {
					if (text.equals(b.text)) {
						return b;
					}
				}
			}
			return null;
		}

	}

	public static enum RedlistCategory {
		EXTINCT("EX"),
		EXTINCT_IN_THE_WILD("EW"),
		REGIONALLY_EXTINCT("RE"),
		CRITICALLY_ENDANGERED("CR"),
		CRITICALLY_ENDANGERED_POSSIBLY_EXTINCT("CR (PE)"),
		CRITICALLY_ENDANGERED_POSSIBLY_EXTINCT_IN_THE_WILD("CR (PEW)"),
		ENDANGERED("EN"),
		VULNERABLE("VU"),
		NEAR_THREATENED("NT"),
		LEAST_CONCERN("LC"),
		DATA_DEFICIENT("DD"),
		NOT_APPLICABLE("NA"),
		NOT_EVALUATED("NE");

		private String text;

		RedlistCategory(String text) {
			this.text = text;
		}

		public String getText() {
			return this.text;
		}

		public static RedlistCategory fromString(String text) {
			if (text != null) {
				for (RedlistCategory b : RedlistCategory.values()) {
					if (text.equals(b.text)) {
						return b;
					}
				}
			}
			return RedlistCategory.DATA_DEFICIENT;
		}

	}

	public static enum SofStatus {
		BREEDING("B"),
		BREEDING_UNCLEAR("b"),
		MIGRANT("M"),
		REGULAR_VISITOR("R"),
		RARE("S"),
		NON_SPONTANEOUS("I"),
		UNSEEN("U"), //never seen in Sweden
		UNCLASSIFIED("W"); //not classified as part of Western Palearctis

		private String text;

		SofStatus(String text) {
		    this.text = text;
		  }

		  public String getText() {
		    return this.text;
		  }

		  public static SofStatus fromString(String text) {
		    if (text != null) {
		      for (SofStatus b : SofStatus.values()) {
		        if (text.equals(b.text)) {
		          return b;
		        }
		      }
		    }
			return SofStatus.UNCLASSIFIED;
		  }
		
	}
        
    public Bird() {
        photos = new ArrayList<FlickrPhoto>();
        audios = new ArrayList<XenoCantoAudio>();
    }

	public Bird(String latinSpecies) {
		this.latinSpecies = latinSpecies;
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
		
	public SofStatus getSofStatus() {
		return sofStatus;
	}

	public void setSofStatus(SofStatus sofStatus) {
		this.sofStatus= sofStatus;
	}

	public String getSpcRecid() {
		return spcRecid;
	}

	public void setSpcRecid(String spcRecid) {
		this.spcRecid = spcRecid;
	}

	public String getDyntaxaTaxonId() {
		return dyntaxaTaxonId;
	}

	public void setDyntaxaTaxonId(String dyntaxaTaxonId) {
		this.dyntaxaTaxonId = dyntaxaTaxonId;
	}

	public RedlistCategory getSwedishRedlistCategory() {
		return swedishRedlistCategory;
	}

	public void setSwedishRedlistCategory(RedlistCategory swedishRedlistCategory) {
		this.swedishRedlistCategory = swedishRedlistCategory;
	}

	public String getPopulationType() {
		return populationType;
	}

	public void setPopulationType(String populationType) {
		this.populationType = populationType;
	}

	public int getMaxPopulationEstimate() {
		return maxPopulationEstimate;
	}

	public void setMaxPopulationEstimate(int maxPopulationEstimate) {
		this.maxPopulationEstimate = maxPopulationEstimate;
	}

	public int getBestPopulationEstimate() {
		return bestPopulationEstimate;
	}

	public void setBestPopulationEstimate(int bestPopulationEstimate) {
		this.bestPopulationEstimate = bestPopulationEstimate;
	}

	public PopulationUnit getPopulationUnit() {
		return populationUnit;
	}

	public void setPopulationUnit(PopulationUnit populationUnit) {
		this.populationUnit = populationUnit;
	}

	public int getMinPopulationEstimate() {
		return minPopulationEstimate;
	}

	public void setMinPopulationEstimate(int minPopulationEstimate) {
		this.minPopulationEstimate = minPopulationEstimate;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Bird bird = (Bird) o;

		return !(latinSpecies != null ? !latinSpecies.equals(bird.latinSpecies) : bird.latinSpecies != null);

	}

	@Override
	public int hashCode() {
		return latinSpecies != null ? latinSpecies.hashCode() : 0;
	}

	@Override
	public String toString() {
		return "Bird [phylogeneticSortId=" + phylogeneticSortId
				+ ", latinOrder=" + latinOrder + ", latinFamily=" + latinFamily
				+ ", latinSpecies=" + latinSpecies + ", swedishOrder="
				+ swedishOrder + ", swedishFamily=" + swedishFamily
				+ ", swedishSpecies=" + swedishSpecies + ", englishSpecies="
				+ englishSpecies + ", sofStatus=" + sofStatus + ", photos=" + photos
				+ "]";
	}


	
}


