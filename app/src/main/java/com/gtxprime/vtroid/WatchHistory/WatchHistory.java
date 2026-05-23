package com.gtxprime.vtroid.WatchHistory;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gtxprime.vtroid.R;
import com.gtxprime.vtroid.Search.VisitedPages;
import com.gtxprime.vtroid.Utils.Utils;
import com.gtxprime.vtroid.VideoPlayer.PlayerActivity;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class WatchHistory extends AppCompatActivity {
    private List<VisitedPages> visitedPages;
    private WListSQLite historySQLite;
    private RecyclerView visitedPagesView;
    ImageView clearHistory;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_history);

        Utils.setPad(findViewById(R.id.mainHolder), "bottom", this);
        findViewById(R.id.mainHolder).setBackground(Utils.bgGrayGenerate(this));

        historySQLite = new WListSQLite(WatchHistory.this);
        visitedPages = historySQLite.getAllVisitedPages();
        visitedPagesView = findViewById(R.id.rvWatchedList);
        clearHistory = findViewById(R.id.btn_delete_history);

        visitedPagesView.setLayoutManager(new LinearLayoutManager(WatchHistory.this));
        visitedPagesView.setAdapter(new WatchedAdapter());

        clearHistory.setOnClickListener(v -> {
            historySQLite.clearHistory();
            visitedPages.clear();
            Objects.requireNonNull(visitedPagesView.getAdapter()).notifyDataSetChanged();
            isHistoryEmpty();
        });
         isHistoryEmpty();
    }

    public class WatchedAdapter extends RecyclerView.Adapter<WatchedAdapter.WatchedItem> {
        @NotNull
        @Override
        public WatchedItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new WatchedItem(LayoutInflater.from(WatchHistory.this).inflate(R.layout.w_history_item, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull WatchedItem holder, int position) {
            holder.bind(visitedPages.get(position));
        }

        @Override
        public int getItemCount() {
            return visitedPages.size();
        }

        class WatchedItem extends RecyclerView.ViewHolder {
            private final TextView title;

            WatchedItem(View itemView) {
                super(itemView);
                title = itemView.findViewById(R.id.video_name);

                itemView.setOnClickListener(v -> {
                    String fUrl = Utils.makeLink(visitedPages.get(getAbsoluteAdapterPosition()).link);
                    startActivity(new Intent(WatchHistory.this, PlayerActivity.class).putExtra("playLink", fUrl));
                });

                itemView.findViewById(R.id.video_menu_more).setOnClickListener(view -> {
                    final PopupMenu popup = new PopupMenu(WatchHistory.this, view);
                    popup.getMenuInflater().inflate(R.menu.menu_history, popup.getMenu());
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        public boolean onMenuItemClick(MenuItem item) {
                            int i = item.getItemId();
                            if (i == R.id.history_delete) {
                                historySQLite.deleteFromHistory(visitedPages.get(getAbsoluteAdapterPosition()).link);
                                visitedPages.remove(getAbsoluteAdapterPosition());
                                notifyItemRemoved(getAbsoluteAdapterPosition());
                                isHistoryEmpty();
                                return true;
                            } else if (i == R.id.history_open) {
                                String fUrl = Utils.makeLink(visitedPages.get(getAbsoluteAdapterPosition()).link);
                                startActivity(new Intent(WatchHistory.this, PlayerActivity.class).putExtra("playLink", fUrl));
                                return true;
                            } else if (i == R.id.history_copy) {
                                Toast.makeText(WatchHistory.this, getString(R.string.copy_msg), Toast.LENGTH_SHORT).show();
                                ClipboardManager clipboardManager = (ClipboardManager) WatchHistory.this.getSystemService(CLIPBOARD_SERVICE);
                                clipboardManager.setPrimaryClip(ClipData.newPlainText("Name Copied", visitedPages.get(getAbsoluteAdapterPosition()).title));
                                return true;
                            } else {
                                return onMenuItemClick(item);
                            }
                        }
                    });
                    popup.show();
                });
            }

            void bind(VisitedPages page) {
                title.setText(page.title);
            }
        }
    }


    private void isHistoryEmpty() {
        if (visitedPages.isEmpty()) {
            findViewById(R.id.noHistorWatch).setVisibility(View.VISIBLE);
            findViewById(R.id.rvWatchedList).setVisibility(View.INVISIBLE);
        } else {
            findViewById(R.id.noHistorWatch).setVisibility(View.INVISIBLE);
            findViewById(R.id.rvWatchedList).setVisibility(View.VISIBLE);
        }
    }
}