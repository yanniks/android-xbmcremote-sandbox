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

package org.xbmc.android.app.ui.menu;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;
import org.xbmc.android.account.authenticator.ui.WizardActivity;
import org.xbmc.android.app.event.HostSwitched;
import org.xbmc.android.app.injection.Injector;
import org.xbmc.android.app.manager.HostManager;
import org.xbmc.android.app.ui.HostChooseActivity;
import org.xbmc.android.app.manager.IconManager;
import org.xbmc.android.app.ui.MoviesActivity;
import org.xbmc.android.remotesandbox.R;
import org.xbmc.android.zeroconf.XBMCHost;

import javax.inject.Inject;
import java.util.ArrayList;

/**
 * The navigation drawer visible throuout the app.
 *
 * @author freezy <freezy@xbmc.org>
 */
public class NavigationDrawerFragment extends Fragment {

	@InjectView(R.id.navdrawer_expandable_list) ExpandableListView list;
	@InjectView(R.id.change_host) Button changeHostButton;
	@InjectView(R.id.current_host) TextView hostLabel;

	@Inject	EventBus bus;
	@Inject HostManager hostManager;
	@Inject IconManager iconManager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Injector.inject(this);
		bus.register(this);
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final View v = inflater.inflate(R.layout.navdrawer, null);
		ButterKnife.inject(this, v);
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		list.setGroupIndicator(null);
		changeHostButton.setTypeface(iconManager.getTypeface());
		changeHostButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (hostManager.getHosts().size() == 0) {
					startActivity(new Intent(getActivity(), WizardActivity.class));
				} else {
					startActivity(new Intent(getActivity(), HostChooseActivity.class));
				}
			}
		});

		final XBMCHost host = hostManager.getActiveHost();
		if (host != null) {
			hostLabel.setText(host.getName());
		}

		final ArrayList<Group> groups = new ArrayList<Group>();
		groups.add(new Group("Home", R.string.ic_home));
		groups.add(new Group("Music", R.string.ic_music, new Child("Albums"), new Child("Artists"), new Child("Genres")));
		groups.add(new Group("Movies", R.string.ic_movie, new Intent(getActivity(), MoviesActivity.class), new Child("Sets"), new Child("Genres"), new Child("Actors"), new Child("Recently Added")));
		groups.add(new Group("TV Shows", R.string.ic_dock, new Child("Title"), new Child("Genres"), new Child("Years"), new Child("Recently Added")));
		groups.add(new Group("Pictures", R.string.ic_picture));
		groups.add(new Group("Addons", R.string.ic_addon, new Intent(getActivity(), WizardActivity.class)));

		list.setAdapter(new ListAdapter(getActivity(), groups));
	}

	public void onEventMainThread(HostSwitched event) {
		hostLabel.setText(event.getHost().getName());
	}

	@Override
	public void onDestroy() {
		bus.unregister(this);
		super.onDestroy();
	}

	private class ListAdapter extends BaseExpandableListAdapter {

		private final Context mContext;
		private final ArrayList<Group> mGroups;
		private final Typeface mIconFont;

		public ListAdapter(Context context, ArrayList<Group> groups) {
			mContext = context;
			mGroups = groups;
			mIconFont = iconManager.getTypeface();
		}

		public View getGroupView(final int groupPosition, boolean isLastChild, View view, final ViewGroup parent) {

			final Group group = mGroups.get(groupPosition);
			if (view == null) {
				LayoutInflater inf = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = inf.inflate(R.layout.navdrawer_item_expandable, null);
			}
			final TextView icon = (TextView) view.findViewById(R.id.icon);
			final TextView label = (TextView) view.findViewById(R.id.label);
			final Button indicator = (Button) view.findViewById(R.id.indicator);

			int foreColor;
			if (group.getName().equals("Home")) {
				foreColor = mContext.getResources().getColor(R.color.holo_blue_bright);
			} else {
				foreColor = mContext.getResources().getColor(R.color.dark_secondary_text);
			}

			label.setTextColor(foreColor);
			label.setText(group.getName());
			indicator.setTextColor(foreColor);
			icon.setTypeface(mIconFont);
			icon.setText(group.getIcon());
			icon.setTextColor(foreColor);
			indicator.setTypeface(mIconFont);
			indicator.setVisibility(group.hasChildren() ? View.VISIBLE : View.GONE);
			indicator.setText(group.collapsed ? R.string.ic_expand : R.string.ic_collapse);
			indicator.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (group.collapsed) {
						((ExpandableListView)parent).expandGroup(groupPosition);
					} else {
						((ExpandableListView)parent).collapseGroup(groupPosition);
					}
					group.toggle();
				}
			});

			view.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (group.hasIntent()) {
						startActivity(group.getIntent());
					}
				}
			});
			return view;
		}

		public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View view, ViewGroup parent) {

			Child child = (Child) getChild(groupPosition, childPosition);
			if (view == null) {
				LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = inflater.inflate(R.layout.navdrawer_item, null);
			}
			TextView tv = (TextView) view.findViewById(R.id.label);
			tv.setText(child.getName());
			return view;
		}

		public Object getGroup(int groupPosition) {
			return mGroups.get(groupPosition);
		}

		public int getGroupCount() {
			return mGroups.size();
		}

		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		public Object getChild(int groupPosition, int childPosition) {
			return mGroups.get(groupPosition).getItems().get(childPosition);
		}

		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		public int getChildrenCount(int groupPosition) {
			return mGroups.get(groupPosition).getItems().size();

		}

		public boolean hasStableIds() {
			return true;
		}

		public boolean isChildSelectable(int arg0, int arg1) {
			return true;
		}
	}


}
