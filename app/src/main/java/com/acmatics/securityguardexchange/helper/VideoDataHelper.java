package com.acmatics.securityguardexchange.helper;


import com.acmatics.securityguardexchange.bean.RowItem;

import java.util.ArrayList;
import java.util.List;

public class VideoDataHelper {

	public static List<RowItem> getRowItems() {
		List<RowItem> rowItems = new ArrayList<>();
		RowItem rowItem = new RowItem();
		rowItem.setVideoId("_XSSogLCE_Q");
		rowItem.setVideoName("Fire Safety");
		rowItems.add(rowItem);

		rowItem = new RowItem();
		rowItem.setVideoId("WL_l94aYkdU");
		rowItem.setVideoName("Methods of Fire Fighting");
		rowItems.add(rowItem);

		rowItem = new RowItem();
		rowItem.setVideoId("T_AtR5haNkE");
		rowItem.setVideoName("Fire Extinguisher and Classification of Fire");
		rowItems.add(rowItem);

		rowItem = new RowItem();
		rowItem.setVideoId("mCUSTQ-tvFc");
		rowItem.setVideoName("Boom Barrier");
		rowItems.add(rowItem);

		rowItem = new RowItem();
		rowItem.setVideoId("vcQKhh7u0kc");
		rowItem.setVideoName("Security Bollard");
		rowItems.add(rowItem);




		return rowItems;
	}
}
