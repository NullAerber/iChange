package com.carporange.ichange.fragment;


import android.app.Fragment;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.carporange.ichange.R;
import com.carporange.ichange.ui.activity.WebActivity;
import com.carporange.ichange.ui.base.BaseFragment;
import com.carporange.ichange.util.ImageService;
import com.carporange.ichange.util.LinkerServer;
import com.dexafree.materialList.card.Card;
import com.dexafree.materialList.card.CardProvider;
import com.dexafree.materialList.card.OnActionClickListener;
import com.dexafree.materialList.card.action.TextViewAction;
import com.dexafree.materialList.listeners.RecyclerItemClickListener;
import com.dexafree.materialList.view.MaterialListView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * A simple {@link Fragment} subclass.
 */
public class MurmurFragment extends BaseFragment {
    View view_murmur;
    private MaterialListView mListView;
    Stack<String> list_id;
    Stack<String> list_title;
    Stack<String> list_url;
    Stack<String> list_descir;

    List<Card> cards;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view_murmur = inflater.inflate(R.layout.fragment_murmur, container, false);
        mListView = (MaterialListView) view_murmur.findViewById(R.id.material_listview);

        fillArray();

        // Add the ItemTouchListener
        mListView.addOnItemTouchListener(new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull Card card, int position) {
                Log.d("CARD_TYPE", "" + card.getTag() + "++++++++" + position);
                final String[] record = ((String) card.getTag()).split(";");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        Intent intent = new Intent(getActivity(), WebActivity.class);
                        intent.putExtra(getString(R.string.URL), record[0]);
                        intent.putExtra(getString(R.string.BAR_TITLE), record[1]);
                        getActivity().startActivity(intent);
                        handler.sendEmptyMessage(0);
                        Looper.loop();
                    }
                }).start();
            }

            @Override
            public void onItemLongClick(@NonNull Card card, int position) {
                Log.d("LONG_CLICK", "" + card.getTag());
            }
        });

        return view_murmur;
    }

    private void fillArray() {
        cards = new ArrayList<>();

        list_id = new Stack<>();
        list_title = new Stack<>();
        list_url = new Stack<>();
        list_descir = new Stack<>();

        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                LinkerServer linkerServer = new LinkerServer("murmur");
                if (linkerServer.Linker()) {
                    String response = linkerServer.getResponse();
                    String[] str_record = response.split("\\|");
                    for (int i = 0; i < str_record.length; ++i) {
                        String[] record = str_record[i].split(";");
                        list_id.push(record[0]);
                        list_title.push(record[1]);
                        list_url.push(record[2]);
                        list_descir.push(record[3]);
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            int length = list_id.size();
                            for (int i = 0; i < length; i++) {
                                cards.add(CreateNewCard(list_id.pop(), list_title.pop(),
                                        list_url.pop(), list_descir.pop()));
                            }
                            mListView.getAdapter().addAll(cards);
                        }
                    });
                } else {
                    Toast.makeText(getActivity(), R.string.REQUEST_FAIL, Toast.LENGTH_SHORT).show();
                    cards.add(CreateNewCard("", "", "", ""));
                    mListView.getAdapter().addAll(cards);
                }
                handler.sendEmptyMessage(0);
                Looper.loop();
            }
        }).start();
    }

    private Card CreateNewCard(final String id, final String title, final String url,
                               final String description) {
        final CardProvider provider = new Card.Builder(getContext())
                .setTag(url + ";" + title)
                .withProvider(new CardProvider())
                .setLayout(R.layout.material_image_with_buttons_card)
                .setTitle(title)
                .setTitleColor(getResources().getColor(R.color.white))
                .setDescription(description)
                .addAction(R.id.left_text_button, new TextViewAction(getContext())
                        .setText(R.string.mf_editor)
                        .setTextResourceColor(R.color.black_button))
                .addAction(R.id.right_text_button, new TextViewAction(getContext())
                        .setText(R.string.mf_more)
                        .setTextResourceColor(R.color.colorTheme)
                        .setListener(new OnActionClickListener() {
                            @Override
                            public void onActionClicked(View view, Card card) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Looper.prepare();
                                        Intent intent = new Intent(getActivity(), WebActivity.class);
                                        intent.putExtra(getString(R.string.URL), url);
                                        intent.putExtra(getString(R.string.BAR_TITLE), title);
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
                    data = ImageService.getImage(getString(R.string.LINKUSRL) + "murmur/" + id + ".jpg");
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
