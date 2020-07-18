package com.example.image_search.UI.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.example.image_search.R;

public class EmptyResultActivity extends AppCompatActivity {
    Button backBtn;
    ImageView emptyIcon;
    TextView mainMessage, secondMessage, suggestMessage;
    TableLayout helpImages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty_result);

        // clear FLAG_TRANSLUCENT_STATUS flag:
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        // finally change the color
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorAccentDark));

        emptyIcon = findViewById(R.id.emptyIcon);
        mainMessage = findViewById(R.id.mainMessage);
        secondMessage = findViewById(R.id.secondMessage);
        suggestMessage = findViewById(R.id.suggestMessage);
        helpImages = findViewById(R.id.helpImages);

        Intent intent = getIntent();
        final String messageList[] = getIntent().getStringArrayExtra("MESSAGES");

        String statusCode, strMainMessage, strSecondMessage;
        statusCode = messageList[0];
        strMainMessage = messageList[1];
        strSecondMessage = messageList[2];

        mainMessage.setText(strMainMessage);
        secondMessage.setText(strSecondMessage);

        if(!statusCode.equals("404")) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
            );
            params.topMargin = 200;
            emptyIcon.setLayoutParams(params);
            emptyIcon.setImageResource(R.drawable.ic_snail);
            emptyIcon.getLayoutParams().width = LinearLayout.LayoutParams.WRAP_CONTENT;
            emptyIcon.getLayoutParams().height = 160;

            suggestMessage.setVisibility(View.GONE);
            helpImages.setVisibility(View.GONE);
        }

        backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent mIntent = new Intent(EmptyResultActivity.this, CaptureImageActivity.class);
            startActivity(mIntent);
            }
        });
    }
}
