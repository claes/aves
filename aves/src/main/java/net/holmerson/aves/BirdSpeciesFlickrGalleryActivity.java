/*
 Copyright (c) 2012 Roman Truba

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 permit persons to whom the Software is furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or substantial
 portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH
 THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package net.holmerson.aves;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import ru.truba.touchgallery.GalleryWidget.BasePagerAdapter.OnItemChangeListener;
import ru.truba.touchgallery.GalleryWidget.GalleryViewPager;
import ru.truba.touchgallery.GalleryWidget.UrlPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import net.holmerson.aves.R;

public class BirdSpeciesFlickrGalleryActivity extends Activity {

    private GalleryViewPager mViewPager;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_layout);
//        birdRepository = ((BirdApplication) getApplication()).getBirdRepository();
//        final Bird bird = birdRepository.getBird(getIntent().getExtras().getString(MainActivity.LATIN_SPECIES));
//        UrlPagerAdapter pagerAdapter = new UrlPagerAdapter(this, FlickrLoader.retrieveUrls(bird.getPhotos(), FlickrPhoto.PX_640));
//        pagerAdapter.setOnItemChangeListener(new OnItemChangeListener() {
//            @Override
//            public void onItemChange(int currentPosition) {
//                Toast.makeText(BirdSpeciesFlickrGalleryActivity.this, (currentPosition + 1) + "/" + bird.getPhotos().size()
//                        + ": " + bird.getPhotos().get(currentPosition).getOwnerName(), Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        mViewPager = (GalleryViewPager) findViewById(R.id.viewer);
//        mViewPager.setOffscreenPageLimit(10);
//        mViewPager.setAdapter(pagerAdapter);

      //  new LoadPhotosOperation(this).execute(getIntent().getExtras().getString(MainActivity.LATIN_SPECIES));
    }
    /*

    private class LoadPhotosOperation extends AsyncTask<String, Void, List<FlickrPhoto>> {

        Activity context;

        public LoadPhotosOperation(Activity context) {
            this.context = context;
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
                    photos.add(photo.get640pxUrl());
                }

                UrlPagerAdapter pagerAdapter = new UrlPagerAdapter(context, photos);
                pagerAdapter.setOnItemChangeListener(new OnItemChangeListener() {
                    @Override
                    public void onItemChange(int currentPosition) {
                        Toast.makeText(BirdSpeciesFlickrGalleryActivity.this, (currentPosition + 1) + "/" + photos.size() + ": " + result.get(currentPosition).getOwnerName(), Toast.LENGTH_SHORT).show();
                    }
                });

                mViewPager = (GalleryViewPager) findViewById(R.id.viewer);
                mViewPager.setOffscreenPageLimit(10);
                mViewPager.setAdapter(pagerAdapter);
            }
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected void onProgressUpdate(Void... values) {
        }
    }
    */
}