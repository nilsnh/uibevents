package no.nilsnh.uibevents;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import no.nilsnh.uibevents.data.EventContract;

public class EventAdapter extends CursorAdapter {

    public static class ViewHolder {
        public final TextView dateFromView;
        public final TextView dateToView;
        public final TextView titleView;
        public final TextView categoryView;

        public ViewHolder(View view) {
            dateFromView = (TextView) view.findViewById(R.id.list_item_date_from_textview);
            dateToView = (TextView) view.findViewById(R.id.list_item_date_to_textview);
            titleView = (TextView) view.findViewById(R.id.list_item_title_textview);
            categoryView = (TextView) view.findViewById(R.id.list_item_category_textview);
        }
    }

    public EventAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_event, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        viewHolder.titleView.setText(cursor.getString(EventFragment.COL_EVENT_TITLE));
        viewHolder.dateFromView.setText(cursor.getString(EventFragment.COL_EVENT_DATE_FROM));
        viewHolder.dateToView.setText(cursor.getString(EventFragment.COL_EVENT_DATE_TO));
        viewHolder.categoryView.setText(cursor.getString(EventFragment.COL_EVENT_TYPE));
    }
}
