package site.zawag.work.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import site.zawag.work.R;
import site.zawag.work.constants.Constants;
import site.zawag.work.model.RefillItem;

import java.util.List;

public class RefillListAdapter extends BaseAdapter implements Constants {

	private FragmentActivity activity;
	private LayoutInflater inflater;
	private List<RefillItem> itemList;

	public RefillListAdapter(FragmentActivity activity, List<RefillItem> itemList) {

		this.activity = activity;
		this.itemList = itemList;
	}

	@Override
	public int getCount() {

		return itemList.size();
	}

	@Override
	public Object getItem(int location) {

		return itemList.get(location);
	}

	@Override
	public long getItemId(int position) {

		return position;
	}
	
	static class ViewHolder {

        public TextView mRefillType;
        public TextView mRefillAmount;
        public TextView mRefillDate;
	        
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder viewHolder = null;

		if (inflater == null) {

            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

		if (convertView == null) {
			
			convertView = inflater.inflate(R.layout.refill_row, null);
			
			viewHolder = new ViewHolder();

            viewHolder.mRefillType = convertView.findViewById(R.id.refill_type);
            viewHolder.mRefillAmount = convertView.findViewById(R.id.refill_amount);
			viewHolder.mRefillDate = convertView.findViewById(R.id.refill_date);

            convertView.setTag(viewHolder);

		} else {
			
			viewHolder = (ViewHolder) convertView.getTag();
		}

        viewHolder.mRefillType.setTag(position);
        viewHolder.mRefillAmount.setTag(position);
        viewHolder.mRefillDate.setTag(position);
		
		final RefillItem item = itemList.get(position);

        viewHolder.mRefillType.setText(this.activity.getString(R.string.label_refill_type_google));

        viewHolder.mRefillAmount.setText(this.activity.getString(R.string.label_refill_amount) + " " + item.getAmount());

        viewHolder.mRefillDate.setText(item.getDate());

		return convertView;
	}
}