/**
 * Copyright 2014 Kakao Corp.
 *
 * Redistribution and modification in source or binary forms are not permitted without specific prior written permission. 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.kakao.kakaocampus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.NetworkImageView;
import com.kakao.APIErrorResult;
import com.kakao.BasicKakaoStoryPostParamBuilder;
import com.kakao.KakaoParameterException;
import com.kakao.KakaoStoryCheckUser;
import com.kakao.KakaoStoryHttpResponseHandler;
import com.kakao.KakaoStoryLinkInfo;
import com.kakao.KakaoStoryMyStoriesParamBuilder;
import com.kakao.KakaoStoryMyStoryParamBuilder;
import com.kakao.KakaoStoryProfile;
import com.kakao.KakaoStoryProfile.BirthdayType;
import com.kakao.KakaoStoryService;
import com.kakao.KakaoStoryService.StoryType;
import com.kakao.LinkKakaoStoryPostParamBuilder;
import com.kakao.LogoutResponseCallback;
import com.kakao.MyStoryInfo;
import com.kakao.NoteKakaoStoryPostParamBuilder;
import com.kakao.PhotoKakaoStoryPostParamBuilder;
import com.kakao.UserManagement;
import com.kakao.UserProfile;
import com.kakao.helper.Logger;
import com.kakao.template.loginbase.GlobalApplication;
import com.kakao.widget.ProfileLayout;

/**
 * 카카오스토리 API인 프로필, 포스팅(이미지 업로드)를 테스트 한다.
 * 유효한 세션이 있다는 검증을 {@link KakaoStoryLoginActivity}로 부터 받고 보여지는 로그인 된 페이지이다.
 */
public class KakaoStoryMainActivity extends Activity {
    private final String noteContent = "A Rainbow - William Wordsworth\n" +
                                        "\n" +
                                        "My heart leaps up when I behold\n" +
                                        "A rainbow in the sky:\n" +
                                        "So was it when my life began;\n" +
                                        "So is it now I am a man;\n" +
                                        "So be it when I shall grow old,\n" +
                                        "Or let me die!\n" +
                                        "The Child is father of the Man;\n" +
                                        "I could wish my days to be\n" +
                                        "Bound each to each by natural piety.";

    private final String photoContent = "This cafe is really awesome!";
    private final String linkContent = "better than expected!";
    private final String execParam = "place=1111";
    private final String marketParam = "referrer=kakaostory";
    private String scrapUrl = "http://developers.kakao.com";
    private NetworkImageView background;
    private ProfileLayout profileLayout;
    private TextView birthdayView;
    private UserProfile userProfile;
    private Button getPostButton;
    private Button getPostsButton;
    private String lastMyStoryId;

