package com.flowring.laleents.tools.cloud.api;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.MainThread;
import androidx.annotation.WorkerThread;

import com.flowring.laleents.model.Http2Return;
import com.flowring.laleents.model.HttpAfReturn;
import com.flowring.laleents.model.HttpReturn;
import com.flowring.laleents.model.stickerlibrary.CustomizeSticker;
import com.flowring.laleents.model.stickerlibrary.Stickerlibrary;
import com.flowring.laleents.tools.CallbackUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

public interface ICloudUtils {

    /**
     * 取的最新APP版本號
     */
    boolean checkAppNeedUpdate(CallbackUtils.TimeoutReturn timeoutReturn);


    /**
     * 忘記密碼時的改密碼
     */
    @WorkerThread
    HttpReturn changePassword(String verifyCode, String UserId, String password);

    /**
     * 忘記密碼時用使用者帳號取的驗證碼
     */
    @WorkerThread
    HttpReturn forgetPasswordCheckEmail(String userAccount);

    /**
     * 註冊
     */
    @WorkerThread
    HttpReturn signup(final JSONObject body);

    /**
     * 確認註冊資料是否重複
     */
    @WorkerThread
    HttpReturn signupCheck(String userId, String phone, String email);

    /**
     * 一般登入
     */
    @WorkerThread
    HttpReturn login(Context context, String deviceID, final String account, final String password);

    /**
     * 接收到第三方登入token 發起登入
     */
    @WorkerThread
    HttpReturn loginThirdParty(String displayName, String deviceID, int type, String sID, File image);

    /**
     * 確認使用者是否已有登入過的行動裝置
     */
    @WorkerThread
    HttpReturn alreadyLoddedIn(String loginType, String userId, String thirdPartyIdentifier, String deviceId, CallbackUtils.TimeoutReturn timeoutReturn);

    /**
     * 接收到第三方登入token 發起登入
     */
    @WorkerThread
    HttpReturn loginSimpleThirdParty(String thirdPartyIdentifier, String deviceId, CallbackUtils.TimeoutReturn timeoutReturn);

    /**
     * 開啟google play下載頁面
     */
    @MainThread
    void gotoGooglePlay(Activity activity);

    /**
     * 取得EIM_QRcode登入資料
     */
    @WorkerThread
    HttpAfReturn getEimQRcode(Context context, String af_token, String qrcode_info_url,CallbackUtils.TimeoutReturn timeoutReturn);

    /**
     * 取得EIM_QRcode登入資料
     * post方法
     * 必定要傳 deviceId參數
     */
    @WorkerThread
    HttpAfReturn getEimQRcodeNew(Context context, String af_token, String qrcode_info_url, String deviceId,CallbackUtils.TimeoutReturn timeoutReturn);


    /**
     * 已登入app時的改密碼
     */
    @WorkerThread
    HttpReturn changeNewPassword(String oldPassword, String newPassword);

    /**
     * 取的主要user詳細資料
     */
    @WorkerThread
    HttpReturn getUserInfo(CallbackUtils.TimeoutReturn timeoutReturn);

    /**
     * 取的 其他user詳細資料
     */
    @WorkerThread
    HttpReturn getUserInfo(String UserId, CallbackUtils.TimeoutReturn timeoutReturn);

    /**
     * 修改user詳細資料
     * 在第一次第三方登入後發送 在設定頁修改完設定值後發送
     */
    @WorkerThread
    HttpReturn uploadUserInfo(Map<String, Object> map);


    /**
     * 登入後啟動推播
     */
    @WorkerThread
    HttpReturn setPusher(String userId, String FCM_token, String uuid, String customerProperties, CallbackUtils.TimeoutReturn timeoutReturn);

    /**
     * 切換帳號時切換推播
     */
    @WorkerThread
    HttpReturn updatePusher(String userId, String uuid, CallbackUtils.TimeoutReturn timeoutReturn);

    /**
     * 登出時關掉通知
     */
    @WorkerThread
    HttpReturn closePusher(String userId, String uuid, CallbackUtils.TimeoutReturn timeoutReturn);

    /**
     * 登入後啟動推播
     */
    @WorkerThread
    HttpReturn setAfPusher(String WFCI_URL, String memId, String userId,String FCM_token, String uuid, String customerProperties, CallbackUtils.TimeoutReturn timeoutReturn);

    /**
     * 登出時關掉通知
     */
    @WorkerThread
    HttpReturn closeAfPusher(String WFCI_URL, String memId, String userId, String FCM_token, String uuid, CallbackUtils.TimeoutReturn timeoutReturn);

