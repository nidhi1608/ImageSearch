package com.example.imagesearch;

import android.content.Context;
import android.content.SearchRecentSuggestionsProvider;
import android.provider.SearchRecentSuggestions;

public class ImagesRecentSearchSuggestions extends SearchRecentSuggestionsProvider {
	public ImagesRecentSearchSuggestions() {
		setupSuggestions(ImagesRecentSearchSuggestions.class.getName(), DATABASE_MODE_QUERIES);
	}
	
	public static SearchRecentSuggestions createSearchRecentSuggestions(final Context context) {
		return new SearchRecentSuggestions(context, ImagesRecentSearchSuggestions.class.getName(), DATABASE_MODE_QUERIES);
	}
}
