package net.holmerson.aves;

public class BirdSpeciesWikipediaEnglishFragment extends AbstractBirdSpeciesWikipediaFragment {

	@Override
	protected String getUrl(String latinSpecies, String englishSpecies) {
		if (englishSpecies == null) {
			englishSpecies = "Dipper";
		}
        String modifiedSpecies = englishSpecies.replaceAll(" ", "_");
		return "http://en.m.wikipedia.org/wiki/"+modifiedSpecies;
	}


}