    /**
     * 使用者登出 userLogout
     */
    @WorkerThread
    HttpReturn userLogout(CallbackUtils.TimeoutReturn timeoutReturn);

    /**
     * token過期或是被失效之後重要
     */
    @WorkerThread
    HttpReturn reToken(CallbackUtils.TimeoutReturn timeoutReturn);

    /**
     * AF 更新Token
     * 傳入參數：
     * 1. token
     * 2. deviceId
     */
    @WorkerThread
    HttpAfReturn renewTokenHaveDeviceId(String afDomain, String af_token, String deviceId, CallbackUtils.TimeoutReturn timeoutReturn);

    /**
     * AF 更新Token
     * 傳入參數：
     * 1. refreshToken
     */
    @WorkerThread
    HttpAfReturn renewToken(String afDomain, CallbackUtils.TimeoutReturn timeoutReturn);

    /**
     * 驗證Token是否有效
     */
    HttpAfReturn tokenValid();




    /**
     * 修改user頭圖
     */
    @WorkerThread
    HttpReturn uploadUserAvatar(File image);


    /**
     * 修改user背景
     */
    @WorkerThread
    HttpReturn uploadUserBackground(File image);

    /**
     * 修改user背景
     */
    @WorkerThread
    HttpReturn resetUserBackground();

    /**
     * 驗證 token 是否正確
     */
    @WorkerThread
    HttpReturn checkToken(CallbackUtils.TimeoutReturn timeoutReturn);

    /**
     * 更新 token
     */
    @WorkerThread
    HttpReturn updateToken();

    /**
     * 取得節慶資料 已廢棄
     */
    @WorkerThread
    HttpReturn getFestival();

    //endregion

    //region  已登入 好友相關

    /**
     * 取得好友清單資料
     */
    @WorkerThread
    HttpReturn getSimpleFriends(String UserId);

    /**
     * 取得好友資料
     */
    @WorkerThread
    HttpReturn getFriendInfo(String FriendId);

    /**
     * 更改好友暱稱
     */
    @WorkerThread
    HttpReturn updateFriendStatus(String friendId, String FriendName);


    /**
     * 更改好友狀態
     */
    @WorkerThread
    HttpReturn updateFriendStatus(String friendId, int status);


    /**
     * 取得其他使用者id 用模糊資料 手機號碼 user帳號
     */
    @WorkerThread
    HttpReturn getUserID(String PhoneOrAccountOrEmail);

    /**
     * 取得尚未審核的好友邀請列表
     */
    @WorkerThread
    HttpReturn getNotYetFriends(CallbackUtils.TimeoutReturn timeoutReturn);

    /**
     * 確認對方是否已經將自己加好友
     */
    @WorkerThread
    boolean getNotYetReceiverFriends(String friendId);

    /**
     * 回覆好友邀請
     */
    HttpReturn setFriendInvite(String frId, boolean agree, String friendId);

    /**
     * 新增好友
     */
    @WorkerThread
    HttpReturn addFriend(String FriendId);

    /**
     * 新增好友
     */
    @WorkerThread
    HttpReturn addFriend(ArrayList<String> phones);

    //endregion

    //region  已登入 聊天室 相簿

    /**
     * 取得聊天室內所有相簿
     */
    @WorkerThread
    HttpReturn getAllAlbum(String roomId);

    /**
     * 新增相簿
     */
    @WorkerThread
    HttpReturn newAlbum(String roomId, String name);

    /**
     * 刪除相簿
     */
    @WorkerThread
    HttpReturn delAlbum(String roomAlbumId);

    /**
     * 批次刪除相簿
     */
    @WorkerThread
    HttpReturn delAlbum(String roomId, ArrayList<String> roomAlbumId);

    /**
     * 取得相簿所有照片
     */
    @WorkerThread
    HttpReturn getPhotos(String roomAlbumId);

    /**
     * 新增照片
     */
    @WorkerThread
    HttpReturn newPhotos(String albumId, File picture);

    /**
     * 刪除照片
     */
    @WorkerThread
    HttpReturn delPhotos(String roomAlbumId, String roomAlbumPhotoId);

    /**
     * 批次刪除照片
     */
    @WorkerThread
    HttpReturn delPhotos(String roomAlbumId, ArrayList<String> roomAlbumPhotoId);

    /**
     * 相簿改名
     */
    @WorkerThread
    HttpReturn reNameAlbum(String roomId, String name, String roomAlbumId);

    //endregion

    //region  已登入 聊天室相關

    /**
     * 取得一部分 聊天室資料用於繪圖 當無本地端資料時使用
     */
    @WorkerThread
    HttpReturn getLittleSimpleRooms();

