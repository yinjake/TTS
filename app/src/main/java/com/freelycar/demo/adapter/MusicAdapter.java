package com.freelycar.demo.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.freelycar.demo.entity.Music;

import java.util.List;


public class MusicAdapter extends BaseAdapter {

    private Context context; // 上下文
    private List<Music> musicList; // 音乐列表

    /**
     * 构造方法
     *
     * @param context
     * @param musicList
     */
    public MusicAdapter(Context context, List<Music> musicList) {
        this.context = context;
        this.musicList = musicList;
    }

    @Override
    public int getCount() {
        return musicList.size();
    }

    @Override
    public Object getItem(int position) {
        return musicList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // 声明视图容器
        ViewHolder holder = null;

        // 判断转换视图是否为空
        if (convertView == null) {
            // 将列表项模板映射成转换视图
            //convertView = LayoutInflater.from(context).inflate(R.layout.music_name_list_item, null);
            // 创建视图容器对象
            holder = new ViewHolder();
            // 实例化转换视图里的控件
         //   holder.tvMusicName = convertView.findViewById(R.id.tvMusicName);
            // 将视图容器附加到转换视图
            convertView.setTag(holder);
        } else {
            // 从转换视图里取出视图容器
            holder = (ViewHolder) convertView.getTag();
        }

        // 获取列表项要显示的数据
        Music music = musicList.get(position);
        // 设置列表项控件的属性（去掉路径和扩展名）
        holder.tvMusicName.setText(music.getMusicName().substring(
                music.getMusicName().lastIndexOf("/") + 1, music.getMusicName().lastIndexOf(".")));

        // 返回转换视图
        return convertView;
    }

    /**
     * 视图容器
     */
    private static class ViewHolder {
        TextView tvMusicName;
    }
}

