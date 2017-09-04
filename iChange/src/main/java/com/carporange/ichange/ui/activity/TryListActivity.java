package com.carporange.ichange.ui.activity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.carporange.ichange.R;
import com.carporange.ichange.ui.base.AerberBaeeActivity;
import com.carporange.ichange.util.ImageService;
import com.carporange.ichange.util.LinkerServer;
import com.dexafree.materialList.card.Card;
import com.dexafree.materialList.card.CardProvider;
import com.dexafree.materialList.card.OnActionClickListener;
import com.dexafree.materialList.card.action.TextViewAction;
import com.dexafree.materialList.listeners.OnDismissCallback;
import com.dexafree.materialList.view.MaterialListView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import cn.bmob.imdemo.model.UserModel;

/**
 * Created by Porster on 16/6/8.
 */

public class TryListActivity extends AerberBaeeActivity {
    private MaterialListView mListView;

    Stack<Card> cards;
    String response;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_try_list);
        initBar(this.getIntent().getStringExtra(getString(R.string.BAR_TITLE)));

        initView();

        // Set the dismiss listener
        mListView.setOnDismissCallback(new OnDismissCallback() {
            @Override
            public void onDismiss(@NonNull Card card, int position) {
                showCenterPopupWindow(mListView,card);
            }
        });
    }

    private void initView() {
        mListView = (MaterialListView) findViewById(R.id.search_material_listview);
        cards = new Stack<>();

        new Thread(new Runnable() {
            @Override
            public void run() {
                List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("username", UserModel.getInstance().getCurrentUser().getUsername()));
                LinkerServer linkerServer = new LinkerServer("try", params);

                if (linkerServer.Linker()) {
                    response = linkerServer.getResponse();
                    final String[] id_record = response.split("\\|");

                    for (int i = 0; i < id_record.length; ++i) {
                        List<NameValuePair> params_detail = new ArrayList<>();
                        params_detail.add(new BasicNameValuePair("id", id_record[i]));
                        LinkerServer linkerServer_detail = new LinkerServer("try_detail", params_detail);

                        if (linkerServer_detail.Linker()) {
                            response = linkerServer_detail.getResponse();
                            final String[] str_record = response.split(";");
                            cards.push(CreateNewCard(str_record[0], str_record[1], str_record[2],
                                    id_record[i]));
                        } else toast(getString(R.string.REQUEST_FAIL));

                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mListView.getAdapter().addAll(cards);
                        }
                    });
                }else toast(getString(R.string.REQUEST_FAIL));
            }
        }).start();

    }

    private Card CreateNewCard(final String title, String designer,
                               String description, final String id) {
        final CardProvider provider = new Card.Builder(this)
                .setTag(id)
                .setDismissible()
                .withProvider(new CardProvider())
                .setLayout(R.layout.material_basic_image_buttons_card_layout)
                .setTitle(title)
                .setSubtitle(designer)
                .setSubtitleColor(+R.color.black)
                .setDescription(description)
                .addAction(R.id.left_text_button, new TextViewAction(this)
                        .setText(R.string.fr_bt_cloth_detail)
                        .setTextResourceColor(R.color.colorTheme)
                        .setListener(new OnActionClickListener() {
                            @Override
                            public void onActionClicked(View view, Card card) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent(TryListActivity.this, ClothDetailActivity.class);
                                        intent.putExtra(getString(R.string.URL), id);
                                        TryListActivity.this.startActivity(intent);
                                    }
                                }).start();
                            }
                        }))
                .addAction(R.id.right_text_button, new TextViewAction(this)
                        .setText(R.string.fr_3D_fit)
                        .setTextResourceColor(R.color.colorTheme)
                        .setListener(new OnActionClickListener() {
                            @Override
                            public void onActionClicked(View view, Card card) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent(TryListActivity.this, TryActivity.class);
                                        intent.putExtra(getString(R.string.BAR_TITLE), getString(R.string.fr_show_3D));
                                        TryListActivity.this.startActivity(intent);
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

    /**
     * 中间弹出PopupWindow
     *
     *  设置PopupWindow以外部分有一中变暗的效果
     * @param view  parent view
     */
    public void showCenterPopupWindow(View view, final Card card) {
        View contentView = LayoutInflater.from(this).inflate(R.layout.popupwindowcheck_layout, null);
        final PopupWindow popupWindow = new PopupWindow(contentView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        TextView tvTitle = (TextView)contentView.findViewById(R.id.tv_title);
        TextView tvConfirm = (TextView)contentView.findViewById(R.id.tv_confirm);
        TextView tvCancel = (TextView)contentView.findViewById(R.id.tv_cancel);
        tvTitle.setText(getString(R.string.fr_delete1) + card.getProvider().getTitle() + getString(R.string.fr_delete2));
        tvConfirm.setText(R.string.fr_yes);
        tvCancel.setText(R.string.fr_cannel);

        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String id = String.valueOf(card.getTag());
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        List<NameValuePair> params = new ArrayList<>();
                        params.add(new BasicNameValuePair("item", id));
                        params.add(new BasicNameValuePair("username", UserModel.getInstance().getCurrentUser().getUsername()));
                        LinkerServer linkerServer = new LinkerServer("try_delete", params);
                        if (linkerServer.Linker()) {
                            toast(getString(R.string.fr_finish_delete) + card.getProvider().getTitle());
                        }else {
                            toast(getString(R.string.REQUEST_FAIL));
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mListView.getAdapter().addAtStart(card);
                                }
                            });
                        }
                    }
                }).start();
                popupWindow.dismiss();
            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                mListView.getAdapter().addAtStart(card);
            }
        });

        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        // 设置PopupWindow以外部分的背景颜色  有一种变暗的效果
        final WindowManager.LayoutParams wlBackground = getWindow().getAttributes();
        wlBackground.alpha = 0.5f;      // 0.0 完全不透明,1.0完全透明
        getWindow().setAttributes(wlBackground);
        // 当PopupWindow消失时,恢复其为原来的颜色
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                wlBackground.alpha = 1.0f;
                getWindow().setAttributes(wlBackground);
            }
        });
        //设置PopupWindow进入和退出动画
        popupWindow.setAnimationStyle(R.style.anim_popup_centerbar);
        // 设置PopupWindow显示在中间
        popupWindow.showAtLocation(view, Gravity.CENTER,0,0);
    }
}
