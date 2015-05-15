/*
 * Copyright (c) Claes Holmerson 2013, 2015. The program is licensed under GNU GPL v3. See LICENSE.txt for details.
 */

package se.eliga.aves.birdlist;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import se.eliga.aves.R;
import se.eliga.aves.model.Taxon;
import se.eliga.aves.model.Bird;
import se.eliga.aves.model.DatabaseHandler;
import se.eliga.aves.model.Family;

/**
 * Created by Claes on 2013-07-20.
 */
class BirdListAdapter extends BaseAdapter implements SectionIndexer {

	private List<Bird> localBirdList = null;
	private Activity context;
	private DatabaseHandler databaseHandler;
	private Map<String, Bird> birds = new LinkedHashMap<String, Bird>();
	private List<Taxon> taxonList = new ArrayList<Taxon>(); // For optimization,
															// not so pretty
	private List<Family> familyList = new ArrayList<Family>();
	private SectionIndexer sectionIndexer;

	private boolean showBreeding = true;
	private boolean showBreedingUnclear = true;
	private boolean showMigrant = true;
	private boolean showRegularVisitor = true;
	private boolean showRare = true;
	private boolean showUnseen = false;
	private boolean showNonSpontaneous = false;
	private String filterString = null;

	private SortOption sortOption = SortOption.SWEDISH;

	public enum SortOption {

		SWEDISH("swedish"),
		ENGLISH("english"),
		SCIENTIFIC("latin"),
		PHYLOGENETIC("phylogenetic");

		private final String code;
		private static final Map<String,SortOption> valuesByCode;

		static {
			valuesByCode = new HashMap<String,SortOption>();
			for(SortOption vehicleType : SortOption.values()) {
				valuesByCode.put(vehicleType.code, vehicleType);
			}
		}

		private SortOption(String code) {
			this.code = code;
		}

		public static SortOption lookupByCode(String code) {
			return valuesByCode.get(code);
		}

		public String getCode() {
			return code;
		}
	}

	public BirdListAdapter(Activity context, DatabaseHandler databaseHandler) {
		this.context = context;
		this.databaseHandler = databaseHandler;
		refresh();
	}

	@Override
	public int getCount() {
		return taxonList.size();
	}

	@Override
	public Taxon getItem(int position) {
		return taxonList.get(position);
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		Taxon taxon = getItem(position);
		if (taxon instanceof Bird) {
			Bird bird = (Bird) taxon;
			BirdHolder holder = null;
			if (convertView == null
					|| convertView.getId() != R.layout.bird_row_layout) {
				holder = new BirdHolder();
				convertView = LayoutInflater.from(context).inflate(
						R.layout.bird_row_layout, parent, false);
				holder.latinSpecies = (TextView) convertView
						.findViewById(R.id.latinTextView);
				holder.swedishSpecies = (TextView) convertView
						.findViewById(R.id.swedishTextView);
				holder.englishSpecies = (TextView) convertView
						.findViewById(R.id.englishTextView);
				holder.image = (ImageView) convertView
						.findViewById(R.id.imageView);
				convertView.setTag(holder);
			} else {
				holder = (BirdHolder) convertView.getTag();
			}

			holder.latinSpecies.setText(bird.getLatinSpecies());
			holder.swedishSpecies.setText(bird.getSwedishSpecies());
			holder.englishSpecies.setText(bird.getEnglishSpecies());
			// context.imageLoader.displayImage("http://farm3.staticflickr.com/2096/2202224560_b4c2306e90_s.jpg",
			// holder.image);

			// imageView.
			// new
			// LoadPhotoOperation(imageView).execute(bird.getLatinSpecies());
		} else if (taxon instanceof Family) {
			Family family = (Family) taxon;
			FamilyHolder holder = null;
			if (convertView == null
					|| convertView.getId() != R.layout.family_row_layout) {
				holder = new FamilyHolder();
				convertView = LayoutInflater.from(context).inflate(
						R.layout.family_row_layout, parent, false);
				holder.swedishFamily = (TextView) convertView
						.findViewById(R.id.familyTextView);
				convertView.setTag(holder);
			} else {
				holder = (FamilyHolder) convertView.getTag();
			}
			holder.swedishFamily.setText(family.getFamily());
		}
		return convertView;
	}

	class BirdHolder {
		public ImageView image;
		public TextView latinSpecies;
		public TextView swedishSpecies;
		public TextView englishSpecies;
	}