    /**
     * 取得聊天室內成員
     */
    @WorkerThread
    HttpReturn getRoomMembers(String roomId, CallbackUtils.TimeoutReturn timeoutReturn);

    /**
     * 取得聊天室內成員
     */
    @WorkerThread
    HttpReturn getGroupMembers(String groupId, CallbackUtils.TimeoutReturn timeoutReturn);


    /**
     * 取得全部聊天室資料用於繪圖 在本地端有資料後 第一次登入使用 並拿到資料時戳
     */
    @WorkerThread
    HttpReturn getAllSimpleRooms(CallbackUtils.TimeoutReturn timeoutReturn);

    @WorkerThread
    HttpReturn getSimpleRooms(int type, CallbackUtils.TimeoutReturn timeoutReturn);

    @WorkerThread
    HttpReturn getOneRoom(String roomId, CallbackUtils.TimeoutReturn timeoutReturn);


    @WorkerThread
    void getRoomMsgs(String roomId, CallbackUtils.ReturnData<JSONArray> OtherCallBack);


    /**
     * 取得全部聊天室 公告
     */
    @WorkerThread
    HttpReturn getAnnouncement(String roomId);

    /**
     * 新增公告
     */
    @WorkerThread
    HttpReturn netAnnouncement(String roomId, String eventId, String message);

    /**
     * 刪除公告
     */
    @WorkerThread
    HttpReturn delAnnouncement(String roomId, String eventId, String message);

    /**
     * 隱藏公告
     */
    @WorkerThread
    HttpReturn hideAnnouncement(String roomId);

    /**
     * 建立新的聊天室
     */
    @WorkerThread
    HttpReturn newRoom(String friend,CallbackUtils.TimeoutReturn timeoutReturn);

    /**
     * 建立新的群組聊天室
     */
    @WorkerThread
    HttpReturn newGroupRoom(String name, int type, ArrayList<String> users, File image, CallbackUtils.TimeoutReturn timeoutReturn);

    /**
     * 建立新的群組聊天室
     */
    @WorkerThread
    HttpReturn getGroupRoom(String groupId, CallbackUtils.TimeoutReturn timeoutReturn);

    /**
     * 建立新的密碼聊天室
     */
    @WorkerThread
    HttpReturn newPassworkRoom(String Passwork,CallbackUtils.TimeoutReturn timeoutReturn);

    /**
     * 取得臨時密碼聊天室
     */
    @WorkerThread
    HttpReturn getPassworkRoom(String Passwork, CallbackUtils.TimeoutReturn timeoutReturn);

    /**
     * 進入密碼聊天室
     */
    @WorkerThread
    HttpReturn addPassworkRoom(int id, String Passwork, String[] userList,CallbackUtils.TimeoutReturn timeoutReturn);

    /**
     * 進入密碼聊天室
     */
    @WorkerThread
    HttpReturn addPassworkRoom(String groupId, String Passwork, CallbackUtils.TimeoutReturn timeoutReturn);

    /**
     * 離開密碼聊天室
     */
    @WorkerThread
    HttpReturn leavePassworkRoom(int id, String Passwork);

    /**
     * 更新聊天室
     */
    @WorkerThread
    HttpReturn updateRoom(String roomId, JSONObject body, CallbackUtils.TimeoutReturn timeoutReturn);


    /**
     * 更新聊天室頭圖
     */
    @WorkerThread
    HttpReturn updateGroupAvatar(String groupId, File image);

    /**
     * 新增群組成員
     */
    @WorkerThread
    HttpReturn addGroupMember(String roomId, String groupId, String[] userList);

    /**
     * 刪除群組成員
     */
    @WorkerThread
    HttpReturn delGroupMember(String roomId, String groupId, String[] userList);

    /**
     * 更新聊天室
     */
    @WorkerThread
    HttpReturn updateGroup(String groupId, JSONObject body, CallbackUtils.TimeoutReturn timeoutReturn);

    /**
     * 更新聊天室
     */
    @WorkerThread
    HttpReturn updateGroupSetting(String groupId, JSONObject body);

    /**
     * 取得群組設定
     */
    @WorkerThread
    HttpReturn getGroupSetting(String groupId);


    /**
     * 取得群組資料
     */
    @WorkerThread
    HttpReturn getGroupInfo(String groupId);

    /**
     * 取得一對一聊天室的設定
     */
    @WorkerThread
    HttpReturn getRoomSetting(String roomId);

    /**
     * 取得一對一聊天室的設定
     */
    @WorkerThread
    void getRoomSetting(String roomId, CallbackUtils.ReturnData data, Class<?> classType);

