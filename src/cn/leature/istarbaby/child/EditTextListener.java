package cn.leature.istarbaby.child;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

public class EditTextListener
{
	private Context mContext;

	private EditTextListener()
	{
	};

	public static EditTextListener textListener = new EditTextListener();

	public static EditTextListener addTextListener()
	{
		return textListener;
	}

	public void Listener(EditText text, Context context)
	{
		this.mContext = context;

		Listener(text);
	}

	public void Listener(EditText text)
	{

		text.addTextChangedListener(new TextWatcher()
		{

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count)
			{
				//
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after)
			{
				//
			}

			@Override
			public void afterTextChanged(Editable s)
			{
				String temp = s.toString();
				int d = temp.indexOf(".");
				if ("0".equals(temp))
				{
					Toast.makeText(mContext, "请输入不小于1", Toast.LENGTH_LONG)
							.show();
					s.delete(s.length() - 1, s.length());
					return;
				}

				if (d < 0)
				{
					if (temp.length() < 2)
						return;

					if (Integer.parseInt(temp) > 199)
					{
						s.delete(s.length() - 1, s.length());
					}
				}
				else if (d > 0)
				{
					if (temp.length() - d > 2)
					{
						// 小数点后有两位的时候
						s.delete(s.length() - 1, s.length());
					}
				}
				else
				{
					s.delete(s.length() - 1, s.length());
				}
			}
		});
	}
}
