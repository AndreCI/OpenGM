package ch.epfl.sweng.opengm.groups;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import java.util.List;

import ch.epfl.sweng.opengm.R;
import ch.epfl.sweng.opengm.parse.PFException;
import ch.epfl.sweng.opengm.parse.PFGroup;
import ch.epfl.sweng.opengm.parse.PFMember;
import ch.epfl.sweng.opengm.parse.PFUser;

public class Members extends AppCompatActivity {

    private ListView list;
    private List<PFMember> members;

    public static final String GROUP_ID = "ch.epfl.sweng.opengm.groups.members.groupid";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_members);

//        int groupId = getIntent().getIntExtra(GROUP_ID, -1);
//        members = OpenGMApplication.getCurrentUser().getGroups().get(groupId).getMembers();

//        int groupId = 0;
//        try {
//            PFUser user = PFUser.fetchExistingUser("oqMblls8Cb");
//            List<PFGroup> groups = user.getGroups();
//            if (groups.isEmpty()) {
//                Log.v("test", "groups is empty");
//            } else {
//                Log.v("test", "groups is not empty");
//            }
//        } catch (PFException e) {
//            e.printStackTrace();
//        }
//
//        list = (ListView) findViewById(R.id.member_list);
//
//        if (!members.isEmpty()) {
//            MembersAdapter adapter = new MembersAdapter(this, R.layout.item_member, members);
//            list.setAdapter(adapter);
//        } else {
//            Log.v("test", "members is empty");
//        }
    }
}
