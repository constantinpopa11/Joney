package it.unitn.disi.joney;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.mapbox.android.gestures.Utils;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MessageListAdapter extends RecyclerView.Adapter {
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;
    private static final int VIEW_TYPE_DATE = 3;

    private Context mContext;
    private ArrayList<Message> mMessageList;
    private int currentUserId;
    private int otherUserId;

    DatabaseHandler db;
    UserProfileImage upi;


    public MessageListAdapter(Context context, ArrayList<Message> messageList,int currentUserId, int otherUserId) {
        mContext = context;
        mMessageList = messageList;
        this.currentUserId = currentUserId;
        this.otherUserId = otherUserId;
        db = new DatabaseHandler(mContext);
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    // Determines the appropriate ViewType according to the sender of the message.
    @Override
        public int getItemViewType(int position) {
        Message message = (Message) mMessageList.get(position);

        /*if(position == 0 || message.isDifferentDate(mMessageList.get(position-1)))
            return VIEW_TYPE_DATE;*/

        if (message.getSenderId() == currentUserId) {
            // If the current user is the sender of the message
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            // If some other user sent the message
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    // Inflates the appropriate layout according to the ViewType.
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_sent, parent, false);
            return new SentMessageHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_received, parent, false);
            return new ReceivedMessageHolder(view);
        } /*else if (viewType == VIEW_TYPE_DATE) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_date, parent, false);
            return new DateMessageHolder(view);
        }*/

        return null;
    }

    // Passes the message object to a ViewHolder so that the contents can be bound to UI.
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        Message message = (Message) mMessageList.get(position);
        //Log.d("Position",String.valueOf(position));
        boolean nextIsReceived = false, previousIsDifferentDate = false;
        if(position < mMessageList.size()-1)
            nextIsReceived = mMessageList.get(position + 1).getSenderId() == mMessageList.get(position).getSenderId();
        if(position == 0)
            previousIsDifferentDate = true;
        else
            previousIsDifferentDate = message.isDifferentDate(mMessageList.get(position-1));

        //Log.d("Next is received",String.valueOf(nextIsReceived));
        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(message,previousIsDifferentDate);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(message,nextIsReceived,previousIsDifferentDate);
                /*break;
            case VIEW_TYPE_DATE:
                ((DateMessageHolder) holder).bind(message);*/
        }
    }

    private class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText, dateText;
        LinearLayout dateLayout;

        SentMessageHolder(View itemView) {
            super(itemView);

            messageText = (TextView) itemView.findViewById(R.id.text_message_body);
            timeText = (TextView) itemView.findViewById(R.id.text_message_time);
            dateText = (TextView) itemView.findViewById(R.id.text_message_date);
            dateLayout = (LinearLayout) itemView.findViewById(R.id.date_layout);
        }

        void bind(Message message, boolean previousIsDifferentDate) {
            messageText.setText(message.getMessage());

            // Format the stored timestamp into a readable String using method.
            timeText.setText(formatDate(message.getDate()));

            dateText.setText(message.dateToString());
            if(!previousIsDifferentDate)
                dateLayout.setVisibility(View.GONE);
        }
    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText, nameText, dateText;
        ImageView profileImage;
        LinearLayout dateLayout;

        ReceivedMessageHolder(View itemView) {
            super(itemView);

            messageText = (TextView) itemView.findViewById(R.id.text_message_body);
            timeText = (TextView) itemView.findViewById(R.id.text_message_time);
            nameText = (TextView) itemView.findViewById(R.id.text_message_name);
            profileImage = (ImageView) itemView.findViewById(R.id.image_message_profile);
            dateText = (TextView) itemView.findViewById(R.id.text_message_date);
            dateLayout = (LinearLayout) itemView.findViewById(R.id.date_layout);
        }

        void bind(Message message, boolean nextIsReceived, boolean previousIsDifferentDate) {
            messageText.setText(message.getMessage());

            // Format the stored timestamp into a readable String using method.
            timeText.setText(formatDate(message.getDate()));

            dateText.setText(message.dateToString());
            if(!previousIsDifferentDate)
                dateLayout.setVisibility(View.GONE);

            if(nextIsReceived) {
                nameText.setVisibility(View.GONE);
                profileImage.setVisibility(View.GONE);
            }
            else
            {
                nameText.setText(db.getUserById(otherUserId).getFirstName());

                // Insert the profile image from the URL into the ImageView.
                upi = db.getUserProfileImage(otherUserId);
                if (upi != null) {
                    File imgFile = new File(upi.getSource());
                    if (imgFile.exists()) {
                        Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                        profileImage.setImageBitmap(myBitmap);
                    }
                }
            }
        }
    }

    /*private class DateMessageHolder extends RecyclerView.ViewHolder {
        TextView dateMessage;

        DateMessageHolder(View itemView) {
            super(itemView);

            dateMessage = (TextView) itemView.findViewById(R.id.text_message_date);
        }

        void bind(Message message) {
            dateMessage.setText(message.dateToString());
        }
    }*/

    private String formatDate(String date)
    {
        //take HH:mm from message date
        return date.substring(11,16);
    }

    public void addMessage(Message message)
    {
        mMessageList.add(message);
    }
}
