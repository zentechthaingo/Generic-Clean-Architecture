package com.zeyad.cleanarchitecture.presentation.views.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.jakewharton.rxbinding.view.RxView;
import com.zeyad.cleanarchitecture.R;
import com.zeyad.cleanarchitecture.presentation.model.UserModel;
import com.zeyad.cleanarchitecture.presentation.views.UserViewHolder;

import java.util.Collection;
import java.util.List;

import rx.Subscription;

/**
 * Adapter that manages a collection of {@link UserModel}.
 */
public class UsersAdapter extends RecyclerView.Adapter<UserViewHolder> {

    public interface OnItemClickListener {
        void onUserItemClicked(UserModel userModel);
    }

    private List<UserModel> usersCollection;
    private final LayoutInflater layoutInflater;
    private OnItemClickListener onItemClickListener;
    private Subscription itemSubscription;

    public UsersAdapter(Context context, Collection<UserModel> usersCollection) {
        validateUsersCollection(usersCollection);
        layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.usersCollection = (List<UserModel>) usersCollection;
    }

    @Override
    public int getItemCount() {
        return (usersCollection != null) ? usersCollection.size() : 0;
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new UserViewHolder(layoutInflater.inflate(R.layout.row_user, parent, false));
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, final int position) {
        final UserModel userModel = usersCollection.get(position);
        holder.getTextViewTitle().setText(userModel.getFullName());
        itemSubscription = RxView.clicks(holder.itemView).subscribe(aVoid -> {
            if (onItemClickListener != null)
                onItemClickListener.onUserItemClicked(userModel);
        });
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setUsersCollection(Collection<UserModel> usersCollection) {
        validateUsersCollection(usersCollection);
        this.usersCollection = (List<UserModel>) usersCollection;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    private void validateUsersCollection(Collection<UserModel> usersCollection) {
        if (usersCollection == null)
            throw new IllegalArgumentException("The list cannot be null");
    }

    public Subscription getItemSubscription() {
        return itemSubscription;
    }

    public void setItemSubscription(Subscription itemSubscription) {
        this.itemSubscription = itemSubscription;
    }
}