    /**
     * @param savedInstanceState 기존 session 정보가 저장된 객체
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeView();
    }

    protected void onResume() {
        super.onResume();
        // 로그인 하면서 caching되어 있는 profile를 그린다.
        userProfile = UserProfile.loadFromCache();
        if (userProfile != null) {
            profileLayout.setUserProfile(userProfile);
        }
    }

    private void redirectLoginActivity() {
        final Intent intent = new Intent(this, KakaoStoryLoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void onClickIsStoryUser() {
        KakaoStoryService.requestIsStoryUser(new MyKakaoStoryHttpResponseHandler<KakaoStoryCheckUser>() {
            @Override
            protected void onHttpSuccess(final KakaoStoryCheckUser kakaoStoryCheckUser) {
                Toast.makeText(getApplicationContext(), "succeeded to check story user : " + kakaoStoryCheckUser.isStoryUser(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onClickProfile() {
        KakaoStoryService.requestProfile(new MyKakaoStoryHttpResponseHandler<KakaoStoryProfile>() {
            @Override
            protected void onHttpSuccess(final KakaoStoryProfile storyProfile) {
                Toast.makeText(getApplicationContext(), "succeeded to get story profile", Toast.LENGTH_SHORT).show();
                applyStoryProfileToView(storyProfile);
            }
        });
    }

    private void uploadImage() {
        try {
            final List<File> files = new ArrayList<File>();
//            final File file1 = new File(writeStoryImage(R.drawable.post_image));
//            files.add(file1);
            // gif 예제
            final File file2 = new File(writeStoryImage(R.drawable.animated_gif));
            files.add(file2);
            KakaoStoryService.requestMultiUpload(new MyKakaoStoryHttpResponseHandler<String[]>() {
                @Override
                protected void onHttpSuccess(final String[] imageURLs) {
                    try {
                        if (imageURLs != null && imageURLs.length != 0) {
                            Toast.makeText(getApplicationContext(), "succeeded to upload image.\n" + Arrays.toString(imageURLs), Toast.LENGTH_SHORT).show();
                            requestPostPhoto(imageURLs);
                        } else {
                            Toast.makeText(getApplicationContext(), "failed to upload image.\nkakaoStoryUpload=null", Toast.LENGTH_SHORT).show();
                        }
                    } finally {
                        deleteTempFiles();
                    }

                }

                @Override
                protected void onHttpSessionClosedFailure(final APIErrorResult errorResult) {
                    try {
                        super.onHttpSessionClosedFailure(errorResult);
                    } finally {
                        deleteTempFiles();
                    }
                }

                @Override
                protected void onNotKakaoStoryUser() {
                    try {
                        super.onNotKakaoStoryUser();
                    } finally {
                        deleteTempFiles();
                    }
                }

                @Override
                protected void onFailure(final APIErrorResult errorResult) {
                    try {
                        super.onFailure(errorResult);
                    } finally {
                        deleteTempFiles();
                    }
                }

                private void deleteTempFiles() {
//                    if(file1 != null)
//                        file1.delete();
                    if(file2 != null)
                        file2.delete();
                }

            }, files);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void getLinkInfo() {
        KakaoStoryService.requestGetLinkInfo(new MyKakaoStoryHttpResponseHandler<KakaoStoryLinkInfo>() {
            @Override
            protected void onHttpSuccess(final KakaoStoryLinkInfo kakaoStoryLinkInfo) {
                if (kakaoStoryLinkInfo != null && kakaoStoryLinkInfo.isValidResult()) {
                    Toast.makeText(getApplicationContext(), "succeeded to get link info.\n" + kakaoStoryLinkInfo, Toast.LENGTH_SHORT).show();
                    requestPostLink(kakaoStoryLinkInfo);
                } else {
                    Toast.makeText(getApplicationContext(), "failed to get link info.\nkakaoStoryLinkInfo=null", Toast.LENGTH_SHORT).show();
                }
            }
        }, scrapUrl);
    }

    private void onClickLogout() {
        UserManagement.requestLogout(new LogoutResponseCallback() {
            @Override
            protected void onSuccess(final long userId) {
                redirectLoginActivity();
            }

            @Override
            protected void onFailure(final APIErrorResult apiErrorResult) {
                redirectLoginActivity();
            }
        });
    }

    private void requestPostNote() {
        final NoteKakaoStoryPostParamBuilder postParamBuilder = new NoteKakaoStoryPostParamBuilder(noteContent);
        requestPost(StoryType.NOTE, postParamBuilder);
    }

    private void requestPostPhoto(final String[] imageURLs) {
        final PhotoKakaoStoryPostParamBuilder postParamBuilder = new PhotoKakaoStoryPostParamBuilder(imageURLs);
        postParamBuilder.setContent(photoContent);
        requestPost(StoryType.PHOTO, postParamBuilder);
    }

    private void requestPostLink(final KakaoStoryLinkInfo kakaoStoryLinkInfo) {
        final LinkKakaoStoryPostParamBuilder postParamBuilder = new LinkKakaoStoryPostParamBuilder(kakaoStoryLinkInfo);
        postParamBuilder.setContent(linkContent);
        requestPost(StoryType.LINK, postParamBuilder);
    }

    private void requestPost(final StoryType storyType, final BasicKakaoStoryPostParamBuilder postParamBuilder) {
        // 앱이 설치되어 있는 경우 kakao<app_key>://kakaostory?place=1111 로 이동.
        // 앱이 설치되어 있지 않은 경우 market://details?id=com.kakao.sample.kakaostory&referrer=kakaostory로 이동
        postParamBuilder.setAndroidExecuteParam(execParam).setIOSExecuteParam(execParam).setAndroidMarketParam(marketParam).setIOSMarketParam(marketParam);
        try {
            final Bundle parameters = postParamBuilder.build();
            KakaoStoryService.requestPost(storyType, new MyKakaoStoryHttpResponseHandler<MyStoryInfo>() {
                @Override
                protected void onHttpSuccess(final MyStoryInfo myStoryInfo) {
                    if(myStoryInfo.getId() != null) {
                        lastMyStoryId = myStoryInfo.getId();
                        getPostButton.setEnabled(true);
                        getPostsButton.setEnabled(true);
                        Toast.makeText(getApplicationContext(), "succeeded to post " + storyType + " on KakaoStory.\nmyStoryId=" + lastMyStoryId, Toast.LENGTH_SHORT).show();
                    } else{
                        Toast.makeText(getApplicationContext(), "failed to post " + storyType + " on KakaoStory.\nmyStoryId=null", Toast.LENGTH_SHORT).show();
                    }
                }
            }, parameters);
        } catch (KakaoParameterException e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void requestGetMyStory() {
        try {
            final Bundle parameters = new KakaoStoryMyStoryParamBuilder(lastMyStoryId).build();
            KakaoStoryService.requestGetMyStory(new MyKakaoStoryHttpResponseHandler<MyStoryInfo>() {
                @Override
                protected void onHttpSuccess(final MyStoryInfo myStoryInfo) {
                    if (lastMyStoryId.equals(myStoryInfo.getId())) {
                        Toast.makeText(getApplicationContext(), "succeeded to get my post from KakaoStory.\nmyStoryInfo=" + myStoryInfo, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "failed to get my post from KakaoStory.\nexpectedId=" + lastMyStoryId + ",id=" + myStoryInfo.getId(), Toast.LENGTH_SHORT).show();
                    }
                }
            }, parameters);
        } catch (KakaoParameterException e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void requestGetMyStories() {
        final Bundle parameters = new KakaoStoryMyStoriesParamBuilder(lastMyStoryId).build();
        KakaoStoryService.requestGetMyStories(new MyKakaoStoryHttpResponseHandler<MyStoryInfo[]>() {
            @Override
            protected void onHttpSuccess(final MyStoryInfo[] myStories) {
                Toast.makeText(getApplicationContext(), "succeeded to get my posts from KakaoStory." +
                    "\ncount=" + myStories.length +
                    "\nstories=" + Arrays.toString(myStories), Toast.LENGTH_SHORT).show();
            }
        }, parameters);
    }

    private void initializeView() {
        setContentView(R.layout.main);
        initializeButtons();
        initializeProfileView();
    }

    private void initializeButtons() {
        final Button userCheckButton = (Button) findViewById(R.id.user_check_button);
        userCheckButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickIsStoryUser();
            }
        });

        final Button profileButton = (Button) findViewById(R.id.profile_button);
        profileButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickProfile();
            }
        });

        // 텍스트만 포스팅
        final Button postTextButton = (Button) findViewById(R.id.text_post_button);
        postTextButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPostNote();
            }
        });

        // 이미지 포스팅. 먼저 이미지를 upload하고 성공하면 이미지와 함께 텍스트를 포스팅한다.
        final Button postImageButton = (Button) findViewById(R.id.image_post_button);
        postImageButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
            }
        });

        // 링크 포스팅. 먼저 스크랩을 수행하고 성공하면 포스팅한다.
        final Button postLinkButton = (Button) findViewById(R.id.link_post_button);
        postLinkButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                getLinkInfo();
            }
        });


        // 마지막 포스팅 정보.
        getPostButton = (Button) findViewById(R.id.get_post_button);
        getPostButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(lastMyStoryId == null){
                    Toast.makeText(getApplicationContext(), "post first then get info", Toast.LENGTH_SHORT).show();
                } else {
                    requestGetMyStory();
                }
            }
        });

        // 최근 포스팅 정보.
        getPostsButton = (Button) findViewById(R.id.get_posts_button);
        getPostsButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lastMyStoryId == null) {
                    Toast.makeText(getApplicationContext(), "post first then get info", Toast.LENGTH_SHORT).show();
                } else {
                    requestGetMyStories();
                }
            }
        });

        Button logoutButton = (Button) findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickLogout();
            }
        });
    }

    private void initializeProfileView() {
        profileLayout = (ProfileLayout) findViewById(R.id.com_kakao_user_profile);
        // background image
        background = (NetworkImageView) findViewById(R.id.background);
        background.setDefaultImageResId(R.drawable.default_background);

        // extra story profile
        birthdayView = (TextView) findViewById(R.id.birthday);
    }

    // profile view에서 story profile을 update 한다.
    private void applyStoryProfileToView(final KakaoStoryProfile storyProfile) {
        if (profileLayout != null) {
            if (userProfile != null)
                profileLayout.setUserProfile(userProfile);

            final String nickName = storyProfile.getNickName();
            if (nickName != null)
                profileLayout.setNickname(nickName);

            final String profileImageURL = storyProfile.getProfileImageURL();
            if (profileImageURL != null)
                profileLayout.setProfileURL(profileImageURL);
        }

        final String backgroundURL = storyProfile.getBgImageURL();
        if (background != null && backgroundURL != null ) {
            Application app = GlobalApplication.getGlobalApplicationContext();
            if (app == null)
                throw new UnsupportedOperationException("needs com.kakao.GlobalApplication in order to use ImageLoader");
            background.setImageUrl(backgroundURL, ((GlobalApplication) app).getImageLoader());
        }

        final Calendar birthday = storyProfile.getBirthdayCalendar();
        final BirthdayType birthDayType = storyProfile.getBirthdayType();
        if (birthdayView != null && birthday != null) {
            StringBuilder displayBirthday = new StringBuilder(8);
            displayBirthday.append(birthday.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.US)).append(" ").append(birthday.get(Calendar.DAY_OF_MONTH));
            if (birthDayType != null)
                displayBirthday.append(" (").append(birthDayType.getDisplaySymbol()).append(")");
            birthdayView.setText(displayBirthday.toString());
        }
    }

    private String writeStoryImage(final int imageResourceId) throws IOException {
        final int bufferSize = 1024;
        InputStream ins = null;
        FileOutputStream fos = null;
        String outputFileName = null;
        try {
            ins = getResources().openRawResource(imageResourceId);

            final TypedValue value = new TypedValue();
            getResources().getValue(imageResourceId, value, true);
            final String imageFileName =  value.string == null ? null : value.string.toString();
            String extension = null;
            if(imageFileName != null)
                extension = getExtension(imageFileName);
            if(extension == null)
                extension = ".jpg";

            final File diskCacheDir = new File(getCacheDir(), "story");

            if (!diskCacheDir.exists())
                diskCacheDir.mkdirs();

            outputFileName = diskCacheDir.getAbsolutePath() + File.separator + "temp_" + System.currentTimeMillis() + extension;

            fos = new FileOutputStream(outputFileName);

            byte[] buffer = new byte[bufferSize];
            int read;
            while((read = ins.read(buffer)) != -1){
                fos.write(buffer, 0, read);
            }
        } finally {
            try {
                if (ins != null)
                    ins.close();
                if (fos != null)
                    fos.close();
            } catch (Throwable t) {
            }
        }

        return outputFileName;
    }

    public static String getExtension(String fileName) {
        String ext = null;
        int i = fileName.lastIndexOf('.');
        int endIndex = fileName.lastIndexOf("?");

        if (i > 0 && i < fileName.length() - 1) {
            Locale curLocale = Locale.getDefault();
            if (endIndex < 0) {
                ext = fileName.substring(i).toLowerCase(curLocale);
            } else {
                ext = fileName.substring(i, endIndex).toLowerCase(curLocale);
            }
        }
        return ext;
    }

    private abstract class MyKakaoStoryHttpResponseHandler<T> extends KakaoStoryHttpResponseHandler<T> {

        @Override
        protected void onHttpSessionClosedFailure(final APIErrorResult errorResult) {
            redirectLoginActivity();
        }

        @Override
        protected void onNotKakaoStoryUser() {
            Toast.makeText(getApplicationContext(), "not KakaoStory user", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onFailure(final APIErrorResult errorResult) {
            final String message = "MyKakaoStoryHttpResponseHandler : failure : " + errorResult;
            Logger.getInstance().d(message);
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        }
    }
}