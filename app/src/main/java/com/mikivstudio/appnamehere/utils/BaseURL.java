package com.mikivstudio.appnamehere.utils;

import com.mikivstudio.appnamehere.BuildConfig;

public class BaseURL {
	//=======minute====
	public static final int loadInterstitialAdDuration = 10;
	public static final String MainServer = BuildConfig.SERVER_DOMAIN;
	public static final String HTTPS_thumbnail =MainServer+"/thumbnail/";
	public static final String HTTPS_skin =MainServer+"/skin/";

	public static String createThumbnailPath(String skinName) {
		return String.format("%s%s.png", HTTPS_thumbnail, skinName);
	}

	public static String createSkinPath(String skinName) {
		return String.format("%s%s.png", HTTPS_skin, skinName);
	}
}
