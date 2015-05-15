package net.holmerson.aves;

import java.util.ArrayList;
import java.util.List;

import ru.truba.touchgallery.GalleryWidget.BasePagerAdapter.OnItemChangeListener;
import ru.truba.touchgallery.GalleryWidget.GalleryViewPager;
import ru.truba.touchgallery.GalleryWidget.UrlPagerAdapter;
import android.os.AsyncTask;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.TextView;

public class LoadPhotosOperation extends
		AsyncTask<String, Void, List<FlickrPhoto>> {

	private View view;

	public LoadPhotosOperation(View context) {
		this.view = context;
	}

	@Override
	protected List<FlickrPhoto> doInBackground(String... params) {
		try {
			return FlickrLoader.getPhotosForBirdSpecies(params[0], 10);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onPostExecute(final List<FlickrPhoto> result) {
		if (result != null) {
			final List<String> photos = new ArrayList<String>();
			int i = 0;
			for (FlickrPhoto photo : result) {
				if (i++ >= 10) {
					break;
				}
				if (photo.getLicense().isUsable()) {
					photos.add(photo.get640pxUrl());
				}
			}

			if (photos.size() > 0) {
				FrameLayout frameLayout = (FrameLayout) view.findViewById(R.id.gallery_frame_layout);
				final View meta = (View) frameLayout.findViewById(R.id.gallery_meta_layout);

				final GalleryViewPager galleryViewPager = (GalleryViewPager) view
						.findViewById(R.id.gallery_view_layout);
				galleryViewPager.setOffscreenPageLimit(10);

				UrlPagerAdapter pagerAdapter = new UrlPagerAdapter(
						view.getContext(), photos) {
					public void setPrimaryItem(ViewGroup container, int position, Object object) {
						super.setPrimaryItem(container, position, object);
						galleryViewPager.mCurrentView.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								if (meta.getVisibility() == View.GONE) {
									meta.setVisibility(View.VISIBLE);
								} else if (meta.getVisibility() == View.VISIBLE) {
									meta.setVisibility(View.GONE);
								}
							}
						});
					}
				};
				galleryViewPager.setAdapter(pagerAdapter);

				pagerAdapter
						.setOnItemChangeListener(new OnItemChangeListener() {
							@Override
							public void onItemChange(int currentPosition) {
								FlickrPhoto photo = result.get(currentPosition);
								((TextView) meta.findViewById(R.id.photoOwnerName)).setText("\u00A9 " + photo.getOwnerName());
								((TextView) meta.findViewById(R.id.photoTitle)).setText(photo.getTitle());
								((TextView) meta.findViewById(R.id.photoLicense)).setText("" + photo.getLicense());
							}
						});

		frameLayout.setVisibility(View.VISIBLE);
				
			} else {
				WebView webView = (WebView) view
						.findViewById(R.id.gallery_empty_sign);
				webView.loadUrl("file:///android_asset/flickr_species_missing.html");
				webView.setBackgroundColor(0x00FFFFFF); 
				webView.setVisibility(View.VISIBLE);	
			}
		}
	}

	@Override
	protected void onPreExecute() {
	}

	@Override
	protected void onProgressUpdate(Void... values) {
	}
}
