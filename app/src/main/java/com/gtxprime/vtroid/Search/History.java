package com.gtxprime.vtroid.Search;

import static com.gtxprime.vtroid.Utils.Utils.PREMIUM;
import static com.gtxprime.vtroid.Utils.Utils.makeLink;
import static com.gtxprime.vtroid.Utils.Utils.premiumBol;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.gtxprime.vtroid.Activities.Home;
import com.gtxprime.vtroid.Activities.WebActivity;
import com.gtxprime.vtroid.Activities.MediaSaver;
import com.gtxprime.vtroid.R;
import com.gtxprime.vtroid.Utils.Utils;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class History extends AppCompatActivity {
    private RecyclerView visitedPagesView;
    EditText searchLink;
    RadioGroup rg;
    String checked = "searchChecked";
    String domain;
    private List<VisitedPages> visitedPages;
    private HistorySQLite historySQLite;
    CardView moviesBtn, seriesBtn;
    public FirebaseFirestore firebaseFirestore;

    @Override
    protected void onResume() {
        viewsD();
        super.onResume();
    }

    @Override
    protected void onRestart() {
        viewsD();
        super.onRestart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history);
        firebaseFirestore = FirebaseFirestore.getInstance();

        viewsD();
        findViewById(R.id.hMain).setBackground(Utils.bgGrayGenerate(this));
        Utils.setPad(findViewById(R.id.hMain), "bottom", this);

        DocumentReference documentReferenceX1 = firebaseFirestore.collection("domain").document("domainName");
        documentReferenceX1.get().addOnSuccessListener(documentSnapshot -> {
            domain = (documentSnapshot.getString("value"));
        });
    }

    public class VisitedPagesAdapter extends RecyclerView.Adapter<VisitedPagesAdapter.VisitedPageItem> {
        @NotNull
        @Override
        public VisitedPageItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new VisitedPageItem(LayoutInflater.from(History.this).inflate(R.layout.history_item, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull VisitedPageItem holder, int position) {
            holder.bind(visitedPages.get(position));
        }

        @Override
        public int getItemCount() {
            return visitedPages.size();
        }

        class VisitedPageItem extends RecyclerView.ViewHolder {
            private final TextView title;
            private final TextView subtitle;

            VisitedPageItem(View itemView) {
                super(itemView);
                title = itemView.findViewById(R.id.row_history_title);
                subtitle = itemView.findViewById(R.id.row_history_subtitle);

                itemView.setOnClickListener(v -> {
                    String fUrl = Utils.makeLink(visitedPages.get(getAbsoluteAdapterPosition()).link);
                    Intent intent = new Intent(History.this, WebActivity.class);
                    intent.putExtra("links", fUrl);
                    startActivity(intent);
                });

                itemView.findViewById(R.id.row_history_menu).setOnClickListener(view -> {
                    final PopupMenu popup = new PopupMenu(History.this, view);
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
                                Intent intent = new Intent(History.this, WebActivity.class);
                                intent.putExtra("links", fUrl);
                                startActivity(intent);
                                return true;
                            } else if (i == R.id.history_copy) {
                                Toast.makeText(History.this, getString(R.string.copy_msg), Toast.LENGTH_SHORT).show();
                                ClipboardManager clipboardManager = (ClipboardManager) History.this.getSystemService(CLIPBOARD_SERVICE);
                                clipboardManager.setPrimaryClip(ClipData.newPlainText("Copied URL", visitedPages.get(getAbsoluteAdapterPosition()).link));
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
                subtitle.setText(page.link);
            }
        }
    }

    private void isHistoryEmpty() {
        if (visitedPages.isEmpty()) {
            findViewById(R.id.llNoHistory).setVisibility(View.VISIBLE);
            findViewById(R.id.llShowHistory).setVisibility(View.INVISIBLE);
        } else {
            findViewById(R.id.llNoHistory).setVisibility(View.INVISIBLE);
            findViewById(R.id.llShowHistory).setVisibility(View.VISIBLE);
        }
    }

    @SuppressLint({"NonConstantResourceId", "NotifyDataSetChanged"})
    public void viewsD() {
        visitedPagesView = findViewById(R.id.rvHistoryList);
        ImageView clearHistory = findViewById(R.id.btn_delete_history);
        searchLink = findViewById(R.id.searchText);
        rg = findViewById(R.id.radioGroup1);
        SharedPreferences premiumPref = getSharedPreferences(PREMIUM, Context.MODE_PRIVATE);
        boolean isPremium = premiumPref.getBoolean(premiumBol, false);
        moviesBtn = findViewById(R.id.moviesBtn);
        seriesBtn = findViewById(R.id.seriesBtn);
        RadioButton adv_downloadRadio = findViewById(R.id.adv_downloadRadio);
        RadioButton searchRadio = findViewById(R.id.searchRadio);

        if (!isPremium) {
            moviesBtn.setVisibility(View.GONE);
            seriesBtn.setVisibility(View.GONE);
            adv_downloadRadio.setVisibility(View.GONE);
            searchRadio.setChecked(true);
        } else {
            moviesBtn.setVisibility(View.VISIBLE);
            seriesBtn.setVisibility(View.VISIBLE);
            adv_downloadRadio.setVisibility(View.VISIBLE);
            adv_downloadRadio.setChecked(true);
            checked = "adv_downloadChecked";
        }

        moviesBtn.setOnClickListener(view -> startActivity(new Intent(History.this, WebActivity.class).putExtra("links", "http://" + domain + "/public_html/movies/all/newest.html")));

        seriesBtn.setOnClickListener(view -> startActivity(new Intent(History.this, WebActivity.class).putExtra("links", "http://" + domain + "/public_html/series/all/newest.html")));

        searchLink.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = searchLink.getText().toString().trim();
                if (text.isEmpty()) {
                    rg.setVisibility(View.GONE);
                } else rg.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        rg.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.searchRadio) {
                checked = "searchChecked";
            } else if (checkedId == R.id.downloadRadio) {
                checked = "downloadChecked";
            } else if (checkedId == R.id.adv_downloadRadio) {
                checked = "adv_downloadChecked";
            }
        });

        searchLink.setOnEditorActionListener((v, actionId, event) -> {
            switch (checked) {
                case "downloadChecked" -> {
                    if (searchLink.getText().toString().contains("www.instagram.com")) {
                        startActivity(new Intent(History.this, MediaSaver.class).putExtra("dowLinks", searchLink.getText().toString().trim()));
                    }
                    searchLink.setText(null);
                }
                case "adv_downloadChecked" -> {
                    if (searchLink.getText().toString().trim().contains("https") && searchLink.getText().toString().contains("http")) {
                        Toast.makeText(History.this, "You can't put links in advance search", Toast.LENGTH_SHORT).show();
                    } else {
                        startActivity(new Intent(History.this, WebActivity.class).putExtra("links", "http://" + domain + "/public_html/search?q=" + searchLink.getText().toString().trim()));
                    }
                    searchLink.setText(null);
                }
                case "searchChecked" -> {
                    startActivity(new Intent(History.this, WebActivity.class).putExtra("links", makeLink(searchLink.getText().toString()).trim()));
                    searchLink.setText(null);
                }
                default -> Toast.makeText(this, "Huihuihui", Toast.LENGTH_SHORT).show();
            }
            return true;
        });

        historySQLite = new HistorySQLite(History.this);
        visitedPages = historySQLite.getAllVisitedPages();

        visitedPagesView.setLayoutManager(new LinearLayoutManager(History.this));
        visitedPagesView.setAdapter(new VisitedPagesAdapter());

        clearHistory.setOnClickListener(v -> {
            historySQLite.clearHistory();
            visitedPages.clear();
            Objects.requireNonNull(visitedPagesView.getAdapter()).notifyDataSetChanged();
            isHistoryEmpty();
        });
        isHistoryEmpty();
    }

    @Override
    public void onBackPressed() {
        Utils.returnToHome(History.this, Home.class);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }
}
