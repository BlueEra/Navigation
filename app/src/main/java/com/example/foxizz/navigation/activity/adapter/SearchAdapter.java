package com.example.foxizz.navigation.activity.adapter;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.example.foxizz.navigation.R;
import com.example.foxizz.navigation.activity.fragment.MainFragment;
import com.example.foxizz.navigation.data.SearchItem;
import com.example.foxizz.navigation.util.Tools;
import com.example.foxizz.navigation.demo.MyPoiSearch;

import static com.example.foxizz.navigation.util.Tools.isAirplaneModeOn;
import static com.example.foxizz.navigation.util.Tools.isNetworkConnected;

/**
 * 搜索到的信息列表的适配器
 */
public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    private MainFragment mainFragment;
    //构造器
    public SearchAdapter(MainFragment mainFragment) {
        this.mainFragment = mainFragment;
    }

    private long clickTime = 0;

    //设置item中的View
    static class ViewHolder extends RecyclerView.ViewHolder {
        View view;
        CardView cardView;
        TextView targetName;
        TextView address;
        TextView distance;
        Button itemButton;
        TextView endText;

        ViewHolder(View view) {
            super(view);
            cardView = view.findViewById(R.id.card_view);
            targetName = view.findViewById(R.id.target_name);
            address = view.findViewById(R.id.address);
            distance = view.findViewById(R.id.distance);
            itemButton = view.findViewById(R.id.item_button);
            endText = view.findViewById(R.id.end_text);
        }
    }

    //获取item数量
    @Override
    public int getItemCount() {
        return mainFragment.searchList.size();
    }

    //获取SearchItem的数据
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SearchItem searchItem = mainFragment.searchList.get(position);
        holder.targetName.setText(searchItem.getTargetName());
        holder.address.setText(searchItem.getAddress());
        holder.distance.setText(searchItem.getDistance() + "km");

        //底部显示提示信息
        if(position == mainFragment.searchList.size() - 1)
            holder.endText.setVisibility(View.VISIBLE);
        else
            holder.endText.setVisibility(View.GONE);
    }

    //为recyclerView的每一个item设置点击事件
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);

        //cardView的点击事件
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onClick(View view) {
                if(unableToClick()) return;

                if(!isNetworkConnected(mainFragment.requireActivity())) {
                    Toast.makeText(mainFragment.requireActivity(),
                            R.string.network_error, Toast.LENGTH_SHORT).show();
                    return;
                }

                if(isAirplaneModeOn(mainFragment.requireActivity())) {
                    Toast.makeText(mainFragment.requireActivity(),
                            R.string.close_airplane_mode, Toast.LENGTH_SHORT).show();
                    return;
                }

                click(holder);

                Tools.expandLayout(mainFragment.requireActivity(),
                        mainFragment.infoLayout, true);//展开详细信息布局

                mainFragment.infoButton.setText(R.string.info_button1);//设置按钮为路线
                mainFragment.infoFlag = true;//设置信息状态为详细信息

                //获取点击的item
                int position = holder.getAdapterPosition();
                SearchItem searchItem = mainFragment.searchList.get(position);

                //移动视角到指定位置
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(searchItem.getLatLng());
                MapStatusUpdate msu= MapStatusUpdateFactory.newLatLngBounds(builder.build());
                mainFragment.mBaiduMap.setMapStatus(msu);

                //清空地图上的所有标记点和绘制的路线
                mainFragment.mBaiduMap.clear();
                //构建Marker图标
                BitmapDescriptor bitmap = BitmapDescriptorFactory
                        .fromResource(R.drawable.ic_to_location);
                //构建MarkerOption，用于在地图上添加Marker
                OverlayOptions option = new MarkerOptions()
                        .position(searchItem.getLatLng())
                        .icon(bitmap);
                //在地图上添加Marker，并显示
                mainFragment.mBaiduMap.addOverlay(option);
            }
        });

        //itemButton的点击事件
        holder.itemButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public void onClick(View view) {
                if(unableToClick()) return;

                if(isNetworkConnected(mainFragment.requireActivity())) {
                    if(isAirplaneModeOn(mainFragment.requireActivity())) {
                        Toast.makeText(mainFragment.requireActivity(),
                                R.string.close_airplane_mode, Toast.LENGTH_SHORT).show();
                    } else {
                        click(holder);

                        Tools.expandLayout(mainFragment.requireActivity(),
                                mainFragment.selectLayout, true);//展开选择布局

                        mainFragment.infoButton.setText(R.string.info_button2);//设置按钮为详细信息
                        mainFragment.infoFlag = false;//设置信息状态为交通选择

                        //重置交通类型为步行
                        mainFragment.routePlanSelect = MainFragment.WALKING;
                        mainFragment.selectButton1.setBackgroundResource(R.drawable.button_background_gray);
                        mainFragment.selectButton2.setBackgroundResource(R.drawable.button_background_black);
                        mainFragment.selectButton3.setBackgroundResource(R.drawable.button_background_gray);
                        mainFragment.selectButton4.setBackgroundResource(R.drawable.button_background_gray);

                        mainFragment.myRoutePlanSearch.startRoutePlanSearch();//开始路线规划
                    }
                } else {
                    Toast.makeText(mainFragment.requireActivity(),
                            R.string.network_error, Toast.LENGTH_SHORT).show();
                }
            }
        });

        //cardView的长按事件
        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(unableToClick()) return false;

                final int position = holder.getAdapterPosition();
                final SearchItem searchItem = mainFragment.searchList.get(position);

                if(mainFragment.isHistorySearchResult) {//如果是搜索历史记录
                    AlertDialog.Builder builder = new AlertDialog.Builder(mainFragment.requireActivity());
                    builder.setTitle(mainFragment.getString(R.string.hint));
                    builder.setMessage("你确定要删除'" + searchItem.getTargetName() + "'吗？");

                    builder.setPositiveButton(mainFragment.getString(R.string.delete), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //在searchList中寻找这条记录
                            for(int i = 0; i < mainFragment.searchList.size(); i++) {
                                if(searchItem.getUid().equals(mainFragment.searchList.get(i).getUid())) {
                                    mainFragment.searchList.remove(searchItem);//移除搜索列表的这条记录
                                    notifyItemRemoved(i);//通知adapter移除这条记录
                                }
                            }

                            mainFragment.dbHelper.deleteSearchData(searchItem.getUid());//删除数据库中的搜索记录
                        }
                    });

                    builder.setNegativeButton(mainFragment.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //do nothing
                        }
                    });

                    builder.show();

                } else {//如果不是
                    //在searchList中寻找这条记录
                    for(int i = 0; i < mainFragment.searchList.size(); i++) {
                        if(searchItem.getUid().equals(mainFragment.searchList.get(i).getUid())) {
                            mainFragment.searchList.remove(searchItem);//移除搜索列表的这条记录
                            notifyItemRemoved(i);//通知adapter移除这条记录
                        }
                    }
                }

                return false;
            }
        });

        return holder;
    }

    //cardView和itemButton共同的点击事件
    @SuppressLint("SetTextI18n")
    private void click(ViewHolder holder) {
        //获取点击的item
        int position = holder.getAdapterPosition();
        SearchItem searchItem = mainFragment.searchList.get(position);

        Tools.expandLayout(mainFragment.requireActivity(),
                mainFragment.searchLayout, false);//收起搜索布局
        if(mainFragment.searchExpandFlag) {//如果状态为展开
            mainFragment.expandSearchDrawer(false);//收起展开的搜索抽屉
            mainFragment.searchExpandFlag = false;//设置状态为收起
        }
        Tools.expandLayout(mainFragment.requireActivity(),
                mainFragment.startLayout, true);//展开开始导航布局

        //设置终点坐标
        mainFragment.endLocation = searchItem.getLatLng();

        mainFragment.myPoiSearch.poiSearchType = MyPoiSearch.DETAIL_SEARCH;//设置为直接详细搜索
        mainFragment.mPoiSearch.searchPoiDetail(//进行详细信息搜索
                (new PoiDetailSearchOption()).poiUids(searchItem.getUid()));
    }

    //不允许同时点击多个item
    private boolean unableToClick() {
        if((System.currentTimeMillis() - clickTime) > 1000) {
            clickTime = System.currentTimeMillis();
            return false;
        }
        else return true;
    }

}