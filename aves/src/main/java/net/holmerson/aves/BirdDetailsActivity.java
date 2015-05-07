package net.holmerson.aves;

import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

public class BirdDetailsActivity extends FragmentActivity {

	BirdDetailsPageAdapter pageAdapter;

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bird_details_layout);
        
//        TextView copyright = (TextView) findViewById(R.id.meta_details_copyright);
//        TextView species = (TextView) findViewById(R.id.meta_details_species);
//        TextView license = (TextView) findViewById(R.id.meta_details_license);
//        
//        final String latinSpecies = getIntent().getExtras().getString(MainActivity.LATIN_SPECIES);
//        final String englishSpecies = getIntent().getExtras().getString(MainActivity.ENGLISH_SPECIES);
//        species.setText(latinSpecies);
//        
//        
//    	List<Fragment> fragments = new ArrayList<Fragment>();
//    	fragments.add(BirdSpeciesFlickrGalleryFragment.newInstance(latinSpecies));
//    	fragments.add(AbstractBirdSpeciesWikipediaFragment.newInstance(englishSpecies));
//    	fragments.add(BirdSpeciesXenoCantoPlayerFragment.newInstance(latinSpecies));

        
//        pageAdapter = new BirdDetailsPageAdapter(getSupportFragmentManager(), fragments);
        
        ViewPager pager = (ViewPager)findViewById(R.id.viewpager);
        pager.setAdapter(pageAdapter);
        
    }

    private class BirdDetailsPageAdapter extends FragmentPagerAdapter {

    	private List<Fragment> fragments;

        public BirdDetailsPageAdapter(FragmentManager fragmentManager, List<Fragment> fragments) {
            super(fragmentManager);
            this.fragments = fragments;
        }
        @Override
        public Fragment getItem(int position) {
            return this.fragments.get(position);
        }
     
        @Override
        public int getCount() {
            return this.fragments.size();
        }
        
        @Override
        public CharSequence getPageTitle(int position) {            
            switch (position) {
            case 0:
                return "Flickr";
            case 1:
                return "Wikipedia";
            case 2:
                return "xeno-canto";
            }
            return null;
        }
    }
    
    
//    
//    public static class TabListener<T extends Fragment> implements ActionBar.TabListener {
//        private Fragment mFragment;
//        private final Activity mActivity;
//        private final String mTag;
//        private final Class<T> mClass;
//
//        /** Constructor used each time a new tab is created.
//          * @param activity  The host Activity, used to instantiate the fragment
//          * @param tag  The identifier tag for the fragment
//          * @param clz  The fragment's Class, used to instantiate the fragment
//          */
//        public TabListener(Activity activity, String tag, Class<T> clz) {
//            mActivity = activity;
//            mTag = tag;
//            mClass = clz;
//        }
//
//        /* The following are each of the ActionBar.TabListener callbacks */
//
//        public void onTabSelected(Tab tab, FragmentTransaction ft) {
//            // Check if the fragment is already initialized
//            if (mFragment == null) {
//                // If not, instantiate and add it to the activity
//                mFragment = Fragment.instantiate(mActivity, mClass.getName());
//                ft.add(android.R.id.content, mFragment, mTag);
//            } else {
//                // If it exists, simply attach it in order to show it
//                ft.attach(mFragment);
//            }
//        }
//
//        public void onTabUnselected(Tab tab, FragmentTransaction ft) {
//            if (mFragment != null) {
//                // Detach the fragment, because another one is being attached
//                ft.detach(mFragment);
//            }
//        }
//
//        public void onTabReselected(Tab tab, FragmentTransaction ft) {
//            // User selected the already selected tab. Usually do nothing.
//        }
//    }    
    
}