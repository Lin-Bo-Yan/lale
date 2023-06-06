package com.flowring.laleents.tools;

import androidx.activity.result.ActivityResult;

import com.flowring.laleents.model.HttpReturn;
import com.flowring.laleents.model.ServerAnnouncement;
import com.flowring.laleents.model.explore.FocusApp;
import com.flowring.laleents.model.explore.Microapp;
import com.flowring.laleents.model.explore.MicroappType;
import com.flowring.laleents.model.explore.Promotion;
import com.flowring.laleents.model.explore.news.RecommendedNews;
import com.flowring.laleents.model.explore.store.StoreRoot;
import com.flowring.laleents.model.msg.MessageInfo;
import com.flowring.laleents.model.msg.SearchMsgs;
import com.flowring.laleents.model.room.PasswordRoomMinInfo;
import com.flowring.laleents.model.user.UserControlCenter;
import com.flowring.laleents.model.user.UserInfo;

import java.util.ArrayList;

public class CallbackUtils {
    public interface APIReturn {
        void Callback(boolean isok, String DataOrErrorMsg);

    }

    public interface HttpCallback {
        HttpReturn Callback();

    }


    public interface messageReturn {
        void Callback(String message);
    }

    public interface CompanyReturn {
        void Callback(UserControlCenter.Company message);
    }

    public interface CompanyAnnouncementReturn {
        void Callback(ArrayList<UserControlCenter.Announcement> message);
    }

    public interface CompanyDashboardReturn {
        void Callback(ArrayList<UserControlCenter.Dashboard> message);
    }

    public interface CompanyModuleReturn {
        void Callback(ArrayList<UserControlCenter.CompanyModule> message);
    }


    public interface IntReturn {
        void Callback(int message);
    }

    public interface noReturn {
        void Callback();

    }

    public interface userReturn {
        void Callback(UserInfo userInfo);

    }

    public interface ReturnNewMessageInfo {
        void Callback(MessageInfo MessageInfo);

    }

    public interface ReturnHttp {
        void Callback(HttpReturn httpReturn);

    }

    public interface ReturnData<T> {
        void Callback(Boolean isOK, String ErrorMsg, T data);

    }


    public interface ReturnExplorePromotion {
        void Callback(ArrayList<Promotion> promotion);

    }

    public interface ReturnExploreMicroappType {
        void Callback(ArrayList<MicroappType> microappType);

    }

    public interface ReturnExploreMicroapp {
        void Callback(ArrayList<Microapp> microapp);

    }

    public interface ReturnExploreDatum {
        void Callback(ArrayList<FocusApp> datum);

    }

    public interface ReturnExploreRecommendedNews {
        void Callback(ArrayList<RecommendedNews> recommendedNews);

    }

    public interface ReturnExploreStoreRoot {
        void Callback(ArrayList<StoreRoot> storeRoot);

    }


    public interface PasswordRoomReturn {
        void Callback(PasswordRoomMinInfo passwordRoomMinInfo);

    }

    public interface SearchMsgsReturn {
        void Callback(SearchMsgs searchMsgs);

    }

    public interface ActivityReturn {
        void Callback(ActivityResult activityResult);

    }

    public interface tokenReturn {
        void Callback();

    }

    public interface deviceReturn{
        void Callback(Boolean deviceReturn);

    }

    public interface announceReturn{
        void Callback(ServerAnnouncement serverAnnouncement);
    }
}
