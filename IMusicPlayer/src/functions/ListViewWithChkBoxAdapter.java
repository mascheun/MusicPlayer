package functions;

import java.util.List;

import com.example.imusicplayer.MainActivity;
import com.example.imusicplayer.PlayListActivity;
import com.example.imusicplayer.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;


public class ListViewWithChkBoxAdapter extends ArrayAdapter<Item>{
	
	private List<Item> itemList;
	private Context context;
	
	public ListViewWithChkBoxAdapter(List<Item> itemList, Context context) {
		super(context, R.layout.single_listview_item, itemList);
		this.itemList = itemList;
		this.context = context;
	}

	private static class itemHolder {
		public TextView itemName;
		public CheckBox chkBox;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	
		View v = convertView;
		
		itemHolder holder = new itemHolder();
		
		if(convertView == null) {
			
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = inflater.inflate(R.layout.single_listview_item, null);
			
			holder.itemName = (TextView) v.findViewById(R.id.deletePlTv);
			holder.chkBox = (CheckBox) v.findViewById(R.id.chk_box);
			
		} else {
			holder = (itemHolder) v.getTag();
		}
		
		Item p = itemList.get(position);
		holder.itemName.setText(p.getName());
		holder.chkBox.setChecked(p.isSelected());
		holder.chkBox.setTag(p);
		
		return v;
	}

}
