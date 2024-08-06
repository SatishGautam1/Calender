package calender.com;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

public class ReminderAdapter extends BaseAdapter {

    private Context context;
    private List<Map.Entry<String, String>> reminders;

    public ReminderAdapter(Context context, List<Map.Entry<String, String>> reminders) {
        this.context = context;
        this.reminders = reminders;
    }

    @Override
    public int getCount() {
        return reminders.size();
    }

    @Override
    public Object getItem(int position) {
        return reminders.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        }

        Map.Entry<String, String> reminder = reminders.get(position);

        TextView textViewDate = convertView.findViewById(R.id.textViewDate);
        TextView textViewReminder = convertView.findViewById(R.id.textViewReminder);

        textViewDate.setText(reminder.getKey());
        textViewReminder.setText(reminder.getValue());

        return convertView;
    }
}
