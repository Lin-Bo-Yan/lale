/*
 * Copyright @ 2019-present 8x8, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jitsi.meet.sdk;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import java.net.URL;


/**
 * This class represents the options when joining a Jitsi Meet conference. The user can create an
 * instance by using {@link JitsiMeetConferenceOptions.Builder} and setting the desired options
 * there.
 *
 * The resulting {@link JitsiMeetConferenceOptions} object is immutable and represents how the
 * conference will be joined.
 */
public class JitsiMeetConferenceOptions implements Parcelable {
    private String isGroupCall;
    private String callType;
    private String avatar;
    private String userId;
    private String displayName;
    private String conferenceName;// conferenceName
    /**
     * Server where the conference should take place.
     */
    private URL serverURL;

    private URL messageServerUrl;
    /**
     * Room name.
     */
    private String room;
    /**
     * JWT token used for authentication.
     */
    private String token;

    /**
     * Config. See: https://github.com/jitsi/jitsi-meet/blob/master/config.js
     */
    private Bundle config;

    /**
     * Feature flags. See: https://github.com/jitsi/jitsi-meet/blob/master/react/features/base/flags/constants.js
     */
    private Bundle featureFlags;

    /**
     * USer information, to be used when no token is specified.
     */
    private JitsiMeetUserInfo userInfo;

    public URL getServerURL() {
        return serverURL;
    }

    public URL getMessageServerURL() {
        return messageServerUrl;
    }

    public String getRoom() {
        return room;
    }

    public String getToken() {
        return token;
    }

    public String getUserId() {
        return userId;
    }

    public Bundle getFeatureFlags() {
        return featureFlags;
    }

    public JitsiMeetUserInfo getUserInfo() {
        return userInfo;
    }

    /**
     * Class used to build the immutable {@link JitsiMeetConferenceOptions} object.
     */
    public static class Builder {
        private String isGroupCall;
        private String callType;
        private String avatar;
        private String userId;
        private String displayName;
        private String conferenceName;
        private URL serverURL;
        private URL  messageServerUrl;
        private String room;
        private String token;

        private Bundle config;
        private Bundle featureFlags;

        private JitsiMeetUserInfo userInfo;

        public Builder() {
            config = new Bundle();
            featureFlags = new Bundle();
        }
        public Builder isGroupCall(String isGroupCall) {
            this.isGroupCall = isGroupCall;

            return this;
        }

        public Builder setCallType(String callType) {
            this.callType = callType;

            return this;
        }
        public Builder setAvatar(String avatar) {
            this.avatar = avatar;

            return this;
        }
        public Builder setUserId(String userId) {
            this.userId = userId;
            return this;
        }
        public Builder setDisplayName(String displayName) {
            this.displayName = displayName;
            return this;
        }
        public Builder setRoomName(String conferenceName){
            this.conferenceName = conferenceName;
            return this;
        }
        /**\
         * Sets the server URL.
         * @param url - {@link URL} of the server where the conference should take place.
         * @return - The {@link Builder} object itself so the method calls can be chained.
         */
        public Builder setServerURL(URL url) {
            this.serverURL = url;

            return this;
        }

        public Builder setMessageServerURL(URL url) {
            this.messageServerUrl = url;

            return this;
        }

        /**
         * Sets the room where the conference will take place.
         * @param room - Name of the room.
         * @return - The {@link Builder} object itself so the method calls can be chained.
         */
        public Builder setRoom(String room) {
            this.room = room;

            return this;
        }

        /**
         * Sets the conference subject.
         * @param subject - Subject for the conference.
         * @return - The {@link Builder} object itself so the method calls can be chained.
         */
        public Builder setSubject(String subject) {
            setConfigOverride("subject", subject);

            return this;
        }

        /**
         * Sets the JWT token to be used for authentication when joining a conference.
         * @param token - The JWT token to be used for authentication.
         * @return - The {@link Builder} object itself so the method calls can be chained.
         */
        public Builder setToken(String token) {
            this.token = token;

            return this;
        }

        /**
         * Indicates the conference will be joined with the microphone muted.
         * @param audioMuted - Muted indication.
         * @return - The {@link Builder} object itself so the method calls can be chained.
         */
        public Builder setAudioMuted(boolean audioMuted) {
            setConfigOverride("startWithAudioMuted", audioMuted);

            return this;
        }

        /**
         * Indicates the conference will be joined in audio-only mode. In this mode no video is
         * sent or received.
         * @param audioOnly - Audio-mode indicator.
         * @return - The {@link Builder} object itself so the method calls can be chained.
         */
        public Builder setAudioOnly(boolean audioOnly) {
            setConfigOverride("startAudioOnly", audioOnly);

            return this;
        }
        /**
         * Indicates the conference will be joined with the camera muted.
         * @param videoMuted - Muted indication.
         * @return - The {@link Builder} object itself so the method calls can be chained.
         */
        public Builder setVideoMuted(boolean videoMuted) {
            setConfigOverride("startWithVideoMuted", videoMuted);

            return this;
        }

