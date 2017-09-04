package com.carporange.ichange.ui.activity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.carporange.ichange.R;
import com.carporange.ichange.ui.base.AerberBaeeActivity;
import com.carporange.ichange.util.ImageService;
import com.carporange.ichange.util.LinkerServer;
import com.carporange.ichange.widget.SearchBarLayout;
import com.dexafree.materialList.card.Card;
import com.dexafree.materialList.card.CardProvider;
import com.dexafree.materialList.card.OnActionClickListener;
import com.dexafree.materialList.card.action.TextViewAction;
import com.dexafree.materialList.view.MaterialListView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AerberBaeeActivity {
    private MaterialListView mListView;
    SearchBarLayout mSearchBar;
    List<String> list_title;
    List<String> list_tag;
    List<String> list_id;
    List<String> list_descir;
    List<String> list_designer;

    List<Card> cards;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initBar(this.getIntent().getStringExtra("bar_title"));

        initView();
        RealTimeSearch();
    }

    private void RealTimeSearch() {
        //Real Time Search
        mSearchBar.getEditor().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Search(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mSearchBar.getEditor().setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                String key = v.getText().toString();
                if (TextUtils.isEmpty(key)) {
                    Toast.makeText(SearchActivity.this, R.string.search_cannot_null,Toast.LENGTH_LONG).show();
                    return true;
                }

                Search(key);

                Toast.makeText(SearchActivity.this, R.string.search_finish,Toast.LENGTH_LONG).show();
                return true;
            }
        });
        //Cancel Search
        mSearchBar.setCancelSearchLayout(new SearchBarLayout.OnCancelSearchLayout() {
            @Override
            public void OnCancel() {
                Search("");
            }
        });
    }

    private void initView() {
        mListView = (MaterialListView) findViewById(R.id.search_material_listview);
        mSearchBar = (SearchBarLayout) findViewById(R.id.searchBarlayout);

        cards = new ArrayList<>();
        list_title = new ArrayList<>();
        list_designer = new ArrayList<>();
        list_tag = new ArrayList<>();
        list_id = new ArrayList<>();
        list_descir = new ArrayList<>();

        new Thread(new Runnable() {
            @Override
            public void run() {
                LinkerServer linkerServer = new LinkerServer("cloth");
                if (linkerServer.Linker()) {
                    String response = linkerServer.getResponse();
                    String[] str_record = response.split("\\|");
                    for (int i = 0; i < str_record.length; ++i) {
                        String[] record = str_record[i].split(";");
                        list_title.add(record[0]);
                        list_designer.add(record[1]);
                        list_tag.add(record[2]);
                        list_id.add(record[3]);
                        list_descir.add(record[4]);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            int length = list_title.size();
                            for (int i = 0; i < length; i++) {
                                cards.add(CreateNewCard(list_title.get(i), list_designer.get(i), list_descir.get(i),
                                        list_tag.get(i), list_id.get(i)));
                            }
                            mListView.getAdapter().addAll(cards);
                        }
                    });
                } else toast(getString(R.string.REQUEST_FAIL));
            }
        }).start();
    }

    private void Search(String key) {
        mListView.getAdapter().clearAll();
        mListView.getAdapter().addAll(cards);
        if (!TextUtils.isEmpty(key)) {
            for (int i = 0; i < list_tag.size(); i++) {
                if ((list_title.get(i).indexOf(key)  == -1)  && (list_tag.get(i).indexOf(key) == -1)
                        && (list_designer.get(i).indexOf(key) == -1) && (list_descir.get(i).indexOf(key) == -1)){
                    mListView.getAdapter().remove(cards.get(i),false);
                }
            }
        }
        mListView.getAdapter().notifyDataSetChanged();
    }

    private Card CreateNewCard(final String title, String designer, String description, String tag,
                               final String id) {
        final CardProvider provider = new Card.Builder(this)
                .setTag(id)
                .withProvider(new CardProvider())
                .setLayout(R.layout.material_basic_image_buttons_card_layout)
                .setTitle(title)
                .setSubtitle(designer)
                .setSubtitleColor(+R.color.black)
                .setDescription(description)
                .addAction(R.id.left_text_button, new TextViewAction(this)
                        .setText(tag)
                        .setTextResourceColor(R.color.black_button))
                .addAction(R.id.right_text_button, new TextViewAction(this)
                        .setText("详情…")
                        .setTextResourceColor(R.color.colorTheme)
                        .setListener(new OnActionClickListener() {
                            @Override
                            public void onActionClicked(View view, Card card) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent(SearchActivity.this, ClothDetailActivity.class);
                                        intent.putExtra("url", id);
                                        SearchActivity.this.startActivity(intent);
                                    }
                                }).start();
                            }
                        }));

        new Thread(new Runnable() {
            @Override
            public void run() {
                byte[] data = new byte[0];
                try {
                    data = ImageService.getImage(getString(R.string.LINKUSRL) + "cloth/" + id + ".jpg");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                final byte[] finalData = data;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        provider.setDrawable(new BitmapDrawable(BitmapFactory.decodeByteArray(finalData, 0, finalData.length)));
                    }
                });
            }
        }).start();

        return provider.endConfig().build();
    }
}
