package cn.leature.istarbaby.monitor;

import java.util.ArrayList;
import java.util.List;

import cn.leature.istarbaby.domain.PreferenceInfo;

import android.content.Context;
import android.content.Intent;

public class MonitorShareModel
{
	private static MonitorShareModel mShareMonitor = null;

	private List<MyMonitor> gMonitorLists = new ArrayList<MyMonitor>();

	private List<MonitorInfo> gMonitorInfoItems = new ArrayList<MonitorInfo>();

	private MyMonitor gCurrentMonitor = null;

	public MonitorShareModel()
	{
		super();
	}

	public static MonitorShareModel getInstance()
	{
		if (mShareMonitor == null)
		{
			mShareMonitor = new MonitorShareModel();
		}

		return mShareMonitor;
	}

	public List<MyMonitor> getMonitorLists()
	{
		return gMonitorLists;
	}

	public void setMonitorLists(List<MyMonitor> monitorLists)
	{
		this.gMonitorLists = monitorLists;
	}

	public MyMonitor getCurrentMonitor()
	{
		return gCurrentMonitor;
	}

	public void setCurrentMonitor(MyMonitor myMonitor)
	{
		this.gCurrentMonitor = myMonitor;
	}

	public List<MonitorInfo> getMonitorInfoItems()
	{
		return gMonitorInfoItems;
	}

	public void setMonitorInfoItems(List<MonitorInfo> items)
	{
		this.gMonitorInfoItems = items;
	}

	public void setMonitorListsAndItems(List<MonitorInfo> items,
			List<MyMonitor> monitorLists)
	{
		this.gMonitorInfoItems = items;
		this.gMonitorLists = monitorLists;
	}

	public void registerIOTCListener(IRegisterMonitorListener listener)
	{

		if (gMonitorLists != null)
		{
			for (int i = 0; i < gMonitorLists.size(); i++)
			{
				MyMonitor monitor = gMonitorLists.get(i);

				monitor.registerIOTCListener(listener);
			}
		}
	}

	public void unregisterIOTCListener(IRegisterMonitorListener listener)
	{

		if (gMonitorLists != null)
		{
			for (int i = 0; i < gMonitorLists.size(); i++)
			{
				MyMonitor monitor = gMonitorLists.get(i);

				monitor.unregisterIOTCListener(listener);
			}
		}
	}

	public void disconnect()
	{

		if (gMonitorLists != null)
		{
			for (int i = 0; i < gMonitorLists.size(); i++)
			{
				MyMonitor monitor = gMonitorLists.get(i);

				monitor.stop(monitor.getChannel());
				monitor.disconnect();
			}
		}
	}

	public void connect()
	{

		if (gMonitorLists != null)
		{
			for (int i = 0; i < gMonitorLists.size(); i++)
			{
				MyMonitor monitor = gMonitorLists.get(i);

				monitor.stop(monitor.getChannel());
				monitor.disconnect();

				monitor.connect(monitor.getUID());
				monitor.start(0, monitor.getName(), monitor.getPassword());
			}
		}
	}

	public void release()
	{

		gCurrentMonitor = null;

		if (gMonitorLists != null)
		{
			for (int i = 0; i < gMonitorLists.size(); i++)
			{
				MyMonitor monitor = gMonitorLists.get(i);

				monitor.stop(monitor.getChannel());
				monitor.disconnect();
				monitor.unregisterAllListener();

				monitor = null;
			}

			gMonitorLists = null;
		}
	}

	public void deleteMonitor(String uid)
	{

		gCurrentMonitor = null;
		if (uid == null || uid.length() == 0)
		{
			return;
		}

		int idx = -1;
		if (gMonitorLists != null)
		{
			for (int i = 0; i < gMonitorLists.size(); i++)
			{
				MyMonitor monitor = gMonitorLists.get(i);

				if (monitor.getUID().equals(uid))
				{
					idx = i;
					break;
				}
			}

			if (idx >= 0 && idx < gMonitorLists.size())
			{
				MyMonitor monitor = gMonitorLists.get(idx);

				monitor.stop(monitor.getChannel());
				monitor.disconnect();
				monitor.unregisterAllListener();

				gMonitorLists.remove(idx);

				monitor = null;
			}

			if (gMonitorLists.size() == 0)
			{
				gMonitorLists = null;
			}
		}
	}

	public void saveCurrentMonitor(String uid)
	{
		gCurrentMonitor = null;

		if (uid == null || uid.length() == 0)
		{
			return;
		}

		if (gMonitorLists != null)
		{
			for (int i = 0; i < gMonitorLists.size(); i++)
			{
				MyMonitor monitor = gMonitorLists.get(i);

				if (monitor.getUID().equals(uid))
				{
					gCurrentMonitor = monitor;
					break;
				}
			}
		}
	}

	public void connectCurrentMonitor()
	{

		if (gCurrentMonitor == null)
		{
			return;
		}

		// 重新连接
		int channel = gCurrentMonitor.getChannel();

		gCurrentMonitor.stop(channel);
		gCurrentMonitor.disconnect();

		gCurrentMonitor.connect(gCurrentMonitor.getUID());
		gCurrentMonitor.start(channel, gCurrentMonitor.getName(),
				gCurrentMonitor.getPassword());
	}

	public void logout(Context context)
	{
		
		if (!PreferenceInfo.get(context, PreferenceInfo.PreferenceFileName,
				false))
		{
			// 退出服务
			Intent intent = new Intent(
					"cn.leature.istarbaby.monitor.listenservice");
			context.stopService(intent);
		}
	}
}
