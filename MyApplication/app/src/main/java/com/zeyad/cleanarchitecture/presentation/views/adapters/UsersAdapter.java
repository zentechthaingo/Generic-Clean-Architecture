package com.zeyad.cleanarchitecture.presentation.views.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.jakewharton.rxbinding.view.RxView;
import com.zeyad.cleanarchitecture.R;
import com.zeyad.cleanarchitecture.presentation.model.UserModel;
import com.zeyad.cleanarchitecture.presentation.views.UserViewHolder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import rx.subscriptions.CompositeSubscription;
// TODO: 4/10/16 Generalize!

/**
 * Adapter that manages a collection of {@link UserModel}.
 */
public class UsersAdapter extends RecyclerView.Adapter<UserViewHolder> {

    public interface OnItemClickListener {
        void onUserItemClicked(int position, UserModel userModel, UserViewHolder holder);

        boolean onItemLongClicked(int position);
    }

    private List<UserModel> mUsersCollection;
    private final LayoutInflater mLayoutInflater;
    private OnItemClickListener mOnItemClickListener;
    private CompositeSubscription mCompositeSubscription;
    private SparseBooleanArray mSelectedItems;

    public UsersAdapter(Context context, Collection<UserModel> usersCollection) {
        validateUsersCollection(usersCollection);
        mLayoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mUsersCollection = (List<UserModel>) usersCollection;
        mSelectedItems = new SparseBooleanArray();
        mCompositeSubscription = new CompositeSubscription();
    }

    @Override
    public int getItemCount() {
        return (mUsersCollection != null) ? mUsersCollection.size() : 0;
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new UserViewHolder(mLayoutInflater.inflate(R.layout.row_user, parent, false));
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, final int position) {
        final UserModel userModel = mUsersCollection.get(position);
        holder.getTextViewTitle().setText(userModel.getFullName());
        holder.getRl_row_user().setBackgroundColor(isSelected(position) ? Color.GRAY : Color.WHITE);
        mCompositeSubscription.add(
                RxView.clicks(holder.itemView).subscribe(aVoid -> {
                    if (mOnItemClickListener != null)
                        mOnItemClickListener.onUserItemClicked(position, userModel, holder);
                }));
        mCompositeSubscription.add(RxView.longClicks(holder.itemView).subscribe(aVoid -> {
            if (mOnItemClickListener != null)
                mOnItemClickListener.onItemLongClicked(mUsersCollection.indexOf(userModel));
        }));
    }

    @Override
    public long getItemId(int position) {
        return mUsersCollection.get(position).getUserId();
    }

    public List<UserModel> getmUsersCollection() {
        return mUsersCollection;
    }

    public void setUsersCollection(Collection<UserModel> mUsersCollection) {
        validateUsersCollection(mUsersCollection);
        this.mUsersCollection = (List<UserModel>) mUsersCollection;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public Collection<Integer> getSelectedItemsIds() {
        ArrayList<Integer> integers = new ArrayList<>();
        for (UserModel userModel : mUsersCollection)
            if (userModel.isChecked())
                integers.add(userModel.getUserId());
        return integers;
    }

    private void validateUsersCollection(Collection<UserModel> usersCollection) {
        if (usersCollection == null)
            throw new IllegalArgumentException("The list cannot be null");
    }

    public CompositeSubscription getmCompositeSubscription() {
        return mCompositeSubscription;
    }

    /**
     * Indicates if the item at position position is selected
     *
     * @param position Position of the item to check
     * @return true if the item is selected, false otherwise
     */
    public boolean isSelected(int position) {
        return getmSelectedItems().contains(position);
    }

    /**
     * Toggle the selection status of the item at a given position
     *
     * @param position Position of the item to toggle the selection status for
     */
    public void toggleSelection(int position) {
        if (mSelectedItems.get(position, false)) {
            mSelectedItems.delete(position);
            mUsersCollection.get(position).setChecked(false);
        } else {
            mSelectedItems.put(position, true);
            mUsersCollection.get(position).setChecked(true);
        }
        notifyItemChanged(position);
    }

    /**
     * Clear the selection status for all items
     */
    public void clearSelection() {
        List<Integer> selection = getmSelectedItems();
        mSelectedItems.clear();
        for (Integer i : selection) {
            mUsersCollection.get(i).setChecked(false);
            notifyItemChanged(i);
        }
    }

    /**
     * Count the selected items
     *
     * @return Selected items count
     */
    public int getSelectedItemCount() {
        return mSelectedItems.size();
    }

    /**
     * Indicates the list of selected items
     *
     * @return List of selected items ids
     */
    public List<Integer> getmSelectedItems() {
        List<Integer> items = new ArrayList<>(mSelectedItems.size());
        for (int i = 0; i < mSelectedItems.size(); ++i)
            items.add(mSelectedItems.keyAt(i));
        return items;
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

    private void removeRange(int positionStart, int itemCount) {
        for (int i = 0; i < itemCount; ++i)
            mUsersCollection.remove(positionStart);
        notifyItemRangeRemoved(positionStart, itemCount);
    }

    //-----------------animations--------------------------//

    public void animateTo(List<UserModel> models) {
        applyAndAnimateRemovals(models);
        applyAndAnimateAdditions(models);
        applyAndAnimateMovedItems(models);
    }

    private void applyAndAnimateRemovals(List<UserModel> newModels) {
        for (int i = mUsersCollection.size() - 1; i >= 0; i--) {
            final UserModel model = mUsersCollection.get(i);
            if (!newModels.contains(model))
                removeItem(i);
        }
    }

    private void applyAndAnimateAdditions(List<UserModel> newModels) {
        for (int i = 0, count = newModels.size(); i < count; i++) {
            final UserModel model = newModels.get(i);
            if (!mUsersCollection.contains(model))
                addItem(i, model);
        }
    }

    private void applyAndAnimateMovedItems(List<UserModel> newModels) {
        for (int toPosition = newModels.size() - 1; toPosition >= 0; toPosition--) {
            final UserModel model = newModels.get(toPosition);
            final int fromPosition = mUsersCollection.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition)
                moveItem(fromPosition, toPosition);
        }
    }

    public UserModel removeItem(int position) {
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mUsersCollection.size());
        return mUsersCollection.remove(position);
    }

    public void addItem(int position, UserModel model) {
        mUsersCollection.add(position, model);
        notifyItemInserted(position);
        notifyItemChanged(position, mUsersCollection.size());
    }

    public void moveItem(int fromPosition, int toPosition) {
        mUsersCollection.add(toPosition, mUsersCollection.remove(fromPosition));
        notifyItemMoved(fromPosition, toPosition);
    }
}