    /**
     * 更新群組背景
     */
    @WorkerThread
    HttpReturn updateGroupBackground(String groupId, File image);

    /**
     * 取得群組QRocde裡面的Verification
     */
    @WorkerThread
    HttpReturn getGroupVerification(String groupId);

    /**
     * 取得群組QRocde裡面的Verification
     */
    @WorkerThread
    HttpReturn gotoGroup(String groupId, String verificationCode);

    /**
     * 刪除聊天訊息
     */
    @WorkerThread
    HttpReturn delRecord(String roomId);

    //endregion

    //region  已登入 訊息相關


    /**
     * 發送檔案
     */
    @WorkerThread
    HttpReturn retractMsg(String roomId, String retract_eventId);

    /**
     * 發送檔案
     */
    @WorkerThread
    HttpReturn sendFile(String roomId, File file);

    /**
     * 取得使用者的收藏訊息
     */
    HttpReturn getKeeps();

    /**
     * 取得使用者的單一收藏訊息
     */
    HttpReturn getKeeps(String keepId);

    /**
     * 搜尋訊息
     * String roomId=""時代表搜尋所有房間
     */
    HttpReturn searchMsg(String keyword, String roomId, CallbackUtils.TimeoutReturn timeoutReturn);

    /**
     * 新增收藏訊息
     */
    HttpReturn newKeep(String eventId);

    /**
     * 刪除收藏訊息
     */
    HttpReturn delKeep(String keepId);

    /**
     * 刪除收藏訊息
     */
    HttpReturn delKeep(ArrayList<String> keepId);
    //endregion

    //region  已登入 探索相關

    /**
     * 取得探索頁的輪播圖
     */
    @WorkerThread
    HttpReturn getExplorePromotion();

    /**
     * 取得所有微服務
     */
    @WorkerThread
    HttpReturn getMicroApps();

    /**
     * 取得焦點活動
     */
    @WorkerThread
    HttpReturn getFocusApps();

    /**
     * 取得推薦消息
     * Recommended news
     */
    @WorkerThread
    String getRecommendedNews();

    /**
     * 取得精選商品
     * Recommended news
     */
    @WorkerThread
    String getMemia();


    /**
     * 取得微服務列表
     * Recommended news
     */
    @WorkerThread
    HttpReturn getMicroappTypes();


    /**
     * 告訴伺服器微服務啟動
     */
    @WorkerThread
    HttpReturn setMicroHistoryOpen(String microAppMenuId, String deviceId, String deviceBrand, String deviceModel, String deviceOsType);

    /**
     * 告訴伺服器微服務關閉
     */
    @WorkerThread
    HttpReturn setMicroHistoryClose(int id);

    /**
     * 取得微服務
     * Recommended news
     */
    @WorkerThread
    HttpReturn getMicroapps(String microAppMenuId);

    /**
     * 微服務加入我的最愛
     */
    @WorkerThread
    HttpReturn addMicroappFavorite(String microAppId, CallbackUtils.TimeoutReturn timeoutReturn);

    /**
     * 微服務移出我的最愛
     */
    @WorkerThread
    HttpReturn delMicroappFavorite(String microAppId, CallbackUtils.TimeoutReturn timeoutReturn);

    /**
     * 微服務是否在我的最愛中
     */
    @WorkerThread
    HttpReturn isMicroappFavorite(String microAppId, CallbackUtils.TimeoutReturn timeoutReturn);

    /**
     * 微服務我的最愛列表
     */
    @WorkerThread
    HttpReturn getMicroappFavoriteList();

    /**
     * 微服務最近常用列表
     */
    @WorkerThread
    HttpReturn getMicroappUsedList();

    /**
     * 取得單項微服務
     */
    @WorkerThread
    HttpReturn getMicroapp(String microAppId);

    /**
     * 取得aftoken
     */
    @WorkerThread
    HttpAfReturn getAfToken(String afServer, CallbackUtils.TimeoutReturn timeoutReturn);

    /**
     * 更新 afToken
     */
    HttpAfReturn renewAfToken(String refreshToken);

    /**
     * 取得公司清單
     */
    @WorkerThread
    HttpAfReturn getCompanyList(String afServer, String afToken, CallbackUtils.TimeoutReturn timeoutReturn);

    /**
     * 取得公司布告欄
     */
    @WorkerThread
    HttpAfReturn getCompanyAnnouncement(String afServer, String afToken, CallbackUtils.TimeoutReturn timeoutReturn);

    /**
     * 取得工作面板列表
     */
    @WorkerThread
    HttpAfReturn getCompanyDashboard(String afServer, String afToken, CallbackUtils.TimeoutReturn timeoutReturn);

