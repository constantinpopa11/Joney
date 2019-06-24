package it.unitn.disi.joney;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FeedbackListAdapter extends ArrayAdapter<Feedback> {
    private Context mContext;
    private List<Feedback> feedbacksList = new ArrayList<>();

    DatabaseHandler db;
    UserProfileImage upi;

    public FeedbackListAdapter(@NonNull Context context, ArrayList<Feedback> list) {
        super(context, 0 , list);
        mContext = context;
        feedbacksList = list;
        db = new DatabaseHandler(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.item_feedback,parent,false);

        Feedback currentFeedback = feedbacksList.get(position);
        CircularImageView ivUserImage;
        TextView tvUserName, tvDate, tvComment;
        RatingBar rbRate;

        ivUserImage = (CircularImageView) listItem.findViewById(R.id.feedback_user_image);
        tvUserName = (TextView) listItem.findViewById(R.id.feedback_user_name);
        tvDate = (TextView) listItem.findViewById(R.id.feedback_date);
        tvComment = (TextView) listItem.findViewById(R.id.feedback_comment);
        rbRate = (RatingBar) listItem.findViewById(R.id.feedback_rating);

        User user = db.getUserById(currentFeedback.getAuthorId());

        // Insert the profile image from the URL into the ImageView.
        upi = db.getUserProfileImage(user.getId());
        if (upi != null) {
            File imgFile = new File(upi.getSource());
            if (imgFile.exists()) {
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                ivUserImage.setImageBitmap(myBitmap);
            }
        }

        tvUserName.setText(user.getFirstName() + " " + user.getLastName());
        tvDate.setText(currentFeedback.dateToString());
        tvComment.setText(currentFeedback.getComment());
        rbRate.setRating((float)currentFeedback.getRating());
        /*switch (currentFeedback.getRating())
        {
            case 1:
            case 2: listItem.setBackgroundColor(Color.parseColor("#ff8484")); break;
            case 3: listItem.setBackgroundColor(Color.parseColor("#fff59b")); break;
            case 4:
            default: listItem.setBackgroundColor(Color.parseColor("#bdffaa")); break;
        }*/

        return listItem;
    }
}