        public Builder setFeatureFlag(String flag, boolean value) {
            this.featureFlags.putBoolean(flag, value);

            return this;
        }

        public Builder setFeatureFlag(String flag, String value) {
            this.featureFlags.putString(flag, value);

            return this;
        }

        public Builder setFeatureFlag(String flag, int value) {
            this.featureFlags.putInt(flag, value);

            return this;
        }

        public Builder setUserInfo(JitsiMeetUserInfo userInfo) {
            this.userInfo = userInfo;

            return this;
        }

        public Builder setConfigOverride(String config, String value) {
            this.config.putString(config, value);

            return this;
        }

        public Builder setConfigOverride(String config, int value) {
            this.config.putInt(config, value);

            return this;
        }

        public Builder setConfigOverride(String config, boolean value) {
            this.config.putBoolean(config, value);

            return this;
        }

        public Builder setConfigOverride(String config, Bundle bundle) {
            this.config.putBundle(config, bundle);

            return this;
        }

        public Builder setConfigOverride(String config, String[] list) {
            this.config.putStringArray(config, list);

            return this;
        }

        /**
         * Builds the immutable {@link JitsiMeetConferenceOptions} object with the configuration
         * that this {@link Builder} instance specified.
         * @return - The built {@link JitsiMeetConferenceOptions} object.
         */
        public JitsiMeetConferenceOptions build() {
            JitsiMeetConferenceOptions options = new JitsiMeetConferenceOptions();

            options.serverURL = this.serverURL;
            options.messageServerUrl = this.messageServerUrl;
            options.room = this.room;
            options.token = this.token;
            options.config = this.config;
            options.featureFlags = this.featureFlags;
            options.callType =this.callType;
            options.isGroupCall = this.isGroupCall;
            options.avatar = this.avatar;
            options.userId = this.userId;
            options.displayName = this.displayName;
            options.conferenceName = this.conferenceName;
            options.userInfo = this.userInfo;
            return options;
        }
    }

    private JitsiMeetConferenceOptions() {
    }

    private JitsiMeetConferenceOptions(Parcel in) {
        serverURL = (URL) in.readSerializable();
        messageServerUrl = (URL) in.readSerializable();
        room = in.readString();
        token = in.readString();
        config = in.readBundle();
        featureFlags = in.readBundle();
        callType = in.readString();
        isGroupCall = in.readString();
        avatar = in.readString();
        userId = in.readString();
        displayName = in.readString();
        conferenceName = in.readString();
        userInfo = new JitsiMeetUserInfo(in.readBundle());
    }

    Bundle asProps() {
        Bundle props = new Bundle();

        // Android always has the PiP flag set by default.
        if (!featureFlags.containsKey("pip.enabled")) {
            featureFlags.putBoolean("pip.enabled", true);
        }

        props.putBundle("flags", featureFlags);

        Bundle urlProps = new Bundle();

        // The room is fully qualified
        if (room != null && room.contains("://")) {
            urlProps.putString("url", room);
        } else {
            if (serverURL != null) {
                urlProps.putString("serverURL", serverURL.toString());
            }
            if(messageServerUrl != null){
                urlProps.putString("messageServerUrl", messageServerUrl.toString());
            }
            if (room != null) {
                urlProps.putString("room", room);
            }
            if (isGroupCall != null) {
                urlProps.putString("isGroupCall", isGroupCall);
            }
            if (callType != null) {
                urlProps.putString("callType", callType);
            }
            if (avatar != null) {
                urlProps.putString("avatar", avatar);
            }
            if (userId != null){
                urlProps.putString("userId", userId);
            }
            if (displayName != null){
                urlProps.putString("displayName", displayName);
            }
            if (conferenceName != null){
                urlProps.putString("conferenceName", conferenceName);
            }
        }

        if (token != null) {
            urlProps.putString("jwt", token);
        }

        if (userInfo != null) {
            props.putBundle("userInfo", userInfo.asBundle());
        }

        urlProps.putBundle("config", config);
        props.putBundle("url", urlProps);

        return props;
    }

    public static final Creator<JitsiMeetConferenceOptions> CREATOR = new Creator<JitsiMeetConferenceOptions>() {
        @Override
        public JitsiMeetConferenceOptions createFromParcel(Parcel in) {
            return new JitsiMeetConferenceOptions(in);
        }

        @Override
        public JitsiMeetConferenceOptions[] newArray(int size) {
            return new JitsiMeetConferenceOptions[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(serverURL);
        dest.writeSerializable(messageServerUrl);
        dest.writeString(room);
        dest.writeString(token);
        dest.writeBundle(config);
        dest.writeBundle(featureFlags);
        dest.writeString(callType);
        dest.writeString(isGroupCall);
        dest.writeString(avatar);
        dest.writeString(userId);
        dest.writeString(displayName);
        dest.writeString(conferenceName);
        dest.writeBundle(userInfo != null ? userInfo.asBundle() : new Bundle());
    }

    @Override
    public int describeContents() {
        return 0;
    }
}