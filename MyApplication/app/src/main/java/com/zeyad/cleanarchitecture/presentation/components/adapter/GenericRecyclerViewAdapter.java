package com.zeyad.cleanarchitecture.presentation.components.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jakewharton.rxbinding.view.RxView;
import com.zeyad.cleanarchitecture.utilities.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rx.subscriptions.CompositeSubscription;

/**
 * @author by zeyad on 19/05/16.
 */
public abstract class GenericRecyclerViewAdapter extends RecyclerView.Adapter<GenericRecyclerViewAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClicked(int position, ItemInfo userViewModel, ViewHolder holder);

        boolean onItemLongClicked(int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements ItemBase {

        public ViewHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void bindData(Object data, SparseBooleanArray selectedItems, int position) {
        }
    }

    public Context mContext;
    public final LayoutInflater mLayoutInflater;
    public List<ItemInfo> mDataList;
    public OnItemClickListener mOnItemClickListener;
    public SparseBooleanArray mSelectedItems;
    public boolean mIsLoadingFooterAdded = false;
    private boolean mHasHeader = false, mHasFooter = false, allowSelection = false;
    private CompositeSubscription mCompositeSubscription;

    public GenericRecyclerViewAdapter(Context context, List<ItemInfo> list) {
        mContext = context;
        validateList(list);
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mDataList = list;
        mSelectedItems = new SparseBooleanArray();
        mCompositeSubscription = Utils.getNewCompositeSubIfUnsubscribed(mCompositeSubscription);
    }

    @Override
    public abstract ViewHolder onCreateViewHolder(ViewGroup parent, int viewType);

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ItemInfo itemInfo = mDataList.get(position);
        holder.bindData(itemInfo.getData(), mSelectedItems, position);
        if (!(hasHeader() && position == 0 || hasFooter() && position == mDataList.size() - 1)) {
            mCompositeSubscription.add(RxView.clicks(holder.itemView).subscribe(aVoid -> {
                if (mOnItemClickListener != null)
                    mOnItemClickListener.onItemClicked(position, itemInfo, holder);
            }));
            mCompositeSubscription.add(RxView.longClicks(holder.itemView).subscribe(aVoid -> {
                if (mOnItemClickListener != null)
                    mOnItemClickListener.onItemLongClicked(position);
            }));
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && hasHeader())
            return ItemInfo.HEADER;
        else if (position == mDataList.size() - 1 && mIsLoadingFooterAdded)
            return ItemInfo.LOADING;
        else if (position == mDataList.size() - 1 && !mIsLoadingFooterAdded && hasFooter())
            return ItemInfo.FOOTER;
        else
            return mDataList != null ? mDataList.get(position).getLayoutId() : 0;
    }

    @Override
    public long getItemId(int position) {
        return mDataList.get(position).getId();
    }

    public List<Long> getSelectedItemsIds() {
        ArrayList<Long> integers = new ArrayList<>();
        for (int i = 0; i < mDataList.size(); i++)
            try {
                if (getSelectedItems().contains(i))
                    integers.add(mDataList.get(i).getId());
            } catch (Exception e) {
                e.printStackTrace();
            }
        return integers;
    }

    @Override
    public int getItemCount() {
        return mDataList != null ? mDataList.size() : 0;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public boolean hasHeader() {
        return mHasHeader;
    }

    public void setHasHeader(boolean hasHeader) {
        mHasHeader = hasHeader;
        if (mDataList.size() > 0)
            if (mHasHeader)
                mDataList.add(0, new ItemInfo<Void>(null, ItemInfo.HEADER) {
                    @Override
                    public long getId() {
                        return HEADER;
                    }
                });
            else if (mDataList.get(0).getId() == ItemInfo.HEADER) mDataList.remove(0);
    }

    public boolean hasFooter() {
        return mHasHeader;
    }

    public void setHasFooter(boolean hasFooter) {
        mHasFooter = hasFooter;
        if (mDataList.size() > 0)
            if (mHasFooter)
                mDataList.add(mDataList.size(), new ItemInfo<Void>(null, ItemInfo.FOOTER) {
                    @Override
                    public long getId() {
                        return FOOTER;
                    }
                });
            else if (mDataList.get(mDataList.size() - 1).getId() == ItemInfo.FOOTER)
                mDataList.remove(mDataList.size() - 1);
    }

    public boolean isAllowSelection() {
        return allowSelection;
    }

    public void setAllowSelection(boolean allowSelection) {
        this.allowSelection = allowSelection;
    }

    public void addLoading() {
        mIsLoadingFooterAdded = true;
        mDataList.add(mDataList.size() - 1, new ItemInfo<Void>(null, ItemInfo.LOADING) {
            @Override
            public long getId() {
                return LOADING;
            }
        });
        notifyItemInserted(mDataList.size() - 1);
    }

    public void removeLoading() {
        mIsLoadingFooterAdded = false;
        int position = mDataList.size() - 1;
        if (mDataList.get(position).getId() == ItemInfo.LOADING) {
            mDataList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void setItemList(List<ItemInfo> dataSet) {
        mDataList.addAll(dataSet);
        validateList(mDataList);
        if (mDataList.get(0) != null)
            mDataList.add(0, null);
        notifyDataSetChanged();
    }

    public List<ItemInfo> getDataList() {
        return mDataList;
    }

    public void setDataList(List<ItemInfo> dataList) {
        validateList(dataList);
        mDataList = dataList;
        notifyDataSetChanged();
    }

//    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
//        this.mOnItemClickListener = mOnItemClickListener;
//    }

    public CompositeSubscription getCompositeSubscription() {
        return mCompositeSubscription;
    }

    /**
     * Indicates if the item at position position is selected
     *
     * @param position Position of the item to check
     * @return true if the item is selected, false otherwise
     */
    public boolean isSelected(int position) throws Exception {
        if (allowSelection)
            return getSelectedItems().contains(position);
        else throw new Exception("Selection mode is disabled!");
    }

    /**
     * Toggle the selection status of the item at a given position
     *
     * @param position Position of the item to toggle the selection status for
     */
    public boolean toggleSelection(int position) {
        if (allowSelection) {
            if (mSelectedItems.get(position, false))
                mSelectedItems.delete(position);
            else
                mSelectedItems.put(position, true);
            notifyItemChanged(position);
            return true;
        } else return false;
    }

    /**
     * Clear the selection status for all items
     */
    public void clearSelection() throws Exception {
        if (allowSelection) {
            List<Integer> selection = getSelectedItems();
            mSelectedItems.clear();
            for (Integer i : selection)
                notifyItemChanged(i);
        } else throw new Exception("Selection mode is disabled!");
    }

    /**
     * Count the selected items
     *
     * @return Selected items count
     */
    public int getSelectedItemCount() throws Exception {
        if (allowSelection)
            return mSelectedItems.size();
        else throw new Exception("Selection mode is disabled!");
    }

    /**
     * Indicates the list of selected items
     *
     * @return List of selected items ids
     */
    public List<Integer> getSelectedItems() throws Exception {
        if (allowSelection) {
            List<Integer> items = new ArrayList<>(mSelectedItems.size());
            for (int i = 0; i < mSelectedItems.size(); ++i)
                items.add(mSelectedItems.keyAt(i));
            return items;
        } else throw new Exception("Selection mode is disabled!");
    }

    public void removeItems(List<Integer> positions) {
        // Reverse-sort the list
        Collections.sort(positions, (lhs, rhs) -> rhs - lhs);
        // Split the list in ranges
        while (!positions.isEmpty())
            if (positions.size() == 1) {
                removeItem(positions.get(0));
                positions.remove(0);
            } else {
                int count = 1;
                while (positions.size() > count && positions.get(count).equals(positions.get(count - 1) - 1))
                    ++count;
                if (count == 1) {
                    removeItem(positions.get(0));
                } else
                    removeRange(positions.get(count - 1), count);
                for (int i = 0; i < count; ++i)
                    positions.remove(0);
            }
    }

    private void validateList(List<ItemInfo> dataList) {
        if (dataList == null)
            throw new IllegalArgumentException("The list cannot be null");
    }

    private void removeRange(int positionStart, int itemCount) {
        for (int i = 0; i < itemCount; ++i)
            mDataList.remove(positionStart);
        notifyItemRangeRemoved(positionStart, itemCount);
    }

    //-----------------animations--------------------------//

    public void animateTo(List<ItemInfo> models) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
    }

    public ItemInfo removeItem(int position) {
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mDataList.size());
        return mDataList.remove(position);
    }

    public void addItem(int position, ItemInfo model) {
        mDataList.add(position, model);
        notifyItemInserted(position);
        notifyItemChanged(position, mDataList.size());
    }

    public void moveItem(int fromPosition, int toPosition) {
        mDataList.add(toPosition, mDataList.remove(fromPosition));
        notifyItemMoved(fromPosition, toPosition);
    }

    private void applyAndAnimateRemovals(List<ItemInfo> newModels) {
        for (int i = mDataList.size() - 1; i >= 0; i--) {
            final ItemInfo model = mDataList.get(i);
            if (!newModels.contains(model))
                removeItem(i);
        }
    }

    private void applyAndAnimateAdditions(List<ItemInfo> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final ItemInfo model = newModels.get(i);
            if (!mDataList.contains(model))
                addItem(i, model);
        }
    }

    private void applyAndAnimateMovedItems(List<ItemInfo> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final ItemInfo model = newModels.get(toPosition);
            final int fromPosition = mDataList.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition)
                moveItem(fromPosition, toPosition);
        }
    }
}