package com.acmatics.securityguardexchange;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.acmatics.securityguardexchange.bean.RowItem;
import com.acmatics.securityguardexchange.common.GcmRegistrationConstant;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TrainingVideoAdapter extends BaseAdapter implements
		YouTubeThumbnailView.OnInitializedListener {

	private Context context;
	private List<RowItem> rowItems;
	private Map<View, YouTubeThumbnailLoader> mLoaders;

	TrainingVideoAdapter(Context context, List<RowItem> rowItems) {
		this.context = context;
		this.rowItems = rowItems;
		mLoaders = new HashMap<View, YouTubeThumbnailLoader>();
	}

	@Override
	public int getCount() {
		return rowItems.size();
	}

	@Override
	public Object getItem(int position) {
		return rowItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return rowItems.indexOf(getItem(position));
	}

	private class ViewHolder {
		TextView videoName;
		YouTubeThumbnailView thumbnail;
//		Button videoLyrics;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder = null;
		RowItem rowItem = (RowItem) rowItems.get(position);
		LayoutInflater mInflater = (LayoutInflater) context
				.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.training_video_items, null);
			holder = new ViewHolder();
			holder.thumbnail = (YouTubeThumbnailView) convertView
					.findViewById(R.id.thumbnail);
			holder.thumbnail.setTag(rowItem.getVideoId());
			holder.thumbnail.initialize(GcmRegistrationConstant.DEVELOPER_KEY, this);

			holder.videoName = (TextView) convertView.findViewById(R.id.videoName);

			holder.videoName.setText(rowItem.getVideoName());

//			holder.videoLyrics = (Button) convertView
//					.findViewById(R.id.lyrics_button);
//			holder.videoLyrics.setTag(position);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
			if (rowItem != null) {
				holder.videoName.setText(rowItem.getVideoName());
//				holder.videoLyrics.setTag(position);
			}

			YouTubeThumbnailLoader loader = mLoaders.get(holder.thumbnail);
			if (loader == null) {
				holder.thumbnail.setTag(rowItem.getVideoId());
			} else {
				holder.thumbnail.setImageResource(R.drawable.ic_loader);
				loader.setVideo(rowItem.getVideoId());
			}
		}

		return convertView;
	}

	@Override
	public void onInitializationFailure(YouTubeThumbnailView arg0,
			YouTubeInitializationResult arg1) {
	}

	@Override
	public void onInitializationSuccess(YouTubeThumbnailView view,
			YouTubeThumbnailLoader loader) {
		String videoId = (String) view.getTag();
		mLoaders.put(view, loader);
		view.setImageResource(R.drawable.ic_loader);
		loader.setVideo(videoId);
	}

}
