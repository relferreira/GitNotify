package com.relferreira.gitnotify.ui.pages;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.relferreira.gitnotify.R;
import com.relferreira.gitnotify.model.Comment;
import com.relferreira.gitnotify.util.RoundBitmapHelper;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by relferreira on 2/11/17.
 */

public class CommentAdapter extends PagesAdapter<CommentAdapter.IssueCommentViewHolder> {

    private final DateFormat dateFormater;
    private Context context;
    private List<Comment> comments;

    public CommentAdapter(Context context, List<Comment> comments) {
        this.context = context;
        this.comments = comments;

        this.dateFormater = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT);
    }

    @Override
    public IssueCommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_issue_comment, parent, false);
        return new IssueCommentViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(IssueCommentViewHolder holder, int position) {
        Comment comment = comments.get(position);
        String author = comment.user().login();
        holder.author.setText(author);
        holder.text.setText(comment.body());
        holder.date.setText(dateFormater.format(comment.createdAt()));
        holder.image.setContentDescription(String.format(context.getString(R.string.action_a11y_image), author));
        Picasso.with(context)
                .load(String.format(context.getString(R.string.profile_image_format), comment.user().avatarUrl()))
                .into(holder.image, new Callback() {
                    @Override
                    public void onSuccess() {
                        BitmapDrawable img = ((BitmapDrawable) holder.image.getDrawable());
                        holder.image.setImageDrawable(RoundBitmapHelper.getRoundImage(img, context.getResources()));
                    }

                    @Override
                    public void onError() {
                        holder.image.setImageResource(R.drawable.error_placeholder);
                    }
                });
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    @SuppressWarnings("unchecked")
    public void setItems(List items) {
        comments.addAll((List<Comment>)items);
        notifyDataSetChanged();
    }

    public class IssueCommentViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.comment_author)
        TextView author;
        @BindView(R.id.comment_date)
        TextView date;
        @BindView(R.id.comment_text)
        TextView text;
        @BindView(R.id.comment_user_image)
        ImageView image;

        public IssueCommentViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}