package com.dandian.pocketmoodle.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.androidquery.AQuery;
import com.dandian.pocketmoodle.R;
import com.zhy.tree.bean.Node;
import com.zhy.tree.bean.TreeListViewAdapter;

public class SimpleTreeAdapter<T> extends TreeListViewAdapter<T>
{
	private AQuery aq;
	public SimpleTreeAdapter(ListView mTree, Context context, List<T> datas,
			int defaultExpandLevel) throws IllegalArgumentException,
			IllegalAccessException
	{
		
		super(mTree, context, datas, defaultExpandLevel);
		aq = new AQuery(context);
	}

	@Override
	public View getConvertView(Node node , final int position, View convertView, ViewGroup parent)
	{
		
		ViewHolder viewHolder = null;
		if (convertView == null)
		{
			convertView = mInflater.inflate(R.layout.tree_list_item, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.icon = (ImageView) convertView
					.findViewById(R.id.id_treenode_icon);
			viewHolder.image = (ImageView) convertView
					.findViewById(R.id.id_treenode_image);
			viewHolder.label = (TextView) convertView
					.findViewById(R.id.id_treenode_label);
			viewHolder.desc = (TextView) convertView
					.findViewById(R.id.id_treenode_desc);
			convertView.setTag(viewHolder);

		} else
		{
			viewHolder = (ViewHolder) convertView.getTag();
		}

		if (node.getIcon() == -1)
		{
			viewHolder.icon.setVisibility(View.INVISIBLE);
			viewHolder.icon.setOnClickListener(null);
		} else
		{
			viewHolder.icon.setVisibility(View.VISIBLE);
			viewHolder.icon.setImageResource(node.getIcon());
			viewHolder.icon.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					expandOrCollapse(position);
				}
			});
		}
		if(node.getImage()!=null && node.getImage().length()>0)
		{
			viewHolder.image.setVisibility(View.VISIBLE);
			aq.id(viewHolder.image).image(node.getImage(), true, true);
		}
		else
			viewHolder.image.setVisibility(View.GONE);
    	viewHolder.label.setText(node.getName());
		viewHolder.desc.setText(node.getDesc());
		return convertView;
	}
	public void expandOrCollapse(int position)
	{
		super.expandOrCollapse(position);
	}
	private final class ViewHolder
	{
		ImageView icon;
		TextView label;
		TextView desc;
		ImageView image;
	}

}
