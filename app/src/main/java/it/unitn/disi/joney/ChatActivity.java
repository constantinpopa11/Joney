package it.unitn.disi.joney;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView mMessageRecycler;
    private MessageListAdapter mMessageAdapter;
    ArrayList<Message> messageList;
    Button btnSendMessage;
    EditText etText;

    int senderId;
    int receiverId;

    DatabaseHandler db = new DatabaseHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        senderId = getIntent().getIntExtra("senderId",-1);
        receiverId = getIntent().getIntExtra("receiverId",-1);

        this.setTitle("Chat with " + db.getUserById(receiverId).getFirstName());

        messageList = db.getUserMessages(senderId,receiverId);
        //Toast.makeText(getApplicationContext(),"Total messages = " + String.valueOf(messageList.size()),Toast.LENGTH_SHORT).show();

        mMessageRecycler = (RecyclerView) findViewById(R.id.reyclerview_message_list);
        mMessageRecycler.setHasFixedSize(true);

        mMessageAdapter = new MessageListAdapter(this, messageList,senderId,receiverId);
        mMessageRecycler.setLayoutManager(new LinearLayoutManager(ChatActivity.this));
        mMessageRecycler.setAdapter(mMessageAdapter);
        mMessageRecycler.scrollToPosition(mMessageAdapter.getItemCount()- 1);

        etText = (EditText) findViewById(R.id.edittext_chatbox);
        btnSendMessage = (Button) findViewById(R.id.button_chatbox_send);
        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etText.getText().length() == 0)
                    Toast.makeText(getApplicationContext(),"Can't send empty message",Toast.LENGTH_SHORT).show();
                else {
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd/HH:mm");
                    Date date = new Date();
                    String now = dateFormat.format(date);

                    Message message = new Message(senderId,receiverId,now,etText.getText().toString());
                    db.addMessage(message);
                    Toast.makeText(getApplicationContext(),"Message sent!",Toast.LENGTH_SHORT).show();
                    etText.setText("");
                    mMessageAdapter.addMessage(message);
                    mMessageAdapter.notifyDataSetChanged();
                    mMessageRecycler.scrollToPosition(mMessageAdapter.getItemCount()- 1);
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
