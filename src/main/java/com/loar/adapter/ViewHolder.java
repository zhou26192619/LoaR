package com.loar.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

public class ViewHolder extends RecyclerView.ViewHolder {
	private static ViewHolder viewHolder;
	private Context context;
	private View convertView;
	private Map<Integer, View> views = new HashMap<>();

	private ViewHolder(Context context, View convertView) {
		super(convertView);
		this.context = context;
		this.convertView = convertView;
	}

	public static ViewHolder getInstance(Context context, int layoutId,
			View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = View.inflate(context, layoutId, null);
			viewHolder = new ViewHolder(context, convertView);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		return viewHolder;
	}

	public View getConvertView() {
		return convertView;
	}

	public <T> T getViewById(int id, Class<T> t) {
		if (views.containsKey(id)) {
			return (T) views.get(id);
		}
		View view = convertView.findViewById(id);
		if (view != null) {
			views.put(id, view);
		}
		return (T) view;
	}
	public void setText(int id,String text) {
		getViewById(id, TextView.class).setText(text);
	}
}
