package org.sorz.lab.smallcloudemoji.adapters;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import org.sorz.lab.smallcloudemoji.R;
import org.sorz.lab.smallcloudemoji.db.DaoSession;
import org.sorz.lab.smallcloudemoji.db.Repository;
import org.sorz.lab.smallcloudemoji.db.RepositoryDao;

import java.util.List;

/**
 * Get repositories from database and generate a view for each them.
 */
public class RepositoryAdapter extends BaseAdapter {
    final private LayoutInflater inflater;
    private List<Repository> repositories;
    private final RepositoryDao repositoryDao;

    public RepositoryAdapter(Context context, DaoSession daoSession) {
        super();
        repositoryDao = daoSession.getRepositoryDao();
        inflater = LayoutInflater.from(context);

        loadRepositories();
    }

    private void loadRepositories() {
        repositories = repositoryDao.queryBuilder()
                .orderAsc(RepositoryDao.Properties.Order)
                .list();
    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public int getCount() {
        return repositories.size();
    }

    @Override
    public Repository getItem(int position) {
        return repositories.get(position);
    }

    @Override
    public long getItemId(int position) {
        return repositories.get(position).getId();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = inflater.inflate(R.layout.item_repository, parent, false);
        TextView alias = (TextView) convertView.findViewById(R.id.repository_alias);
        TextView url = (TextView) convertView.findViewById(R.id.repository_url);
        ImageButton hiddenButton = (ImageButton) convertView.findViewById(R.id.button_hidden);
        Repository repository = getItem(position);
        alias.setText(repository.getAlias());
        url.setText(repository.getUrl());
        if (repository.getHidden())
            hiddenButton.setBackgroundResource(R.drawable.ic_eye_slash);
        else
            hiddenButton.setBackgroundResource(R.drawable.ic_eye_normal);
        convertView.findViewById(R.id.repository_buttons).setTag(repository);
        convertView.findViewById(R.id.repository_progressbar).setVisibility(View.GONE);
        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return repositories.isEmpty();
    }

    @Override
    public void notifyDataSetChanged() {
        loadRepositories();
        super.notifyDataSetChanged();
    }
}
