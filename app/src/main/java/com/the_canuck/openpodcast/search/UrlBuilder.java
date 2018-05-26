package com.the_canuck.openpodcast.search;


import com.the_canuck.openpodcast.search.enums.Queryable;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import okhttp3.HttpUrl;

public class UrlBuilder {
//    private String queryUrl = null;

    public UrlBuilder() {
    }

    /**
     * Encodes special characters not supported as query terms.
     *
     * @return encoded query terms
     */
    public String encodeQueryTerms(String query) {
        // Not 100% if this is useful. TODO: Test if this is needed.
        String encodedTerms = "";
        try {
            encodedTerms = URLEncoder.encode(query, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return encodedTerms;
    }

    /**
     * Creates a url using the itunes/apple search api, query terms, and other required params.
     *
     * @param query terms entered by user to search for
     * @return complete url for search terms on itunes
     */
    public String createQueryUrl(String query, boolean isGenre) {
        final String PODCAST = "podcast";
        final String LIMIT = "15";
        String APPLE_API_ENDPOINT = "https://itunes.apple.com/search?";
        HttpUrl.Builder builder = HttpUrl.parse(APPLE_API_ENDPOINT).newBuilder();

        if (!isGenre) {
            builder.addEncodedQueryParameter(Queryable.TERM.getValue(), query);
        } else {
            builder.addQueryParameter(Queryable.GENREID.getValue(), query);
            builder.addQueryParameter(Queryable.TERM.getValue(), PODCAST);
            builder.addQueryParameter(Queryable.LIMIT.getValue(),LIMIT);
        }

        builder.addEncodedQueryParameter(Queryable.MEDIA.getValue(), PODCAST);

        return builder.build().toString();
    }


    // TODO: Delete this later if above methods for sure work
//    public String buildQueryUrl(final String query, final boolean isGenre) {
//
//        String encodedTerms = "";
//        try {
//            final String PODCAST = "podcast";
//            String APPLE_API_ENDPOINT = "https://itunes.apple.com/search?";
//            HttpUrl.Builder builder = HttpUrl.parse(APPLE_API_ENDPOINT).newBuilder();
//
//            if (!isGenre) {
//                encodedTerms = URLEncoder.encode(query, "UTF-8");
//                builder.addEncodedQueryParameter(Queryable.TERM.getValue(), encodedTerms);
//            } else {
//                builder.addQueryParameter(Queryable.GENREID.getValue(), query);
//                builder.addQueryParameter(Queryable.TERM.getValue(), "podcast");
//            }
//
//            builder.addEncodedQueryParameter(Queryable.MEDIA.getValue(), PODCAST);
//
//            setQueryUrl(builder.build().toString());
//
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//
//        // TODO: Another bad hack probably, test more later before removing
//        while (getQueryUrl() == null) {
//            try {
//                Thread.sleep(100);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//        return queryUrl;
//    }
//
//    public String getQueryUrl() {
//        return queryUrl;
//    }
//
//    public UrlBuilder setQueryUrl(String queryUrl) {
//        this.queryUrl = queryUrl;
//        return this;
//    }
}