	class FamilyHolder {
		public TextView swedishFamily;
	}

	public void refresh() {
		//if (localBirdList == null) {
			localBirdList = databaseHandler.getAllSpecies(filterString);
		//}

		switch (sortOption) {
		case SWEDISH:
			sectionIndexer = new SwedishSectionIndexer();
			Collections.sort(localBirdList, new SwedishBirdComparator());
			break;
		case ENGLISH:
			sectionIndexer = new EnglishSectionIndexer();
			Collections.sort(localBirdList, new EnglishBirdComparator());
			break;
		case SCIENTIFIC:
			sectionIndexer = new LatinSectionIndexer();
			Collections.sort(localBirdList, new LatinBirdComparator());
			break;
		case PHYLOGENETIC:
			sectionIndexer = new PhylogeneticSectionIndexer();
			Collections.sort(localBirdList, new PhylogeneticBirdComparator());
			break;
		}

		birds.clear();
		taxonList.clear();
		familyList.clear();

		for (Bird bird : localBirdList) {
			if ((showBreeding && Bird.Status.BREEDING.equals(bird.getStatus()))
					|| (showBreedingUnclear && Bird.Status.BREEDING_UNCLEAR
							.equals(bird.getStatus()))
					|| (showMigrant && Bird.Status.MIGRANT.equals(bird
							.getStatus()))
					|| (showRegularVisitor && Bird.Status.REGULAR_VISITOR
							.equals(bird.getStatus()))
					|| (showUnseen && Bird.Status.UNSEEN.equals(bird
							.getStatus()))
					|| (showNonSpontaneous && Bird.Status.NON_SPONTANEOUS
							.equals(bird.getStatus()))
					|| (showRare && Bird.Status.RARE.equals(bird.getStatus()))) {

				birds.put(bird.getLatinSpecies(), bird);

				if (SortOption.PHYLOGENETIC.equals(sortOption)) {
					Family family = new Family(bird.getSwedishOrder(),
							bird.getSwedishFamily());
					if (!familyList.contains(family)) {
						familyList.add(family);
						taxonList.add(new Family(bird.getSwedishOrder(), bird
								.getSwedishFamily()));
					}
				}
				taxonList.add(bird);
			}
		}
	}

	public SortOption getSortOption() {
		return sortOption;
	}

	public void setSortOption(SortOption sortOption) {
		this.sortOption = sortOption;
	}

	public boolean isShowBreeding() {
		return showBreeding;
	}

	public void setShowBreeding(boolean showBreeding) {
		this.showBreeding = showBreeding;
	}

	public boolean isShowBreedingUnclear() {
		return showBreedingUnclear;
	}

	public void setShowBreedingUnclear(boolean showBreedingUnclear) {
		this.showBreedingUnclear = showBreedingUnclear;
	}

	public boolean isShowMigrant() {
		return showMigrant;
	}

	public void setShowMigrant(boolean showMigrant) {
		this.showMigrant = showMigrant;
	}

	public boolean isShowRegularVisitor() {
		return showRegularVisitor;
	}

	public void setShowRegularVisitor(boolean showRegularVisitor) {
		this.showRegularVisitor = showRegularVisitor;
	}

	public boolean isShowRare() {
		return showRare;
	}

	public void setShowRare(boolean showRare) {
		this.showRare = showRare;
	}

	public boolean isShowUnseen() {
		return showUnseen;
	}

	public void setShowUnseen(boolean showUnseen) {
		this.showUnseen = showUnseen;
	}

	public boolean isShowNonSpontaneous() {
		return showNonSpontaneous;
	}

	public void setShowNonSpontaneous(boolean showNonSpontaneous) {
		this.showNonSpontaneous = showNonSpontaneous;
	}

	class PhylogeneticSectionIndexer implements SectionIndexer {

		@Override
		public Object[] getSections() {
			return new String[] {};
		}

		@Override
		public int getPositionForSection(int section) {
			return 0;
		}

		@Override
		public int getSectionForPosition(int position) {
			return 0;
		}
	}

	class SwedishSectionIndexer implements SectionIndexer {

		protected Object[] sections = new String[] { "A", "B", "C", "D", "E",
				"F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q",
				"R", "S", "T", "U", "V", "W", "X", "Y", "Z", "\u00C5", "\u00C4", "\u00D6" };
		
		private Collator collator = Collator.getInstance(new Locale("sv"));		

		@Override
		public Object[] getSections() {
			return sections;
		}

