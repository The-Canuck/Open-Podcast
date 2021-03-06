package com.the_canuck.openpodcast.download;

import android.app.DownloadManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.webkit.MimeTypeMap;

import com.the_canuck.openpodcast.Episode;
import com.the_canuck.openpodcast.R;
import com.the_canuck.openpodcast.fragments.settings.PreferenceKeys;
import com.the_canuck.openpodcast.misc_helpers.StringHelper;

import org.apache.commons.io.FilenameUtils;

import java.io.File;


public class DownloadHelper {

    public final static String STATUS_FAILED = "STATUS_FAILED";
    public final static String STATUS_PAUSED = "STATUS_PAUSED";
    public final static String STATUS_PENDING = "STATUS_PENDING";
    public final static String STATUS_RUNNING = "STATUS_RUNNING";
    public final static String STATUS_SUCCESSFUL = "STATUS_SUCCESSFUL";

    private Episode episode;
    private Context context;
    private long enqueue;
    private DownloadManager downloadManager;
    private String path;

    public DownloadHelper() {
    }

    public DownloadHelper(Episode episode, Context context) {
        this.episode = episode;
        this.context = context;
    }

    /**
     * Downloads the specified podcast episode with download manager.
     *
     * @return the enqueue which is the unique id for the download.
     */
    public long downloadEpisode() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean downloadOverRoaming = prefs.getBoolean(context.getString
                (R.string.pref_network_roaming), false);
        int allowedNetworks = Integer.valueOf(prefs.getString(context.getString(R.string.pref_network_select_type), "3"));

        String mimeType = getMimeType(episode.getMediaUrl());

        // Replacing invalid characters was a quick and very dirty hack, will be encoded instead
//        title = title.replaceAll("/", " ");
//        title = title.replaceAll("#", " ");
        // Has been replaced with proper encoding now. Leaving this in just incase for now.

        String title = StringHelper.encodeFileName(episode.getTitle());

        path = File.separator + episode.getCollectionId() + File.separator
                + title + "."
                + FilenameUtils.getExtension(episode.getMediaUrl());

        downloadManager = (DownloadManager) context.getSystemService
                (Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(
                Uri.parse(episode.getMediaUrl()));
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_PODCASTS,
                path);

        request.setMimeType(mimeType);
        request.setTitle("Downloading " + episode.getTitle());
        request.setDescription("Downloading " + episode.getTitle());
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        request.setAllowedOverRoaming(downloadOverRoaming);

        // Allowed networks holds network pref which corresponds with Request.NETWORK_TYPE
//         3 doesn't exist as an entry but is a custom one to select either as allowed
        if (allowedNetworks == 3) {
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE);
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
        } else {
            request.setAllowedNetworkTypes(allowedNetworks);
        }


        enqueue = downloadManager.enqueue(request);
        setTempDownloadPref(enqueue);
        return enqueue;
    }

    /**
     * Sets the download id/enqueue as null to initialize the id key. For easy use later.
     *
     * @param id the long enqueue from downloadEpisode()
     */
    private void setTempDownloadPref(long id) {
        SharedPreferences prefs = context.getSharedPreferences(PreferenceKeys.PREF_DOWNLOADS,
                Context.MODE_PRIVATE);

        prefs.edit().putString(String.valueOf(id), "null").apply();
    }

    /**
     * Checks the status of the download.
     *
     * @return the status code for the download
     */
    public String getDownloadStatus(long id) {
        DownloadManager dm = (DownloadManager) context.getSystemService
                (Context.DOWNLOAD_SERVICE);
        Cursor cursor = dm.query(new DownloadManager.Query().setFilterById(id));
        String statusText = "";

        if (cursor.moveToFirst()) {
            int mStatus = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
            switch (mStatus) {
                case DownloadManager.STATUS_FAILED:
                    statusText = STATUS_FAILED;
                    break;
                case DownloadManager.STATUS_PAUSED:
                    statusText = STATUS_PAUSED;
                    break;
                case DownloadManager.STATUS_PENDING:
                    statusText = STATUS_PENDING;
                    break;
                case DownloadManager.STATUS_RUNNING:
                    statusText = STATUS_RUNNING;
                    break;
                case DownloadManager.STATUS_SUCCESSFUL:
                    statusText = STATUS_SUCCESSFUL;
                    break;
                default:
                    statusText = STATUS_FAILED;
                    break;
            }
        }
        return statusText;
    }

    /**
     * Gets the MIME type from the passed in url.
     *
     * @param url the file url being downloaded from
     * @return the MIME type as a String
     */
    private String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

    /**
     * Gets the path to the downloaded file (not including the Environment.DIRECTORY_PODCASTS).
     *
     * @return string of the file path
     */
    public String getPath() {
        return path;
    }

    public Uri getDownloadUri(long id) {
        return downloadManager.getUriForDownloadedFile(id);
    }

    public long getEnqueue() {
        return enqueue;
    }

    public DownloadHelper setEnqueue(long enqueue) {
        this.enqueue = enqueue;
        return this;
    }

    public DownloadHelper setContext(Context context) {
        this.context = context;
        return this;
    }
}
