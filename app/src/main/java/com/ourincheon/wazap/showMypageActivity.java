package com.ourincheon.wazap;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.google.gson.Gson;
import com.ourincheon.wazap.Retrofit.regUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.GsonConverterFactory;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by Hsue.
 */
public class showMypageActivity extends AppCompatActivity {

    public static Context showContext;
    ImageView profileImg;
    String thumbnail;
    regUser reguser;
    NotoTextView Title;
    private TextView sName, sMajor, sUniv, sLoc, sKakao, sIntro, sExp,sSkill,pBtn;
    FloatingActionButton sImg;
    String user_id;
    int flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        showContext = this;
        Intent intent = getIntent();
        user_id =  intent.getExtras().getString("user_id");
        flag = intent.getExtras().getInt("flag");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.detail_btn_back_white);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Title =(NotoTextView)findViewById(R.id.mytext);
        sName = (TextView) findViewById(R.id.sName);
        sMajor = (TextView)  findViewById(R.id.sMajor);
        sUniv = (TextView)  findViewById(R.id.sUniv);
        sLoc = (TextView)  findViewById(R.id.sLoc);
        sKakao = (TextView)  findViewById(R.id.sKakao);
        sIntro = (TextView) findViewById(R.id.sIntro);
        sExp = (TextView) findViewById(R.id.sExp);
        sSkill = (TextView) findViewById(R.id.sSkill);
        profileImg = (ImageView) findViewById(R.id.sPro);
        sImg = (FloatingActionButton)findViewById(R.id.fab);
        pBtn = (TextView)findViewById(R.id.pButton);

        if(flag==1) {
    /*        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
            profileImg = (ImageView) findViewById(R.id.sPro);
            thumbnail = pref.getString("profile_img", "");
            System.out.println(pref.getString("access_token", ""));
            ThumbnailImage thumb = new ThumbnailImage(thumbnail, profileImg);
            thumb.execute();
            */
            Title.setText("팀장 상세 프로필");
        }
        else if(flag==2)
            Title.setText("팀원 상세 프로필");
       /* else if(flag==3) {
            Title.setText("신청자 프로필");
            pBtn.setVisibility(View.VISIBLE);
        }*/
        else
            Title.setText("나의 프로필");

        loadPage();

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPage();
    }

    void loadPage()
    {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://come.n.get.us.to/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WazapService service = retrofit.create(WazapService.class);

//        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
 //       String user_id = pref.getString("user_id", "");
        Log.d("SUCCESS", user_id );

        Call<regUser> call = service.getUserInfo(user_id);
        call.enqueue(new Callback<regUser>() {
            @Override
            public void onResponse(Response<regUser> response) {
                if (response.isSuccess() && response.body() != null) {

                    Log.d("SUCCESS", response.message());
                    reguser = response.body();

                    //user = response.body();
                    //Log.d("SUCCESS", reguser.getMsg());

                    String result = new Gson().toJson(reguser);
                    Log.d("SUCESS-----", result);

                    JSONObject jsonRes;
                    try {
                        jsonRes = new JSONObject(result);
                        JSONArray jsonArr = jsonRes.getJSONArray("data");
                        sName.setText(jsonArr.getJSONObject(0).getString("username"));
                        sMajor.setText(jsonArr.getJSONObject(0).getString("major"));
                        sUniv.setText(jsonArr.getJSONObject(0).getString("school"));
                        sLoc.setText(jsonArr.getJSONObject(0).getString("locate"));
                        //sKakao.setText(jsonArr.getJSONObject(0).getString("kakao_  id"));
                        sIntro.setText(jsonArr.getJSONObject(0).getString("introduce"));
                        sExp.setText(jsonArr.getJSONObject(0).getString("exp"));
                        sSkill.setText(jsonArr.getJSONObject(0).getString("skill"));

                        if(flag != 2)
                            sKakao.setText(jsonArr.getJSONObject(0).getString("kakao_id"));

                        try {
                            thumbnail = URLDecoder.decode(jsonArr.getJSONObject(0).getString("profile_img"), "EUC_KR");
                            Glide.with(showContext).load(thumbnail).asBitmap().centerCrop().override(150, 150).into(new BitmapImageViewTarget(sImg) {
                                @Override
                                protected void setResource(Bitmap resource) {
                                    RoundedBitmapDrawable circularBitmapDrawable =
                                            RoundedBitmapDrawableFactory.create(showContext.getResources(), resource);
                                    circularBitmapDrawable.setCircular(true);
                                    sImg.setImageDrawable(circularBitmapDrawable);
                                }
                            });
                         //   ThumbnailImage thumb = new ThumbnailImage(thumbnail, profileImg);
                         //   thumb.execute();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }

                    } catch (JSONException e) {
                    }

                } else if (response.isSuccess()) {
                    Log.d("Response Body isNull", response.message());
                } else {
                    Log.d("Response Error Body", response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
                Log.e("Errorglg''';kl", t.getMessage());
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        if(flag==0) {
            getMenuInflater().inflate(R.menu.menu_show_mypage, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        if(flag==0) {
            //noinspection SimplifiableIfStatement
            if (id == R.id.action_edit) {
                Intent i = new Intent(showMypageActivity.this, MypageActivity.class);
                startActivity(i);
            }
        }

        return super.onOptionsItemSelected(item);
    }
}
