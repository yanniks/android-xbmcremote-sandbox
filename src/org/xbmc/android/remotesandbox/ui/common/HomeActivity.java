/*
 *      Copyright (C) 2005-2015 Team XBMC
 *      http://xbmc.org
 *
 *  This Program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2, or (at your option)
 *  any later version.
 *
 *  This Program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with XBMC Remote; see the file license.  If not, write to
 *  the Free Software Foundation, 675 Mass Ave, Cambridge, MA 02139, USA.
 *  http://www.gnu.org/copyleft/gpl.html
 *
 */

package org.xbmc.android.remotesandbox.ui.common;

import org.xbmc.android.remotesandbox.R;
import org.xbmc.android.remotesandbox.ui.base.ActionBarActivity;
import org.xbmc.android.remotesandbox.ui.base.ActionBarHelper;
import org.xbmc.android.remotesandbox.ui.base.ActionBarHelperBase;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class HomeActivity extends ActionBarActivity {
	
//	private final static String TAG = HomeActivity.class.getSimpleName();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		setTitle(null);
		
/*		final AccountManager am = AccountManager.get(this);
		final Account[] accounts = am.getAccountsByType(Constants.ACCOUNT_TYPE);
		for (Account account : accounts) {
			Log.e(TAG, "Address: " + am.getUserData(account, Constants.DATA_ADDRESS));
			Log.e(TAG, "Port: " + am.getUserData(account, Constants.DATA_PORT));
		}*/

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.refresh_menu_items, menu);
		super.onCreateOptionsMenu(menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.menu_refresh) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
