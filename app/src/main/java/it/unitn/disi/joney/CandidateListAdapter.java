package it.unitn.disi.joney;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CandidateListAdapter extends ArrayAdapter<JobCandidate> {

    private Context mContext;
    private List<JobCandidate> candidateList;
    DatabaseHandler db;
    int currentUserId;

    public CandidateListAdapter(@NonNull Context context, List<JobCandidate> list) {
        super(context, 0 , list);
        mContext = context;
        candidateList = list;
        db = new DatabaseHandler(mContext);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        currentUserId = prefs.getInt(Constants.PREF_CURRENT_USER_ID, Constants.NO_USER_LOGGED_IN);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.item_candidate,parent,false);

        final JobCandidate jobCandidate = candidateList.get(position);

        ImageView userProfileImage = (ImageView)listItem.findViewById(R.id.iv_user_profile_image);
        UserProfileImage upi = db.getUserProfileImage(jobCandidate.getCandidateId());
        if (upi != null) {
            File imgFile = new File(upi.getSource());
            if (imgFile.exists()) {
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                userProfileImage.setImageBitmap(myBitmap);
            }
        }

        float averageRating = Utils.getUserAverageRating(mContext, jobCandidate.getCandidateId());
        RatingBar rbAverageRating = (RatingBar) listItem.findViewById(R.id.rb_rating_average);
        rbAverageRating.setRating(averageRating);


        LinearLayout llChat = (LinearLayout) listItem.findViewById(R.id.ll_chat);
        llChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intChat = new Intent(mContext, ChatActivity.class);
                intChat.putExtra("senderId", currentUserId);
                intChat.putExtra("receiverId", jobCandidate.getCandidateId());
                //Log.d("ID",String.valueOf(jobCandidate.getCandidateId()));
                mContext.startActivity(intChat);
            }
        });

        User candidate = db.getUserById(jobCandidate.getCandidateId());
        TextView tvCandidateName = (TextView)listItem.findViewById(R.id.tv_user_name);
        tvCandidateName.setText(candidate.getFirstName() + " " + candidate.getLastName());

        Button btnAccept = (Button)listItem.findViewById(R.id.btn_accept);
        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.updateJobStatus(jobCandidate.getJobId(), Constants.JOB_STATUS_AWAITING_COMPLETION);
                db.updateJobWorker(jobCandidate.getJobId(), jobCandidate.getCandidateId());

                //Toast.makeText(mContext, "Candidate accepted successfully", Toast.LENGTH_SHORT).show();

                Intent jobDetailIntent = new Intent(mContext, JobDetailActivity.class);
                jobDetailIntent.putExtra(Constants.JOB_ID_EXTRA, jobCandidate.getJobId());
                jobDetailIntent.putExtra(Constants.JOB_DETAIL_ACTIVITY_TYPE, Constants.POSTED_JOB_DETAILS);
                jobDetailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                mContext.startActivity(jobDetailIntent);
            }
        });

        return listItem;
    }
}