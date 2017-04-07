package com.loar.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public abstract class RecyclerAdapter<T> extends RecyclerView.Adapter<ViewHolder> {

    protected Context context;
    protected List<T> datas;
    protected int layoutId;

    public RecyclerAdapter(Context context, List<T> datas, int layoutId) {
        this.context = context;
        this.layoutId = layoutId;
        setDatas(datas);
    }

    public void setDatas(List<T> datas) {
        if (datas == null) {
            datas = new ArrayList<>();
        }
        this.datas = datas;
        notifyDataSetChanged();
    }

    public void addDatas(List<T> datas) {
        if (datas == null) {
            return;
        }
        this.datas.addAll(datas);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return datas == null ? 0 : datas.size();
    }

    @Override
    public void onBindViewHolder(ViewHolder vh, int position) {
        onBindViewHolder(vh, datas, position);
    }

    public abstract void onBindViewHolder(ViewHolder vh, List<T> datas,
                                          int position);

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        return ViewHolder.getInstance(context, layoutId, null, viewGroup);
    }

    // ==================== 数据页 ========

    private int pageSize = 15;
    private int pageIndex = 1;
    private int firstPage = pageIndex;

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * 设置请求第一页的下标从firstopage开始
     *
     * @param firstPage
     */
    public void setFirstPage(int firstPage) {
        this.firstPage = firstPage;
        setPageIndex(firstPage);
    }

    public void resetPageIndex() {
        this.pageIndex = firstPage;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    /**
     * 下一页
     */
    public void nextPage() {
        this.pageIndex++;
    }

    /**
     * 上一页
     */
    public void prePage() {
        this.pageIndex--;
    }

    public boolean isFirstPage() {
        return firstPage == pageIndex;
    }

    /**
     * 根据总数判断是否还有下一页
     *
     * @return
     */
    public boolean hasMorePage() {
        return pageSize * (pageIndex - firstPage + 1) <= getItemCount();
    }
}
