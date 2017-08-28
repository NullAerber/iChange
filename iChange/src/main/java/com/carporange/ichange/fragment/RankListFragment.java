package com.carporange.ichange.fragment;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.carporange.ichange.R;
import com.carporange.ichange.ui.activity.ClothDetailActivity;
import com.carporange.ichange.ui.base.BaseFragment;
import com.carporange.ichange.util.ImageService;
import com.carporange.ichange.util.LinkerServer;
import com.dexafree.materialList.card.Card;
import com.dexafree.materialList.card.CardProvider;
import com.dexafree.materialList.card.OnActionClickListener;
import com.dexafree.materialList.card.action.TextViewAction;
import com.dexafree.materialList.view.MaterialListView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RankListFragment extends BaseFragment {
    View view_rank_list;

    private MaterialListView mListView;
    List<String> list_title;
    List<String> list_tag;
    List<String> list_id;
    List<String> list_descir;
    List<String> list_designer;

    List<Card> cards;
    String response;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view_rank_list = inflater.inflate(R.layout.fragment_ranklist, container, false);
        mListView = (MaterialListView) view_rank_list.findViewById(R.id.search_material_listview);

        initView();
        return view_rank_list;
    }

    private void initView() {
        cards = new ArrayList<>();
        list_title = new ArrayList<>();
        list_designer = new ArrayList<>();
        list_tag = new ArrayList<>();
        list_id = new ArrayList<>();
        list_descir = new ArrayList<>();

        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                LinkerServer linkerServer = new LinkerServer("cloth");
                if (linkerServer.Linker()) {
                    response = linkerServer.getResponse();
                    String[] str_record = response.split("\\|");
                    for (int i = 0; i < str_record.length; ++i) {
                        String[] record = str_record[i].split(";");
                        list_title.add(record[0]);
                        list_designer.add(record[1]);
                        list_tag.add(record[2]);
                        list_id.add(record[3]);
                        list_descir.add(record[4]);
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            int length = list_title.size();
                            for (int i = 0; i < length; i++) {
                                cards.add(CreateNewCard(list_title.get(i), list_designer.get(i),
                                        list_descir.get(i), list_tag.get(i),
                                        list_id.get(i)));
                            }
                            mListView.getAdapter().addAll(cards);
                        }
                    });
                } else {
                    Toast.makeText(getActivity(), R.string.request_fail, Toast.LENGTH_SHORT).show();
                }
                handler.sendEmptyMessage(0);
                Looper.loop();
            }
        }).start();

    }

    private Card CreateNewCard(final String title, String designer, String description, String tag,
                               final String id) {
        final CardProvider provider = new Card.Builder(getContext())
                .setTag(id)
                .withProvider(new CardProvider())
                .setLayout(R.layout.material_basic_image_buttons_card_layout)
                .setTitle(title)
                .setSubtitle(designer)
                .setSubtitleColor(+R.color.black)
                .setDescription(description)
                .addAction(R.id.left_text_button, new TextViewAction(getContext())
                        .setText(tag)
                        .setTextResourceColor(R.color.black_button))
                .addAction(R.id.right_text_button, new TextViewAction(getContext())
                        .setText("详情…")
                        .setTextResourceColor(R.color.Themered)
                        .setListener(new OnActionClickListener() {
                            @Override
                            public void onActionClicked(View view, Card card) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Looper.prepare();
                                        Intent intent = new Intent(getActivity(), ClothDetailActivity.class);
                                        intent.putExtra("url", id);
                                        getActivity().startActivity(intent);
                                        handler.sendEmptyMessage(0);
                                        Looper.loop();
                                    }
                                }).start();
                            }
                        }));

        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                byte[] data = new byte[0];
                try {
                    data = ImageService.getImage(getString(R.string.LinkUrl) + "cloth/" + id + ".jpg");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                final byte[] finalData = data;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        provider.setDrawable(new BitmapDrawable(BitmapFactory.decodeByteArray(finalData, 0, finalData.length)));
                    }
                });
                handler.sendEmptyMessage(0);
                Looper.loop();
            }
        }).start();

        return provider.endConfig().build();
    }


}
