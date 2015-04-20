/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package no.nilsnh.uibevents;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();
    static final String DETAIL_URI = "URI";
    private Uri mUri;
    private static final int DETAIL_LOADER = 0;

    static final int COL_EVENT_ID = 0;
    static final int COL_EVENT_API_ID = 1;
    static final int COL_EVENT_TYPE = 2;
    static final int COL_EVENT_TITLE = 3;
    static final int COL_EVENT_DATE_FROM = 4;
    static final int COL_EVENT_DATE_TO = 5;
    static final int COL_EVENT_LOCATION = 6;
    static final int COL_EVENT_DETAILS = 7;
    static final int COL_EVENT_URL = 8;


    private TextView titleView;
    private TextView categoryView;
    private TextView detailView;
    private TextView dateFromView;
    private TextView dateToView;

    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(DetailFragment.DETAIL_URI);
        }
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        titleView = (TextView) rootView.findViewById(R.id.list_item_title_textview);
        categoryView = (TextView) rootView.findViewById(R.id.list_item_category_textview);
        detailView = (TextView) rootView.findViewById(R.id.list_item_details_textview);
        dateFromView = (TextView) rootView.findViewById(R.id.list_item_date_from_textview);
        dateToView = (TextView) rootView.findViewById(R.id.list_item_date_to_textview);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if ( null != mUri ) {
            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    null,
                    null,
                    null,
                    null
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            titleView.setText(data.getString(COL_EVENT_TITLE));
            categoryView.setText(data.getString(COL_EVENT_TYPE));
            detailView.setText(data.getString(COL_EVENT_DETAILS));
            dateFromView.setText(data.getString(COL_EVENT_DATE_FROM));
            dateToView.setText(data.getString(COL_EVENT_DATE_TO));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) { }
}