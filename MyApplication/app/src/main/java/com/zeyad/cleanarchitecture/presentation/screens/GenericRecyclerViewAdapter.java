package com.zeyad.cleanarchitecture.presentation.screens;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.zeyad.cleanarchitecture.utilities.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rx.subscriptions.CompositeSubscription;

/**
 * @author by zeyad on 19/05/16.
 */
public abstract class GenericRecyclerViewAdapter<M, H extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<H> {

    public interface OnItemClickListener {
        void onItemClicked(int position, Object model, RecyclerView.ViewHolder holder);

        boolean onItemLongClicked(int position);
    }

    public final LayoutInflater mLayoutInflater;
    public List<M> mDataList;
    public OnItemClickListener mOnItemClickListener;
    public SparseBooleanArray mSelectedItems;
    public boolean mIsLoadingFooterAdded = false;
    private boolean mHasHeader = false, allowSelection = false;
    private CompositeSubscription mCompositeSubscription;

    public GenericRecyclerViewAdapter(Context context, List<M> list) {
        validateList(list);
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mDataList = list;
        mSelectedItems = new SparseBooleanArray();
        mCompositeSubscription = Utils.getNewCompositeSubIfUnsubscribed(mCompositeSubscription);
    }

    @Override
    public abstract H onCreateViewHolder(ViewGroup parent, int viewType);

    @Override
    public abstract void onBindViewHolder(H holder, int position);

    @Override
    public abstract int getItemViewType(int position);

    @Override
    public abstract long getItemId(int position);

    public abstract List<Integer> getSelectedItemsIds();

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    public boolean hasHeader() {
        return mHasHeader;
    }

    public void hasHeader(boolean hasHeader) {
        mHasHeader = hasHeader;
    }

    public boolean isAllowSelection() {
        return allowSelection;
    }

    public void setAllowSelection(boolean allowSelection) {
        this.allowSelection = allowSelection;
    }

    public void addLoading() {
        mIsLoadingFooterAdded = true;
        mDataList.add(null);
        notifyItemInserted(mDataList.size() - 1);
    }

    public void removeLoading() {
        mIsLoadingFooterAdded = false;
        int position = mDataList.size() - 1;
        if (mDataList.get(position) == null) {
            mDataList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void setItemList(List<M> dataSet) {
        mDataList.addAll(dataSet);
        validateList(mDataList);
        if (mDataList.get(0) != null)
            mDataList.add(0, null);
        notifyDataSetChanged();
    }

    public List<M> getDataList() {
        return mDataList;
    }

    public void setDataList(List<M> dataList) {
        validateList(dataList);
        mDataList = dataList;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

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

    private void validateList(List<M> dataList) {
        if (dataList == null)
            throw new IllegalArgumentException("The list cannot be null");
    }

    private void removeRange(int positionStart, int itemCount) {
        for (int i = 0; i < itemCount; ++i)
            mDataList.remove(positionStart);
        notifyItemRangeRemoved(positionStart, itemCount);
    }

    //-----------------animations--------------------------//

    public void animateTo(List<M> models) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
    }

    public M removeItem(int position) {
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mDataList.size());
        return mDataList.remove(position);
    }

    public void addItem(int position, M model) {
        mDataList.add(position, model);
        notifyItemInserted(position);
        notifyItemChanged(position, mDataList.size());
    }

    public void moveItem(int fromPosition, int toPosition) {
        mDataList.add(toPosition, mDataList.remove(fromPosition));
        notifyItemMoved(fromPosition, toPosition);
    }

    private void applyAndAnimateRemovals(List<M> newModels) {
        for (int i = mDataList.size() - 1; i >= 0; i--) {
            final M model = mDataList.get(i);
            if (!newModels.contains(model))
                removeItem(i);
        }
    }

    private void applyAndAnimateAdditions(List<M> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final M model = newModels.get(i);
            if (!mDataList.contains(model))
                addItem(i, model);
        }
    }

    private void applyAndAnimateMovedItems(List<M> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final M model = newModels.get(toPosition);
            final int fromPosition = mDataList.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition)
                moveItem(fromPosition, toPosition);
        }
    }
}