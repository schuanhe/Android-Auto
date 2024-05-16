package com.schuanhe.auto_redbook.scheme

class RedBook {

    companion object {
        /**
         * 账号与安全
         */
        const val XHS_ACCOUNT_AND_SECURITY = "xhsdiscover://account/bind/"

        /**
         * 分享给用户
         */
        const val XHS_SHARE_TO_USER = "xhsdiscover://choose_share_user"

        /**
         * 深色设置
         */
        const val XHS_DARK_MODE_SETTING = "xhsdiscover://dark_mode_setting/"

        /**
         * 视频作品页
         */
        fun xhsVideoFeed(id: String) = "xhsdiscover://video_feed/$id"

        /**
         * 通用设置
         */
        const val XHS_GENERAL_SETTING = "xhsdiscover://general_setting/"

        /**
         * 记录我的日常
         */
        const val XHS_HEY_HOME_FEED = "xhsdiscover://hey_home_feed/"

        /**
         * 发布语音
         */
        const val XHS_HEY_POST = "xhsdiscover://hey_post/"

        /**
         * 主页
         */
        const val XHS_HOME = "xhsdiscover://home/"

        /**
         * 发现列表
         */
        const val XHS_HOME_EXPLORE = "xhsdiscover://home/explore"

        /**
         * 关注列表
         */
        const val XHS_HOME_FOLLOW = "xhsdiscover://home/follow"

        /**
         * 同城列表
         */
        const val XHS_HOME_LOCALFEED = "xhsdiscover://home/localfeed"

        /**
         * 商城
         */
        const val XHS_HOME_STORE = "xhsdiscover://home/store"

        /**
         * 商品搜索
         */
        const val XHS_INSTORE_SEARCH = "xhsdiscover://instore_search/result"

        /**
         * 商品搜索关键词
         */
        fun xhsInstoreSearchWithKeyword(key:String) = "xhsdiscover://instore_search/result?keyword=$key"

        /**
         * 作品页
         */
        fun xhsItem(id: String) = "xhsdiscover://item/$id"

        /**
         * 文字作品页
         */
        fun xhsItemNormal(id: String) = "xhsdiscover://item/$id?type=normal"

        /**
         * 视频作品页
         */
        fun xhsItemVideo(id: String) = "xhsdiscover://item/$id?type=video"

        /**
         * 搜索关键词
         */
        fun xhsSearchWithKeyword(key:String) = "xhsdiscover://search/result?keyword=$key"

        /**
         * 编辑资料
         */
        const val XHS_ME_PROFILE = "xhsdiscover://me/profile"

        /**
         * 收到的赞和收藏
         */
        const val XHS_MESSAGE_COLLECTIONS = "xhsdiscover://message/collections"

        /**
         * 收到的评论和@
         */
        const val XHS_MESSAGE_COMMENTS = "xhsdiscover://message/comments"

        /**
         * 新增关注
         */
        const val XHS_MESSAGE_FOLLOWERS = "xhsdiscover://message/followers"

        /**
         * 系统通知
         */
        const val XHS_MESSAGE_NOTIFICATIONS = "xhsdiscover://message/notifications"

        /**
         * 陌生人消息
         */
        const val XHS_MESSAGE_STRANGERS = "xhsdiscover://message/strangers"

        /**
         * 消息
         */
        const val XHS_MESSAGES = "xhsdiscover://messages"

        /**
         * 通知设置
         */
        const val XHS_NOTIFICATION_SETTING = "xhsdiscover://notification_setting/"

        /**
         * 发布作品-相册
         */
        const val XHS_POST_ALBUM = "xhsdiscover://post/"

        /**
         * 发布笔记
         */
        const val XHS_POST_NOTE = "xhsdiscover://post_note"

        /**
         * 发布视频
         */
        val XHS_POST_VIDEO = "xhsdiscover://post_video"

        /**
         * 发布视频-全部相册
         */
        val XHS_POST_VIDEO_ALL_ALBUM = "xhsdiscover://post_video_album"

        /**
         * 我的个人页面
         */
        val XHS_PROFILE = "xhsdiscover://profile"

        /**
         * 商品搜索推荐
         */
        val XHS_INSTORE_SEARCH_RECOMMEND = "xhsdiscover://instore_search/recommend"

        /**
         * 通讯录好友
         */
        val XHS_RECOMMEND_CONTACTS = "xhsdiscover://recommend/contacts"

        /**
         * 推荐用户
         */
        val XHS_RECOMMEND_USER = "xhsdiscover://recommend/user"

        /**
         * 搜索
         */
        val XHS_SEARCH = "xhsdiscover://search/result"

        /**
         * 商城
         */
        val XHS_STORE = "xhsdiscover://store"

        /**
         * 开发者模式,可以修改登陆账号
         */
        val XHS_SYSTEM_SETTINGS = "xhsdiscover://system_settings/"

        /**
         * 话题
         */
        val XHS_TOPIC_KEYWORD = "xhsdiscover://topic/v2/keyword"

        /**
         * 用户主页
         */
        fun xhsUserProfile(userId: String) = "xhsdiscover://user/$userId"

        /**
         * TA的粉丝
         */
        fun xhsUserFollowers(userId: String) = "xhsdiscover://user/$userId/followers"
    }

}



