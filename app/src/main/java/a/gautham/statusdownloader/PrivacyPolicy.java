package a.gautham.statusdownloader;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class PrivacyPolicy extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);

        Toolbar toolbar = findViewById(R.id.toolbarPolicy);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView textView = findViewById(R.id.policy_view);

        textView.setText("\n" +
                "\n" +
                "Your privacy is important to us. It is Gautham's policy to respect your privacy regarding any information we may collect from you through our app, Status Downloader.\n" +
                "\n" +
                "We only ask for personal information when we truly need it to provide a service to you. We collect it by fair and lawful means, with your knowledge and consent. We also let you know why we’re collecting it and how it will be used.\n" +
                "\n" +
                "We only retain collected information for as long as necessary to provide you with your requested service. What data we store, we’ll protect within commercially acceptable means to prevent loss and theft, as well as unauthorized access, disclosure, copying, use or modification.\n" +
                "\n" +
                "We don’t share any personalldy identifying information publicly or with third-parties, except when required to by law.\n" +
                "\n" +
                "Our app may link to external sites that are not operated by us. Please be aware that we have no control over the content and practices of these sites, and cannot accept responsibility or liability for their respective privacy policies.\n" +
                "\n" +
                "You are free to refuse our request for your personal information, with the understanding that we may be unable to provide you with some of your desired services.\n" +
                "\n" +
                "Your continued use of our app will be regarded as acceptance of our practices around privacy and personal information. If you have any questions about how we handle user data and personal information, feel free to contact us.\n" +
                "\n" +
                "This policy is effective as of 22 April 2020.\n");

    }
}