		@Override
		public int getPositionForSection(int section) {
			int i = 0;
			for (Taxon taxon : taxonList) {
				if (taxon instanceof Bird) {
					String firstChar = ((Bird) taxon).getSwedishSpecies().substring(0, 1);
					if ((section >= sections.length)
							|| (collator.compare(firstChar, sections[section]) >= 0)) {
						return i;
					}
				}
				i++;
			}
			return 0;
		}

		@Override
		public int getSectionForPosition(int position) {
			Taxon taxon = getItem(position);
			if (taxon instanceof Bird) {
				String firstChar = ((Bird) taxon).getSwedishSpecies()
						.substring(0, 1);
				for (int i = 0; i < sections.length; i++) {
					if (firstChar.equals(sections[i])) {
						return i;
					}
				}
			}
			return 0;
		}
	}

	class EnglishSectionIndexer implements SectionIndexer {

		protected Object[] sections = new String[] { "A", "B", "C", "D", "E",
				"F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q",
				"R", "S", "T", "U", "V", "W", "X", "Y", "Z" };

		protected Collator collator = Collator.getInstance(Locale.ENGLISH);		

		@Override
		public Object[] getSections() {
			return sections;
		}

		@Override
		public int getPositionForSection(int section) {
			int i = 0;
			for (Taxon taxon : taxonList) {
				if (taxon instanceof Bird) {
					String firstChar = ((Bird) taxon).getEnglishSpecies().substring(0, 1);
					if ((section >= sections.length)
							|| (collator.compare(firstChar, sections[section]) >= 0)) {
						return i;
					}
				}
				i++;
			}
			return 0;
		}

		@Override
		public int getSectionForPosition(int position) {
			Taxon taxon = getItem(position);
			if (taxon instanceof Bird) {
				String firstChar = ((Bird) taxon).getEnglishSpecies()
						.substring(0, 1);
				for (int i = 0; i < sections.length; i++) {
					if (firstChar.equals(sections[i])) {
						return i;
					}
				}
			}
			return 0;
		}
	}

	class LatinSectionIndexer extends EnglishSectionIndexer {

		@Override
		public int getPositionForSection(int section) {
			int i = 0;
			for (Taxon taxon : taxonList) {
				if (taxon instanceof Bird) {
					String firstChar = ((Bird) taxon).getLatinSpecies().substring(0, 1);
					if ((section >= sections.length)
							|| (collator.compare(firstChar, sections[section]) >= 0)) {
						return i;
					}
				}
				i++;
			}
			return 0;
		}

		@Override
		public int getSectionForPosition(int position) {
			Taxon taxon = getItem(position);
			if (taxon instanceof Bird) {
				String firstChar = ((Bird) taxon).getLatinSpecies().substring(
						0, 1);
				for (int i = 0; i < sections.length; i++) {
					if (firstChar.equals(sections[i])) {
						return i;
					}
				}
			}
			return 0;
		}
	}

	class SwedishBirdComparator implements Comparator<Bird> {

		private Collator collator = Collator.getInstance(new Locale("sv"));

		@Override
		public int compare(Bird lhs, Bird rhs) {
			return collator.compare(lhs.getSwedishSpecies(),
					rhs.getSwedishSpecies());
		}
	}

	class EnglishBirdComparator implements Comparator<Bird> {
		@Override
		public int compare(Bird lhs, Bird rhs) {
			return lhs.getEnglishSpecies().compareTo(rhs.getEnglishSpecies());
		}
	}

	class LatinBirdComparator implements Comparator<Bird> {
		@Override
		public int compare(Bird lhs, Bird rhs) {
			return lhs.getLatinSpecies().compareTo(rhs.getLatinSpecies());
		}
	}

	class PhylogeneticBirdComparator implements Comparator<Bird> {
		@Override
		public int compare(Bird lhs, Bird rhs) {
			return new Integer(lhs.getPhylogeneticSortId()).compareTo(rhs
					.getPhylogeneticSortId());
		}
	}

	@Override
	public Object[] getSections() {
		return sectionIndexer.getSections();
	}

	@Override
	public int getPositionForSection(int section) {
		return sectionIndexer.getPositionForSection(section);
	}

	@Override
	public int getSectionForPosition(int position) {
		return sectionIndexer.getSectionForPosition(position);
	}

	public String getFilterString() {
		return filterString;
	}

	public void setFilterString(String filterString) {
		this.filterString = filterString;
	}
}
