package cn.wm.uhfProject;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class EpcListAdapter extends RecyclerView.Adapter<EpcListAdapter.ViewHolder> {

    private Context context;
    private List<String> list;

    public EpcListAdapter(Context context, List<String> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = View.inflate(context, android.R.layout.simple_list_item_1, null);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvEpc.setText(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvEpc;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEpc = itemView.findViewById(android.R.id.text1);
        }
    }
}
