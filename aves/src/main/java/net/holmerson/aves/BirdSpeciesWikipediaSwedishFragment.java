package net.holmerson.aves;

public class BirdSpeciesWikipediaSwedishFragment extends AbstractBirdSpeciesWikipediaFragment {

	@Override
	protected String getUrl(String latinSpecies, String englishSpecies) {
        String modifiedSpecies = latinSpecies.replaceAll(" ", "_");
		return "http://sv.m.wikipedia.org/wiki/"+modifiedSpecies;
	}

}
