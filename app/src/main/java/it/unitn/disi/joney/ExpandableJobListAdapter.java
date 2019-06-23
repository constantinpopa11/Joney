package it.unitn.disi.joney;

import java.io.File;
import java.util.List;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ExpandableJobListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<Job> jobs;
    int tab;
    int activityType = Constants.POSTED_JOB_DETAILS;

    DatabaseHandler db;

    public ExpandableJobListAdapter(Context context, List<Job> jobs, int tab) {
        this.context = context;
        this.jobs = jobs;
        this.tab = tab;
        db = new DatabaseHandler(context);
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

        User author = db.getUserById(job.getAuthorId());
        TextView tvAuthor = (TextView) convertView.findViewById(R.id.tv_job_author);
        tvAuthor.setText(author.getFirstName() + " " + author.getLastName());

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
    public View getGroupView(final int listPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        Job job = (Job) getGroup(listPosition);

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_job_item_header, null);
        }

        TextView tvJobTitle = (TextView) convertView.findViewById(R.id.tv_job_title);
        tvJobTitle.setText(job.getTitle());

        TextView tvJobCategory = (TextView) convertView.findViewById(R.id.tv_job_category);
        tvJobCategory.setText(job.getJobCategory().getName());

        ImageView ivMainPicture = (ImageView) convertView.findViewById(R.id.iv_job_picture);


        if (tab == Constants.POSTED_JOB_TAB) {
            TextView tvNewCandidates = (TextView) convertView.findViewById(R.id.tv_new_candidates);
            int candidatesNum = db.getCandidatesByJobId(job.getId()).size();
            if(candidatesNum > 0)
                tvNewCandidates.setText(candidatesNum + " new candidates");
            else {
                LinearLayout llCandidates = (LinearLayout) convertView.findViewById(R.id.ll_new_candidates);
                llCandidates.setVisibility(View.GONE);
            }

            activityType = Constants.POSTED_JOB_DETAILS;


        } else {

            LinearLayout llCandidates = (LinearLayout) convertView.findViewById(R.id.ll_new_candidates);
            llCandidates.setVisibility(View.GONE);

            if(tab == Constants.PENDING_JOB_TAB)
                activityType = Constants.PENDING_JOB_DETAILS;
            else if(tab == Constants.COMPLETED_JOB_TAB)
                activityType = Constants.COMPLETED_JOB_DETAILS;
            else if(tab == Constants.SEARCH_RESULT_TAB)
                activityType = Constants.JOB_RESULT_DETAILS;

        }

        List<JobImage> jobImages = db.getJobImages(job.getId());
        if (jobImages.size() > 0) {
            File imgFile = new File(jobImages.get(0).getSource());
            if (imgFile.exists()) {
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                ivMainPicture.setImageBitmap(myBitmap);
            }
        }

        final Intent jobDetailIntent = new Intent(context, JobDetailActivity.class);
        jobDetailIntent.putExtra(Constants.JOB_ID_EXTRA, job.getId());
        jobDetailIntent.putExtra(Constants.JOB_DETAIL_ACTIVITY_TYPE, activityType);

        ivMainPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(jobDetailIntent);
            }
        });

        LinearLayout llJobHeader = convertView.findViewById(R.id.ll_job_header);
        llJobHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.startActivity(jobDetailIntent);
            }
        });

        TextView tvPay = (TextView) convertView.findViewById(R.id.tv_pay);
        tvPay.setText("€ " + Integer.toString(job.getPay()));

        TextView tvStatus = (TextView) convertView.findViewById(R.id.tv_status);
        ImageView ivStatus = (ImageView) convertView.findViewById(R.id.iv_status);


        if (job.getStatus() == Constants.JOB_STATUS_AWAITING_CANDIDATES) {
            ivStatus.setBackground(context.getDrawable(R.drawable.ic_people));
            tvStatus.setText("Awaiting candidates");

        } else {

            LinearLayout llCandidates = (LinearLayout) convertView.findViewById(R.id.ll_new_candidates);
            llCandidates.setVisibility(View.GONE);

            if (job.getStatus() == Constants.JOB_STATUS_AWAITING_COMPLETION) {
                ivStatus.setBackground(context.getDrawable(R.drawable.ic_times));
                tvStatus.setText("In progress");
            } else {
                ivStatus.setBackground(context.getDrawable(R.drawable.ic_check));
                tvStatus.setText("Completed");
            }
        }

        TextView tvCreatedAt = (TextView) convertView.findViewById(R.id.tv_created_at);
        tvCreatedAt.setText(job.getCreatedAt());

        //TextView tvAuthor = (TextView) convertView.findViewById(R.id.tv_author);
        //tvAuthor.setText(job.getAuthor().getFirstName() + " " + job.getAuthor().getLastName());


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