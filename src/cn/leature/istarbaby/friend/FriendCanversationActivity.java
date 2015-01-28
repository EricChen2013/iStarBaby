package cn.leature.istarbaby.friend;

import cn.leature.istarbaby.R;
import cn.leature.istarbaby.R.layout;
import cn.leature.istarbaby.R.menu;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class FriendCanversationActivity extends Activity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friend_canversation);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.friend_canversation, menu);
		return true;
	}

}