    /**
     * 取得應用列表
     */
    @WorkerThread
    HttpAfReturn getCompanyModule(String afServer, String afToken, String companyId, CallbackUtils.TimeoutReturn timeoutReturn);


    //endregion

    //region  已登入 貼圖相關

    /**
     * 取得貼圖商店列表
     */
    @WorkerThread
    ArrayList<Stickerlibrary> getAllStickerlibrary();

    /**
     * 取得使用者貼圖列表
     */
    @WorkerThread
    ArrayList<Stickerlibrary> getUserStickers();

    /**
     * 新增貼圖列表至使用者貼圖列表
     */
    @WorkerThread
    HttpReturn newStickerlibrary(String stickerLibraryId);

    /**
     * 取得自訂貼圖庫
     */
    @WorkerThread
    ArrayList<CustomizeSticker> getCustomizeStickerlibrary();

    /**
     * 新增自訂貼圖
     */
    @WorkerThread
    HttpReturn newCustomizeSticker(File image);

    /**
     * 刪除自訂貼圖
     */
    @WorkerThread
    HttpReturn delCustomizeSticker(String imageId);


    //endregion

    //region  已登入 af 組織樹相關

    /**
     * 拿取使用者頭像
     */
    @WorkerThread
    HttpAfReturn orgtreeuserimage(String afDomain, String[] UserIds, CallbackUtils.TimeoutReturn timeoutReturn);
    //endregion

    /**
     * 查詢伺服器維護公告 - 執行中的(區間內)
     */
    @WorkerThread
    HttpReturn announceServer(CallbackUtils.TimeoutReturn timeoutReturn);

    /**
     * 查詢伺服器維護公告 - 執行中的(區間內) - 給定時間
     */
    @WorkerThread
    HttpReturn announceServerGivenTime(String givenTime);


    /**
     * 查詢伺服器維護公告 - 所有類型最近的一筆資料
     */
    @WorkerThread
    HttpReturn latestAnnounce(CallbackUtils.TimeoutReturn timeoutReturn);

    /**
     * 查詢伺服器維護公告 - 所有類型最近的一筆資料 - 給定時間
     */
    @WorkerThread
    HttpReturn latestAnnounceGivenTime(String givenTime);

    /**
     * 系統設定 - 取得 Eim 所有系統資訊
     */
    @WorkerThread
    HttpReturn getEimAllSystemInfor(CallbackUtils.TimeoutReturn timeoutReturn);

    /**
     * 系統設定 - (Adm)更新系統資訊
     */
    @WorkerThread
    HttpReturn updataSystemInfor(JSONArray settingsArray, CallbackUtils.TimeoutReturn timeoutReturn);

    /**
     * 浮水印 - (Adm)取得所有浮水印模板資訊
     */
    @WorkerThread
    HttpReturn getAllWatermarkTemplates(CallbackUtils.TimeoutReturn timeoutReturn);

    /**
     * 浮水印 - 取得預設浮水印模板資訊
     */
    @WorkerThread
    HttpReturn getDefaultWatermarkTemplate(CallbackUtils.TimeoutReturn timeoutReturn);

    /**
     * 浮水印 - 文字浮水印構成
     */
    @WorkerThread
    Http2Return textWatermark(String textContent, CallbackUtils.TimeoutReturn timeoutReturn);

    /**
     * 系統設定 - 取得 純辦公 所有系統資訊
     */
    @WorkerThread
    Http2Return getAppWorkAllSystemInfor(CallbackUtils.TimeoutReturn timeoutReturn);

    /**
     * 取得 Lale 使用平台最低可相容版本
     */
    @WorkerThread
    HttpReturn googlePlatformVersion(CallbackUtils.TimeoutReturn timeoutReturn);


    byte[] getFile(String url);

    /**
     * 取得web版本
     */
    @WorkerThread
    String webVersion(String url,CallbackUtils.TimeoutReturn timeoutReturn);

    /**
     * 帳號登入
     */
    @WorkerThread
    HttpAfReturn aflogin(String account, String password, String url, CallbackUtils.TimeoutReturn timeoutReturn);

    /**
     * 帳號登入
     * 多參數：deviceId
     * 更換新的 domain
     */
    @WorkerThread
    HttpAfReturn afloginNew(String account, String password, String url, CallbackUtils.TimeoutReturn timeoutReturn);

    /**
     * 拿 agentflow server的版號
     */
    @WorkerThread
    HttpAfReturn afServerVersion(String afUrl, CallbackUtils.TimeoutReturn timeoutReturn);

}

