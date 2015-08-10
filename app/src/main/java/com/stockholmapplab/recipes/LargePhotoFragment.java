package com.stockholmapplab.recipes;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.ImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.stockholmapplab.recipes.R;

public class LargePhotoFragment extends Fragment {

	private ImageLoader mImgLoader;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final View fragmentView = inflater.inflate(R.layout.fr_large_photo,
				container, false);
		ImageView image = (ImageView) fragmentView.findViewById(R.id.detailsLargeImg);
		String imageLink = getArguments().getString("link");
		// initialise ImageLoader
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				getActivity()).build();
		mImgLoader = ImageLoader.getInstance();
		mImgLoader.init(config);
		mImgLoader.displayImage(imageLink, image, new ImageLoadingListener() {
			
			@Override
			public void onLoadingStarted(String imageUri, View view) {
				fragmentView.findViewById(R.id.detailsLargeProgressBar).setVisibility(View.VISIBLE);
			}
			
			@Override
			public void onLoadingFailed(String imageUri, View view,
					FailReason failReason) {
				fragmentView.findViewById(R.id.detailsLargeProgressBar).setVisibility(View.GONE);
			}
			
			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				fragmentView.findViewById(R.id.detailsLargeProgressBar).setVisibility(View.GONE);
				view.setVisibility(View.VISIBLE);
			}
			
			@Override
			public void onLoadingCancelled(String imageUri, View view) {
				fragmentView.findViewById(R.id.detailsLargeProgressBar).setVisibility(View.GONE);
			}
		});
		return fragmentView;
	}
}