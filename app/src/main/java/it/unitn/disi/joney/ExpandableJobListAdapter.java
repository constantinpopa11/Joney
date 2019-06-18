package it.unitn.disi.joney;

import java.util.List;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ExpandableJobListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<Job> jobs;
    int tab;

    public ExpandableJobListAdapter(Context context, List<Job> jobs, int tab) {
        this.context = context;
        this.jobs = jobs;
        this.tab = tab;
    }

    @Override
    public Object getChild(int listPosition, int expandedListPosition) {
        return this.jobs.get(listPosition);
    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return 0;
    }

    @Override
    public View getChildView(int listPosition, final int expandedListPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final Job job = (Job) getChild(listPosition, expandedListPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_job_item_details, null);
        }
        TextView tvJobDescription = (TextView) convertView.findViewById(R.id.tv_job_description);
        tvJobDescription.setText(job.getDescription());

        TextView tvJobLocation = (TextView) convertView.findViewById(R.id.tv_job_location);
        //String addressLatLon = String.valueOf(job.getLatitude()) + "," + String.valueOf(job.getLongitude());
        String address = Utils.getStreetName(job.getLatitude(),job.getLongitude(),context);
        tvJobLocation.setText(address);

        return convertView;
    }

    @Override
    public int getChildrenCount(int listPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int listPosition) {
        return this.jobs.get(listPosition);
    }

    @Override
    public int getGroupCount() {
        return this.jobs.size();
    }

    @Override
    public long getGroupId(int listPosition) {
        return listPosition;
    }

    @Override
    public View getGroupView(int listPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        Job job = (Job) getGroup(listPosition);

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_job_item_header, null);
        }

        TextView tvJobTitle = (TextView) convertView.findViewById(R.id.tv_job_title);
        tvJobTitle.setTypeface(null, Typeface.BOLD);
        tvJobTitle.setText(job.getTitle());

        TextView tvJobCategory = (TextView) convertView.findViewById(R.id.tv_job_category);
        tvJobCategory.setText(job.getJobCategory().getName());


        if(tab == Constants.POSTED_JOB_TAB){
            LinearLayout llStatus = (LinearLayout) convertView.findViewById(R.id.ll_status);
            llStatus.setVisibility(View.GONE);

            TextView tvNewCandidates = (TextView) convertView.findViewById(R.id.tv_new_candidates);
            tvNewCandidates.setText("3 new candidates");

        } else {
            LinearLayout llNewCandidates = (LinearLayout) convertView.findViewById(R.id.ll_new_candidates);
            llNewCandidates.setVisibility(View.GONE);

            TextView tvStatus = (TextView) convertView.findViewById(R.id.tv_status);
            ImageView ivStatus = (ImageView) convertView.findViewById(R.id.iv_status);

            if(job.isCompleted()) {
                ivStatus.setBackground(context.getDrawable(R.drawable.ic_check));
                tvStatus.setText("Completed");
            } else {
                ivStatus.setBackground(context.getDrawable(R.drawable.ic_times));
                tvStatus.setText("Not completed");
            }
        }

        TextView tvCreatedAt = (TextView) convertView.findViewById(R.id.tv_created_at);
        tvCreatedAt.setText(job.getCreatedAt());

        TextView tvAuthor = (TextView) convertView.findViewById(R.id.tv_author);
        tvAuthor.setText(job.getAuthor().getFirstName() + " " + job.getAuthor().getLastName());


        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
        return false;
    }
}